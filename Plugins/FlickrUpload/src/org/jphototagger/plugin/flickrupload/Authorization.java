package org.jphototagger.plugin.flickrupload;

import java.awt.Desktop;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.AuthInterface;
import com.aetrion.flickr.auth.Permission;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.util.Bundle;

/**
 *
 *
 * @author Elmar Baumann
 */
final class Authorization {

    private static final String KEY_TOKEN = "org.jphototagger.plugin.flickrupload.FlickrToken";
    private RequestContext requestContext;
    private String frob;
    private String token;
    private AuthInterface authInterface;
    private boolean authenticated;
    private Auth auth;

    public AuthInterface getAuthInterface() {
        assert authenticated;

        return authInterface;
    }

    public void deleteToken() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        if (storage != null) {
            storage.removeKey(KEY_TOKEN);
        }
    }

    private String getToken() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage == null
                ? ""
                : storage.getString(KEY_TOKEN);
    }

    private void setToken(String token) {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        if (storage != null) {
            storage.setString(KEY_TOKEN, token);
        }
    }

    public boolean authenticate() {
        try {
            Flickr flickr = new Flickr("1efba3cf4198b683047512bec1429f19", "b58bc39d8aedd4c5", new REST());

            Flickr.debugStream = false;
            requestContext = RequestContext.getRequestContext();
            authInterface = flickr.getAuthInterface();
            frob = authInterface.getFrob();
            token = getToken();

            if (token == null) {
                authenticateViaWebBrowser();
            } else {
                auth = new Auth();
                auth.setToken(token);
            }

            requestContext.setAuth(auth);
            authenticated = true;

            return true;
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, Bundle.getString(Authorization.class, "Auth.Error"));
        }

        return false;
    }

    private void authenticateViaWebBrowser() throws Exception {
        URL url = authInterface.buildAuthenticationUrl(Permission.DELETE, frob);

        JOptionPane.showMessageDialog(ComponentUtil.getFrameWithIcon(), Bundle.getString(Authorization.class, "Auth.Info.GetToken.Browse"));
        Desktop.getDesktop().browse(url.toURI());
        JOptionPane.showMessageDialog(ComponentUtil.getFrameWithIcon(), Bundle.getString(Authorization.class, "Auth.Info.GetToken.Confirm"));
        auth = authInterface.getToken(frob);
        token = auth.getToken();
        setToken(token);
    }
}
