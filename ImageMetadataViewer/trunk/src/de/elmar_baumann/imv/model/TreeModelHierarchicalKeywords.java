package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.database.DatabaseHierarchicalKeywords;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 * Model of data in {@link DatabaseHierarchicalKeywords}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-11
 */
public final class TreeModelHierarchicalKeywords extends DefaultTreeModel {

    private final DefaultMutableTreeNode ROOT;
    private final DatabaseHierarchicalKeywords db =
            DatabaseHierarchicalKeywords.INSTANCE;

    public TreeModelHierarchicalKeywords() {
        super(new DefaultMutableTreeNode(Bundle.getString(
                "TreeModelHierarchicalKeywords.DisplayName.Root"))); // NOI18N
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
                    null, idParent, keyword, true);
            if (db.insert(child)) {
                DefaultMutableTreeNode childNode =
                        new DefaultMutableTreeNode(child);
                insertNodeInto(childNode, parentNode, parentNode.getChildCount());
            } else {
                MessageDisplayer.error(null,
                        "TreeModelHierarchicalKeywords.Error.DbInsert", keyword); // NOI18N
            }
        }
    }

    private boolean checkKeywordExists(
            DefaultMutableTreeNode parentNode, String keyword) {
        if (childHasKeyword(parentNode, keyword)) {
            MessageDisplayer.error(null,
                    "TreeModelHierarchicalKeywords.Error.KeywordExists", // NOI18N
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
            MessageDisplayer.error(null,
                    "TreeModelHierarchicalKeywords.Error.DbRemove", // NOI18N
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
                MessageDisplayer.error(null,
                        "TreeModelHierarchicalKeywords.Error.DbUpdate", keyword); // NOI18N
            }
        }
    }

    /**
     * Moves a node to another node.
     * 
     * @param source  node to move
     * @param target  new parent of <code>source</code>
     * @param keyword keyword of <code>source</code>
     */
    public void move(DefaultMutableTreeNode source,
            DefaultMutableTreeNode target, HierarchicalKeyword keyword) {
        if (checkIsBelow(source, target) && checkIsChild(target, source) &&
                setIdParent(keyword, target)) {
            if (db.update(keyword)) {
                DefaultMutableTreeNode removeNode =
                        TreeUtil.findNodeWithUserObject(
                        ROOT, source.getUserObject());
                if (removeNode != null) {
                    removeNodeFromParent(removeNode);
                }
                insertNodeInto(source, target, target.getChildCount());
            }
        }
    }

    private boolean setIdParent(
            HierarchicalKeyword keyword, DefaultMutableTreeNode parentNode) {
        if (parentNode.equals(ROOT)) {
            keyword.setIdParent(null);
            return true;
        } else {
            Object userObject = parentNode.getUserObject();
            if (userObject instanceof HierarchicalKeyword) {
                keyword.setIdParent(((HierarchicalKeyword) userObject).getId());
                return true;
            }
        }
        AppLog.logWarning(TreeModelHierarchicalKeywords.class,
                Bundle.getString(
                "TreeModelHierarchicalKeywords.Error.SetIdParent", parentNode)); // NOI18N
        return false;
    }

    private boolean checkIsBelow(
            DefaultMutableTreeNode source, DefaultMutableTreeNode target) {
        if (TreeUtil.isBelow(source, target)) {
            MessageDisplayer.error(null,
                    "TreeModelHierarchicalKeywords.Error.Move.NodeIsBelow", // NOI18N
                    source, target);
            return false;
        }
        return true;
    }

    private boolean checkIsChild(
            DefaultMutableTreeNode target, DefaultMutableTreeNode source) {
        if (TreeUtil.isChild(target, source)) {
            MessageDisplayer.error(null,
                    "TreeModelHierarchicalKeywords.Error.Move.NodeIsChild", // NOI18N
                    source, target);
            return false;
        }
        return true;
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
