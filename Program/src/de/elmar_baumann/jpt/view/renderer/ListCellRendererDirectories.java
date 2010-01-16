/*
 * JPhotoTagger tags and finds images fast
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
package de.elmar_baumann.jpt.view.renderer;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.io.DirectoryInfo;
import de.elmar_baumann.jpt.resource.Bundle;
import java.awt.Component;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.filechooser.FileSystemView;

/**
 * Benutzt vom ScanDirectoriesDialog, zeigt Systemordnericons vor
 * den Verzeichnisnamen an.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-07-25
 * @see     de.elmar_baumann.jpt.view.dialogs.UpdateMetadataOfDirectoriesDialog
 */
public final class ListCellRendererDirectories extends DefaultListCellRenderer {

    private static final FileSystemView FILE_SYSTEM_VIEW = FileSystemView.getFileSystemView();
    private static final long           serialVersionUID = 1443237617540897116L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        DirectoryInfo directoryInfo = (DirectoryInfo) value;
        File dir = directoryInfo.getDirectory();
        if (dir.exists()) {
            synchronized (FILE_SYSTEM_VIEW) {
                try {
                    label.setIcon(FILE_SYSTEM_VIEW.getSystemIcon(dir));
                } catch (Exception ex) {
                    AppLogger.logSevere(ListCellRendererDirectories.class, ex);
                }
            }
        }
        label.setText(getLabelText(directoryInfo));
        return label;
    }

    private static String getLabelText(DirectoryInfo directoryInfo) {
        return Bundle.getString("ListCellRendererDirectories.LabelText", directoryInfo.getDirectory().getAbsolutePath(), directoryInfo.getImageFileCount());
    }
}
