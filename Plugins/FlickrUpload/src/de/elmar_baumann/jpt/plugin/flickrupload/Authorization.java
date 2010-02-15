package de.elmar_baumann.jpt.plugin.flickrupload;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.AuthInterface;
import com.aetrion.flickr.auth.Permission;
import java.awt.Desktop;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-02-14
 */
final class Authorization {

    private static final String         KEY_TOKEN     = "de.elmar_baumann.jpt.plugin.flickrupload.FlickrToken";
    private              RequestContext requestContext;
    private              String         frob;
    private              String         token;
    private              AuthInterface  authInterface;
    private              boolean        authenticated;
    private              Auth           auth;
    private final        Properties     properties;

    Authorization(Properties properties) {
        this.properties = properties;
    }

    public AuthInterface getAuthInterface() {
        assert authenticated;
        return authInterface;
    }

    public void deleteToken() {
        properties.remove(KEY_TOKEN);
    }

    public boolean authenticate() {
        try {
            Flickr flickr = new Flickr("1efba3cf4198b683047512bec1429f19", "b58bc39d8aedd4c5", new REST());
            Flickr.debugStream = false;
            requestContext = RequestContext.getRequestContext();
            authInterface  = flickr.getAuthInterface();
            frob           = authInterface.getFrob();
            token          = properties.getProperty(KEY_TOKEN);
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
            JOptionPane.showMessageDialog(null, Bundle.getString("Auth.Error"));
        }
        return false;
    }

    private void authenticateViaWebBrowser() throws Exception {
        URL url = authInterface.buildAuthenticationUrl(Permission.DELETE, frob);
        LargeMessagesDialog dlg = new LargeMessagesDialog(Bundle.getString("Auth.Info.GetToken.Browse", url.toExternalForm()));
        dlg.setVisible(true);
        Desktop.getDesktop().browse(url.toURI());
        JOptionPane.showMessageDialog(null, Bundle.getString("Auth.Info.GetToken.Confirm"));
        auth = authInterface.getToken(frob);
        token = auth.getToken();
        properties.setProperty(KEY_TOKEN, token);
    }
}
