package de.elmar_baumann.jpt.plugin.flickrupload;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

// Code: http://www.exampledepot.com/egs/javax.crypto/desstring.html

final class Crypt {

    private Cipher ecipher;
    private Cipher dcipher;

    Crypt(SecretKey key) {
        try {
            ecipher = Cipher.getInstance("DESede");
            dcipher = Cipher.getInstance("DESede");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    String encrypt(String str) {
        try {
            byte[] utf8 = str.getBytes("UTF8");
            byte[] enc = ecipher.doFinal(utf8);
            return Base64.encodeBytes(enc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    String decrypt(String str) {
        try {
            byte[] dec = Base64.decode(str);
            byte[] utf8 = dcipher.doFinal(dec);
            return new String(utf8, "UTF8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
