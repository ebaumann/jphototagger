package org.jphototagger.program.cache;

import org.jphototagger.image.util.ImageUtil;
import org.jphototagger.lib.io.FileLock;
import org.jphototagger.program.app.logging.AppLogger;
import org.jphototagger.program.io.RuntimeUtil;
import org.jphototagger.program.UserSettings;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.swing.ImageIcon;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.IoUtil;

/**
 * Persistent stored (cached) thumbnails.
 *
 * @author  Martin Pohlack, Elmar Baumann
 */
public final class PersistentThumbnails {

    private PersistentThumbnails() {
    }

    /**
     * Writes a thumbnail.
     *
     * @param thumbnail thumbnail
     * @param imageFile image file
     */
    public static void writeThumbnail(Image thumbnail, File imageFile) {
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
            if (!RuntimeUtil.lockLogWarning(tnFile, PersistentThumbnails.class)) {
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
            AppLogger.logSevere(PersistentThumbnails.class, ex);
        } finally {
            FileLock.INSTANCE.unlock(tnFile, PersistentThumbnails.class);
            IoUtil.close(fos);
        }
    }

    private static void logWriteThumbnail(File tnFile) {
        AppLogger.logInfo(PersistentThumbnails.class, "PersistentThumbnails.Info.WriteThumbnail", tnFile);
    }

    public static boolean deleteThumbnail(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        File tnFile = getThumbnailFile(imageFile);

        if ((tnFile != null) && !tnFile.delete()) {
            AppLogger.logWarning(PersistentThumbnails.class, "DatabaseImageFiles.Error.DeleteThumbnail", tnFile,
                    imageFile);

            return false;
        }

        return true;
    }

    /**
     * Returns an existing thumbnail.
     *
     * @param  md5Filename name returned by {@link #getMd5FilenameOfAbsolutePath(File) }
     * @return             thumbnail if the thumbnail file exists and was read
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
            AppLogger.logSevere(PersistentThumbnails.class, ex);
        } finally {
            IoUtil.close(fis);
        }

        return thumbnail;
    }

    /**
     * Returns the thumbnail for an image file.
     *
     * @param  imageFile image file
     * @return           thumbnail or null if the thumbnail does not exist
     */
    public static Image getThumbnail(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        String md5Filename = FileUtil.getMd5FilenameOfAbsolutePath(imageFile);

        return getThumbnail(md5Filename);
    }

    /**
     * Returns the thumbnail file for an image file.
     *
     * @param  imageFile image file
     * @return           thumbnail file or null on errors. The file may not
     *                   exists, {@link File#exists()} can be false.
     */
    public static File getThumbnailFile(File imageFile) {
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
     * @param  imageFile image file
     * @return           true if the image file has a thumbnail
     */
    public static boolean existsThumbnail(File imageFile) {
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
     * @param  md5Filename hash
     * @return             thumbnail file
     */
    private static File getThumbnailfile(String md5Filename) {
        String dir = UserSettings.INSTANCE.getThumbnailsDirectoryName();

        return new File(dir + File.separator + md5Filename + ".jpeg");
    }

    public static boolean renameThumbnail(File fromImageFile, File toImageFile) {
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

        if (!fromTnFile.renameTo(toTnFile)) {
            AppLogger.logWarning(PersistentThumbnails.class, "PersistentThumbnails.Error.Rename", fromImageFile,
                    toImageFile);

            return false;
        }

        return true;
    }
}
