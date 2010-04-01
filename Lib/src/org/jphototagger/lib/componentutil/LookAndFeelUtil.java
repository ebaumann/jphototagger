/*
 * @(#)LookAndFeelUtil.java    Created on 2008-07-14
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
 * @author  Elmar Baumann
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
            Logger.getLogger(LookAndFeelUtil.class.getName()).log(Level.SEVERE,
                             null, ex);
        }
    }

    private LookAndFeelUtil() {}
}
