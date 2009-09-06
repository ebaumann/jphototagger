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
                new ShowThumbnailsContainingAllKeywords2(getKeywordStringPaths()));
    }

    private List<List<String>> getKeywordStringPaths() {
        List<List<String>> keywordPaths = new ArrayList<List<String>>();
        List<List<HierarchicalKeyword>> hkwp = getKeywordPaths();
        for (List<HierarchicalKeyword> kws : hkwp) {
            List<String> stringKeywords = new ArrayList<String>();
            for (HierarchicalKeyword kw : kws) {
                stringKeywords.add(kw.getKeyword());
            }
            keywordPaths.add(stringKeywords);
        }
        return keywordPaths;
    }

    private List<List<HierarchicalKeyword>> getKeywordPaths() {
        TreePath[] selPaths = tree.getSelectionPaths();
        List<List<HierarchicalKeyword>> paths =
                new ArrayList<List<HierarchicalKeyword>>();
        for (TreePath selPath : selPaths) {
            DefaultMutableTreeNode selNode =
                    (DefaultMutableTreeNode) selPath.getLastPathComponent();
            List<HierarchicalKeyword> kwPath =
                    new ArrayList<HierarchicalKeyword>();
            for (Object userObject : selNode.getUserObjectPath()) {
                if (userObject instanceof HierarchicalKeyword) {
                    HierarchicalKeyword kw = (HierarchicalKeyword) userObject;
                    if (kw.isReal()) {
                        kwPath.add(kw);
                    }
                }
            }
            paths.add(kwPath);
        }
        return paths;
    }
}
