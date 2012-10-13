package org.jphototagger.program.module.thumbnails.cache;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.jphototagger.domain.thumbnails.ThumbnailsDirectoryProvider;
import org.jphototagger.image.util.ImageUtil;
import org.jphototagger.lib.io.FileLock;
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

    static void writeThumbnail(Image thumbnail, File imageFile) {
        if (thumbnail == null) {
            throw new NullPointerException("thumbnail == null");
        }
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        FileOutputStream fos = null;
        File tnFile = getThumbnailFile(imageFile);
        if (tnFile == null) {
            return;
        }
        try {
            if (!FileLock.INSTANCE.lockLogWarning(tnFile, PersistentThumbnails.class)) {
                return;
            }
            logWriteThumbnail(tnFile);
            fos = new FileOutputStream(tnFile);
            fos.getChannel().lock();
            ByteArrayInputStream is = ImageUtil.getByteArrayInputStream(thumbnail, "jpeg");
            if (is != null) {
                int nextByte;
                while ((nextByte = is.read()) != -1) {
                    fos.write(nextByte);
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            FileLock.INSTANCE.unlock(tnFile, PersistentThumbnails.class);
            IoUtil.close(fos);
        }
    }

    private static void logWriteThumbnail(File tnFile) {
        LOGGER.log(Level.INFO, "Writing thumbnail ''{0}''", tnFile);
    }

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

    /**
     * Returns an existing thumbnail.
     *
     * @param md5Filename name returned by {@code #getMd5FilenameOfAbsolutePath(File) }
     * @return thumbnail if the thumbnail file exists and was read
     */
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
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            IoUtil.close(fis);
        }
        return thumbnail;
    }

    /**
     * Returns the thumbnail for an image file.
     *
     * @param imageFile image file
     * @return thumbnail or null if the thumbnail does not exist
     */
    static Image getThumbnail(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        String md5Filename = FileUtil.getMd5FilenameOfAbsolutePath(imageFile);
        return getThumbnail(md5Filename);
    }

    /**
     * Returns the thumbnail file for an image file.
     *
     * @param imageFile image file
     * @return thumbnail file or null on errors. The file may not exists, {@code File#exists()} can be false.
     */
    static File getThumbnailFile(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        String md5Filename = FileUtil.getMd5FilenameOfAbsolutePath(imageFile);
        return (md5Filename == null)
                ? null
                : getThumbnailfile(md5Filename);
    }

    /**
     * Returns whether an image file has a thumbnail file.
     *
     * @param imageFile image file
     * @return true if the image file has a thumbnail
     */
    static boolean existsThumbnail(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        File tnFile = getThumbnailFile(imageFile);
        return (tnFile == null)
                ? false
                : tnFile.exists();
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

    static boolean renameThumbnail(File fromImageFile, File toImageFile) {
        if (fromImageFile == null) {
            throw new NullPointerException("fromImageFile == null");
        }
        if (toImageFile == null) {
            throw new NullPointerException("toImageFile == null");
        }
        final String fromMd5Filename = FileUtil.getMd5FilenameOfAbsolutePath(fromImageFile);
        if (fromMd5Filename == null) {
            return false;
        }
        File fromTnFile = getThumbnailfile(fromMd5Filename);
        if (fromTnFile == null) {
            return false;
        }
        File toTnFile = getThumbnailfile(FileUtil.getMd5FilenameOfAbsolutePath(toImageFile));
        if (toTnFile.exists()) {
            LOGGER.log(Level.WARNING,
                    "Thumbnail file ''{0}'' for image file  ''{1}'' already exists and can't be renamed!",
                    new Object[]{toTnFile, toImageFile});

        } else if (!fromTnFile.renameTo(toTnFile)) {
            LOGGER.log(Level.WARNING,
                    "Thumbnail file ''{0}'' couldn''t be renamed to ''{1}'' (Image file ''{2}'' was renamed to ''{3}''!",
                    new Object[]{fromTnFile, toTnFile, fromImageFile, toImageFile});

        }
        fromTnFile.delete();
        return toTnFile.exists();
    }
}
