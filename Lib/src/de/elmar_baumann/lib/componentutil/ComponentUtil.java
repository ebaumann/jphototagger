/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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
 * @version 2008-10-27
 */
public final class ComponentUtil {

    /**
     * Invalidates, validates and repaint a component.
     * 
     * @param component  component
     */
    public static void forceRepaint(Component component) {
        if (component == null)
            throw new NullPointerException("component == null"); // NOI18N

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
