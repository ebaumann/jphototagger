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
package de.elmar_baumann.jpt.controller.hierarchicalkeywords;

import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.renderer.TreeCellRendererHierarchicalKeywords;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

/**
 * Listens to a {@link ThumbnailsPanel} and highlights in the tree
 * of a  {@link HierarchicalKeywordsPanel} the keywords of the selected image.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-23
 */
public final class ControllerHighlightHierarchicalKeywords
        implements ThumbnailsPanelListener {

    private final ThumbnailsPanel tnPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final HierarchicalKeywordsPanel appHkPanel =
            appPanel.getPanelHierarchicalKeywords();
    private final HierarchicalKeywordsPanel dlgHkPanel =
            InputHelperDialog.INSTANCE.getPanelKeywords();
    private final JTree treeAppPanel = appHkPanel.getTree();
    private final JTree treeDialog = dlgHkPanel.getTree();

    public ControllerHighlightHierarchicalKeywords() {
        listen();
    }

    private void listen() {
        tnPanel.addThumbnailsPanelListener(this);
    }

    @Override
    public void thumbnailsSelectionChanged() {
        removeKeywords();
        if (tnPanel.getSelectionCount() == 1) {
            List<File> selFile = tnPanel.getSelectedFiles();
            assert selFile.size() == 1;
            if (selFile.size() == 1 && hasSidecarFile(selFile)) {
                Collection<String> keywords =
                        db.getDcSubjectsOfFile(selFile.get(0).getAbsolutePath());
                setKeywords(treeAppPanel, keywords);
                setKeywords(treeDialog, keywords);
                //expandTreeNodes(appHkPanel);
                //expandTreeNodes(dlgHkPanel);
            }
        }
    }

    private void setKeywords(JTree tree, Collection<String> keywords) {
        TreeCellRenderer r = tree.getCellRenderer();
        assert r instanceof TreeCellRendererHierarchicalKeywords :
                "Not a TreeCellRendererHierarchicalKeywords: " + r; // NOI18N
        if (r instanceof TreeCellRendererHierarchicalKeywords) {
            //TreeUtil.expandAll(tree, true);
            ((TreeCellRendererHierarchicalKeywords) r).setKeywords(keywords);
            tree.repaint();
        }
    }

    private void expandTreeNodes(HierarchicalKeywordsPanel panel) {
        if (!panel.isExpandedAll()) {
            panel.expandAll(true);
        }
    }

    private void removeKeywords() {
        setKeywords(treeAppPanel, new ArrayList<String>());
        setKeywords(treeDialog, new ArrayList<String>());
    }

    private boolean hasSidecarFile(List<File> selFile) {
        assert selFile.size() == 1 :
                "Size < 1: " + selFile.size() + " - " + selFile; // NOI18N
        return XmpMetadata.hasImageASidecarFile(selFile.get(0).getAbsolutePath());
    }

    @Override
    public void thumbnailsChanged() {
        // ignore
    }
}
