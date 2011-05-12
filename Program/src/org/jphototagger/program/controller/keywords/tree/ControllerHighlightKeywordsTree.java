package org.jphototagger.program.controller.keywords.tree;

import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.renderer.TreeCellRendererKeywords;


import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import org.jdesktop.swingx.JXTree;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Listens to a {@link ThumbnailsPanel} and highlights in the tree
 * of a  {@link KeywordsPanel} the keywords of the selected image.
 *
 * @author Elmar Baumann
 */
public final class ControllerHighlightKeywordsTree implements ThumbnailsPanelListener {
    public ControllerHighlightKeywordsTree() {
        listen();
    }

    private void listen() {
        GUI.getThumbnailsPanel().addThumbnailsPanelListener(this);
    }

    @Override
    public void thumbnailsSelectionChanged() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                applyCurrentSelection();
            }
        });
    }

    private void applyCurrentSelection() {
        removeKeywords();

        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

        if (tnPanel.getSelectionCount() == 1) {
            List<File> selFiles = tnPanel.getSelectedFiles();

            if ((selFiles.size() == 1) && hasSidecarFile(selFiles)) {
                Collection<String> keywords = DatabaseImageFiles.INSTANCE.getDcSubjectsOf(selFiles.get(0));

                setKeywords(GUI.getEditKeywordsTree(), keywords);
                setKeywords(GUI.getInputHelperKeywordsTree(), keywords);
            }
        }
    }

    private void setKeywords(JTree tree, Collection<String> keywords) {
        TreeCellRenderer treeCellRenderer = tree.getCellRenderer();

        if (treeCellRenderer instanceof JXTree.DelegatingRenderer) {
            treeCellRenderer = ((JXTree.DelegatingRenderer)treeCellRenderer).getDelegateRenderer();

        }

        if (treeCellRenderer instanceof TreeCellRendererKeywords) {
            ((TreeCellRendererKeywords) treeCellRenderer).setHighlightKeywords(keywords);
            tree.repaint();
        }
    }

    private void removeKeywords() {
        setKeywords(GUI.getEditKeywordsTree(), new ArrayList<String>());
        setKeywords(GUI.getInputHelperKeywordsTree(), new ArrayList<String>());
    }

    private boolean hasSidecarFile(List<File> selFile) {
        assert selFile.size() == 1 : "Size < 1: " + selFile.size() + " - " + selFile;

        return XmpMetadata.hasImageASidecarFile(selFile.get(0));
    }

    @Override
    public void thumbnailsChanged() {

        // ignore
    }
}
