package de.elmar_baumann.lib.componentutil;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Window;

/**
 * 
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/27
 */
public final class ComponentUtil {

    /**
     * Invalidates, validates and repaint a component.
     * 
     * @param component  component
     */
    public static void forceRepaint(Component component) {
        if (component == null)
            throw new NullPointerException("component == null");

        component.invalidate();
        component.validate();
        component.repaint();
    }

    /**
     * Centers a window on the screen.
     *
     * @param window window to center
     */
    public static void centerScreen(Window window) {
        Dimension screenDimension = window.getToolkit().getScreenSize();
        Rectangle frameBounds = window.getBounds();
        window.setLocation((screenDimension.width - frameBounds.width) / 2,
                (screenDimension.height - frameBounds.height) / 2);
    }

    private ComponentUtil() {
    }
}
