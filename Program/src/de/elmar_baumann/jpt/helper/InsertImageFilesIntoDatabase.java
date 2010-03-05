/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.data.Exif;
import de.elmar_baumann.jpt.data.ImageFile;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.cache.PersistentThumbnails;
import de.elmar_baumann.jpt.data.Program;
import de.elmar_baumann.jpt.database.DatabaseActionsAfterDbInsertion;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpLastModified;
import de.elmar_baumann.jpt.event.UpdateMetadataCheckEvent;
import de.elmar_baumann.jpt.event.UpdateMetadataCheckEvent.Type;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.impl.ProgressListenerSupport;
import de.elmar_baumann.jpt.event.listener.UpdateMetadataCheckListener;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.event.listener.impl.ListenerSupport;
import de.elmar_baumann.jpt.image.thumbnail.ThumbnailUtil;
import de.elmar_baumann.jpt.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.lib.image.util.IconUtil;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Inserts or updates image file metadata - EXIF, thumbnail, XMP - into the
 * database.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class InsertImageFilesIntoDatabase extends Thread {

    private final        DatabaseImageFiles                           db                      = DatabaseImageFiles.INSTANCE;
    private final        ProgressListenerSupport                      progressListenerSupport = new ProgressListenerSupport();
    private final        ListenerSupport<UpdateMetadataCheckListener> updateListenerSupport   = new ListenerSupport<UpdateMetadataCheckListener>();
    private              ProgressEvent                                progressEvent           = new ProgressEvent(this, null);
    private static final Image                                        ERROR_THUMBNAIL         = IconUtil.getIconImage(JptBundle.INSTANCE.getString("InsertImageFilesIntoDatabase.ErrorThumbnailPath"));
    private final        List<String>                                 imageFilenames;
    private final        Set<Insert>                                  what                    = new HashSet<Insert>();
    private              boolean                                      stop;

    /**
     * Metadata to insert.
     */
    public enum Insert {

        /**
         * Insert or update EXIF, thumbnail and XMP against these rules:
         * 
         * <ul>
         * <li>Update EXIF, if the image file's file system last modified
         *     timestamp is not equal to it's database timestamp
         *     <code>files.lastmodified</code></li>
         * <li>Insert or update the thumbnail, if the image file's file system
         *     last modified timestamp is not equal to it's database timestamp
         *     <code>files.lastmodified</code></li>
         * <li>Insert or update XMP if the XMP sidecar file's file system last
         *     modified timestamp is not equal to it's database timestamp
         *     <code>files.xmp_lastmodified</code></li>
         * </ul>
         */
        OUT_OF_DATE,

        /**
         * Insert or update the image file's EXIF metadata regardless of
         * timestamps
         */
        EXIF,

        /**
         * Insert or update the image file's thumbnail regardless of timestamps
         */
        THUMBNAIL,

        /**
         * Insert or update the image file's XMP metadata from it's XMP sidecar
         * file regardless of timestamps
         */
        XMP;
    }

    /**
     * Constructor.
     *
     * @param imageFilenames names of the image files, whoes metadatada shall be
     *                       inserted or updated
     * @param what           metadata to insert
     */
    public InsertImageFilesIntoDatabase(List<String> imageFilenames, Insert... what) {

        this.imageFilenames = new ArrayList<String>(imageFilenames);

        for (Insert ins : what) {
            this.what.add(ins);
        }

        setName("Inserting image files into database @ " + getClass().getSimpleName());
    }

    @Override
    public void run() {
        int count = imageFilenames.size();
        int index = 0;

        notifyStarted();

        for (index = 0; !isInterrupted() && !stop && index < count; index++) {
            String imageFilename = imageFilenames.get(index);

            // Notify before inserting to enable progress listeners displaying the current image filename
            notifyPerformed(index + 1, imageFilename);

            if (checkExists(imageFilename)) {
                ImageFile imageFile = getImageFile(imageFilename);
                if (isUpdate(imageFile)) {
                    setExifDateToXmpDateCreated(imageFile);
                    logInsertImageFile(imageFile);
                    db.insertOrUpdate(imageFile);
                    runActionsAfterInserting(imageFile);
                }
            }
        }
        notifyEnded(index);
    }

    private boolean isUpdate(ImageFile imageFile) {
        return imageFile.getExif() != null ||
                imageFile.getXmp() != null ||
                imageFile.getThumbnail() != null;
    }

    private ImageFile getImageFile(String imageFilename) {
        ImageFile imageFile = new ImageFile();

        imageFile.setFilename(imageFilename);
        imageFile.setLastmodified(FileUtil.getLastModified(imageFilename));

        if (isUpdateThumbnail(imageFilename)) {
            imageFile.addInsertIntoDb(Insert.THUMBNAIL);
            createAndSetThumbnail(imageFile);
        }
        if (isUpdateXmp(imageFilename)) {
            imageFile.addInsertIntoDb(Insert.XMP);
            setXmp(imageFile);
        }
        if (isUpdateExif(imageFilename)) {
            imageFile.addInsertIntoDb(Insert.EXIF);
            setExif(imageFile);
        }
        return imageFile;
    }

    private boolean isUpdateThumbnail(String imageFilename) {
        return what.contains(Insert.THUMBNAIL) ||
               what.contains(Insert.OUT_OF_DATE) &&
                    (!existsThumbnail(imageFilename) || !isThumbnailUpToDate(imageFilename));
    }

    private boolean existsThumbnail(String imageFilename) {
        return PersistentThumbnails.existsThumbnailForImagefile(imageFilename);
    }

    private boolean isUpdateExif(String imageFilename) {
        return what.contains(Insert.EXIF) ||
               (what.contains(Insert.OUT_OF_DATE) && !isImageFileUpToDate(imageFilename));
    }

    private boolean isUpdateXmp(String imageFilename) {
        return what.contains(Insert.XMP) ||
               (what.contains(Insert.OUT_OF_DATE) && !isXmpUpToDate(imageFilename));
    }

    private boolean isImageFileUpToDate(String imageFilename) {
        long dbTime   = db.getLastModifiedImageFile(imageFilename);
        long fileTime = FileUtil.getLastModified(imageFilename);

        return fileTime == dbTime;
    }

    private boolean isThumbnailUpToDate(String imageFilename) {
        File tnFile = PersistentThumbnails.getThumbnailFileOfImageFile(imageFilename);
        if (tnFile == null || !tnFile.exists()) return false;

        long lastModifiedTn  = tnFile.lastModified();
        long lastModifiedImg = FileUtil.getLastModified(imageFilename);

        return lastModifiedTn >= lastModifiedImg;
    }

    private boolean isXmpUpToDate(String imageFilename) {
        String sidecarFileName = XmpMetadata.getSidecarFilename(imageFilename);

        return sidecarFileName == null
               ? UserSettings.INSTANCE.isScanForEmbeddedXmp() && isEmbeddedXmpUpToDate(imageFilename)
               : isXmpSidecarFileUpToDate(imageFilename, sidecarFileName);
    }

    private boolean isXmpSidecarFileUpToDate(String imageFilename, String sidecarFilename) {
        long dbTime   = db.getLastModifiedXmp(imageFilename);
        long fileTime = FileUtil.getLastModified(sidecarFilename);

        return fileTime == dbTime;
    }

    private boolean isEmbeddedXmpUpToDate(String imageFilename) {
        long dbTime   = db.getLastModifiedXmp(imageFilename);
        long fileTime = FileUtil.getLastModified(imageFilename);

        if (dbTime == fileTime) return true;

        boolean hasEmbeddedXmp = XmpMetadata.getEmbeddedXmp(imageFilename) != null; // slow if large image file whitout XMP

        if (!hasEmbeddedXmp) { // Avoid unneccesary 2nd calls
            db.setLastModifiedXmp(imageFilename, fileTime);
        }
        return !hasEmbeddedXmp;
    }

    private void createAndSetThumbnail(ImageFile imageFile) {
        File   file      = imageFile.getFile();
        Image  thumbnail = ThumbnailUtil.getThumbnail(file);

        imageFile.setThumbnail(thumbnail);
        if (thumbnail == null) {
            errorMessageNullThumbnail(file.getAbsolutePath());
            imageFile.setThumbnail(ERROR_THUMBNAIL);
        }
    }

    private void setExif(ImageFile imageFile) {
        Exif exif = ExifMetadata.getExif(imageFile.getFile());
        if (exif != null && !exif.isEmpty()) {
            imageFile.setExif(exif);
        } else {
            imageFile.setExif(null);
        }
    }

    private void setXmp(ImageFile imageFile) {
        String imageFilename = imageFile.getFilename();
        Xmp    xmp           = XmpMetadata.hasImageASidecarFile(imageFilename)
                                   ? XmpMetadata.getXmpFromSidecarFileOf(imageFilename)
                                   : UserSettings.INSTANCE.isScanForEmbeddedXmp()
                                   ? XmpMetadata.getEmbeddedXmp(imageFilename)
                                   : null;

        writeSidecarFileIfNotExists(imageFilename, xmp);
        if (xmp != null && !xmp.isEmpty()) {
            imageFile.setXmp(xmp);
        }
    }

    private void setExifDateToXmpDateCreated(ImageFile imageFile) {
        Exif    exif              = imageFile.getExif();
        Xmp     xmp               = imageFile.getXmp();
        boolean hasExif           = exif != null;
        boolean hasXmp            = xmp  != null;
        boolean hasXmpDateCreated = hasXmp  && xmp.contains(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
        boolean hasExifDate       = hasExif && exif.getDateTimeOriginal() != null;

        if (hasXmpDateCreated || !hasXmp || !hasExif || !hasExifDate) return;

        xmp.setValue(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE, exif.getXmpDateCreated());

        File sidecarFile = new File(XmpMetadata.suggestSidecarFilename(imageFile.getFilename()));
        if (sidecarFile.canWrite()) {
            XmpMetadata.writeXmpToSidecarFile( xmp, sidecarFile.getAbsolutePath());
            xmp.setValue(ColumnXmpLastModified.INSTANCE, sidecarFile.lastModified());
        }
    }

    private void writeSidecarFileIfNotExists(String imageFilename, Xmp xmp) {
        if ( xmp != null &&
            !XmpMetadata.hasImageASidecarFile(imageFilename) &&
             XmpMetadata.canWriteSidecarFileForImageFile(imageFilename)) {

            String sidecarFilename = XmpMetadata.suggestSidecarFilename(imageFilename);

            XmpMetadata.writeXmpToSidecarFile(xmp, sidecarFilename);
        }
    }

    private void runActionsAfterInserting(ImageFile imageFile) {

        if (!isRunActionsAfterInserting(imageFile)) return;

        File          imgFile = imageFile.getFile();
        List<Program> actions = DatabaseActionsAfterDbInsertion.INSTANCE.getAll();

        for (Program action : actions) {
            StartPrograms programStarter = new StartPrograms(null);

            programStarter.startProgram(action, Collections.singletonList(imgFile));
        }
    }

    private boolean isRunActionsAfterInserting(ImageFile imageFile) {

        UserSettings settings = UserSettings.INSTANCE;

        return settings.isExecuteActionsAfterImageChangeInDbAlways() ||
               settings.isExecuteActionsAfterImageChangeInDbIfImageHasXmp() && imageFile.getXmp() != null;
    }

    /**
     * A <em>soft</em> interrupt: I/O operations can finishing their current
     * process.
     */
    public void cancel() {
        stop = true;
    }

    /**
     * Adds a listener. It will be called when a file was processed.
     *
     * @param listener listener
     */
    public void addUpdateMetadataCheckListener(UpdateMetadataCheckListener listener) {
        updateListenerSupport.add(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener action listener
     * @see   #addUpdateMetadataCheckListener(UpdateMetadataCheckListener)
     */
    public void removeUpdateMetadataCheckListener(UpdateMetadataCheckListener listener) {
        updateListenerSupport.remove(listener);
    }

    private void notifyUpdateMetadataCheckListener(Type type, String filename) {
        UpdateMetadataCheckEvent         event     = new UpdateMetadataCheckEvent(type, filename);
        Set<UpdateMetadataCheckListener> listeners = updateListenerSupport.get();

        synchronized (listeners) {
            for (UpdateMetadataCheckListener listener : listeners) {
                listener.actionPerformed(event);
            }
        }
    }

    /**
     * Adds a progress listener.
     *
     * {@link ProgressEvent#getInfo()} contains a {@code java.lang.String} of
     * the updated
     * @param listener
     */
    public void addProgressListener(ProgressListener listener) {
        progressListenerSupport.add(listener);
    }

    public void removeProgressListener(ProgressListener listener) {
        progressListenerSupport.remove(listener);
    }

    private void notifyStarted() {
        notifyUpdateMetadataCheckListener(Type.CHECK_STARTED, null);
        progressEvent.setMinimum(0);
        progressEvent.setMaximum(imageFilenames.size());
        progressEvent.setValue(0);
        progressListenerSupport.notifyStarted(progressEvent);
    }

    private void notifyPerformed(int value, String filename) {
        informationMessagePerformed(filename);
        notifyUpdateMetadataCheckListener(Type.CHECKING_FILE, filename);
        progressEvent.setValue(value);
        progressEvent.setInfo(filename);
        progressListenerSupport.notifyPerformed(progressEvent);
    }

    private void notifyEnded(int filecount) {
        informationMessageEnded(filecount);
        notifyUpdateMetadataCheckListener(Type.CHECK_FINISHED, null);
        progressListenerSupport.notifyEnded(progressEvent);
    }

    private void errorMessageNullThumbnail(String filename) {
        AppLogger.logWarning(InsertImageFilesIntoDatabase.class, "InsertImageFilesIntoDatabase.Error.NullThumbnail", filename);
    }

    private void informationMessagePerformed(String filename) {
        AppLogger.logFinest(InsertImageFilesIntoDatabase.class, "InsertImageFilesIntoDatabase.Info.CheckImageForModifications", filename);
    }

    private void informationMessageEnded(int filecount) {
        AppLogger.logInfo(InsertImageFilesIntoDatabase.class, "InsertImageFilesIntoDatabase.Info.UpdateMetadataFinished", filecount);
    }

    private boolean checkExists(String imageFilename) {
        if (!FileUtil.existsFile(imageFilename)) {
            AppLogger.logInfo(getClass(), "InsertImageFilesIntoDatabase.Error.ImageFileDoesNotExist", imageFilename);
            return false;
        }
        return true;
    }

    private void logInsertImageFile(ImageFile data) {
        Object[] params = {
            data.getFile().getAbsolutePath(),
            data.getExif() == null
            ? JptBundle.INSTANCE.getString("InsertImageFilesIntoDatabase.Info.StartInsert.No")
            : JptBundle.INSTANCE.getString("InsertImageFilesIntoDatabase.Info.StartInsert.Yes"),
            data.getXmp() == null
            ? JptBundle.INSTANCE.getString("InsertImageFilesIntoDatabase.Info.StartInsert.No")
            : JptBundle.INSTANCE.getString("InsertImageFilesIntoDatabase.Info.StartInsert.Yes"),
            data.getThumbnail() == null
            ? JptBundle.INSTANCE.getString("InsertImageFilesIntoDatabase.Info.StartInsert.No")
            : JptBundle.INSTANCE.getString("InsertImageFilesIntoDatabase.Info.StartInsert.Yes")};
        AppLogger.logInfo(InsertImageFilesIntoDatabase.class, "InsertImageFilesIntoDatabase.Info.StartInsert", params);
    }
}
