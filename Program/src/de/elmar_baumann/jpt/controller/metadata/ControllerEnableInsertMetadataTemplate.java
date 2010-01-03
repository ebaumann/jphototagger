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
package de.elmar_baumann.jpt.controller.metadata;

import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * Listens for selections of items in the directory tree view. A tree item
 * represents a directory. If a new item is selected, this controller enables or
 * disables the button {@link AppPanel#getButtonMetadataTemplateInsert()}
 * depending on whether the directory is writable or not (when a directory is
 * not writable, no XMP metadata files can be written into this directory).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-22
 */
public final class ControllerEnableInsertMetadataTemplate implements
        TreeSelectionListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JTree treeDirectories = appPanel.getTreeDirectories();
    private final JButton buttonMetadataTemplateInsert =
            appPanel.getButtonMetadataTemplateInsert();

    public ControllerEnableInsertMetadataTemplate() {
        listen();
    }

    private void listen() {
        treeDirectories.addTreeSelectionListener(this);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (e.isAddedPath()) {
            setEnabledButtonInsert();
        }
    }

    private void setEnabledButtonInsert() {
        if (treeDirectories.getSelectionPath().getLastPathComponent() instanceof File) {
            String fileName = ((File) treeDirectories.getSelectionPath().
                    getLastPathComponent()).getAbsolutePath();
            File file = new File(fileName);

            buttonMetadataTemplateInsert.setEnabled(
                    file.isDirectory() && file.canWrite());
        }
    }
}
