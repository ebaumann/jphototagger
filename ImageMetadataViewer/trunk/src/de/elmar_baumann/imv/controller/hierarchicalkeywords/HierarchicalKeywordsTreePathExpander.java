package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.InputHelperDialog;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Expands the path of all {@link HierarchicalKeywordsPanel}s trees.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-31
 */
public final class HierarchicalKeywordsTreePathExpander {

    public static void expand(DefaultMutableTreeNode node) {
        TreePath path = new TreePath(node.getPath());
        GUI.INSTANCE.getAppPanel().getTreeHierarchicalKeywords().expandPath(path);
        InputHelperDialog.INSTANCE.getPanelKeywords().getTree().expandPath(path);
    }

    private HierarchicalKeywordsTreePathExpander() {
    }
}
