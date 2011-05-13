package org.jphototagger.lib.componentutil;

import java.awt.Color;
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
 * @author Elmar Baumann
 */
public final class LookAndFeelUtil {

    /**
     * Returns a color from the {@link UIManager}.
     *
     * Some renderers, e.g. <code>DefaultTreeCellRenderer</code> are setting
     * <code>null</code> as background color if the color is an instance of
     * <code>ColorUIResource</code> which the UI manager does return.
     *
     * @param  propertyKey property key
     * @return             color or null
     */

    //
    //
    public static Color getUiColor(String propertyKey) {
        if (propertyKey == null) {
            throw new NullPointerException("propertyKey == null");
        }

        Color col = UIManager.getColor(propertyKey);

        return (col == null)
               ? null
               : new Color(col.getRGB());
    }

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

    private LookAndFeelUtil() {}
}
