package de.elmar_baumann.lib.system;

import de.elmar_baumann.lib.util.Version;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/04/30
 */
public final class SystemUtil {

    /**
     * Returns the Version of the JVM.
     * 
     * @return Version or null if not found
     */
    public static Version getJavaVersion() {
        Version version = null;
        String versionProperty = System.getProperty("java.version");
        StringTokenizer tok = new StringTokenizer(versionProperty, ".");
        if (tok.countTokens() >= 2) {
            try {
                int major = Integer.parseInt(tok.nextToken());
                int minor = Integer.parseInt(tok.nextToken());
                return new Version(major, minor);
            } catch (Exception ex) {
                Logger.getLogger(SystemUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return version;
    }

    private SystemUtil() {
    }
}
