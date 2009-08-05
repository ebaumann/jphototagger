package de.elmar_baumann.imv.helper;

import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Helps with hierarchical keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-05
 */
public final class HierarchicalKeywordsHelper {

    /**
     * Adds the keyword - contained as user object in a d.m. tree node -
     * and all it's parents to the metadata edit panel.
     *
     * @param node node with hierarchical keyword. <em>All parents of that
     *             node have to be an instance of
     *             {@link DefaultMutableTreeNode}!</em>
     */
    public static void addKeywordsToEditPanel(DefaultMutableTreeNode node) {
        EditMetadataPanelsArray editPanels =
                GUI.INSTANCE.getAppPanel().getMetadataEditPanelsArray();
        for (String keyword : getKeywordStrings(node, true)) {
            editPanels.addText(ColumnXmpDcSubjectsSubject.INSTANCE, keyword);
        }
    }

    /**
     * Returns a keyword - contained as user object in a default mutable tree
     * node - and all it's parents.
     *
     * @param node node with hierarchical keyword. <em>All parents of that
     *             node have to be instances of {@link DefaultMutableTreeNode}!</em>
     * @param real true if only real keywords shall be added
     * @return     all keywords
     */
    public static List<HierarchicalKeyword> getKeywords(
            DefaultMutableTreeNode node, boolean real) {
        List<HierarchicalKeyword> list = new ArrayList<HierarchicalKeyword>();
        while (node != null) {
            Object userObject = node.getUserObject();
            if (userObject instanceof HierarchicalKeyword) {
                HierarchicalKeyword keyword = (HierarchicalKeyword) userObject;
                if (!real || real && keyword.isReal()) {
                    list.add(keyword);
                }
            }
            TreeNode parent = node.getParent();
            assert parent == null || parent instanceof DefaultMutableTreeNode :
                    "Parent is not a DefaultMutableTreeNode: " + parent;
            node = parent instanceof DefaultMutableTreeNode
                   ? (DefaultMutableTreeNode) parent
                   : null;
        }
        return list;
    }

    /**
     * Returns a keyword - contained as user object in a default mutable tree
     * node - and all it's parents as a list of strings.
     *
     * @param node node with hierarchical keyword. <em>All parents of that
     *             node have to be instances of {@link DefaultMutableTreeNode}!</em>
     * @param real true if only real keywords shall be added
     * @return     all keywords as strings
     */
    public static List<String> getKeywordStrings(
            DefaultMutableTreeNode node, boolean real) {
        List<String> list = new ArrayList<String>();
        for (HierarchicalKeyword keyword : getKeywords(node, real)) {
            list.add(keyword.getKeyword());
        }
        return list;
    }

    private HierarchicalKeywordsHelper() {
    }
}
