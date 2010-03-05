/*
 * JPhotoTagger tags and finds images fast.
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
package de.elmar_baumann.jpt.view.renderer;

import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.data.Program;
import java.awt.Component;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.filechooser.FileSystemView;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2008-11-04
 */
public final class ListCellRendererPrograms extends DefaultListCellRenderer {

    private static final FileSystemView FILE_SYSTEM_VIEW = FileSystemView.getFileSystemView();
    private static final Icon           ICON_ERROR       = AppLookAndFeel.getIcon("icon_error.png");
    private static final long           serialVersionUID = 8523795184154878875L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Program program = (Program) value;
        label.setText(program.getAlias());
        File file = program.getFile();
        if (file.exists()) {
            if (file.exists()) {
                synchronized (FILE_SYSTEM_VIEW) {
                    try {
                        setIcon(FILE_SYSTEM_VIEW.getSystemIcon(file));
                    } catch (Exception ex) {
                        AppLogger.logSevere(ListCellRendererPrograms.class, ex);
                    }
                }
            }
        } else {
            label.setIcon(ICON_ERROR);
        }
        return label;
    }
}
