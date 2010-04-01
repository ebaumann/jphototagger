/*
 * @(#)ComponentUtil.java    Created on 2008-10-27
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.lib.componentutil;

import java.awt.Component;
import java.awt.Container;
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
 * @author  Elmar Baumann
 */
public final class ComponentUtil {

    /**
     * Invalidates, validates and repaint a component.
     *
     * @param component  component
     */
    public static void forceRepaint(Component component) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }

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
        Rectangle frameBounds     = window.getBounds();

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

        return (frames.size() == 0)
               ? null
               : frames.get(0);
    }

    /**
     * Returns all elements of a specific class from a container.
     *
     * <em>Only elements of that class are detected, not sub- and supertyes!</em>
     *
     * @param <T>       class type
     * @param container container
     * @param clazz     class
     * @return          found elements or empty list
     */
    public static <T> List<T> getAllOf(Container container, Class<T> clazz) {
        if (container == null) {
            throw new NullPointerException("container == null");
        }

        if (clazz == null) {
            throw new NullPointerException("clazz == null");
        }

        List<T> components = new ArrayList<T>();

        addAllOf(container, clazz, components);

        return components;
    }

    @SuppressWarnings("unchecked")
    private static <T> void addAllOf(Container container, Class<T> clazz,
                                     List<T> all) {
        int count = container.getComponentCount();

        if (container.getClass().equals(clazz)) {
            all.add((T) container);
        }

        for (int i = 0; i < count; i++) {
            Component component = container.getComponent(i);

            if (component instanceof Container) {
                addAllOf((Container) component, clazz, all);    // Recursive
            } else if (component.getClass().equals(clazz)) {
                all.add((T) component);
            }
        }
    }

    /**
     * Makes a window visible - if invisible - and brings it to front.
     *
     * @param window window
     */
    public static void show(Window window) {
        if (!window.isVisible()) {
            window.setVisible(true);
        }

        window.toFront();
    }

    private ComponentUtil() {}
}
