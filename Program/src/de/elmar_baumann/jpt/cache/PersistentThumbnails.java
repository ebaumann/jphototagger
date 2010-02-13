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
package de.elmar_baumann.jpt.cache;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.io.IoUtil;
import de.elmar_baumann.lib.image.util.ImageUtil;
import de.elmar_baumann.lib.io.FileLock;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 * Persistent stored (cached) thumbnails.
 *
 * @author  Martin Pohlack <martinp@gmx.de>, Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-13
 */
public final class PersistentThumbnails {

    public static void writeThumbnail(Image thumbnail, String hash) {
        FileOutputStream fos = null;
        File tnFile = getThumbnailfile(hash);
        if (tnFile == null) return;
        try {
            if (!IoUtil.lockLogWarning(tnFile, PersistentThumbnails.class)) return;
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
            closeStream(fos);
        }
    }

    private static void logWriteThumbnail(File tnFile) {
        AppLogger.logInfo(PersistentThumbnails.class, "PersistentThumbnails.Info.WriteThumbnail", tnFile);
    }

    public static boolean deleteThumbnail(String hash) {
        File thumbnailFile = getThumbnailfile(hash);
        if (thumbnailFile == null) return false;
        if (thumbnailFile.exists()) {
            return thumbnailFile.delete();
        }
        return false;
    }

    public static Image getThumbnail(String hash) {
        Image thumbnail = null;
        FileInputStream fis = null;
        try {
            File tnFile = getThumbnailfile(hash);
            if (tnFile == null) return null;
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
            closeStream(fis);
        }
        return thumbnail;
    }

    /**
     * Returns the thumbnail file for an image file.
     *
     * Shortcut for: {@code getThumbnailfile(getMd5Filename(imageFilename))}.
     *
     * @param  imageFilename filename of the image file (full path)
     * @return               thumbnail file (may not exists, check with
     *                       {@link File#exists()})
     */
    public static File getThumbnailFileOfImageFile(String imageFilename) {
        String md5Filename = getMd5Filename(imageFilename);
        return md5Filename == null
                ? null
                : getThumbnailfile(md5Filename);
    }

    /**
     * Returns whether an image file has a thumbnail file.
     *
     * @param  imageFilename name of the image file
     * @return               true if the image file has a thumbnail
     */
    public static boolean existsThumbnailForImagefile(String imageFilename) {
        File tnFile = getThumbnailFileOfImageFile(imageFilename);
        return tnFile == null
                ? false
                : tnFile.exists();
    }

    /**
     * Compute final name of thumbnail on disk.
     *
     * @param  hash hash
     * @return      file or null if the file couldn't be created
     */
    public static File getThumbnailfile(String hash) {
        String dir = UserSettings.INSTANCE.getThumbnailsDirectoryName();
        try {
            FileUtil.ensureDirectoryExists(new File(dir));
            return new File(dir + File.separator + hash + ".jpeg");
        } catch (Exception ex) {
            AppLogger.logSevere(PersistentThumbnails.class, ex);
        }
        return null;
    }

    /* Adapts the thumbnail's name to changes in the name of the original file
     */
    public static void updateThumbnailName(String oldName, String newName) {
        final String md5File = getMd5Filename(oldName);
        if (md5File == null) return;
        File tnFile = getThumbnailfile(md5File);
        if (tnFile == null) return;
        
        if (!tnFile.renameTo(getThumbnailfile(getMd5Filename(newName)))) {
            AppLogger.logWarning(PersistentThumbnails.class, "PersistentThumbnails.Error.Rename", oldName, newName);
        }
    }

    private static String getCanonicalFilepath(String filename) {
        File file = new File(filename);
        try {
            return file.getCanonicalPath();
        } catch (IOException ex) {
            AppLogger.logSevere(PersistentThumbnails.class, ex);
        }
        return "";
    }

    /* Compute an MD5 hash from a fully canonicalized filename.
     * @return MD5 filename or null on errors
     */
    public static String getMd5Filename(String cFilename) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception ex) {
            Logger.getLogger(PersistentThumbnails.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        md5.reset();
        md5.update(("file://" + getCanonicalFilepath(cFilename)).getBytes());
        byte[] result = md5.digest();

        StringBuffer hex = new StringBuffer();
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

    private static void closeStream(FileInputStream fis) {
        if (fis != null) {
            try {
                fis.close();
            } catch (Exception ex) {
                AppLogger.logSevere(PersistentThumbnails.class, ex);
            }
        }
    }

    private static void closeStream(FileOutputStream fos) {
        if (fos != null) {
            try {
                fos.close();
            } catch (Exception ex) {
                AppLogger.logSevere(PersistentThumbnails.class, ex);
            }
        }
    }

    private PersistentThumbnails() {
    }
}
