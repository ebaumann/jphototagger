/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elmar_baumann.jpt.plugin.flickrupload;

import com.aetrion.flickr.uploader.Uploader;
import de.elmar_baumann.jpt.plugin.Plugin;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-02-13
 */
public final class FlickrUpload extends Plugin {

    private              Uploader  uploader;
    private static final long      serialVersionUID = 1526844548400296813L;
    private static final String    KEY_USERNAME     = "de.elmar_baumann.jpt.plugin.flickrupload.FlickrUsername";
    private static final String    KEY_PASSWORD     = "de.elmar_baumann.jpt.plugin.flickrupload.FlickrPassword";
    private static final String    API_KEY          = "";
    private static final SecretKey SEC_KEY;

    static {
        String key         = "d?=30sd#fgvjie}[]§$aslkg";
        byte[] secKeyBytes = new byte[24];

        for (int i = 0; i < secKeyBytes.length; i++) {
            secKeyBytes[i] = (byte) key.charAt(i);
        }

        SEC_KEY = new SecretKeySpec(secKeyBytes, "DESede");
    }

    @Override
    public String getName() {
        return ResourceBundle.getBundle("de/elmar_baumann/jpt/plugin/flickrupload/Bundle").
                getString("FlickrUpload.Name");
    }

    @Override
    public String getDescription() {
        return ResourceBundle.getBundle("de/elmar_baumann/jpt/plugin/flickrupload/Bundle").
                getString("FlickrUpload.Description");
    }

    @Override
    public JPanel getSettingsPanel() {
        return new SettingsPanel(getProperties());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        upload();
    }

    private void upload() {
        String password = getPassword(getProperties());
        String username = getUsername(getProperties());
        if (!checkExistsUserData(username, password)) return;

        //uploader = new Uploader(

        for (File file : getFiles()) {
            System.out.println("UP: " + file);
        }
    }

    private boolean checkExistsUserData(String username, String password) {
        if (username == null || username.isEmpty() ||
            password == null || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, getMessageNoUserData());
            SettingsDialog dlg = new SettingsDialog();
            dlg.setProperties(getProperties());
            dlg.setVisible(true);
        }
        return true;
    }

    private String getMessageNoUserData() {
        return ResourceBundle.getBundle("de/elmar_baumann/jpt/plugin/cftc/Bundle").
                getString("FlickrUpload.Error.NoUserData");
    }

    static String getUsername(Properties p) {
        if (!p.containsKey(KEY_USERNAME)) return null;
        return p.getProperty(KEY_USERNAME).trim();
    }

    static String getPassword(Properties p) {
        if (!p.containsKey(KEY_PASSWORD)) return null;
        String encryptedPassword = p.getProperty(KEY_PASSWORD).trim();
        if (encryptedPassword.isEmpty()) return null;
        try {
            Crypt decrypter = new Crypt(SEC_KEY);
            return decrypter.decrypt(encryptedPassword);
        } catch (Exception ex) {
            Logger.getLogger(FlickrUpload.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    static void saveUsername(String username, Properties p) {
        p.setProperty(KEY_USERNAME, username.trim());
    }

    static boolean savePassword(String plainPassword, Properties p) {
        try {
            Crypt encrypter = new Crypt(SEC_KEY);
            p.setProperty(KEY_PASSWORD, encrypter.encrypt(plainPassword));
            return true;
        } catch (Exception ex) {
            Logger.getLogger(FlickrUpload.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
