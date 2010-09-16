/*
 * @(#)ControllerHighlightKeywordsTree.java    Created on 2009-07-23
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.keywords.tree;

import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.renderer.TreeCellRendererKeywords;
import org.jphototagger.program.view.ViewUtil;

import java.awt.EventQueue;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

/**
 * Listens to a {@link ThumbnailsPanel} and highlights in the tree
 * of a  {@link KeywordsPanel} the keywords of the selected image.
 *
 * @author  Elmar Baumann
 */
public final class ControllerHighlightKeywordsTree
        implements ThumbnailsPanelListener {
    public ControllerHighlightKeywordsTree() {
        listen();
    }

    private void listen() {
        ViewUtil.getThumbnailsPanel().addThumbnailsPanelListener(this);
    }

    @Override
    public void thumbnailsSelectionChanged() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                applyCurrentSelection();
            }
        });
    }

    private void applyCurrentSelection() {
        removeKeywords();

        ThumbnailsPanel tnPanel = ViewUtil.getThumbnailsPanel();

        if (tnPanel.getSelectionCount() == 1) {
            List<File> selFiles = tnPanel.getSelectedFiles();

            if ((selFiles.size() == 1) && hasSidecarFile(selFiles)) {
                Collection<String> keywords =
                    DatabaseImageFiles.INSTANCE.getDcSubjectsOf(
                        selFiles.get(0));

                setKeywords(ViewUtil.getEditKeywordsTree(), keywords);
                setKeywords(ViewUtil.getInputHelperKeywordsTree(), keywords);
            }
        }
    }

    private void setKeywords(JTree tree, Collection<String> keywords) {
        TreeCellRenderer r = tree.getCellRenderer();

        if (r instanceof TreeCellRendererKeywords) {
            ((TreeCellRendererKeywords) r).setSelImgKeywords(keywords);
            tree.repaint();
        }
    }

    private void removeKeywords() {
        setKeywords(ViewUtil.getEditKeywordsTree(), new ArrayList<String>());
        setKeywords(ViewUtil.getInputHelperKeywordsTree(),
                    new ArrayList<String>());
    }

    private boolean hasSidecarFile(List<File> selFile) {
        assert selFile.size() == 1 :
               "Size < 1: " + selFile.size() + " - " + selFile;

        return XmpMetadata.hasImageASidecarFile(selFile.get(0));
    }

    @Override
    public void thumbnailsChanged() {

        // ignore
    }
}
