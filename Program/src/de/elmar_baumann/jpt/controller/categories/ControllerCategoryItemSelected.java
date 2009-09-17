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
package de.elmar_baumann.jpt.controller.categories;

import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.listener.RefreshListener;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.util.Set;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Listens for selections of items in the category list. A list item represents
 * a category. If a new item is selected, this controller sets the files of the
 * selected category to the image file thumbnails panel.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerCategoryItemSelected implements
        ListSelectionListener, RefreshListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JList listCategories = appPanel.getListCategories();
    private final ThumbnailsPanel thumbnailsPanel =
            appPanel.getPanelThumbnails();

    public ControllerCategoryItemSelected() {
        listen();
    }

    private void listen() {
        listCategories.addListSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.CATEGORY);
    }

    @Override
    public void refresh() {
        if (listCategories.getSelectedIndex() >= 0) {
            setFilesToThumbnailsPanel();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            setFilesToThumbnailsPanel();
        }
    }

    private void setFilesToThumbnailsPanel() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (listCategories.getSelectedIndex() >= 0) {
                    String category = (String) listCategories.getSelectedValue();
                    Set<String> filenames = db.getFilenamesOfCategory(category);

                    thumbnailsPanel.setFiles(
                            FileUtil.getAsFiles(filenames), Content.CATEGORY);
                }
            }
        });
    }
}
