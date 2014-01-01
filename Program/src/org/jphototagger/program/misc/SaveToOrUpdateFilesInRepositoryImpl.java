package org.jphototagger.program.misc;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bushe.swing.event.EventBus;
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.event.listener.ProgressListenerSupport;
import org.jphototagger.domain.image.ImageFile;
import org.jphototagger.domain.metadata.event.UpdateMetadataCheckEvent;
import org.jphototagger.domain.metadata.event.UpdateMetadataCheckEvent.Type;
import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.metadata.exif.ExifUtil;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpIptc4XmpCoreDateCreatedMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpLastModifiedMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpModifier;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.repository.ActionsAfterRepoUpdatesRepository;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.repository.SaveOrUpdate;
import org.jphototagger.domain.repository.SaveToOrUpdateFilesInRepository;
import org.jphototagger.domain.repository.ThumbnailsRepository;
import org.jphototagger.image.util.ThumbnailCreatorService;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.ThreadUtil;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.program.module.programs.StartPrograms;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.jphototagger.xmp.XmpMetadata;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = SaveToOrUpdateFilesInRepository.class)
public final class SaveToOrUpdateFilesInRepositoryImpl extends Thread implements Cancelable, SaveToOrUpdateFilesInRepository {

    private static final Logger LOGGER = Logger.getLogger(SaveToOrUpdateFilesInRepositoryImpl.class.getName());
    private final ImageFilesRepository imageFilesRepository = Lookup.getDefault().lookup(ImageFilesRepository.class);
    private final ActionsAfterRepoUpdatesRepository actionsAfterRepoUpdatesRepository = Lookup.getDefault().lookup(ActionsAfterRepoUpdatesRepository.class);
    private final ProgressListenerSupport progessListeners = new ProgressListenerSupport();
    private final ProgressEvent progressEvent = new ProgressEvent.Builder().source(this).build();
    private final Set<SaveOrUpdate> saveOrUpdate = EnumSet.noneOf(SaveOrUpdate.class);
    private final List<File> files;
    private final ThumbnailsRepository thumbnailsRepository = Lookup.getDefault().lookup(ThumbnailsRepository.class);
    private final XmpSidecarFileResolver xmpSidecarFileResolver = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);
    private final Collection<? extends XmpModifier> xmpModifiers = Lookup.getDefault().lookupAll(XmpModifier.class);
    private volatile boolean cancel;

    public SaveToOrUpdateFilesInRepositoryImpl() {
        super("JPhotoTagger: Inserting image files into repository");
        files = new ArrayList<>();
    }

    public SaveToOrUpdateFilesInRepositoryImpl(Collection<? extends File> files, SaveOrUpdate... saveOrUpdate) {
        super("JPhotoTagger: Inserting image files into repository");
        if (files == null) {
            throw new NullPointerException("files == null");
        }
        if (saveOrUpdate == null) {
            throw new NullPointerException("saveOrUpdate == null");
        }
        this.files = new ArrayList<>(files);
        this.saveOrUpdate.addAll(Arrays.asList(saveOrUpdate));
    }

    @Override
    public SaveToOrUpdateFilesInRepository createInstance(Collection<? extends File> files, SaveOrUpdate... saveOrUpdate) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }
        if (saveOrUpdate == null) {
            throw new NullPointerException("what == saveOrUpdate");
        }
        return new SaveToOrUpdateFilesInRepositoryImpl(files, saveOrUpdate);
    }

    @Override
    public void saveOrUpdateInNewThread() {
        start();
    }

    @Override
    public void saveOrUpdateWaitForTermination() {
        ThreadUtil.runInThisThread(this);
    }

    @Override
    public void run() {
        int count = files.size();
        int index;
        notifyStarted();
        for (index = 0; !cancel && !isInterrupted() && (index < count); index++) {
            File file = files.get(index);
            if (checkExists(file)) {
                deleteXmpFromRepositoryIfAbsentInFilesystem(file);
                ImageFile imageFile = createImageFile(file);
                if (isUpdate(imageFile)) {
                    setExifDateToXmpDateCreated(imageFile);
                    logInsertImageFile(imageFile);
                    imageFilesRepository.saveOrUpdateImageFile(imageFile);
                    runActionsAfterInserting(imageFile);
                }
            }
            notifyPerformed(index + 1, file);
        }
        notifyEnded(index);
    }

    private boolean isUpdate(ImageFile imageFile) {
        return (imageFile.getExif() != null) || (imageFile.getXmp() != null) || (imageFile.getThumbnail() != null);
    }

    private ImageFile createImageFile(File file) {
        ImageFile imageFile = new ImageFile();
        imageFile.setFile(file);
        imageFile.setLastmodified(file.lastModified());
        imageFile.setSizeInBytes(file.length());
        if (isUpdateThumbnail(file)) {
            imageFile.addToSaveIntoRepository(SaveOrUpdate.THUMBNAIL);
            createAndSetThumbnailToImageFile(imageFile);
        }
        if (isUpdateXmp(file)) {
            imageFile.addToSaveIntoRepository(SaveOrUpdate.XMP);
            setXmpToImageFile(imageFile);
        }
        if (isUpdateExif(file)) {
            imageFile.addToSaveIntoRepository(SaveOrUpdate.EXIF);
            setExifToImageFile(imageFile);
        }
        return imageFile;
    }

    private boolean isUpdateThumbnail(File imageFile) {
        return saveOrUpdate.contains(SaveOrUpdate.THUMBNAIL)
                || (saveOrUpdate.contains(SaveOrUpdate.OUT_OF_DATE)
                && (!existsThumbnail(imageFile) || !isThumbnailUpToDate(imageFile)));
    }

    private boolean existsThumbnail(File imageFile) {
        return thumbnailsRepository.existsThumbnail(imageFile);
    }

    private boolean isUpdateExif(File imageFile) {
        return saveOrUpdate.contains(SaveOrUpdate.EXIF)
                || (saveOrUpdate.contains(SaveOrUpdate.OUT_OF_DATE) && !isImageFileUpToDate(imageFile));
    }

    private boolean isUpdateXmp(File file) {
        return saveOrUpdate.contains(SaveOrUpdate.XMP)
                || (saveOrUpdate.contains(SaveOrUpdate.OUT_OF_DATE) && !isXmpUpToDate(file));
    }

    private boolean isImageFileUpToDate(File imageFile) {
        long repoTime = imageFilesRepository.findImageFilesLastModifiedTimestamp(imageFile);
        long fileTime = imageFile.lastModified();
        long repoSizeInBytes = imageFilesRepository.findImageFilesSizeInBytes(imageFile);
        long fileSizeInBytes = imageFile.length();
        return fileTime == repoTime && fileSizeInBytes == repoSizeInBytes;
    }

    private boolean isThumbnailUpToDate(File file) {
        long lastModifiedThumbnailFile = thumbnailsRepository.findLastModified(file);
        if (lastModifiedThumbnailFile < 0) {
            return false;
        }
        long lastModifiedFile = file.lastModified();
        return lastModifiedThumbnailFile >= lastModifiedFile;
    }

    private boolean isXmpUpToDate(File file) {
        File xmpFile = xmpSidecarFileResolver.getXmpSidecarFileOrNullIfNotExists(file);
        return (xmpFile == null)
                ? isScanForEmbeddedXmp() && isEmbeddedXmpUpToDate(file)
                : isXmpSidecarFileUpToDate(file, xmpFile);
    }

    private boolean isScanForEmbeddedXmp() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(DomainPreferencesKeys.KEY_SCAN_FOR_EMBEDDED_XMP)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_SCAN_FOR_EMBEDDED_XMP)
                : false;
    }

    private boolean isXmpSidecarFileUpToDate(File imageFile, File sidecarFile) {
        long repoTime = imageFilesRepository.findXmpFilesLastModifiedTimestamp(imageFile);
        long fileTime = sidecarFile.lastModified();
        return fileTime == repoTime;
    }

    private boolean isEmbeddedXmpUpToDate(File file) {
        long repoTime = imageFilesRepository.findXmpFilesLastModifiedTimestamp(file);
        long fileTime = file.lastModified();
        if (repoTime == fileTime) {
            return true;
        }
        // slow if large image file whitout XMP
        boolean hasEmbeddedXmp = XmpMetadata.getEmbeddedXmp(file) != null;
        if (!hasEmbeddedXmp) {    // Avoid unneccesary 2nd calls
            imageFilesRepository.setLastModifiedToXmpSidecarFileOfImageFile(file, fileTime);
        }
        return !hasEmbeddedXmp;
    }

    private void createAndSetThumbnailToImageFile(ImageFile imageFile) {
        File file = imageFile.getFile();
        Image thumbnail = ThumbnailCreatorService.INSTANCE.createThumbnail(file);
        imageFile.setThumbnail(thumbnail);
        if (thumbnail == null) {
            logErrorNullThumbnail(file);
            imageFile.setThumbnail(AppLookAndFeel.ERROR_THUMBNAIL);
        }
    }

    private void setExifToImageFile(ImageFile imageFile) {
        File file = imageFile.getFile();
        Exif exif = ExifUtil.readExif(file);
        imageFile.setExif(exif);
    }

    private void setXmpToImageFile(ImageFile imageFile) {
        File file = imageFile.getFile();
        Xmp xmp;
        try {
            xmp = xmpSidecarFileResolver.hasXmpSidecarFile(file)
                    ? XmpMetadata.getXmpFromSidecarFileOf(file)
                    : isScanForEmbeddedXmp()
                    ? XmpMetadata.getEmbeddedXmp(file)
                    : null;
        } catch (IOException ex) {
            Logger.getLogger(SaveToOrUpdateFilesInRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        modifyXmp(xmpSidecarFileResolver.findSidecarFile(xmpSidecarFileResolver.suggestXmpSidecarFile(file)), xmp);
        writeSidecarFileIfNotExists(file, xmp);
        if ((xmp != null) && !xmp.isEmpty()) {
            imageFile.setXmp(xmp);
        }
    }

    private void modifyXmp(File sidecarFile, Xmp xmp) {
        if (sidecarFile == null || xmp == null) {
            return;
        }
        for (XmpModifier xmpModifier : xmpModifiers) {
            if (xmpModifier.modifyXmp(sidecarFile, xmp)) {
                XmpMetadata.writeXmpToSidecarFile(xmp, sidecarFile);
            }
        }
    }

    private void setExifDateToXmpDateCreated(ImageFile imageFile) {
        Exif exif = imageFile.getExif();
        Xmp xmp = imageFile.getXmp();
        boolean hasExif = exif != null;
        boolean hasXmp = xmp != null;
        boolean hasXmpDateCreated = hasXmp && xmp.contains(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE);
        boolean hasExifDate = hasExif && (exif.getDateTimeOriginal() != null);
        if (hasXmpDateCreated || !hasXmp || !hasExif || !hasExifDate) {
            return;
        }
        xmp.setValue(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE, exif.getXmpDateCreated());
        File sidecarFile = xmpSidecarFileResolver.suggestXmpSidecarFile(imageFile.getFile());
        if (sidecarFile.canWrite()) {
            XmpMetadata.writeXmpToSidecarFile(xmp, sidecarFile);
            xmp.setValue(XmpLastModifiedMetaDataValue.INSTANCE, sidecarFile.lastModified());
        }
    }

    private void writeSidecarFileIfNotExists(File imageFile, Xmp xmp) {
        if ((xmp != null) && !xmpSidecarFileResolver.hasXmpSidecarFile(imageFile)
                && XmpMetadata.canWriteSidecarFileForImageFile(imageFile)) {
            File sidecarFile = xmpSidecarFileResolver.suggestXmpSidecarFile(imageFile);
            XmpMetadata.writeXmpToSidecarFile(xmp, sidecarFile);
        }
    }

    private void runActionsAfterInserting(ImageFile imageFile) {
        if (!isRunActionsAfterInserting(imageFile)) {
            return;
        }
        File file = imageFile.getFile();
        List<Program> actions = actionsAfterRepoUpdatesRepository.findAllActions();
        for (Program action : actions) {
            StartPrograms programStarter = new StartPrograms();
            programStarter.startProgram(action, Collections.singletonList(file), true);
        }
    }

    private void deleteXmpFromRepositoryIfAbsentInFilesystem(File file) {
        if (xmpSidecarFileResolver.hasXmpSidecarFile(file)) {
            return;
        }
        if (imageFilesRepository.existsXmpForFile(file)) {
            LOGGER.log(Level.INFO, "Deleting from Repository XMP of file ''{0}'' - it does not have (anymore) a XMP sidecar file", file);
            imageFilesRepository.deleteXmpOfFile(file);
        }
    }

    private boolean isRunActionsAfterInserting(ImageFile imageFile) {
        return isExecuteActionsAfterImageChangeInDbAlways()
                || (isExecuteActionsAfterImageChangeInDbIfImageHasXmp() && (imageFile.getXmp() != null));
    }

    private boolean isExecuteActionsAfterImageChangeInDbIfImageHasXmp() {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        return preferences.containsKey(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP)
                ? preferences.getBoolean(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP)
                : false;
    }

    private boolean isExecuteActionsAfterImageChangeInDbAlways() {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        return preferences.containsKey(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS)
                ? preferences.getBoolean(AppPreferencesKeys.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS)
                : false;
    }

    @Override
    public void cancel() {
        cancel = true;
    }

    private void notifyUpdateMetadataCheckListener(Type type, File file) {
        UpdateMetadataCheckEvent evt = new UpdateMetadataCheckEvent(type, file);
        EventBus.publish(evt);
    }

    @Override
    public void addProgressListener(ProgressListener progessListener) {
        if (progessListener == null) {
            throw new NullPointerException("progessListener == null");
        }
        progessListeners.add(progessListener);
    }

    @Override
    public void removeProgressListener(ProgressListener progessListener) {
        if (progessListener == null) {
            throw new NullPointerException("progessListener == null");
        }
        progessListeners.remove(progessListener);
    }

    private void notifyStarted() {
        notifyUpdateMetadataCheckListener(Type.CHECK_STARTED, null);
        progressEvent.setMinimum(0);
        progressEvent.setMaximum(files.size());
        progressEvent.setValue(0);
        progessListeners.notifyStarted(progressEvent);
    }

    private void notifyPerformed(int value, File file) {
        logPerformed(file);
        notifyUpdateMetadataCheckListener(Type.CHECKING_FILE, file);
        progressEvent.setValue(value);
        progressEvent.setInfo(file);
        progessListeners.notifyPerformed(progressEvent);
    }

    private void notifyEnded(int filecount) {
        logEnded(filecount);
        notifyUpdateMetadataCheckListener(Type.CHECK_FINISHED, null);
        progessListeners.notifyEnded(progressEvent);
    }

    private void logErrorNullThumbnail(File file) {
        LOGGER.log(Level.WARNING, "Thumbnail couldn''t be created for image file ''{0}''", file);
    }

    private void logPerformed(File file) {
        LOGGER.log(Level.FINEST, "Synchronizing ''{0}'' with the repository", file);
    }

    private void logEnded(int filecount) {
        LOGGER.log(Level.INFO, "Synchronized {0} image files with the repository", filecount);
    }

    private boolean checkExists(File imageFile) {
        if (!imageFile.exists()) {
            LOGGER.log(Level.INFO, "Image file ''{0}'' does not (longer) exist and will not be updated in the repository", imageFile);
            return false;
        }
        return true;
    }

    private void logInsertImageFile(ImageFile data) {
        Object[] params = {data.getFile().getAbsolutePath(), (data.getExif() == null)
            ? Bundle.getString(SaveToOrUpdateFilesInRepositoryImpl.class, "SaveToOrUpdateFilesInRepositoryImpl.Info.StartInsert.No")
            : Bundle.getString(SaveToOrUpdateFilesInRepositoryImpl.class, "SaveToOrUpdateFilesInRepositoryImpl.Info.StartInsert.Yes"), (data.getXmp() == null)
            ? Bundle.getString(SaveToOrUpdateFilesInRepositoryImpl.class, "SaveToOrUpdateFilesInRepositoryImpl.Info.StartInsert.No")
            : Bundle.getString(SaveToOrUpdateFilesInRepositoryImpl.class, "SaveToOrUpdateFilesInRepositoryImpl.Info.StartInsert.Yes"), (data.getThumbnail() == null)
            ? Bundle.getString(SaveToOrUpdateFilesInRepositoryImpl.class, "SaveToOrUpdateFilesInRepositoryImpl.Info.StartInsert.No")
            : Bundle.getString(SaveToOrUpdateFilesInRepositoryImpl.class, "SaveToOrUpdateFilesInRepositoryImpl.Info.StartInsert.Yes")};
        LOGGER.log(Level.INFO, "Add metadata into the repository of file ''{0}'': EXIF: {1}, XMP: {2}, Thumbnail: {3}", params);
    }
}
