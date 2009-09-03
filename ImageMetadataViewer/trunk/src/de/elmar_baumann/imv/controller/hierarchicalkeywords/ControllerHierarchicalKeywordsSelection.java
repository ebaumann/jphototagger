package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.controller.keywords.ShowThumbnailsContainingKeywords;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Listens to {@link AppPanel#getTreeSelHierarchicalKeywords()} and on selection
 * shows thumbnails of the selected keyword and all it's
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-09-02
 */
public final class ControllerHierarchicalKeywordsSelection
        implements TreeSelectionListener {

    private final JTree tree = GUI.INSTANCE.getAppPanel().
            getTreeSelHierarchicalKeywords();

    public ControllerHierarchicalKeywordsSelection() {
        listen();
    }

    private void listen() {
        tree.getSelectionModel().addTreeSelectionListener(this);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (e.isAddedPath()) {
            showThumbnailsOfSelKeywords();
        }
    }

    private void showThumbnailsOfSelKeywords() {
        List<Object> nodes = getSubtreeNodes();
        if (nodes != null) {
            List<String> keywords = new ArrayList<String>(nodes.size());
            for (Object node : nodes) {
                if (node instanceof DefaultMutableTreeNode) {
                    Object userObject =
                            ((DefaultMutableTreeNode) node).getUserObject();
                    if (userObject instanceof HierarchicalKeyword) {
                        HierarchicalKeyword hk =
                                (HierarchicalKeyword) userObject;
                        if (hk.isReal()) {
                            keywords.add(hk.getKeyword());
                        }
                    }
                }
            }
            SwingUtilities.invokeLater(
                    new ShowThumbnailsContainingKeywords(keywords));
        }
    }

    private List<Object> getSubtreeNodes() {
        TreePath[] paths = tree.getSelectionPaths();
        assert paths != null;
        if (paths != null) {
            List<Object> children = new ArrayList<Object>();
            for (TreePath path : paths) {
                Object parent = path.getLastPathComponent();
                children.addAll(TreeUtil.getAllChildren(tree.getModel(), parent));
                children.add(parent);
            }
            return children;
        }
        return null;
    }
}
