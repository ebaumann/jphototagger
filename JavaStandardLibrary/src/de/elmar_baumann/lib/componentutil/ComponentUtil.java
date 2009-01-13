package de.elmar_baumann.lib.componentutil;

import java.awt.Component;

/**
 * 
 * All functions are throwing a <code>NullPointerException</code> if a parameter
 * is null and it is not documentet that it can be null.
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

    private ComponentUtil() {
    }
}
