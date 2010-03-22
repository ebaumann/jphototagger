/*
 * @(#)ListCellRendererFileSystem.java    Created on 2008-09-14
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

package org.jphototagger.lib.renderer;

import java.awt.Component;

import java.io.File;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListCellRenderer;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Renders an file specific icon for cell values that are an instance of
 * {@link java.io.File}. Uses
 * {@link javax.swing.filechooser.FileSystemView#getSystemIcon(java.io.File)}.
 *
 * @author  Elmar Baumann
 */
public final class ListCellRendererFileSystem extends DefaultListCellRenderer {
    private static final FileSystemView FILE_SYSTEM_VIEW =
        FileSystemView.getFileSystemView();
    private static final long serialVersionUID = 7162791469100194476L;
    private final boolean     absolutePathName;

    /**
     * Constructor.
     *
     * @param absolutePathName true, if the absolute path shall be displayed and
     *                     false, if only the file name shall be displayed.
     *                     Default: false (only the file name shall be displayed).
     */
    public ListCellRendererFileSystem(boolean absolutePathName) {
        this.absolutePathName = absolutePathName;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
                           index, isSelected, cellHasFocus);

        if (value instanceof File) {
            File file = (File) value;

            if (file.exists()) {
                synchronized (FILE_SYSTEM_VIEW) {
                    try {
                        label.setIcon(FILE_SYSTEM_VIEW.getSystemIcon(file));
                    } catch (Exception ex) {
                        Logger.getLogger(
                            ListCellRendererFileSystem.class.getName()).log(
                            Level.WARNING, null, ex);
                    }
                }
            }

            label.setText(absolutePathName
                          ? file.getAbsolutePath()
                          : file.getName());
        }

        return label;
    }
}
