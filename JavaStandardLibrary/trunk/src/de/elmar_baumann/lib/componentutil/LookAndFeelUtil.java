package de.elmar_baumann.lib.componentutil;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;

/**
 * The application's Look and Feel.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/14
 */
public final class LookAndFeelUtil {

    /**
     * Set's the application's Look and Feel. Must be called before the first
     * GUI element will be created.
     */
    public static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            Logger.getLogger(LookAndFeelUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private LookAndFeelUtil() {
    }
}
