package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.controller.keywords.ShowThumbnailsContainingAllKeywords2;
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
            SwingUtilities.invokeLater(
                    new ShowThumbnailsContainingAllKeywords2(getKeywordPaths()));
    }

    private List<List<String>> getKeywordPaths() {
        List<List<String>> keywordPaths = new ArrayList<List<String>>();
        List<List<HierarchicalKeyword>> hkwp =
                getKeywordPaths(getSubtreePaths(getSelectedTreeNodes()));
        for (List<HierarchicalKeyword> kws : hkwp) {
            List<String> stringKeywords = new ArrayList<String>();
            for (HierarchicalKeyword kw : kws) {
                stringKeywords.add(kw.getKeyword());
            }
            keywordPaths.add(stringKeywords);
        }
        return keywordPaths;
    }

    private List<List<HierarchicalKeyword>> getKeywordPaths(
            List<List<DefaultMutableTreeNode>> nodePaths) {
        List<List<HierarchicalKeyword>> paths =
                new ArrayList<List<HierarchicalKeyword>>();
        for (List<DefaultMutableTreeNode> nodePath : nodePaths) {
            List<HierarchicalKeyword> keywordPath =
                    new ArrayList<HierarchicalKeyword>();
            for (DefaultMutableTreeNode node : nodePath) {
                Object userObject = node.getUserObject();
                assert userObject instanceof HierarchicalKeyword;
                if (userObject instanceof HierarchicalKeyword) {
                    HierarchicalKeyword hk = (HierarchicalKeyword) userObject;
                    if (hk.isReal()) {
                        keywordPath.add(hk);
                    }
                }
            }
            paths.add(keywordPath);
        }
        return paths;
    }
    
    private List<List<DefaultMutableTreeNode>> getSubtreePaths(
            List<DefaultMutableTreeNode> selectedNodes) {
        List<List<DefaultMutableTreeNode>> paths =
                new ArrayList<List<DefaultMutableTreeNode>>();
        for (DefaultMutableTreeNode selectedNode : selectedNodes) {
            paths.addAll(TreeUtil.getSubtreePaths(selectedNode));
        }
        return paths;
    }

    private List<DefaultMutableTreeNode> getSelectedTreeNodes() {
        TreePath[] selPaths = tree.getSelectionPaths();
        List<DefaultMutableTreeNode> selNodes =
                new ArrayList<DefaultMutableTreeNode>();
        assert selPaths != null;
        if (selPaths != null) {
            for (TreePath path : selPaths) {
                Object selNode = path.getLastPathComponent();
                assert selNode instanceof DefaultMutableTreeNode : selNode;
                if (selNode instanceof DefaultMutableTreeNode) {
                    selNodes.add((DefaultMutableTreeNode) selNode);
                }
            }
        }
        return selNodes;
    }
}
