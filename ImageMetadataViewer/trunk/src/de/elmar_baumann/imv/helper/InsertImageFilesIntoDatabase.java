package de.elmar_baumann.imv.helper;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.Exif;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.data.Iptc;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.database.DatabaseActionsAfterDbInsertion;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.image.thumbnail.ThumbnailUtil;
import de.elmar_baumann.imv.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.imv.image.metadata.iptc.IptcMetadata;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.resource.MutualExcludedResource;
import java.awt.Image;
import java.io.File;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
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
    private final List<String> filenames;
    private final EnumSet<Insert> what;
    private final MutualExcludedResource progressBarResource;
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
            MutualExcludedResource progressBarResource) {

        this.filenames = filenames;
        this.what = what;
        this.progressBarResource = progressBarResource;
        setName("Inserting image files into database @ " + getClass().getName()); // NOI18N
    }

    @Override
    public void run() {
        int count = filenames.size();
        progressStarted(count); // NOI18N
        for (int index = 0; !isInterrupted() && index < count; index++) {
            String filename = filenames.get(index);
            ImageFile imageFile = getImageFile(filename);
            if (isUpdate(imageFile)) {
                logInsertImageFile(imageFile);
                db.insertOrUpdateImageFile(imageFile);
                runActionsAfterInserting(imageFile);
            }
            progressPerformed(index + 1, index + 1 < count
                                         ? filenames.get(index + 1)
                                         : filename);
        }
        progressEnded(count);
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
        return what.contains(Insert.THUMBNAIL) ||
                (what.contains(Insert.OUT_OF_DATE) &&
                !isImageFileUpToDate(filename));
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
        AppLog.logWarning(InsertImageFilesIntoDatabase.class, Bundle.getString(
                "InsertImageFilesIntoDatabase.Error.NullThumbnail", // NOI18N
                filename));
    }

    private void getProgressBar() {
        if (progressBarResource != null) {
            Object o = progressBarResource.getResource(this);
            assert o == null || o instanceof JProgressBar :
                    o + " is not a JPogressBar!"; // NOI18N
            if (o instanceof JProgressBar) {
                progressBar = (JProgressBar) o;
            } else if (o != null) {
                progressBarResource.releaseResource(this);
            }
        }
    }

    private void releaseProgressBar() {
        if (progressBar != null && progressBarResource != null) {
            progressBarResource.releaseResource(this);
        }
    }

    private void progressStarted(int filecount) {
        getProgressBar();
        if (progressBar != null) {
            progressBar.setMinimum(0);
            progressBar.setMaximum(filecount);
            progressBar.setValue(0);
        }
    }

    private void progressPerformed(int value, String filename) {
        informationMessagePerformed(filename);
        if (progressBar != null) {
            progressBar.setValue(value);
            progressBar.setToolTipText(filename);
        }
    }

    private void progressEnded(int filecount) {
        informationMessageEnd(filecount);
        if (progressBar != null) {
            progressBar.setValue(filecount);
            progressBar.setToolTipText(""); // NOI18N
            releaseProgressBar();
        }
    }

    private void informationMessagePerformed(String filename) {
        AppLog.logFinest(InsertImageFilesIntoDatabase.class, Bundle.getString(
                "InsertImageFilesIntoDatabase.Info.CheckImageForModifications", // NOI18N
                filename));
    }

    private void informationMessageEnd(int filecount) {
        AppLog.logInfo(InsertImageFilesIntoDatabase.class, Bundle.getString(
                "InsertImageFilesIntoDatabase.Info.UpdateMetadataFinished", // NOI18N
                filecount));
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
        AppLog.logInfo(InsertImageFilesIntoDatabase.class, Bundle.getString(
                "InsertImageFilesIntoDatabase.Info.StartInsert", // NOI18N
                params));
    }
}
