package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.Exif;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.data.Iptc;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.database.DatabaseActionsAfterDbInsertion;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.image.thumbnail.ThumbnailUtil;
import de.elmar_baumann.imv.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.imv.image.metadata.iptc.IptcMetadata;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * Writes image file metadata into the database if out of date or not existing.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class InsertImageFilesIntoDatabase implements Runnable {

    private static final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();
    private final int maxThumbnailLength = UserSettings.INSTANCE.getMaxThumbnailLength();
    private final boolean useEmbeddedThumbnails = UserSettings.INSTANCE.isUseEmbeddedThumbnails();
    private final String externalThumbnailCreationCommand = UserSettings.INSTANCE.getExternalThumbnailCreationCommand();
    private final List<String> filenames;
    private final EnumSet<Insert> what;
    private boolean stop = false;
    private long startTime;

    /**
     * Metadata to insert.
     */
    public enum Insert {

        /**
         * Insert metadata (EXIF, thumbnail, XMP) if the database's timestamp is
         * not equal to the file's date
         */
        OUT_OF_DATE,
        /**
         * Insert into the database the image file's EXIF or update it, even
         * when the image file's timestamp in the database is equal to the last
         * modification time of the image file in the file system
         */
        EXIF,
        /**
         * Insert into the database the image file's thumbnail or update it, even
         * when the image file's timestamp in the database is equal to the last
         * modification time of the image file in the file system
         */
        THUMBNAIL,
        /**
         * Insert into the database the image file's XMP data or update it, even
         * when the XMP file's timestamp in the database is equal to the last
         * modification time of the XMP file in the file system
         */
        XMP;
    }

    /**
     * Constructor.
     * 
     * @param filenames  names of the <em>image</em> files to be updated
     * @param what       what to insert
     */
    public InsertImageFilesIntoDatabase(List<String> filenames, EnumSet<Insert> what) {
        this.filenames = filenames;
        this.what = what;
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
    public synchronized void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    public synchronized void removeProgressListener(ProgressListener listener) {
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
        notifyProgressStarted(count > 0 ? filenames.get(0) : "");
        for (int index = 0; !stop && index < count; index++) {
            String filename = filenames.get(index);
            logCheckForUpdate(filename);
            ImageFile imageFile = getImageFile(filename);
            if (isUpdate(imageFile)) {
                logInsertImageFile(imageFile);
                db.insertImageFile(imageFile);
                runActionsAfterInserting(imageFile);
            }
            notifyProgressPerformed(index + 1, index + 1 < count ? filenames.get(index + 1) : filename);
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
        return what.contains(Insert.THUMBNAIL) ||
                (what.contains(Insert.OUT_OF_DATE) && !isImageFileUpToDate(filename));
    }

    private boolean isUpdateExif(String filename) {
        return what.contains(Insert.EXIF) ||
                (what.contains(Insert.OUT_OF_DATE) && !isImageFileUpToDate(filename));
    }

    private boolean isUpdateXmp(String filename) {
        return what.contains(Insert.XMP) ||
                (what.contains(Insert.OUT_OF_DATE) && !isXmpFileUpToDate(filename));
    }

    private boolean isImageFileUpToDate(String filename) {
        long dbTime = db.getLastModifiedImageFile(filename);
        long fileTime = FileUtil.getLastModified(filename);
        return fileTime == dbTime;
    }

    private boolean isXmpFileUpToDate(String imageFilename) {
        String sidecarFileName = XmpMetadata.getSidecarFilename(imageFilename);
        return sidecarFileName == null
                ? isEmbeddedXmpUpToDate(imageFilename)
                : isXmpSidecarFileUpToDate(imageFilename, sidecarFileName);
    }

    private boolean isXmpSidecarFileUpToDate(String imageFilename, String sidecarFilename) {
        assert FileUtil.existsFile(sidecarFilename);
        long dbTime = db.getLastModifiedXmp(imageFilename);
        long fileTime = FileUtil.getLastModified(sidecarFilename);
        return fileTime == dbTime;
    }

    private boolean isEmbeddedXmpUpToDate(String imageFilename) {
        if (!UserSettings.INSTANCE.isScanForEmbeddedXmp()) {
            return true;
        }
        long dbTime = db.getLastModifiedXmp(imageFilename);
        long fileTime = FileUtil.getLastModified(imageFilename);
        if (dbTime == fileTime) {
            return true;
        }
        boolean hasEmbeddedXmp = XmpMetadata.getEmbeddedXmp(imageFilename) != null; // slow if large image file whitout XMP
        return !hasEmbeddedXmp || fileTime == dbTime;
    }

    private void setThumbnail(ImageFile imageFile) {
        String filename = imageFile.getFilename();
        Image thumbnail = null;
        File file = new File(filename);
        if (UserSettings.INSTANCE.isCreateThumbnailsWithExternalApp()) {
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

    private void setXmp(ImageFile imageFile) {
        String imageFilename = imageFile.getFilename();
        Xmp xmp = XmpMetadata.getXmp(imageFilename);
        if (xmp == null) {
            xmp = getXmpFromIptc(imageFilename);
        }
        writeSidecarFileIfNotExists(imageFilename, xmp);
        if (xmp != null && !xmp.isEmpty()) {
            imageFile.setXmp(xmp);
        }
    }

    private Xmp getXmpFromIptc(String imageFilename) {
        Xmp xmp = null;
        Iptc iptc = IptcMetadata.getIptc(new File(imageFilename));
        if (iptc != null) {
            xmp = new Xmp();
            xmp.setIptc(iptc, Xmp.SetIptc.DONT_CHANGE_EXISTING_VALUES);
        }
        return xmp;
    }

    private void writeSidecarFileIfNotExists(String imageFilename, Xmp xmp) {
        if (xmp != null && !XmpMetadata.existsSidecarFile(imageFilename) &&
                XmpMetadata.canWriteSidecarFile(imageFilename)) {
            XmpMetadata.writeMetadataToSidecarFile(
                    XmpMetadata.suggestSidecarFilename(imageFilename), xmp);
        }
    }

    private void runActionsAfterInserting(ImageFile imageFile) {
        if (!isRunActionsAfterInserting(imageFile)) return;
        File imgFile = imageFile.getFile();
        List<Program> actions = DatabaseActionsAfterDbInsertion.INSTANCE.getAll();
        for (Program action : actions) {
            ProgramStarter programStarter = new ProgramStarter(null);
            programStarter.startProgram(action, Collections.singletonList(imgFile));
        }
    }

    private boolean isRunActionsAfterInserting(ImageFile imageFile) {
        UserSettings settings = UserSettings.INSTANCE;
        return settings.isExecuteActionsAfterImageChangeInDbAlways() || settings.isExecuteActionsAfterImageChangeInDbIfImageHasXmp() &&
                imageFile.getXmp() != null;
    }

    private void errorMessageNullThumbnail(String filename) {
        AppLog.logWarning(InsertImageFilesIntoDatabase.class, Bundle.getString(
                "ImageMetadataToDatabase.ErrorMessage.NullThumbnail",
                filename));
    }

    private synchronized void notifyProgressStarted(String filename) {
        for (ProgressListener listener : progressListeners) {
            listener.progressStarted(getProgressEvent(0, filename));
        }
    }

    private synchronized void notifyProgressPerformed(int value, String filename) {
        for (ProgressListener listener : progressListeners) {
            ProgressEvent event = getProgressEvent(value, filename);
            listener.progressPerformed(event);
            checkStop(event);
        }
    }

    private synchronized void notifyProgressEnded() {
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

    private void logCheckForUpdate(String filename) {
        AppLog.logFinest(InsertImageFilesIntoDatabase.class, Bundle.getString(
                "ImageMetadataToDatabase.InformationMessage.CheckForUpdate",
                filename));
    }

    private void logInsertImageFile(ImageFile data) {
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
        AppLog.logInfo(InsertImageFilesIntoDatabase.class, Bundle.getString(
                "ImageMetadataToDatabase.InformationMessage.StartInsert",
                params));
    }
}
