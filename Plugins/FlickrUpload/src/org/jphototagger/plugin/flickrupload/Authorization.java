package org.jphototagger.plugin.flickrupload;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.uploader.Uploader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.awt.DesktopUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.openide.util.Lookup;
import org.scribe.model.Token;
import org.scribe.model.Verifier;

// https://raw.githubusercontent.com/callmeal/Flickr4Java/master/Flickr4Java/src/examples/java/AuthExample.java
/**
 * Usage:
 * <pre>
 * Authorization auth = new Authorization();
 * if (auth.authenticate()) {
 *     // do something, e.g. auth.getUploader().upload(...);
 * }
 * </pre>
 * @author Elmar Baumann
 */
final class Authorization {

    static final String API_KEY = "1332b9a79e9df756826426bdf591730c";
    static final String SECRET_KEY = "524ec6a92786c41b";
    private static final String KEY_TOKEN = "org.jphototagger.plugin.flickrupload.FlickrToken";
    private Flickr flickr;

    public void deleteToken() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        if (prefs != null) {
            prefs.removeKey(KEY_TOKEN);
        }
    }

    private String getPersistedTokenKey() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs == null
                ? ""
                : prefs.getString(KEY_TOKEN);
    }

    private void persistTokenKey(String token) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        if (prefs != null) {
            prefs.setString(KEY_TOKEN, token);
        }
    }

    public Uploader getUploader() {
        return flickr.getUploader();
    }

    public boolean authenticate() {
        try {
            flickr = new Flickr(API_KEY, SECRET_KEY, new REST());

            Flickr.debugStream = false;

            if (!StringUtil.hasContent(getPersistedTokenKey())) {
                if (!authenticateViaWebBrowser()) {
                    return false;
                }
            } else {
                Auth auth = new Auth();
                auth.setToken(getPersistedTokenKey());
                flickr.setAuth(auth);
            }
            return true;
        } catch (Throwable t) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, t);
            JOptionPane.showMessageDialog(null, Bundle.getString(Authorization.class, "Auth.Error"));
        }

        return false;
    }

    private boolean authenticateViaWebBrowser() throws Exception {
        AuthInterface authInterface = flickr.getAuthInterface();
        Token requestToken = authInterface.getRequestToken();
        String url = authInterface.getAuthorizationUrl(requestToken, Permission.DELETE);

        JOptionPane.showMessageDialog(ComponentUtil.findFrameWithIcon(), Bundle.getString(Authorization.class, "Auth.Info.GetToken.Browse"));
        DesktopUtil.browse(url, "JPhotoTagger.Plugin.FlickrUpload.Browse");

        String inputTokenKey = JOptionPane.showInputDialog(ComponentUtil.findFrameWithIcon(), Bundle.getString(Authorization.class, "Auth.Input.TokenKey"));
        if (StringUtil.hasContent(inputTokenKey)) {
            Token accessToken = authInterface.getAccessToken(requestToken, new Verifier(inputTokenKey));
            flickr.setAuth(authInterface.checkToken(accessToken));
            persistTokenKey(inputTokenKey);
            return true;
        }
        return false;
    }
}
