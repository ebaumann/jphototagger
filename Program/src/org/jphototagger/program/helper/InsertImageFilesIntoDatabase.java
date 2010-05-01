/*
 * @(#)InsertImageFilesIntoDatabase.java    Created on 2008-10-05
 *
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

package org.jphototagger.program.helper;

import org.jphototagger.lib.concurrent.Cancelable;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.cache.PersistentThumbnails;
import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.ImageFile;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseActionsAfterDbInsertion;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpLastModified;
import org.jphototagger.program.event.listener.impl.ListenerSupport;
import org.jphototagger.program.event.listener.impl.ProgressListenerSupport;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.listener.UpdateMetadataCheckListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.event.UpdateMetadataCheckEvent;
import org.jphototagger.program.event.UpdateMetadataCheckEvent.Type;
import org.jphototagger.program.image.metadata.exif.ExifMetadata;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.image.thumbnail.ThumbnailUtil;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;

import java.awt.Image;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jphototagger.program.app.AppLookAndFeel;

/**
 * Inserts or updates image file metadata - EXIF, thumbnail, XMP - into the
 * database.
 *
 * @author  Elmar Baumann
 */
public final class InsertImageFilesIntoDatabase extends Thread
        implements Cancelable {
    private final DatabaseImageFiles                           db =
        DatabaseImageFiles.INSTANCE;
    private final ProgressListenerSupport                      pls =
        new ProgressListenerSupport();
    private final ListenerSupport<UpdateMetadataCheckListener> ls =
        new ListenerSupport<UpdateMetadataCheckListener>();
    private ProgressEvent     progressEvent = new ProgressEvent(this, null);
    private final Set<Insert> what          = new HashSet<Insert>();
    private final List<File>  imageFiles;
    private boolean           cancel;

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
     * @param imageFiles image files, whoes metadatada shall be inserted or
     *                   updated
     * @param what       metadata to insert
     */
    public InsertImageFilesIntoDatabase(List<File> imageFiles, Insert... what) {
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        if (what == null) {
            throw new NullPointerException("what == null");
        }

        this.imageFiles = new ArrayList<File>(imageFiles);

        for (Insert ins : what) {
            this.what.add(ins);
        }

        setName("Inserting image files into database @ "
                + getClass().getSimpleName());
    }

    @Override
    public void run() {
        int count = imageFiles.size();
        int index = 0;

        notifyStarted();

        for (index = 0; !cancel &&!isInterrupted() && (index < count);
                index++) {
            File imgFile = imageFiles.get(index);

            // Notify before inserting to enable progress listeners displaying
            // the current image file
            notifyPerformed(index + 1, imgFile);

            if (checkExists(imgFile)) {
                ImageFile imageFile = getImageFile(imgFile);

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
        return (imageFile.getExif() != null) || (imageFile.getXmp() != null)
               || (imageFile.getThumbnail() != null);
    }

    private ImageFile getImageFile(File imgFile) {
        ImageFile imageFile = new ImageFile();

        imageFile.setFile(imgFile);
        imageFile.setLastmodified(imgFile.lastModified());

        if (isUpdateThumbnail(imgFile)) {
            imageFile.addInsertIntoDb(Insert.THUMBNAIL);
            createAndSetThumbnail(imageFile);
        }

        if (isUpdateXmp(imgFile)) {
            imageFile.addInsertIntoDb(Insert.XMP);
            setXmp(imageFile);
        }

        if (isUpdateExif(imgFile)) {
            imageFile.addInsertIntoDb(Insert.EXIF);
            setExif(imageFile);
        }

        return imageFile;
    }

    private boolean isUpdateThumbnail(File imageFile) {
        return what.contains(Insert.THUMBNAIL)
               || (what.contains(Insert.OUT_OF_DATE)
                   && (!existsThumbnail(imageFile)
                       ||!isThumbnailUpToDate(imageFile)));
    }

    private boolean existsThumbnail(File imageFile) {
        return PersistentThumbnails.existsThumbnail(imageFile);
    }

    private boolean isUpdateExif(File imageFile) {
        return what.contains(Insert.EXIF)
               || (what.contains(Insert.OUT_OF_DATE)
                   &&!isImageFileUpToDate(imageFile));
    }

    private boolean isUpdateXmp(File imageFile) {
        return what.contains(Insert.XMP)
               || (what.contains(Insert.OUT_OF_DATE)
                   &&!isXmpUpToDate(imageFile));
    }

    private boolean isImageFileUpToDate(File imageFile) {
        long dbTime   = db.getImageFileLastModified(imageFile);
        long fileTime = imageFile.lastModified();

        return fileTime == dbTime;
    }

    private boolean isThumbnailUpToDate(File imageFile) {
        File tnFile = PersistentThumbnails.getThumbnailFile(imageFile);

        if ((tnFile == null) ||!tnFile.exists()) {
            return false;
        }

        long lastModifiedTn  = tnFile.lastModified();
        long lastModifiedImg = imageFile.lastModified();

        return lastModifiedTn >= lastModifiedImg;
    }

    private boolean isXmpUpToDate(File imageFile) {
        File xmpFile = XmpMetadata.getSidecarFile(imageFile);

        return (xmpFile == null)
               ? UserSettings.INSTANCE.isScanForEmbeddedXmp()
                 && isEmbeddedXmpUpToDate(imageFile)
               : isXmpSidecarFileUpToDate(imageFile, xmpFile);
    }

    private boolean isXmpSidecarFileUpToDate(File imageFile, File sidecarFile) {
        long dbTime   = db.getLastModifiedXmp(imageFile);
        long fileTime = sidecarFile.lastModified();

        return fileTime == dbTime;
    }

    private boolean isEmbeddedXmpUpToDate(File imageFile) {
        long dbTime   = db.getLastModifiedXmp(imageFile);
        long fileTime = imageFile.lastModified();

        if (dbTime == fileTime) {
            return true;
        }

        // slow if large image file whitout XMP
        boolean hasEmbeddedXmp = XmpMetadata.getEmbeddedXmp(imageFile) != null;

        if (!hasEmbeddedXmp) {    // Avoid unneccesary 2nd calls
            db.setLastModifiedXmp(imageFile, fileTime);
        }

        return !hasEmbeddedXmp;
    }

    private void createAndSetThumbnail(ImageFile imageFile) {
        File  file      = imageFile.getFile();
        Image thumbnail = ThumbnailUtil.getThumbnail(file);

        imageFile.setThumbnail(thumbnail);

        if (thumbnail == null) {
            errorMessageNullThumbnail(file);
            imageFile.setThumbnail(AppLookAndFeel.ERROR_THUMBNAIL);
        }
    }

    private void setExif(ImageFile imageFile) {
        Exif exif = ExifMetadata.getExif(imageFile.getFile());

        if ((exif != null) &&!exif.isEmpty()) {
            imageFile.setExif(exif);
        } else {
            imageFile.setExif(null);
        }
    }

    private void setXmp(ImageFile imageFile) {
        File imgFile = imageFile.getFile();
        Xmp  xmp     = XmpMetadata.hasImageASidecarFile(imgFile)
                       ? XmpMetadata.getXmpFromSidecarFileOf(imgFile)
                       : UserSettings.INSTANCE.isScanForEmbeddedXmp()
                         ? XmpMetadata.getEmbeddedXmp(imgFile)
                         : null;

        writeSidecarFileIfNotExists(imgFile, xmp);

        if ((xmp != null) &&!xmp.isEmpty()) {
            imageFile.setXmp(xmp);
        }
    }

    private void setExifDateToXmpDateCreated(ImageFile imageFile) {
        Exif    exif    = imageFile.getExif();
        Xmp     xmp     = imageFile.getXmp();
        boolean hasExif = exif != null;
        boolean hasXmp  = xmp != null;
        boolean hasXmpDateCreated =
            hasXmp && xmp.contains(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
        boolean hasExifDate = hasExif && (exif.getDateTimeOriginal() != null);

        if (hasXmpDateCreated ||!hasXmp ||!hasExif ||!hasExifDate) {
            return;
        }

        xmp.setValue(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE,
                     exif.getXmpDateCreated());

        File sidecarFile = XmpMetadata.suggestSidecarFile(imageFile.getFile());

        if (sidecarFile.canWrite()) {
            XmpMetadata.writeXmpToSidecarFile(xmp, sidecarFile);
            xmp.setValue(ColumnXmpLastModified.INSTANCE,
                         sidecarFile.lastModified());
        }
    }

    private void writeSidecarFileIfNotExists(File imageFile, Xmp xmp) {
        if ((xmp != null) &&!XmpMetadata.hasImageASidecarFile(imageFile)
                && XmpMetadata.canWriteSidecarFileForImageFile(imageFile)) {
            File sidecarFile = XmpMetadata.suggestSidecarFile(imageFile);

            XmpMetadata.writeXmpToSidecarFile(xmp, sidecarFile);
        }
    }

    private void runActionsAfterInserting(ImageFile imageFile) {
        if (!isRunActionsAfterInserting(imageFile)) {
            return;
        }

        File          imgFile = imageFile.getFile();
        List<Program> actions =
            DatabaseActionsAfterDbInsertion.INSTANCE.getAll();

        for (Program action : actions) {
            StartPrograms programStarter = new StartPrograms(null);

            programStarter.startProgram(action,
                                        Collections.singletonList(imgFile));
        }
    }

    private boolean isRunActionsAfterInserting(ImageFile imageFile) {
        UserSettings settings = UserSettings.INSTANCE;

        return settings.isExecuteActionsAfterImageChangeInDbAlways()
               || (settings.isExecuteActionsAfterImageChangeInDbIfImageHasXmp()
                   && (imageFile.getXmp() != null));
    }

    /**
     * A <em>soft</em> interrupt: I/O operations can finishing their current
     * process.
     */
    @Override
    public void cancel() {
        cancel = true;
    }

    /**
     * Adds a listener. It will be called when a file was processed.
     *
     * @param listener listener
     */
    public void addUpdateMetadataCheckListener(
            UpdateMetadataCheckListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.add(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener action listener
     * @see   #addUpdateMetadataCheckListener(UpdateMetadataCheckListener)
     */
    public void removeUpdateMetadataCheckListener(
            UpdateMetadataCheckListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.remove(listener);
    }

    private void notifyUpdateMetadataCheckListener(Type type, File file) {
        UpdateMetadataCheckEvent         evt =
            new UpdateMetadataCheckEvent(type, file);
        for (UpdateMetadataCheckListener listener : ls.get()) {
            listener.actionPerformed(evt);
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
        AppLogger.logWarning(
            InsertImageFilesIntoDatabase.class,
            "InsertImageFilesIntoDatabase.Error.NullThumbnail", file);
    }

    private void informationMessagePerformed(File file) {
        AppLogger.logFinest(
            InsertImageFilesIntoDatabase.class,
            "InsertImageFilesIntoDatabase.Info.CheckImageForModifications",
            file);
    }

    private void informationMessageEnded(int filecount) {
        AppLogger.logInfo(
            InsertImageFilesIntoDatabase.class,
            "InsertImageFilesIntoDatabase.Info.UpdateMetadataFinished",
            filecount);
    }

    private boolean checkExists(File imageFile) {
        if (!imageFile.exists()) {
            AppLogger.logInfo(
                getClass(),
                "InsertImageFilesIntoDatabase.Error.ImageFileDoesNotExist",
                imageFile);

            return false;
        }

        return true;
    }

    private void logInsertImageFile(ImageFile data) {
        Object[] params = { data.getFile().getAbsolutePath(),
                            (data.getExif() == null)
                            ? JptBundle.INSTANCE.getString(
                                "InsertImageFilesIntoDatabase.Info.StartInsert.No")
                            : JptBundle.INSTANCE.getString(
                                "InsertImageFilesIntoDatabase.Info.StartInsert.Yes"),
                            (data.getXmp() == null)
                            ? JptBundle.INSTANCE.getString(
                                "InsertImageFilesIntoDatabase.Info.StartInsert.No")
                            : JptBundle.INSTANCE.getString(
                                "InsertImageFilesIntoDatabase.Info.StartInsert.Yes"),
                            (data.getThumbnail() == null)
                            ? JptBundle.INSTANCE.getString(
                                "InsertImageFilesIntoDatabase.Info.StartInsert.No")
                            : JptBundle.INSTANCE.getString(
                                "InsertImageFilesIntoDatabase.Info.StartInsert.Yes") };

        AppLogger.logInfo(InsertImageFilesIntoDatabase.class,
                          "InsertImageFilesIntoDatabase.Info.StartInsert",
                          params);
    }
}
