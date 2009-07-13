package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.database.DatabaseHierarchicalKeywords;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.dialogs.HierarchicalKeywordsDialog;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Model of data in {@link DatabaseHierarchicalKeywords}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/11
 */
public final class TreeModelHierarchicalKeywords extends DefaultTreeModel {

    private final DefaultMutableTreeNode ROOT;
    private final DatabaseHierarchicalKeywords db =
            DatabaseHierarchicalKeywords.INSTANCE;

    public TreeModelHierarchicalKeywords() {
        super(new DefaultMutableTreeNode(Bundle.getString(
                "TreeModelHierarchicalKeywords.DisplayName.Root")));
        ROOT = (DefaultMutableTreeNode) getRoot();
        createTree();
    }

    /**
     * Adds a keyword to a node.
     *
     * @param parentNode parent node; the keyword becomes a child
     * @param keyword    keyword to add
     */
    public synchronized void addKeyword(
            DefaultMutableTreeNode parentNode, String keyword) {
        if (!checkKeywordExists(parentNode, keyword)) {
            return;
        }
        Object userObject = parentNode.getUserObject();
        boolean parentIsRoot = parentNode.equals(ROOT);
        assert parentIsRoot ||
                userObject instanceof HierarchicalKeyword : parentNode;
        if (parentIsRoot || userObject instanceof HierarchicalKeyword) {
            Long idParent = parentIsRoot
                    ? null
                    : ((HierarchicalKeyword) userObject).getId();
            HierarchicalKeyword child = new HierarchicalKeyword(
                    null, idParent, keyword);
            if (db.insert(child)) {
                DefaultMutableTreeNode childNode =
                        new DefaultMutableTreeNode(child);
                insertNodeInto(childNode, parentNode, parentNode.getChildCount());
                HierarchicalKeywordsDialog.INSTANCE.getPanel().getTree().
                        expandPath(new TreePath(parentNode.getPath()));
            } else {
                MessageDisplayer.error(
                        "TreeModelHierarchicalKeywords.Error.DbInsert", keyword);
            }
        }
    }

    private boolean checkKeywordExists(
            DefaultMutableTreeNode parentNode, String keyword) {
        if (childHasKeyword(parentNode, keyword)) {
            MessageDisplayer.error(
                    "TreeModelHierarchicalKeywords.Error.KeywordExists",
                    keyword, parentNode);
            return false;
        }
        return true;
    }

    private boolean childHasKeyword(
            DefaultMutableTreeNode parentNode, String keyword) {
        for (Enumeration children = parentNode.children();
                children.hasMoreElements();) {
            Object o = children.nextElement();
            if (o instanceof DefaultMutableTreeNode) {
                Object userObject = ((DefaultMutableTreeNode) o).getUserObject();
                if (userObject instanceof HierarchicalKeyword) {
                    String s =
                            ((HierarchicalKeyword) userObject).getKeyword();
                    if (s != null && s.equals(keyword)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public synchronized void removeKeyword(DefaultMutableTreeNode keywordNode) {
        List<HierarchicalKeyword> delKeywords =
                new ArrayList<HierarchicalKeyword>();
        Object o = keywordNode.getUserObject();
        assert o instanceof HierarchicalKeyword : o;
        for (Enumeration e = keywordNode.preorderEnumeration();
                e.hasMoreElements();) {
            Object el = e.nextElement();
            assert el instanceof DefaultMutableTreeNode : el;
            if (el instanceof DefaultMutableTreeNode) {
                Object userObject =
                        ((DefaultMutableTreeNode) el).getUserObject();
                assert userObject instanceof HierarchicalKeyword : userObject;
                if (userObject instanceof HierarchicalKeyword) {
                    delKeywords.add((HierarchicalKeyword) userObject);
                }
            }
        }
        if (db.delete(delKeywords)) {
            removeNodeFromParent(keywordNode);
        } else {
            MessageDisplayer.error(
                    "TreeModelHierarchicalKeywords.Error.DbRemove",
                    keywordNode.toString());
        }
    }

    /**
     * Notifies this model that a keyword has been changed.
     *
     * Updates the database and fires that nodes were changed.
     *
     * @param node    node where the keyword was changed
     * @param keyword keyword that was changed
     */
    public void changed(DefaultMutableTreeNode node, HierarchicalKeyword keyword) {
        assert node.getUserObject().equals(keyword) : node.getUserObject();
        TreeNode parent = node.getParent();
        if (parent instanceof DefaultMutableTreeNode) {
            if (db.update(keyword)) {
                DefaultMutableTreeNode parentNode =
                        (DefaultMutableTreeNode) parent;
                fireTreeNodesChanged(this, parentNode.getPath(),
                        new int[]{parentNode.getIndex(node)}, new Object[]{node});
            } else {
                MessageDisplayer.error(
                        "TreeModelHierarchicalKeywords.Error.DbUpdate", keyword);
            }
        }
    }

    private void createTree() {
        Collection<HierarchicalKeyword> roots = db.getRoots();
        for (HierarchicalKeyword rootKeyword : roots) {
            DefaultMutableTreeNode rootNode =
                    new DefaultMutableTreeNode(rootKeyword);
            insertNodeInto(rootNode, ROOT, ROOT.getChildCount());
            insertChildren(rootNode);
        }
    }

    private void insertChildren(DefaultMutableTreeNode parentNode) {
        HierarchicalKeyword parent =
                (HierarchicalKeyword) parentNode.getUserObject();
        Collection<HierarchicalKeyword> children =
                db.getChildren(parent.getId());
        for (HierarchicalKeyword child : children) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
            insertNodeInto(childNode, parentNode, parentNode.getChildCount());
            insertChildren(childNode); // recursive
        }
    }
}
