package org.jphototagger.lib.util;

import java.awt.Desktop;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Elmar Baumann
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

    /**
     * Returns whether {@code Desktop#mail(java.net.URI)} can be called.
     * <p>
     * Shorthand for {@code Desktop#isDesktopSupported()} &amp;&amp;
     * {@code Desktop#isSupported(Desktop.Action)}.
     *
     * @return true, if mailing is possible
     */
    public static boolean canMail() {
        return isSupported(Desktop.Action.MAIL);
    }

    /**
     * Returns whether {@code Desktop#browse(java.net.URI)} can be called.
     * <p>
     * Shorthand for {@code Desktop#isDesktopSupported()} &amp;&amp;
     * {@code Desktop#isSupported(Desktop.Action)}.
     *
     * @return true, if browsing is possible
     */
    public static boolean canBrowse() {
        return isSupported(Desktop.Action.BROWSE);
    }

    /**
     * Returns whether {@code Desktop#open(java.io.File)} can be called.
     * <p>
     * Shorthand for {@code Desktop#isDesktopSupported()} &amp;&amp;
     * {@code Desktop#isSupported(Desktop.Action)}.
     *
     * @return true, if opening is possible
     */
    public static boolean canOpen() {
        return isSupported(Desktop.Action.OPEN);
    }

    /**
     * Returns whether {@code Desktop#edit(java.io.File)} can be called.
     * <p>
     * Shorthand for {@code Desktop#isDesktopSupported()} &amp;&amp;
     * {@code Desktop#isSupported(Desktop.Action)}.
     *
     * @return true, if editing is possible
     */
    public static boolean canEdit() {
        return isSupported(Desktop.Action.EDIT);
    }

    /**
     * Returns whether {@code Desktop#print(java.io.File)} can be called.
     * <p>
     * Shorthand for {@code Desktop#isDesktopSupported()} &amp;&amp;
     * {@code Desktop#isSupported(Desktop.Action)}.
     *
     * @return true, if editing is possible
     */
    public static boolean canPrint() {
        return isSupported(Desktop.Action.PRINT);
    }

    private static boolean isSupported(Desktop.Action action) {
        return Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(action);
    }

    /**
     * Returns whether the VM runs on a Windows operating system.
     *
     * @return true if the VM runs on a Windows operating system
     */
    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();

        return os.contains("windows");
    }

    /**
     * Returns whether the VM runs on a Macintosh operating system.
     *
     * @return true if the VM runs on a Windows operating system
     */
    public static boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();

        return os.contains("mac");
    }

    private SystemUtil() {
    }
}
