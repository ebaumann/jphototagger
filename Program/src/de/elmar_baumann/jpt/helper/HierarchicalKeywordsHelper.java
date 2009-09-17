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
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.data.HierarchicalKeyword;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.EditMetadataPanelsArray;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

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
                    "Not a DefaultMutableTreeNode: " + parent; // NOI18N
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

    /**
     * Selects in {@link AppPanel#getTreeSelHierarchicalKeywords()} a node with
     * a specific hierarchical keyword.
     *
     * @param tree    tree with {@link TreeModelHierarchicalKeywords} and all
     *                nodes of the type {@link DefaultMutableTreeNode}
     * @param keyword keyword to select
     */
    public static void selectNode(JTree tree, HierarchicalKeyword keyword) {
        TreeModelHierarchicalKeywords model =
                (TreeModelHierarchicalKeywords) tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        DefaultMutableTreeNode selNode = null;
        for (Enumeration e = root.breadthFirstEnumeration();
                selNode == null && e.hasMoreElements();) {
            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode) e.nextElement();
            Object userObject = node.getUserObject();
            if (userObject instanceof HierarchicalKeyword) {
                HierarchicalKeyword hkw = (HierarchicalKeyword) userObject;
                if (hkw.equals(keyword)) {
                    selNode = node;
                }
            }
        }
        if (selNode != null) {
            tree.setSelectionPath(new TreePath(selNode.getPath()));
        }
    }

    private HierarchicalKeywordsHelper() {
    }
}
