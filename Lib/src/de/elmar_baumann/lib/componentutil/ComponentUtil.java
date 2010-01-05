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
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Finds frames with an icon: Frames of {@link Frame#getFrames()} where
     * {@link Frame#getIconImage()} returns not null.
     *
     * @return frames with an icon or an empty list
     * @see    #getFrameWithIcon()
     */
    public static List<Frame> findFramesWithIcons() {
        List<Frame> frames    = new ArrayList<Frame>();
        Frame[]     allFrames = Frame.getFrames();

        for (Frame frame : allFrames) {
            if (frame.getIconImage() != null) {
                frames.add(frame);
            }
        }

        return frames;
    }

    /**
     * Returns the first found frame of {@link #findFramesWithIcons()}.
     * <p>
     * Especially for usage in a <code>JOptionPane#show...Dialog()</code>
     * instead of null. Then in the dialog frame an icon will be displayed
     * that is different to the Java "coffee cup" icon.
     *
     * @return frame or null
     */
    public static Frame getFrameWithIcon() {
        List<Frame> frames = findFramesWithIcons();

        return frames.size() == 0 ? null : frames.get(0);
    }

    private ComponentUtil() {
    }
}
