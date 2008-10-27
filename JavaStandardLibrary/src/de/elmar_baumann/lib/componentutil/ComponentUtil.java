package de.elmar_baumann.lib.componentutil;

import java.awt.Component;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/27
 */
public class ComponentUtil {

    /**
     * Invalidates, validates and repaint a component.
     * 
     * @param component  component
     */
    public static void forceRepaint(Component component) {
        component.invalidate();
        component.validate();
        component.repaint();
    }
}
