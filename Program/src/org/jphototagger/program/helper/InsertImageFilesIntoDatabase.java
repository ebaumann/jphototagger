package org.jphototagger.program.helper;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.EventBus;
import org.jphototagger.api.core.Storage;
import org.jphototagger.domain.repository.InsertIntoRepository;
import org.jphototagger.domain.database.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.domain.database.xmp.ColumnXmpLastModified;
import org.jphototagger.domain.metadata.event.UpdateMetadataCheckEvent;
import org.jphototagger.domain.metadata.event.UpdateMetadataCheckEvent.Type;
import org.jphototagger.domain.event.listener.ProgressListenerSupport;
import org.jphototagger.domain.exif.Exif;
import org.jphototagger.domain.image.ImageFile;
import org.jphototagger.domain.xmp.Xmp;
import org.jphototagger.exif.ExifMetadata;
import org.jphototagger.exif.cache.ExifCache;
import org.jphototagger.lib.concurrent.Cancelable;
import org.jphototagger.api.event.ProgressEvent;
import org.jphototagger.api.event.ProgressListener;
import org.jphototagger.domain.repository.ImageFileRepository;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppFileFilters;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.cache.PersistentThumbnails;
import org.jphototagger.domain.database.programs.Program;
import org.jphototagger.program.database.DatabaseActionsAfterDbInsertion;
import org.jphototagger.program.image.thumbnail.ThumbnailUtil;
import org.jphototagger.xmp.XmpMetadata;
import org.openide.util.Lookup;

/**
 * Inserts or updates image file metadata - EXIF, thumbnail, XMP - into the
 * database.
 *
 * @author Elmar Baumann
 */
public final class InsertImageFilesIntoDatabase extends Thread implements Cancelable {

    private final ImageFileRepository repo = Lookup.getDefault().lookup(ImageFileRepository.class);
    private final ProgressListenerSupport pls = new ProgressListenerSupport();
    private ProgressEvent progressEvent = new ProgressEvent(this, null);
    private final Set<InsertIntoRepository> what = new HashSet<InsertIntoRepository>();
    private final List<File> imageFiles;
    private boolean cancel;
    private static final Logger LOGGER = Logger.getLogger(InsertImageFilesIntoDatabase.class.getName());

    /**
     * Constructor.
     *
     * @param imageFiles image files, whoes metadatada shall be inserted or
     *                   updated
     * @param what       metadata to insertAction
     */
    public InsertImageFilesIntoDatabase(List<File> imageFiles, InsertIntoRepository... what) {
        super("JPhotoTagger: Inserting image files into database");

        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        if (what == null) {
            throw new NullPointerException("what == null");
        }

        this.imageFiles = new ArrayList<File>(imageFiles);
        this.what.addAll(Arrays.asList(what));
    }

    @Override
    public void run() {
        int count = imageFiles.size();
        int index = 0;

        notifyStarted();

        for (index = 0; !cancel && !isInterrupted() && (index < count); index++) {
            File imgFile = imageFiles.get(index);

            // Notify before inserting to enable progress listeners displaying
            // the current image file
            notifyPerformed(index + 1, imgFile);

            if (checkExists(imgFile)) {
                ImageFile imageFile = getImageFile(imgFile);

                if (isUpdate(imageFile)) {
                    setExifDateToXmpDateCreated(imageFile);
                    logInsertImageFile(imageFile);
                    repo.insertOrUpdateImageFile(imageFile);
                    runActionsAfterInserting(imageFile);
                }
            }
        }

        notifyEnded(index);
    }

    private boolean isUpdate(ImageFile imageFile) {
        return (imageFile.getExif() != null) || (imageFile.getXmp() != null) || (imageFile.getThumbnail() != null);
    }

    private ImageFile getImageFile(File imgFile) {
        ImageFile imageFile = new ImageFile();

        imageFile.setFile(imgFile);
        imageFile.setLastmodified(imgFile.lastModified());

        if (isUpdateThumbnail(imgFile)) {
            imageFile.addInsertIntoDb(InsertIntoRepository.THUMBNAIL);
            createAndSetThumbnail(imageFile);
        }

        if (isUpdateXmp(imgFile)) {
            imageFile.addInsertIntoDb(InsertIntoRepository.XMP);
            setXmp(imageFile);
        }

        if (isUpdateExif(imgFile)) {
            imageFile.addInsertIntoDb(InsertIntoRepository.EXIF);
            setExif(imageFile);
        }

        return imageFile;
    }

    private boolean isUpdateThumbnail(File imageFile) {
        return what.contains(InsertIntoRepository.THUMBNAIL)
                || (what.contains(InsertIntoRepository.OUT_OF_DATE)
                && (!existsThumbnail(imageFile) || !isThumbnailUpToDate(imageFile)));
    }

    private boolean existsThumbnail(File imageFile) {
        return PersistentThumbnails.existsThumbnail(imageFile);
    }

    private boolean isUpdateExif(File imageFile) {
        return what.contains(InsertIntoRepository.EXIF) || (what.contains(InsertIntoRepository.OUT_OF_DATE) && !isImageFileUpToDate(imageFile));
    }

    private boolean isUpdateXmp(File imageFile) {
        return what.contains(InsertIntoRepository.XMP) || (what.contains(InsertIntoRepository.OUT_OF_DATE) && !isXmpUpToDate(imageFile));
    }

    private boolean isImageFileUpToDate(File imageFile) {
        long dbTime = repo.getImageFilesLastModifiedTimestamp(imageFile);
        long fileTime = imageFile.lastModified();

        return fileTime == dbTime;
    }

    private boolean isThumbnailUpToDate(File imageFile) {
        File tnFile = PersistentThumbnails.getThumbnailFile(imageFile);

        if ((tnFile == null) || !tnFile.exists()) {
            return false;
        }

        long lastModifiedTn = tnFile.lastModified();
        long lastModifiedImg = imageFile.lastModified();

        return lastModifiedTn >= lastModifiedImg;
    }

    private boolean isXmpUpToDate(File imageFile) {
        File xmpFile = XmpMetadata.getSidecarFile(imageFile);

        return (xmpFile == null)
                ? isScanForEmbeddedXmp() && isEmbeddedXmpUpToDate(imageFile)
                : isXmpSidecarFileUpToDate(imageFile, xmpFile);
    }

    private boolean isScanForEmbeddedXmp() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        return storage.containsKey(Storage.KEY_SCAN_FOR_EMBEDDED_XMP)
                ? storage.getBoolean(Storage.KEY_SCAN_FOR_EMBEDDED_XMP)
                : false;
    }

    private boolean isXmpSidecarFileUpToDate(File imageFile, File sidecarFile) {
        long dbTime = repo.getXmpFilesLastModifiedTimestamp(imageFile);
        long fileTime = sidecarFile.lastModified();

        return fileTime == dbTime;
    }

    private boolean isEmbeddedXmpUpToDate(File imageFile) {
        long dbTime = repo.getXmpFilesLastModifiedTimestamp(imageFile);
        long fileTime = imageFile.lastModified();

        if (dbTime == fileTime) {
            return true;
        }

        // slow if large image file whitout XMP
        boolean hasEmbeddedXmp = XmpMetadata.getEmbeddedXmp(imageFile) != null;

        if (!hasEmbeddedXmp) {    // Avoid unneccesary 2nd calls
            repo.setLastModifiedToXmpSidecarFileOfImageFile(imageFile, fileTime);
        }

        return !hasEmbeddedXmp;
    }

    private void createAndSetThumbnail(ImageFile imageFile) {
        File file = imageFile.getFile();
        Image thumbnail = ThumbnailUtil.getThumbnail(file);

        imageFile.setThumbnail(thumbnail);

        if (thumbnail == null) {
            errorMessageNullThumbnail(file);
            imageFile.setThumbnail(AppLookAndFeel.ERROR_THUMBNAIL);
        }
    }

    private void setExif(ImageFile imageFile) {
        File file = imageFile.getFile();
        Exif exif = null;

        if (!AppFileFilters.INSTANCE.isUserDefinedFileType(file)) {
            ExifCache.INSTANCE.deleteCachedExifTags(file);
            exif = ExifMetadata.getExif(file);
        }

        if ((exif != null) && !exif.isEmpty()) {
            imageFile.setExif(exif);
        } else {
            imageFile.setExif(null);
        }
    }

    private void setXmp(ImageFile imageFile) {
        File imgFile = imageFile.getFile();
        Xmp xmp = null;

        try {
            xmp = XmpMetadata.hasImageASidecarFile(imgFile)
                    ? XmpMetadata.getXmpFromSidecarFileOf(imgFile)
                    : isScanForEmbeddedXmp()
                    ? XmpMetadata.getEmbeddedXmp(imgFile)
                    : null;
        } catch (IOException ex) {
            Logger.getLogger(InsertImageFilesIntoDatabase.class.getName()).log(Level.SEVERE, null, ex);

            return;
        }

        writeSidecarFileIfNotExists(imgFile, xmp);

        if ((xmp != null) && !xmp.isEmpty()) {
            imageFile.setXmp(xmp);
        }
    }

    private void setExifDateToXmpDateCreated(ImageFile imageFile) {
        Exif exif = imageFile.getExif();
        Xmp xmp = imageFile.getXmp();
        boolean hasExif = exif != null;
        boolean hasXmp = xmp != null;
        boolean hasXmpDateCreated = hasXmp && xmp.contains(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
        boolean hasExifDate = hasExif && (exif.getDateTimeOriginal() != null);

        if (hasXmpDateCreated || !hasXmp || !hasExif || !hasExifDate) {
            return;
        }

        xmp.setValue(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE, exif.getXmpDateCreated());

        File sidecarFile = XmpMetadata.suggestSidecarFile(imageFile.getFile());

        if (sidecarFile.canWrite()) {
            XmpMetadata.writeXmpToSidecarFile(xmp, sidecarFile);
            xmp.setValue(ColumnXmpLastModified.INSTANCE, sidecarFile.lastModified());
        }
    }

    private void writeSidecarFileIfNotExists(File imageFile, Xmp xmp) {
        if ((xmp != null) && !XmpMetadata.hasImageASidecarFile(imageFile)
                && XmpMetadata.canWriteSidecarFileForImageFile(imageFile)) {
            File sidecarFile = XmpMetadata.suggestSidecarFile(imageFile);

            XmpMetadata.writeXmpToSidecarFile(xmp, sidecarFile);
        }
    }

    private void runActionsAfterInserting(ImageFile imageFile) {
        if (!isRunActionsAfterInserting(imageFile)) {
            return;
        }

        File imgFile = imageFile.getFile();
        List<Program> actions = DatabaseActionsAfterDbInsertion.INSTANCE.getAllActions();

        for (Program action : actions) {
            StartPrograms programStarter = new StartPrograms(null);

            programStarter.startProgram(action, Collections.singletonList(imgFile), true);
        }
    }

    private boolean isRunActionsAfterInserting(ImageFile imageFile) {
        return isExecuteActionsAfterImageChangeInDbAlways()
                || (isExecuteActionsAfterImageChangeInDbIfImageHasXmp() && (imageFile.getXmp() != null));
    }

    private boolean isExecuteActionsAfterImageChangeInDbIfImageHasXmp() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        return storage.containsKey(Storage.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP)
                ? storage.getBoolean(Storage.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP)
                : false;
    }

    private boolean isExecuteActionsAfterImageChangeInDbAlways() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        return storage.containsKey(Storage.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS)
                ? storage.getBoolean(Storage.KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS)
                : false;
    }

    /**
     * A <em>soft</em> interrupt: I/O operations can finishing their current
     * process.
     */
    @Override
    public void cancel() {
        cancel = true;
    }

    private void notifyUpdateMetadataCheckListener(Type type, File file) {
        UpdateMetadataCheckEvent evt = new UpdateMetadataCheckEvent(type, file);

        EventBus.publish(evt);
    }

    /**
     * Adds a progress listener.
     *
     * {@link ProgressEvent#getInfo()} contains a {@code java.lang.String} of
     * the updated
     * @param listener
     */
    public void addProgressListener(ProgressListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        pls.add(listener);
    }

    public void removeProgressListener(ProgressListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        pls.remove(listener);
    }

    private void notifyStarted() {
        notifyUpdateMetadataCheckListener(Type.CHECK_STARTED, null);
        progressEvent.setMinimum(0);
        progressEvent.setMaximum(imageFiles.size());
        progressEvent.setValue(0);
        pls.notifyStarted(progressEvent);
    }

    private void notifyPerformed(int value, File file) {
        informationMessagePerformed(file);
        notifyUpdateMetadataCheckListener(Type.CHECKING_FILE, file);
        progressEvent.setValue(value);
        progressEvent.setInfo(file);
        pls.notifyPerformed(progressEvent);
    }

    private void notifyEnded(int filecount) {
        informationMessageEnded(filecount);
        notifyUpdateMetadataCheckListener(Type.CHECK_FINISHED, null);
        pls.notifyEnded(progressEvent);
    }

    private void errorMessageNullThumbnail(File file) {
        LOGGER.log(Level.WARNING, "Thumbnail couldn't be created for image file ''{0}''", file);
    }

    private void informationMessagePerformed(File file) {
        LOGGER.log(Level.FINEST, "Synchronizing ''{0}'' with the database", file);
    }

    private void informationMessageEnded(int filecount) {
        LOGGER.log(Level.INFO, "Synchronized {0} image files with the database", filecount);
    }

    private boolean checkExists(File imageFile) {
        if (!imageFile.exists()) {
            LOGGER.log(Level.WARNING, "Image file ''{0}'' does not (longer) exist and will not be updated in the database", imageFile);

            return false;
        }

        return true;
    }

    private void logInsertImageFile(ImageFile data) {
        Object[] params = {data.getFile().getAbsolutePath(), (data.getExif() == null)
            ? Bundle.getString(InsertImageFilesIntoDatabase.class, "InsertImageFilesIntoDatabase.Info.StartInsert.No")
            : Bundle.getString(InsertImageFilesIntoDatabase.class, "InsertImageFilesIntoDatabase.Info.StartInsert.Yes"), (data.getXmp() == null)
            ? Bundle.getString(InsertImageFilesIntoDatabase.class, "InsertImageFilesIntoDatabase.Info.StartInsert.No")
            : Bundle.getString(InsertImageFilesIntoDatabase.class, "InsertImageFilesIntoDatabase.Info.StartInsert.Yes"), (data.getThumbnail() == null)
            ? Bundle.getString(InsertImageFilesIntoDatabase.class, "InsertImageFilesIntoDatabase.Info.StartInsert.No")
            : Bundle.getString(InsertImageFilesIntoDatabase.class, "InsertImageFilesIntoDatabase.Info.StartInsert.Yes")};

        LOGGER.log(Level.INFO, "Add metadata into the database of file ''{0}'': EXIF: {1}, XMP: {2}, Thumbnail: {3}", params);
    }
}
