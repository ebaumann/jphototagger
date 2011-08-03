package org.jphototagger.dtncreators;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.core.UserDirectoryProvider;
import org.openide.util.Lookup;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class Util {

    public static void browse(String uri) {
        if (uri == null) {
            throw new NullPointerException("uri == null");
        }

        try {
            Desktop.getDesktop().browse(new URI(uri));
        } catch (Throwable t) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    public static File lookupUserDirectory() {
        UserDirectoryProvider provider = Lookup.getDefault().lookup(UserDirectoryProvider.class);

        return provider == null
                ? null
                : provider.getUserDirectory();
    }

    private Util() {
    }
}
