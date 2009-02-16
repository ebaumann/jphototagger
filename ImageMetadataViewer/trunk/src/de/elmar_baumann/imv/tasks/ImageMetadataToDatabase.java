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
import de.elmar_baumann.imv.types.DatabaseUpdate;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.Image;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Write image file metadata into the database.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ImageMetadataToDatabase implements Runnable {

    private static final DatabaseImageFiles db = DatabaseImageFiles.getInstance();
    private final List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();
    private final int maxThumbnailWidth = UserSettings.getInstance().getMaxThumbnailWidth();
    private final boolean useEmbeddedThumbnails = UserSettings.getInstance().isUseEmbeddedThumbnails();
    private final List<String> filenames;
    private final DatabaseUpdate update;
    private boolean stop = false;
    private long startTime;
    private int delaySeconds = 0;

    /**
     * Constructor.
     * 
     * @param filenames  names of the <em>image</em> files to be updated
     * @param update     update modalities
     */
    public ImageMetadataToDatabase(List<String> filenames, DatabaseUpdate update) {
        this.filenames = filenames;
        this.update = update;
    }

    @Override
    public void run() {
        delay();
        notifyProgressStarted();
        int count = filenames.size();
        startTime = System.currentTimeMillis();
        for (int index = 0; !stop && index < count; index++) {
            String filename = filenames.get(index);
            ImageFile data = getImageFileData(filename);
            if (isUpdate(data)) {
                logInsertImageFile(data);
                db.insertImageFile(data);
            }
            notifyProgressPerformed(index + 1, filename);
        }
        notifyProgressEnded();
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
     * Sets the time to wait before the metadata will be created.
     * 
     * @param seconds  senconds to wait. Default: zero
     */
    public void setDelaySeconds(int seconds) {
        delaySeconds = seconds;
    }

    private void delay() {
        if (delaySeconds > 0) {
            try {
                Thread.sleep(delaySeconds * 1000);
            } catch (InterruptedException ex) {
                de.elmar_baumann.imv.Log.logWarning(getClass(), ex);
            }
        }
    }

    private boolean isUpdate(ImageFile data) {
        return data.getExif() != null ||
            data.getXmp() != null ||
            data.getThumbnail() != null;
    }

    private boolean isUpdateThumbnail(String filename) {
        return update.isUpdate(DatabaseUpdate.THUMBNAIL) ||
            !isImageFileUpToDate(filename);
    }

    private boolean isUpdateXmp(String filename) {
        return update.isUpdate(DatabaseUpdate.XMP) ||
            !isXmpFileUpToDate(filename);
    }

    private boolean isUpdateExif(String filename) {
        return update.isUpdate(DatabaseUpdate.EXIF) ||
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

    private ImageFile getImageFileData(String filename) {
        ImageFile imageFileData = new ImageFile();
        imageFileData.setFilename(filename);
        imageFileData.setLastmodified(FileUtil.getLastModified(filename));
        if (isUpdateThumbnail(filename)) {
            imageFileData.setThumbnail(getThumbnail(filename));
        }
        if (isUpdateXmp(filename)) {
            setXmp(imageFileData);
        }
        if (isUpdateExif(filename)) {
            setExif(imageFileData);
        }
        return imageFileData;
    }

    private Image getThumbnail(String filename) {
        UserSettings settings = UserSettings.getInstance();
        Image thumbnail = null;
        File file = new File(filename);
        if (settings.isCreateThumbnailsWithExternalApp()) {
            thumbnail = ThumbnailUtil.getThumbnailFromExternalApplication(
                file, settings.getExternalThumbnailCreationCommand(), maxThumbnailWidth);
        } else {
            thumbnail = ThumbnailUtil.getThumbnail(
                file, maxThumbnailWidth, useEmbeddedThumbnails);
        }
        if (thumbnail == null) {
            notifyNullThumbnail(filename);
        }
        return thumbnail;
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
        Log.logInfo(ImageMetadataToDatabase.class, msg.format(params));
    }

    private void setExif(ImageFile imageFileData) {
        Exif exif = ExifMetadata.getExif(imageFileData.getFile());
        if (exif != null && !exif.isEmpty()) {
            imageFileData.setExif(exif);
        }
    }

    private void setXmp(ImageFile imageFileData) {
        Xmp xmp = XmpMetadata.getXmp(imageFileData.getFilename());
        if (xmp != null && !xmp.isEmpty()) {
            imageFileData.setXmp(xmp);
        }
    }

    /**
     * Unterbricht die Abarbeitung der Bilddateien.
     */
    public void stop() {
        stop = true;
    }

    private void notifyNullThumbnail(String filename) {
        MessageFormat msg = new MessageFormat(Bundle.getString("ImageMetadataToDatabase.ErrorMessage.NullThumbnail")); // NOI18N
        Object[] params = {filename};
        String formattedMessage = msg.format(params);
        Log.logWarning(ImageMetadataToDatabase.class, formattedMessage);
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
            if (event.isStop()) {
                stop();
            }
        }
    }

    private void notifyProgressEnded() {
        for (ProgressListener listener : progressListeners) {
            listener.progressEnded(getProgressEvent(filenames.size(), Bundle.getString("ImageMetadataToDatabase.InformationMessage.ProgressEnded"))); // NOI18N
        }
    }

    private ProgressEvent getProgressEvent(int value, Object info) {
        int taskCount = filenames.size();
        ProgressEvent evt = new ProgressEvent(this, 0, taskCount, value, info);
        if (value > 0) {
            long usedTime = System.currentTimeMillis() - startTime;
            long timePerTask = usedTime / value;
            int remainingTaskCount = taskCount - value;
            evt.setMilliSecondsRemaining(timePerTask * remainingTaskCount);
        }
        return evt;
    }
}
