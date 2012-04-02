package org.jphototagger.lib.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Elmar Baumann
 */
public final class Md5Util {

    public static String getMd5FromStream(InputStream inStream) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] buffer = new byte[4 * 1024];
        int lengthRead = inStream.read(buffer);
        while (lengthRead > 0) {
            digest.update(buffer, 0, lengthRead);
            lengthRead = inStream.read(buffer);
        }
        return getFormatted(digest);
    }

//    public static String getMd5FromStream(InputStream is) throws IOException, NoSuchAlgorithmException {
//        if (is == null) {
//            throw new NullPointerException("is == null");
//        }
//        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
//        int bytesRead;
//        byte[] buffer = new byte[4 * 1024];
//        do {
//            bytesRead = is.read(buffer);
//            if (bytesRead > 0) {
//                messageDigest.update(buffer, 0, bytesRead);
//            }
//        } while (bytesRead != -1);
//        byte[] digest = messageDigest.digest();
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < digest.length; i++) {
//            sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
//        }
//        return sb.toString();
//    }

    private static String getFormatted(MessageDigest digest) {
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return String.format("%1$032x", bigInt);
    }

    private Md5Util() {
    }
}
