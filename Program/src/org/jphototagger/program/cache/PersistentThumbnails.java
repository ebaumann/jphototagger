package org.jphototagger.program.cache;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.io.RuntimeUtil;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.image.util.ImageUtil;
import org.jphototagger.lib.io.FileLock;
import org.jphototagger.lib.io.FileUtil;

import java.awt.Image;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.security.MessageDigest;

import javax.swing.ImageIcon;

/**
 * Persistent stored (cached) thumbnails.
 *
 * @author  Martin Pohlack, Elmar Baumann
 */
public final class PersistentThumbnails {
    private PersistentThumbnails() {}

    /**
     * Writes a thumbnail.
     *
     * @param thumbnail thumbnail
     * @param imageFile image file
     */
    public static void writeThumbnail(Image thumbnail,
            File imageFile) {
        if (thumbnail == null) {
            throw new NullPointerException("thumbnail == null");
        }

        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        FileOutputStream fos    = null;
        File             tnFile = getThumbnailFile(imageFile);

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

            ByteArrayInputStream is =
                ImageUtil.getByteArrayInputStream(thumbnail, "jpeg");

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
            FileUtil.close(fos);
        }
    }

    private static void logWriteThumbnail(File tnFile) {
        AppLogger.logInfo(PersistentThumbnails.class,
                          "PersistentThumbnails.Info.WriteThumbnail", tnFile);
    }

    public static boolean deleteThumbnail(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        File tnFile = getThumbnailFile(imageFile);

        if ((tnFile != null) &&!tnFile.delete()) {
            AppLogger.logWarning(PersistentThumbnails.class,
                                 "DatabaseImageFiles.Error.DeleteThumbnail",
                                 tnFile, imageFile);

            return false;
        }

        return true;
    }

    /**
     * Returns an existing thumbnail.
     *
     * @param  md5Filename name returned by {@link #getMd5Filename(File) }
     * @return             thumbnail if the thumbnail file exists and was read
     */
    private static Image getThumbnail(String md5Filename) {
        Image           thumbnail = null;
        FileInputStream fis       = null;

        try {
            File tnFile = getThumbnailfile(md5Filename);

            if (tnFile == null) {
                return null;
            }

            if (tnFile.exists()) {
                fis = new FileInputStream(tnFile);

                int    bytecount = fis.available();
                byte[] bytes     = new byte[bytecount];

                fis.read(bytes, 0, bytecount);

                ImageIcon icon = new ImageIcon(bytes);

                thumbnail = icon.getImage();
            }
        } catch (Exception ex) {
            AppLogger.logSevere(PersistentThumbnails.class, ex);
        } finally {
            FileUtil.close(fis);
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

        String md5Filename = getMd5Filename(imageFile);

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

        String md5Filename = getMd5Filename(imageFile);

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

    public static boolean renameThumbnail(File fromImageFile,
            File toImageFile) {
        if (fromImageFile == null) {
            throw new NullPointerException("fromImageFile == null");
        }

        if (toImageFile == null) {
            throw new NullPointerException("toImageFile == null");
        }

        final String fromMd5Filename = getMd5Filename(fromImageFile);

        if (fromMd5Filename == null) {
            return false;
        }

        File fromTnFile = getThumbnailfile(fromMd5Filename);

        if (fromTnFile == null) {
            return false;
        }

        File toTnFile = getThumbnailfile(getMd5Filename(toImageFile));

        if (!fromTnFile.renameTo(toTnFile)) {
            AppLogger.logWarning(PersistentThumbnails.class,
                                 "PersistentThumbnails.Error.Rename",
                                 fromImageFile, toImageFile);

            return false;
        }

        return true;
    }

    /*
     *  Compute an MD5 hash from a fully canonicalized filename.
     * @return MD5 filename or null on errors
     */
    private static String getMd5Filename(File file) {
        MessageDigest md5;

        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception ex) {
            AppLogger.logSevere(PersistentThumbnails.class, ex);

            return null;
        }

        md5.reset();
        md5.update(("file://" + file.getAbsolutePath()).getBytes());

        byte[]        result = md5.digest();
        StringBuilder hex    = new StringBuilder();

        for (int i = 0; i < result.length; i++) {
            if ((result[i] & 0xff) == 0) {
                hex.append("00");
            } else if ((result[i] & 0xff) < 0x10) {
                hex.append("0" + Integer.toHexString(0xFF & result[i]));
            } else {
                hex.append(Integer.toHexString(0xFF & result[i]));
            }
        }

        return hex.toString();
    }
}
