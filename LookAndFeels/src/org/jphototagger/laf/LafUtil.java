package org.jphototagger.laf;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * @author Elmar Baumann
 */
public final class LafUtil {

    public static boolean canInstall(String classname) {
        if (classname == null) {
            throw new NullPointerException("classname == null");
        }
        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if (classname.equals(info.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param classNameWithoutPackagePath e.g. "Nimbus" case insensitive
     * @param defaultPath return this, if not found
     * @return class name of LaF prepended by package name or defaultPath if not found
     */
    public static String findLookuAndFeel(String classNameWithoutPackagePath, String defaultPath) {
        if (defaultPath == null) {
            throw new NullPointerException("defaultPath == null");
        }
        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            String lafClassName = info.getClassName();
            if (lafClassName.toLowerCase().endsWith('.' + classNameWithoutPackagePath.toLowerCase())) {
                return lafClassName;
            }
        }
        return defaultPath;
    }

    private LafUtil() {
    }
}
