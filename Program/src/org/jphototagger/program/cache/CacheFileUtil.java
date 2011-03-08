/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jphototagger.program.cache;

import java.io.File;
import java.security.MessageDigest;
import org.jphototagger.program.app.AppLogger;

/**
 *
 *
 * @author Elmar Baumann
 */
final public class CacheFileUtil {

    public static void ensureCacheDirectoriesExists() {
        ExifCache.ensureCacheDiretoryExists();
    }

    /**
     * Computes a MD5 hash from a fully canonicalized filename.
     *
     * @return MD5 filename
     */
    static String getMd5Filename(File file) {
        MessageDigest md5;

        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception ex) {
            AppLogger.logSevere(CacheFileUtil.class, ex);

            return null;
        }

        md5.reset();
        md5.update(("file://" + file.getAbsolutePath()).getBytes());

        byte[] result = md5.digest();
        StringBuilder hex = new StringBuilder();

        for (int i = 0; i < result.length; i++) {
            if ((result[i] & 0xff) == 0) {
                hex.append("00");
            } else if ((result[i] & 0xff) < 0x10) {
                hex.append("0").append(Integer.toHexString(0xFF & result[i]));
            } else {
                hex.append(Integer.toHexString(0xFF & result[i]));
            }
        }

        return hex.toString();
    }

    private CacheFileUtil() {
    }
}
