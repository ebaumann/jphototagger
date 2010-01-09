/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.data.Exif;
import de.elmar_baumann.jpt.data.ImageFile;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.cache.PersistentThumbnails;
import de.elmar_baumann.jpt.data.Iptc;
import de.elmar_baumann.jpt.data.Program;
import de.elmar_baumann.jpt.database.DatabaseActionsAfterDbInsertion;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.CheckForUpdateMetadataEvent;
import de.elmar_baumann.jpt.event.CheckForUpdateMetadataEvent.Type;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.ProgressListenerSupport;
import de.elmar_baumann.jpt.event.listener.CheckingForUpdateMetadataListener;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.image.thumbnail.ThumbnailUtil;
import de.elmar_baumann.jpt.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.jpt.image.metadata.iptc.IptcMetadata;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Writes image file metadata into the database if out of date or not existing.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class InsertImageFilesIntoDatabase extends Thread {

    private final DatabaseImageFiles                     db                               = DatabaseImageFiles.INSTANCE;
    private final int                                    maxThumbnailLength               = UserSettings.INSTANCE.getMaxThumbnailLength();
    private final boolean                                useEmbeddedThumbnails            = UserSettings.INSTANCE.isUseEmbeddedThumbnails();
    private final String                                 externalThumbnailCreationCommand = UserSettings.INSTANCE.getExternalThumbnailCreationCommand();
    private final ProgressListenerSupport                progressListenerSupport          = new ProgressListenerSupport();
    private final Set<CheckingForUpdateMetadataListener> updateMetadataListeners          = Collections.synchronizedSet(new HashSet<CheckingForUpdateMetadataListener>());
    private       ProgressEvent                          progressEvent                    = new ProgressEvent(this, null);
    private final List<String>                           filenames;
    private final EnumSet<Insert>                        what;
    private       String                                 currentFilename;
    private       boolean                                cancelled;

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
     * @param filenames names of the <em>image</em> files to be updated
     * @param what      which image metadata to insert
     */
    public InsertImageFilesIntoDatabase(List<String> filenames, EnumSet<Insert> what) {
        this.filenames = new ArrayList<String>(filenames);
        this.what      = what;
        setName("Inserting image files into database @ " + getClass().getSimpleName());
    }

    /**
     * A <em>soft</em> interrupt: I/O operations can finishing their current
     * process.
     */
    public synchronized void cancel() {
        cancelled = true;
    }

    /**
     * Adds a listener. It will be called when a file was processed.
     *
     * @param listener listener
     */
    public void addUpdateMetadataListener(CheckingForUpdateMetadataListener listener) {
        updateMetadataListeners.add(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener action listener
     * @see   #addUpdateMetadataListener(CheckingForUpdateMetadataListener)
     */
    public void removeUpdateMetadataListener(CheckingForUpdateMetadataListener listener) {
        updateMetadataListeners.remove(listener);
    }

    private void notifyUpdateMetadataListener(Type type, String filename) {
        CheckForUpdateMetadataEvent event = new CheckForUpdateMetadataEvent(type, filename);
        synchronized (updateMetadataListeners) {
            for (CheckingForUpdateMetadataListener listener : updateMetadataListeners) {
                listener.actionPerformed(event);
            }
        }
    }

    public void addProgressListener(ProgressListener listener) {
        progressListenerSupport.addProgressListener(listener);
    }

    public void removeProgressListener(ProgressListener listener) {
        progressListenerSupport.removeProgressListener(listener);
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
        notifyStarted();
        int checkCount = 0;
        for (int index = 0; !isInterrupted() && !cancelled && index < count; index++) {
            String filename = filenames.get(index);
            currentFilename = filename;
            ImageFile imageFile = getImageFile(filename);
            notifyPerformed(index + 1, index + 1 < count
                                                ? filenames.get(index + 1)
                                                : filename);
            if (isUpdate(imageFile)) {
                setExifDateToXmpDateCreated(imageFile);
                logInsertImageFile(imageFile);
                db.insertOrUpdate(imageFile);
                runActionsAfterInserting(imageFile);
            }
            checkCount++;
        }
        currentFilename = null;
        notifyEnded(checkCount);
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
            imageFile.addInsertIntoDb(InsertImageFilesIntoDatabase.Insert.THUMBNAIL);
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
        long dbTime   = db.getLastModifiedImageFile(filename);
        long fileTime = new File(filename).lastModified();

        return fileTime == dbTime;
    }

    private boolean isXmpFileUpToDate(String imageFilename) {
        String sidecarFileName = XmpMetadata.getSidecarFilenameOfImageFileIfExists(imageFilename);

        return sidecarFileName == null
               ? isEmbeddedXmpUpToDate(imageFilename)
               : isXmpSidecarFileUpToDate(imageFilename, sidecarFileName);
    }

    private boolean isXmpSidecarFileUpToDate(String imageFilename, String sidecarFilename) {
        assert FileUtil.existsFile(new File(sidecarFilename));
        long dbTime   = db.getLastModifiedXmp(imageFilename);
        long fileTime = new File(sidecarFilename).lastModified();

        return fileTime == dbTime;
    }

    private boolean isEmbeddedXmpUpToDate(String imageFilename) {
        if (!UserSettings.INSTANCE.isScanForEmbeddedXmp()) {
            return true;
        }
        long dbTime   = db.getLastModifiedXmp(imageFilename);
        long fileTime = new File(XmpMetadata.suggestSidecarFilenameForImageFile(imageFilename)).lastModified();
        if (dbTime == fileTime) {
            return true;
        }
        boolean hasEmbeddedXmp = XmpMetadata.getEmbeddedXmp(imageFilename) != null; // slow if large image file whitout XMP

        if (!hasEmbeddedXmp) { // Avoid unneccesary 2nd calls
            db.setLastModifiedXmp(imageFilename, fileTime);
        }
        return !hasEmbeddedXmp || fileTime == dbTime;
    }

    private void setThumbnail(ImageFile imageFile) {
        String filename  = imageFile.getFilename();
        Image  thumbnail = null;
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
        Xmp    xmp           = XmpMetadata.getXmpOfImageFile(imageFilename);
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

    private void setExifDateToXmpDateCreated(ImageFile imageFile) {
        Exif exif = imageFile.getExif();
        Xmp  xmp  = imageFile.getXmp();

        if (exif == null || xmp == null || exif.getDateTimeOriginal() == null ||
                xmp.getIptc4XmpCoreDateCreated() != null) return;

        xmp.setIptc4XmpCoreDateCreated(exif.getXmpDateCreated());

        File xmpFile = new File(XmpMetadata.suggestSidecarFilenameForImageFile(imageFile.getFilename()));
        if (xmpFile.canWrite()) {
            XmpMetadata.writeMetadataToSidecarFile(xmpFile.getAbsolutePath(), xmp);
            xmp.setLastModified(xmpFile.lastModified());
        }
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
        File          imgFile = imageFile.getFile();
        List<Program> actions = DatabaseActionsAfterDbInsertion.INSTANCE.getAll();

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
                "InsertImageFilesIntoDatabase.Error.NullThumbnail", filename);
    }

    private void notifyStarted() {
        notifyUpdateMetadataListener(Type.CHECK_STARTED, null);
        progressEvent.setMinimum(0);
        progressEvent.setMaximum(filenames.size());
        progressEvent.setValue(0);
        progressListenerSupport.notifyStarted(progressEvent);
    }

    private void notifyPerformed(int value, String filename) {
        informationMessagePerformed(filename);
        notifyUpdateMetadataListener(Type.CHECKING_FILE, filename);
        progressEvent.setValue(value);
        progressEvent.setInfo(filename);
        progressListenerSupport.notifyPerformed(progressEvent);
    }

    private void notifyEnded(int filecount) {
        informationMessageEnded(filecount);
        notifyUpdateMetadataListener(Type.CHECK_FINISHED, null);
        progressListenerSupport.notifyEnded(progressEvent);
    }

    private void informationMessagePerformed(String filename) {
        AppLog.logFinest(InsertImageFilesIntoDatabase.class,
                "InsertImageFilesIntoDatabase.Info.CheckImageForModifications",
                filename);
    }

    private void informationMessageEnded(int filecount) {
        AppLog.logInfo(InsertImageFilesIntoDatabase.class,
                "InsertImageFilesIntoDatabase.Info.UpdateMetadataFinished",
                filecount);
    }

    private void logInsertImageFile(ImageFile data) {
        Object[] params = {
            data.getFile().getAbsolutePath(),
            data.getExif() == null
            ? Bundle.getString("InsertImageFilesIntoDatabase.Info.StartInsert.No")
            : Bundle.getString("InsertImageFilesIntoDatabase.Info.StartInsert.Yes"),
            data.getXmp() == null
            ? Bundle.getString("InsertImageFilesIntoDatabase.Info.StartInsert.No")
            : Bundle.getString("InsertImageFilesIntoDatabase.Info.StartInsert.Yes"),
            data.getThumbnail() == null
            ? Bundle.getString("InsertImageFilesIntoDatabase.Info.StartInsert.No")
            : Bundle.getString("InsertImageFilesIntoDatabase.Info.StartInsert.Yes")};
        AppLog.logInfo(InsertImageFilesIntoDatabase.class,
                "InsertImageFilesIntoDatabase.Info.StartInsert", params);
    }
}
