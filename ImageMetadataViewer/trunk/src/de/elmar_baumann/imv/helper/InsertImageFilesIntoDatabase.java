package de.elmar_baumann.imv.helper;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.Exif;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.cache.PersistentThumbnails;
import de.elmar_baumann.imv.data.Iptc;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.database.DatabaseActionsAfterDbInsertion;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.CheckForUpdateMetadataEvent;
import de.elmar_baumann.imv.event.CheckForUpdateMetadataEvent.Type;
import de.elmar_baumann.imv.event.listener.CheckingForUpdateMetadataListener;
import de.elmar_baumann.imv.image.thumbnail.ThumbnailUtil;
import de.elmar_baumann.imv.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.imv.image.metadata.iptc.IptcMetadata;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.panels.ProgressBarAutomaticTasks;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.resource.MutualExcludedResource;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JProgressBar;

/**
 * Writes image file metadata into the database if out of date or not existing.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class InsertImageFilesIntoDatabase extends Thread {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final int maxThumbnailLength =
            UserSettings.INSTANCE.getMaxThumbnailLength();
    private final boolean useEmbeddedThumbnails =
            UserSettings.INSTANCE.isUseEmbeddedThumbnails();
    private final String externalThumbnailCreationCommand =
            UserSettings.INSTANCE.getExternalThumbnailCreationCommand();
    private String currentFilename;
    private final Set<CheckingForUpdateMetadataListener> actionListeners =
            Collections.synchronizedSet(
            new HashSet<CheckingForUpdateMetadataListener>());
    private final List<String> filenames;
    private final EnumSet<Insert> what;
    private final MutualExcludedResource<? extends JProgressBar> progressBarResource;
    private boolean cancelled;
    private JProgressBar progressBar;

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
     * @param filenames           names of the <em>image</em> files to be updated
     * @param what                what to insert
     * @param progressBarResource a resource with an <code>JProgressBar</code>
     *                            as
     *                            {@link MutualExcludedResource#getResource(java.lang.Object)
     *                            or null if this class shall not display the
     *                            progress
     */
    public InsertImageFilesIntoDatabase(
            List<String> filenames,
            EnumSet<Insert> what,
            MutualExcludedResource<? extends JProgressBar> progressBarResource) {

        this.filenames = new ArrayList<String>(filenames);
        this.what = what;
        this.progressBarResource = progressBarResource;
        setName("Inserting image files into database @ " + getClass().getName()); // NOI18N
    }

    /**
     * A <em>soft</em> interrupt: I/O operations can finishing their current
     * process.
     */
    public synchronized void cancel() {
        cancelled = true;
    }

    /**
     * Adds an action listener. It will be called when a file was processed.
     *
     * @param listener action listener
     */
    public void addActionListener(CheckingForUpdateMetadataListener listener) {
        actionListeners.add(listener);
    }

    /**
     * Removes an action listener.
     *
     * @param listener action listener
     * @see   #addActionListener(java.awt.event.ActionListener)
     */
    public void removeActionListener(CheckingForUpdateMetadataListener listener) {
        actionListeners.remove(listener);
    }

    private void notifyActionListeners(Type type, String filename) {
        CheckForUpdateMetadataEvent event =
                new CheckForUpdateMetadataEvent(type, filename);
        synchronized (actionListeners) {
            for (CheckingForUpdateMetadataListener listener : actionListeners) {
                listener.actionPerformed(event);
            }
        }
    }

    /**
     * Returns the currently processed filename.
     *
     * @return filename or null if no file was processed
     */
    public String getCurrentFilename() {
        return currentFilename;
    }

    @Override
    public void run() {
        int count = filenames.size();
        updateStarted(count); // NOI18N
        int checkCount = 0;
        for (int index = 0; !isInterrupted() && !cancelled && index < count;
                index++) {
            String filename = filenames.get(index);
            currentFilename = filename;
            ImageFile imageFile = getImageFile(filename);
            updateCheckWillPerformed(index + 1, index + 1 < count
                                                ? filenames.get(index + 1)
                                                : filename);
            if (isUpdate(imageFile)) {
                logInsertImageFile(imageFile);
                db.insertOrUpdateImageFile(imageFile);
                runActionsAfterInserting(imageFile);
            }
            checkCount++;
        }
        currentFilename = null;
        updateFinished(checkCount);
    }

    private boolean isUpdate(ImageFile imageFile) {
        return imageFile.getExif() != null ||
                imageFile.getXmp() != null ||
                imageFile.getThumbnail() != null;
    }

    private ImageFile getImageFile(String filename) {
        ImageFile imageFile = new ImageFile();
        imageFile.setFilename(filename);
        imageFile.setLastmodified(new File(filename).lastModified());
        if (isUpdateThumbnail(filename)) {
            imageFile.addInsertIntoDb(
                    InsertImageFilesIntoDatabase.Insert.THUMBNAIL);
            setThumbnail(imageFile);
        }
        if (isUpdateXmp(filename)) {
            imageFile.addInsertIntoDb(InsertImageFilesIntoDatabase.Insert.XMP);
            setXmp(imageFile);
        }
        if (isUpdateExif(filename)) {
            imageFile.addInsertIntoDb(InsertImageFilesIntoDatabase.Insert.EXIF);
            setExif(imageFile);
        }
        return imageFile;
    }

    private boolean isUpdateThumbnail(String filename) {
        return !existsThumbnail(filename) ||
                what.contains(Insert.THUMBNAIL) ||
                (what.contains(Insert.OUT_OF_DATE) &&
                !isImageFileUpToDate(filename));
    }

    private boolean existsThumbnail(String filename) {
        return PersistentThumbnails.getThumbnailFileOfImageFile(filename).exists();
    }

    private boolean isUpdateExif(String filename) {
        return what.contains(Insert.EXIF) ||
                (what.contains(Insert.OUT_OF_DATE) &&
                !isImageFileUpToDate(filename));
    }

    private boolean isUpdateXmp(String filename) {
        return what.contains(Insert.XMP) ||
                (what.contains(Insert.OUT_OF_DATE) &&
                !isXmpFileUpToDate(filename));
    }

    private boolean isImageFileUpToDate(String filename) {
        long dbTime = db.getLastModifiedImageFile(filename);
        long fileTime = new File(filename).lastModified();
        return fileTime == dbTime;
    }

    private boolean isXmpFileUpToDate(String imageFilename) {
        String sidecarFileName =
                XmpMetadata.getSidecarFilenameOfImageFileIfExists(imageFilename);
        return sidecarFileName == null
               ? isEmbeddedXmpUpToDate(imageFilename)
               : isXmpSidecarFileUpToDate(imageFilename, sidecarFileName);
    }

    private boolean isXmpSidecarFileUpToDate(
            String imageFilename, String sidecarFilename) {
        assert FileUtil.existsFile(new File(sidecarFilename));
        long dbTime = db.getLastModifiedXmp(imageFilename);
        long fileTime = new File(sidecarFilename).lastModified();
        return fileTime == dbTime;
    }

    private boolean isEmbeddedXmpUpToDate(String imageFilename) {
        if (!UserSettings.INSTANCE.isScanForEmbeddedXmp()) {
            return true;
        }
        long dbTime = db.getLastModifiedXmp(imageFilename);
        long fileTime = new File(imageFilename).lastModified();
        if (dbTime == fileTime) {
            return true;
        }
        boolean hasEmbeddedXmp =
                XmpMetadata.getEmbeddedXmp(imageFilename) != null; // slow if large image file whitout XMP
        // Avoid unneccesary 2nd calls
        if (!hasEmbeddedXmp) {
            db.setLastModifiedXmp(imageFilename, fileTime);
        }
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
        Xmp xmp = XmpMetadata.getXmpOfImageFile(imageFilename);
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
        if (xmp != null && !XmpMetadata.hasImageASidecarFile(imageFilename) &&
                XmpMetadata.canWriteSidecarFileForImageFile(imageFilename)) {
            XmpMetadata.writeMetadataToSidecarFile(
                    XmpMetadata.suggestSidecarFilenameForImageFile(imageFilename),
                    xmp);
        }
    }

    private void runActionsAfterInserting(ImageFile imageFile) {
        if (!isRunActionsAfterInserting(imageFile)) return;
        File imgFile = imageFile.getFile();
        List<Program> actions =
                DatabaseActionsAfterDbInsertion.INSTANCE.getAll();
        for (Program action : actions) {
            StartPrograms programStarter = new StartPrograms(null);
            programStarter.startProgram(action, Collections.singletonList(
                    imgFile));
        }
    }

    private boolean isRunActionsAfterInserting(ImageFile imageFile) {
        UserSettings settings = UserSettings.INSTANCE;
        return settings.isExecuteActionsAfterImageChangeInDbAlways() ||
                settings.isExecuteActionsAfterImageChangeInDbIfImageHasXmp() &&
                imageFile.getXmp() != null;
    }

    private void errorMessageNullThumbnail(String filename) {
        AppLog.logWarning(InsertImageFilesIntoDatabase.class,
                "InsertImageFilesIntoDatabase.Error.NullThumbnail", filename); // NOI18N
    }

    private void releaseProgressBar() {
        if (progressBar != null && progressBarResource != null) {
            progressBar.setString(""); // NOI18N
            progressBarResource.releaseResource(this);
            progressBar = null;
        }
    }

    private void updateStarted(int filecount) {
        if (progressBarResource == null) return;
        progressBar = progressBarResource.getResource(this);
        if (progressBar == null) {
            AppLog.logInfo(getClass(), "ProgressBar.Locked", getClass(), // NOI18N
                    progressBarResource.getOwner());
        } else {
            progressBar.setMinimum(0);
            progressBar.setMaximum(filecount);
            progressBar.setValue(0);
            progressBar.setStringPainted(true);
            progressBar.setString(Bundle.getString(
                    progressBarResource == ProgressBarAutomaticTasks.INSTANCE ?
                        "InsertImageFilesIntoDatabase.ProgressBarAutomaticTasks.String" // NOI18N
                        : "InsertImageFilesIntoDatabase.ProgressBarUserTasks.String")); // NOI18N
        }
        notifyActionListeners(Type.CHECK_STARTED, null);
    }

    private void updateCheckWillPerformed(int value, String filename) {
        informationMessagePerformed(filename);
        notifyActionListeners(Type.CHECKING_FILE, filename);
        if (progressBar != null) {
            progressBar.setValue(value);
            progressBar.setToolTipText(filename);
        }
    }

    private void updateFinished(int filecount) {
        informationMessageEnd(filecount);
        if (progressBar != null) {
            progressBar.setValue(filecount);
            progressBar.setToolTipText(""); // NOI18N
            progressBar.setString(""); // NOI18N
            releaseProgressBar();
        }
        notifyActionListeners(Type.CHECK_FINISHED, null);
    }

    private void informationMessagePerformed(String filename) {
        AppLog.logFinest(InsertImageFilesIntoDatabase.class,
                "InsertImageFilesIntoDatabase.Info.CheckImageForModifications", // NOI18N
                filename);
    }

    private void informationMessageEnd(int filecount) {
        AppLog.logInfo(InsertImageFilesIntoDatabase.class,
                "InsertImageFilesIntoDatabase.Info.UpdateMetadataFinished", // NOI18N
                filecount);
    }

    private void logInsertImageFile(ImageFile data) {
        Object[] params = {
            data.getFile().getAbsolutePath(),
            data.getExif() == null
            ? Bundle.getString(
            "InsertImageFilesIntoDatabase.Info.StartInsert.No") // NOI18N
            : Bundle.getString(
            "InsertImageFilesIntoDatabase.Info.StartInsert.Yes"), // NOI18N
            data.getXmp() == null
            ? Bundle.getString(
            "InsertImageFilesIntoDatabase.Info.StartInsert.No") // NOI18N
            : Bundle.getString(
            "InsertImageFilesIntoDatabase.Info.StartInsert.Yes"), // NOI18N
            data.getThumbnail() == null
            ? Bundle.getString(
            "InsertImageFilesIntoDatabase.Info.StartInsert.No") // NOI18N
            : Bundle.getString(
            "InsertImageFilesIntoDatabase.Info.StartInsert.Yes")}; // NOI18N
        AppLog.logInfo(InsertImageFilesIntoDatabase.class,
                "InsertImageFilesIntoDatabase.Info.StartInsert", params); // NOI18N
    }
}
