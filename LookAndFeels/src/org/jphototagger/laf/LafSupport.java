package org.jphototagger.laf;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * @author Elmar Baumann
 */
public final class LafSupport {

    public static boolean canInstall(String classname) {
        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if (classname.equals(info.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private LafSupport() {
    }
}
