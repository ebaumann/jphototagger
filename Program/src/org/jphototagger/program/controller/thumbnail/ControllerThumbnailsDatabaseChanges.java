package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.program.cache.ThumbnailCache;
import org.jphototagger.program.cache.XmpCache;
import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.listener.DatabaseImageFilesListener;
import org.jphototagger.program.resource.GUI;


import java.io.File;

import java.util.Collections;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerThumbnailsDatabaseChanges implements DatabaseImageFilesListener {
    public ControllerThumbnailsDatabaseChanges() {
        listen();
    }

    private void listen() {
        DatabaseImageFiles.INSTANCE.addListener(this);
    }

    private void updateXmpCache(final File imageFile) {
        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                XmpCache.INSTANCE.remove(imageFile);
                XmpCache.INSTANCE.notifyUpdate(imageFile);
            }
        });
    }

    @Override
    public void xmpInserted(File imageFile, Xmp xmp) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        updateXmpCache(imageFile);
    }

    @Override
    public void xmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        updateXmpCache(imageFile);
    }

    @Override
    public void xmpDeleted(File imageFile, Xmp xmp) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        updateXmpCache(imageFile);
    }

    @Override
    public void thumbnailUpdated(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        final File file = imageFile;

        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                ThumbnailCache.INSTANCE.remove(file);
                ThumbnailCache.INSTANCE.notifyUpdate(file);
            }
        });
    }

    @Override
    public void imageFileDeleted(final File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUI.getThumbnailsPanel().removeFiles(Collections.singleton(imageFile));
            }
        });
    }

    @Override
    public void imageFileInserted(File imageFile) {

        // ignore
    }

    @Override
    public void imageFileRenamed(File oldImageFile, File newImageFile) {

        // ignore
    }

    @Override
    public void exifUpdated(File imageFile, Exif oldExif, Exif updatedExif) {

        // ignore
    }

    @Override
    public void exifInserted(File imageFile, Exif exif) {

        // ignore
    }

    @Override
    public void dcSubjectDeleted(String dcSubject) {

        // ignore
    }

    @Override
    public void dcSubjectInserted(String dcSubject) {

        // ignore
    }

    @Override
    public void exifDeleted(File imageFile, Exif exif) {

        // ignore
    }
}
