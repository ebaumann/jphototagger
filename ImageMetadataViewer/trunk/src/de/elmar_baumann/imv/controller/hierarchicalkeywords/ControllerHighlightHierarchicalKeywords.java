package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.InputHelperDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import de.elmar_baumann.imv.view.renderer.TreeCellRendererHierarchicalKeywords;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

/**
 * Listens to a {@link ImageFileThumbnailsPanel} and highlights in the tree
 * of a  {@link HierarchicalKeywordsPanel} the keywords of the selected image.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-23
 */
public final class ControllerHighlightHierarchicalKeywords
        implements ThumbnailsPanelListener {

    private final ImageFileThumbnailsPanel tnPanel =
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
                expandTreeNodes(appHkPanel);
                expandTreeNodes(dlgHkPanel);
            }
        }
    }

    private void setKeywords(JTree tree, Collection<String> keywords) {
        TreeCellRenderer r = tree.getCellRenderer();
        assert r instanceof TreeCellRendererHierarchicalKeywords :
                "Not a TreeCellRendererHierarchicalKeywords: " + r;
        if (r instanceof TreeCellRendererHierarchicalKeywords) {
            TreeUtil.expandAll(tree, true);
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
                "Size < 1: " + selFile.size() + " - " + selFile;
        return XmpMetadata.hasImageASidecarFile(selFile.get(0).getAbsolutePath());
    }

    @Override
    public void thumbnailsChanged() {
        // ignore
    }
}
