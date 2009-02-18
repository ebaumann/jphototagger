package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.Log;
import de.elmar_baumann.imv.data.Exif;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.image.thumbnail.ThumbnailUtil;
import de.elmar_baumann.imv.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.MetaDataForceDbUpdate;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.Image;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Write image file metadata into the database.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class InsertImageFilesIntoDatabase implements Runnable {

    private static final DatabaseImageFiles db = DatabaseImageFiles.getInstance();
    private final List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();
    private final int maxThumbnailLength = UserSettings.getInstance().getMaxThumbnailLength();
    private final boolean useEmbeddedThumbnails = UserSettings.getInstance().isUseEmbeddedThumbnails();
    private final String externalThumbnailCreationCommand = UserSettings.getInstance().getExternalThumbnailCreationCommand();
    private final List<String> filenames;
    private final EnumSet<MetaDataForceDbUpdate> forceUpdateOf;
    volatile private boolean stop = false;
    private long startTime;

    /**
     * Constructor.
     * 
     * @param filenames    names of the <em>image</em> files to be updated
     * @param forceUpdate  metadata to insert even when file's dates equals to
     *                     their time stamps in the database
     */
    public InsertImageFilesIntoDatabase(List<String> filenames, EnumSet<MetaDataForceDbUpdate> forceUpdateOf) {
        this.filenames = filenames;
        this.forceUpdateOf = forceUpdateOf;
    }

    /**
     * Fügt einen Fortschrittsbeobachter hinzu. Dieser wird benachrichtigt,
     * bevor die erste Bilddatei abgearbeitet wurde
     * ({@link de.elmar_baumann.imv.event.ProgressListener#progressStarted(de.elmar_baumann.imv.event.ProgressEvent)}),
     * nach dem Abarbeiten jeder Bilddatei
     * ({@link de.elmar_baumann.imv.event.ProgressListener#progressPerformed(de.elmar_baumann.imv.event.ProgressEvent)})
     * und nachdem alle Bilddateien abgearbeitet sind
     * ({@link de.elmar_baumann.imv.event.ProgressListener#progressEnded(de.elmar_baumann.imv.event.ProgressEvent)}).
     *
     * @param listener Fortschrittsbeobachter
     */
    public void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    public void removeProgressListener(ProgressListener listener) {
        progressListeners.remove(listener);
    }

    /**
     * Unterbricht die Abarbeitung der Bilddateien.
     */
    public void stop() {
        stop = true;
    }

    private void checkStop(ProgressEvent event) {
        if (event.isStop()) {
            stop();
        }
    }

    @Override
    public void run() {
        int count = filenames.size();
        startTime = System.currentTimeMillis();
        notifyProgressStarted();
        for (int index = 0; !stop && index < count; index++) {
            String filename = filenames.get(index);
            ImageFile imageFile = getImageFile(filename);
            if (isUpdate(imageFile)) {
                logInsertImageFile(imageFile);
                db.insertImageFile(imageFile);
            }
            notifyProgressPerformed(index + 1, filename);
        }
        notifyProgressEnded();
    }

    private boolean isUpdate(ImageFile imageFile) {
        return imageFile.getExif() != null ||
                imageFile.getXmp() != null ||
                imageFile.getThumbnail() != null;
    }

    private ImageFile getImageFile(String filename) {
        ImageFile imageFile = new ImageFile();
        imageFile.setFilename(filename);
        imageFile.setLastmodified(FileUtil.getLastModified(filename));
        if (isUpdateThumbnail(filename)) {
            setThumbnail(imageFile);
        }
        if (isUpdateXmp(filename)) {
            setXmp(imageFile);
        }
        if (isUpdateExif(filename)) {
            setExif(imageFile);
        }
        return imageFile;
    }

    private boolean isUpdateThumbnail(String filename) {
        return forceUpdateOf.contains(MetaDataForceDbUpdate.THUMBNAIL) ||
                !isImageFileUpToDate(filename);
    }

    private boolean isUpdateXmp(String filename) {
        return forceUpdateOf.contains(MetaDataForceDbUpdate.XMP) ||
                !isXmpFileUpToDate(filename);
    }

    private boolean isUpdateExif(String filename) {
        return forceUpdateOf.contains(MetaDataForceDbUpdate.EXIF) ||
                !isImageFileUpToDate(filename);
    }

    private boolean isImageFileUpToDate(String filename) {
        long dbTime = db.getLastModifiedImageFile(filename);
        long fileTime = FileUtil.getLastModified(filename);
        return fileTime == dbTime;
    }

    private boolean isXmpFileUpToDate(String imageFilename) {
        String sidecarFileName = XmpMetadata.getSidecarFilename(imageFilename);
        if (sidecarFileName == null) {
            return true;
        }
        long dbTime = db.getLastModifiedXmp(imageFilename);
        long fileTime = FileUtil.getLastModified(sidecarFileName);
        return fileTime == dbTime;
    }

    private void setThumbnail(ImageFile imageFile) {
        String filename = imageFile.getFilename();
        Image thumbnail = null;
        File file = new File(filename);
        if (UserSettings.getInstance().isCreateThumbnailsWithExternalApp()) {
            thumbnail = ThumbnailUtil.getThumbnailFromExternalApplication(
                    file, externalThumbnailCreationCommand, maxThumbnailLength);
        } else {
            thumbnail = ThumbnailUtil.getThumbnail(
                    file, maxThumbnailLength, useEmbeddedThumbnails);
        }
        imageFile.setThumbnail(thumbnail);
        if (thumbnail == null) {
            errorMessageNullThumbnail(filename);
        }
    }

    private void setExif(ImageFile imageFile) {
        Exif exif = ExifMetadata.getExif(imageFile.getFile());
        if (exif != null && !exif.isEmpty()) {
            imageFile.setExif(exif);
        }
    }

    private void setXmp(ImageFile imageFileData) {
        Xmp xmp = XmpMetadata.getXmp(imageFileData.getFilename());
        if (xmp != null && !xmp.isEmpty()) {
            imageFileData.setXmp(xmp);
        }
    }

    private void errorMessageNullThumbnail(String filename) {
        MessageFormat msg = new MessageFormat(Bundle.getString("ImageMetadataToDatabase.ErrorMessage.NullThumbnail")); // NOI18N
        Object[] params = {filename};
        String formattedMessage = msg.format(params);
        Log.logWarning(InsertImageFilesIntoDatabase.class, formattedMessage);
    }

    private void notifyProgressStarted() {
        for (ProgressListener listener : progressListeners) {
            listener.progressStarted(getProgressEvent(0, Bundle.getString("ImageMetadataToDatabase.InformationMessage.ProgressStarted"))); // NOI18N
        }
    }

    private void notifyProgressPerformed(int value, String filename) {
        for (ProgressListener listener : progressListeners) {
            ProgressEvent event = getProgressEvent(value, filename);
            listener.progressPerformed(event);
            checkStop(event);
        }
    }

    private void notifyProgressEnded() {
        for (ProgressListener listener : progressListeners) {
            listener.progressEnded(getProgressEvent(filenames.size(), Bundle.getString("ImageMetadataToDatabase.InformationMessage.ProgressEnded"))); // NOI18N
        }
    }

    private ProgressEvent getProgressEvent(int value, Object info) {
        int fileCount = filenames.size();
        if (value > 0) {
            long usedTime = System.currentTimeMillis() - startTime;
            long timePerTask = usedTime / value;
            int remainingTaskCount = fileCount - value;
            long milliSecondsRemaining = timePerTask * remainingTaskCount;
            return new ProgressEvent(this, 0, fileCount, value, milliSecondsRemaining, info);
        } else {
            return new ProgressEvent(this, 0, fileCount, value, info);
        }
    }

    private void logInsertImageFile(ImageFile data) {
        MessageFormat msg = new MessageFormat(Bundle.getString("ImageMetadataToDatabase.InformationMessage.StartInsert"));
        Object[] params = {
            data.getFile().getAbsolutePath(),
            data.getExif() == null
            ? Bundle.getString("ImageMetadataToDatabase.InformationMessage.StartInsert.No")
            : Bundle.getString("ImageMetadataToDatabase.InformationMessage.StartInsert.Yes"),
            data.getXmp() == null
            ? Bundle.getString("ImageMetadataToDatabase.InformationMessage.StartInsert.No")
            : Bundle.getString("ImageMetadataToDatabase.InformationMessage.StartInsert.Yes"),
            data.getThumbnail() == null
            ? Bundle.getString("ImageMetadataToDatabase.InformationMessage.StartInsert.No")
            : Bundle.getString("ImageMetadataToDatabase.InformationMessage.StartInsert.Yes")};
        Log.logInfo(InsertImageFilesIntoDatabase.class, msg.format(params));
    }
}
