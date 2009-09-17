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
package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.HierarchicalKeyword;
import de.elmar_baumann.jpt.database.DatabaseHierarchicalKeywords;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.model.TreeNodeSortedChildren;
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
        super(new TreeNodeSortedChildren(Bundle.getString(
                "TreeModelHierarchicalKeywords.DisplayName.Root"))); // NOI18N
        ROOT = (DefaultMutableTreeNode) getRoot();
        createTree();
    }

    /**
     * Returns a child with a specific name
     * ({@link HierarchicalKeyword#getKeyword()}.
     *
     * @param  parent parent node
     * @param  name   name
     * @return        first child node with that name or null if no child has
     *                that name
     */
    public DefaultMutableTreeNode findChildByName(
            DefaultMutableTreeNode parent, String name) {
        for (Enumeration e = parent.children(); e.hasMoreElements();) {
            DefaultMutableTreeNode child =
                    (DefaultMutableTreeNode) e.nextElement();
            Object userObject = child.getUserObject();
            if (userObject instanceof HierarchicalKeyword) {
                if (((HierarchicalKeyword) userObject).getKeyword().
                        equalsIgnoreCase(name)) {
                    return child;
                }
            }
        }
        return null;
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
                insertNode(parentNode, new TreeNodeSortedChildren(child));
            } else {
                MessageDisplayer.error(null,
                        "TreeModelHierarchicalKeywords.Error.DbInsert", keyword); // NOI18N
            }
        }
    }

    private void insertNode(
            DefaultMutableTreeNode parent, DefaultMutableTreeNode child) {
        parent.add(child);
        int childIndex = parent.getIndex(child);
        fireTreeNodesInserted(this, parent.getPath(), new int[]{childIndex},
                new Object[]{child});
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
        assert o instanceof HierarchicalKeyword :
                "Not a HierarchicalKeyword: " + o; // NOI18N
        for (Enumeration e = keywordNode.preorderEnumeration();
                e.hasMoreElements();) {
            Object el = e.nextElement();
            assert el instanceof DefaultMutableTreeNode :
                    "Not a DefaultMutableTreeNode: " + el; // NOI18N
            if (el instanceof DefaultMutableTreeNode) {
                Object userObject =
                        ((DefaultMutableTreeNode) el).getUserObject();
                assert userObject instanceof HierarchicalKeyword :
                        "Not a HierarchicalKeyword: " + userObject; // NOI18N
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
                insertNode(target, source);
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
                "TreeModelHierarchicalKeywords.Error.SetIdParent", parentNode); // NOI18N
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
                    new TreeNodeSortedChildren(rootKeyword);
            insertNode(ROOT, rootNode);
            insertChildren(rootNode);
        }
    }

    private void insertChildren(DefaultMutableTreeNode parentNode) {
        HierarchicalKeyword parent =
                (HierarchicalKeyword) parentNode.getUserObject();
        Collection<HierarchicalKeyword> children =
                db.getChildren(parent.getId());
        for (HierarchicalKeyword child : children) {
            DefaultMutableTreeNode childNode = new TreeNodeSortedChildren(child);
            insertNode(parentNode, childNode);
            insertChildren(childNode); // recursive
        }
    }
}
