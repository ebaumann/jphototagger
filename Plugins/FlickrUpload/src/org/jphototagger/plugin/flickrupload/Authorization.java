package org.jphototagger.plugin.flickrupload;

import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.AuthInterface;
import com.aetrion.flickr.auth.Permission;
import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.REST;
import org.jphototagger.lib.componentutil.ComponentUtil;
import java.awt.Desktop;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jphototagger.lib.util.ServiceLookup;
import org.jphototagger.services.Storage;

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
        Storage storage = ServiceLookup.lookup(Storage.class);

        if (storage != null) {
            storage.removeKey(KEY_TOKEN);
        }
    }

    private String getToken() {
        Storage storage = ServiceLookup.lookup(Storage.class);

        return storage == null
                ? ""
                : storage.getString(KEY_TOKEN);
    }

    private void setToken(String token) {
        Storage storage = ServiceLookup.lookup(Storage.class);

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
            JOptionPane.showMessageDialog(null, FlickrBundle.INSTANCE.getString("Auth.Error"));
        }

        return false;
    }

    private void authenticateViaWebBrowser() throws Exception {
        URL url = authInterface.buildAuthenticationUrl(Permission.DELETE, frob);

        JOptionPane.showMessageDialog(ComponentUtil.getFrameWithIcon(),
                FlickrBundle.INSTANCE.getString("Auth.Info.GetToken.Browse"));
        Desktop.getDesktop().browse(url.toURI());
        JOptionPane.showMessageDialog(ComponentUtil.getFrameWithIcon(),
                FlickrBundle.INSTANCE.getString("Auth.Info.GetToken.Confirm"));
        auth = authInterface.getToken(frob);
        token = auth.getToken();
        setToken(token);
    }
}
