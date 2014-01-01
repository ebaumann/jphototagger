package org.jphototagger.program.module.thumbnails.cache;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.jphototagger.domain.thumbnails.ThumbnailsDirectoryProvider;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.IoUtil;
import org.openide.util.Lookup;

/**
 * Persistent stored (cached) thumbnails.
 *
 * @author Martin Pohlack, Elmar Baumann
 */
final class PersistentThumbnails {

    private static final Logger LOGGER = Logger.getLogger(PersistentThumbnails.class.getName());
    private static final String THUMBNAILS_DIRECTORY_NAME;

    static {
        ThumbnailsDirectoryProvider provider = Lookup.getDefault().lookup(ThumbnailsDirectoryProvider.class);
        File thumbnailsDirectory = provider.getThumbnailsDirectory();
        THUMBNAILS_DIRECTORY_NAME = thumbnailsDirectory.getAbsolutePath();
    }

    private PersistentThumbnails() {
    }

    @Deprecated
    static boolean deleteThumbnail(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        File tnFile = getThumbnailFile(imageFile);
        if ((tnFile != null) && !tnFile.delete()) {
            LOGGER.log(Level.WARNING,
                    "Thumbnail ''{0}'' of image file ''{1}'' couldn''t be deleted!",
                    new Object[]{tnFile, imageFile});
            return false;
        }

        return true;
    }

    private static Image getThumbnail(String md5Filename) {
        Image thumbnail = null;
        FileInputStream fis = null;
        try {
            File tnFile = getThumbnailfile(md5Filename);
            if (tnFile == null) {
                return null;
            }
            if (tnFile.exists()) {
                fis = new FileInputStream(tnFile);
                int bytecount = fis.available();
                byte[] bytes = new byte[bytecount];
                fis.read(bytes, 0, bytecount);
                ImageIcon icon = new ImageIcon(bytes);
                thumbnail = icon.getImage();
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        } finally {
            IoUtil.close(fis);
        }
        return thumbnail;
    }

    @Deprecated
    static Image getThumbnail(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        String md5Filename = FileUtil.getMd5FilenameOfAbsolutePath(imageFile);
        return getThumbnail(md5Filename);
    }

    private static File getThumbnailFile(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        String md5Filename = FileUtil.getMd5FilenameOfAbsolutePath(imageFile);
        return (md5Filename == null)
                ? null
                : getThumbnailfile(md5Filename);
    }

    /**
     * Compute final name of thumbnail on disk.
     *
     * @param md5Filename hash
     * @return thumbnail file
     */
    private static File getThumbnailfile(String md5Filename) {
        return new File(THUMBNAILS_DIRECTORY_NAME + File.separator + md5Filename + ".jpeg");
    }
}
