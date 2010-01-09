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
import java.util.Arrays;
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

    private final DefaultMutableTreeNode       ROOT;
    private final DatabaseHierarchicalKeywords db  = DatabaseHierarchicalKeywords.INSTANCE;

    public TreeModelHierarchicalKeywords() {
        super(new TreeNodeSortedChildren(Bundle.getString("TreeModelHierarchicalKeywords.DisplayName.Root")));
        ROOT = (DefaultMutableTreeNode) getRoot();
        createTree();
    }

    /**
     * Returns a child with a specific name ({@link HierarchicalKeyword#getName()}.
     *
     * @param  parent parent node
     * @param  name   name
     * @return        first child node with that name or null if no child has
     *                that name
     */
    public DefaultMutableTreeNode findChildByName(DefaultMutableTreeNode parent, String name) {

        for (Enumeration e = parent.children(); e.hasMoreElements();) {

            DefaultMutableTreeNode child      = (DefaultMutableTreeNode) e.nextElement();
            Object                 userObject = child.getUserObject();

            if (userObject instanceof HierarchicalKeyword) {
                if (((HierarchicalKeyword) userObject).getName().equalsIgnoreCase(name)) {
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
     * @param real       true if the keyword is a real keyword
     */
    public synchronized void addKeyword(
            DefaultMutableTreeNode parentNode,
            String                 keyword,
            boolean                real
            ) {
        if (!ensureIsNotChild(parentNode, keyword)) return;

        Object  userObject   = parentNode.getUserObject();
        boolean parentIsRoot = parentNode.equals(ROOT);

        assert parentIsRoot || userObject instanceof HierarchicalKeyword : parentNode;

        if (parentIsRoot || userObject instanceof HierarchicalKeyword) {
            Long idParent = parentIsRoot
                    ? null
                    : ((HierarchicalKeyword) userObject).getId();
            HierarchicalKeyword child =
                    new HierarchicalKeyword(null, idParent, keyword, real);
            if (db.insert(child)) {
                insertNode(parentNode, new TreeNodeSortedChildren(child));
            } else {
                MessageDisplayer.error(null, "TreeModelHierarchicalKeywords.Error.DbInsert", keyword);
            }
        }
    }

    public synchronized void copySubtree(
            DefaultMutableTreeNode source,
            DefaultMutableTreeNode target
            ) {
        if (!ensureIsNotChild(target, source.getUserObject().toString()) ||
            ! ensureTargetIsNotBelowSource(source, target)) return;
        cpySubtree(source, target);
    }

    private synchronized void cpySubtree(
            DefaultMutableTreeNode source,
            DefaultMutableTreeNode target
            ) {
        DefaultMutableTreeNode newTarget = deepCopy(source, target);
        for (Enumeration e = source.children(); e.hasMoreElements(); ) {
            cpySubtree((DefaultMutableTreeNode) e.nextElement(), newTarget); // Recursive
        }
    }

    private synchronized DefaultMutableTreeNode deepCopy(
            DefaultMutableTreeNode source,
            DefaultMutableTreeNode target
            ) {
        HierarchicalKeyword srcKeyword    = (HierarchicalKeyword) source.getUserObject();
        HierarchicalKeyword targetKeyword = (HierarchicalKeyword) target.getUserObject();
        HierarchicalKeyword keyword       = new HierarchicalKeyword(null, targetKeyword.getId(), srcKeyword.getName(), srcKeyword.isReal());

        if (db.insert(keyword)) {
            DefaultMutableTreeNode node = new TreeNodeSortedChildren(keyword);
            target.add(node);
            fireTreeNodesInserted(this, target.getPath(), new int[]{target.getIndex(node)}, new Object[]{node});
            return node;
        } else {
            MessageDisplayer.error(null, "TreeModelHierarchicalKeywords.Error.DbCopy", keyword.getName(), targetKeyword.getName());
        }
        return null;
    }

    private void insertNode(DefaultMutableTreeNode parent, DefaultMutableTreeNode child) {
        parent.add(child);
        int childIndex = parent.getIndex(child);
        fireTreeNodesInserted(this, parent.getPath(), new int[]{childIndex}, new Object[]{child});
    }

    private boolean ensureIsNotChild(DefaultMutableTreeNode parentNode, String keyword) {
        if (childHasKeyword(parentNode, keyword)) {
            MessageDisplayer.error(null, "TreeModelHierarchicalKeywords.Error.KeywordExists", keyword, parentNode);
            return false;
        }
        return true;
    }

    private boolean ensureTargetIsNotBelowSource(
            DefaultMutableTreeNode source, DefaultMutableTreeNode target) {

        boolean isBelow = TreeUtil.isAbove(source, target);
        if  (isBelow) {
            MessageDisplayer.error(null, "TreeModelHierarchicalKeywords.Error.TargetBelowSource");
            return false;
        }
        return true;
    }

    private boolean childHasKeyword(DefaultMutableTreeNode parentNode, String keyword) {
        for (Enumeration children = parentNode.children(); children.hasMoreElements();) {
            Object o = children.nextElement();
            if (o instanceof DefaultMutableTreeNode) {
                Object userObject = ((DefaultMutableTreeNode) o).getUserObject();
                if (userObject instanceof HierarchicalKeyword) {
                    String s = ((HierarchicalKeyword) userObject).getName();
                    if (s != null && s.equals(keyword)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public synchronized void removeKeyword(DefaultMutableTreeNode keywordNode) {

        List<HierarchicalKeyword> delKeywords = new ArrayList<HierarchicalKeyword>();
        Object                    o           = keywordNode.getUserObject();

        assert o instanceof HierarchicalKeyword : "Not a HierarchicalKeyword: " + o;

        for (Enumeration e = keywordNode.preorderEnumeration(); e.hasMoreElements();) {
            Object el = e.nextElement(); assert el instanceof DefaultMutableTreeNode : el;
            if (el instanceof DefaultMutableTreeNode) {
                Object userObject = ((DefaultMutableTreeNode) el).getUserObject();

                assert userObject instanceof HierarchicalKeyword : userObject;

                if (userObject instanceof HierarchicalKeyword) {
                    delKeywords.add((HierarchicalKeyword) userObject);
                }
            }
        }
        if (db.delete(delKeywords)) {
            removeNodeFromParent(keywordNode);
        } else {
            MessageDisplayer.error(null, "TreeModelHierarchicalKeywords.Error.DbRemove", keywordNode.toString());
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
                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parent;
                fireTreeNodesChanged(this, parentNode.getPath(), new int[]{parentNode.getIndex(node)}, new Object[]{node});
            } else {
                MessageDisplayer.error(null, "TreeModelHierarchicalKeywords.Error.DbUpdate", keyword);
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
    public void move(
            DefaultMutableTreeNode source,
            DefaultMutableTreeNode target,
            HierarchicalKeyword    keyword
            ) {
        if (ensureIsNotChild(target, keyword.getName()) &&
            ensureTargetIsNotBelowSource(source, target) &&
            setIdParent(keyword, target)) {

            if (db.update(keyword)) {
                DefaultMutableTreeNode removeNode =
                        TreeUtil.findNodeWithUserObject(ROOT, source.getUserObject());

                if (removeNode != null) {
                    removeNodeFromParent(removeNode);
                }
                insertNode(target, source);
            }
        }
    }

    private boolean setIdParent(HierarchicalKeyword keyword, DefaultMutableTreeNode parentNode) {
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
        AppLog.logWarning(TreeModelHierarchicalKeywords.class, "TreeModelHierarchicalKeywords.Error.SetIdParent", parentNode);
        return false;
    }

    private void createTree() {
        Collection<HierarchicalKeyword> roots = db.getRoots();
        for (HierarchicalKeyword rootKeyword : roots) {
            DefaultMutableTreeNode rootNode = new TreeNodeSortedChildren(rootKeyword);
            insertNode(ROOT, rootNode);
            insertChildren(rootNode);
        }
    }

    private void insertChildren(DefaultMutableTreeNode parentNode) {
        HierarchicalKeyword parent = (HierarchicalKeyword) parentNode.getUserObject();
        Collection<HierarchicalKeyword> children = db.getChildren(parent.getId());
        for (HierarchicalKeyword child : children) {
            DefaultMutableTreeNode childNode = new TreeNodeSortedChildren(child);
            insertNode(parentNode, childNode);
            insertChildren(childNode); // recursive
        }
    }

    /**
     * Sets all real keywords with a specific name to a different name.
     * <p>
     * Renames the keywords in the database, does not touch the sidecar files.
     *
     * @param oldName old name
     * @param newName new name
     */
    public synchronized void setAllRenamed(String oldName, String newName) {
        assert !newName.equalsIgnoreCase(oldName) : oldName;
        if (db.updateRenameAll(oldName, newName) > 0) {
            for (Enumeration e = ROOT.depthFirstEnumeration(); e.hasMoreElements(); ) {
                DefaultMutableTreeNode node       = (DefaultMutableTreeNode) e.nextElement();
                Object                 userObject = node.getUserObject();

                if (userObject instanceof HierarchicalKeyword) {
                    HierarchicalKeyword kw = (HierarchicalKeyword) userObject;
                    if (kw.isReal() && kw.getName().equalsIgnoreCase(oldName)) {
                        kw.setKeyword(newName);
                    }
                }
            }
        }
    }

    /**
     * Removes a real keyword with a specific name if it has no children.
     * <p>
     * Also updates the database; does not change the sidecar files.
     *
     * @param name keyword name.
     */
    public synchronized void removeRootItemWithoutChildren(String name) {
        for (Enumeration e = ROOT.children(); e.hasMoreElements(); ) {
            DefaultMutableTreeNode node       = (DefaultMutableTreeNode) e.nextElement();
            Object                 userObject = node.getUserObject();

            if (node.getChildCount() <= 0 && userObject instanceof HierarchicalKeyword) {
                HierarchicalKeyword kw = (HierarchicalKeyword) userObject;
                if (kw.isReal() && kw.getName().equalsIgnoreCase(name) &&
                        db.delete(Arrays.asList(kw))) {
                    int index = ROOT.getIndex(node);
                    ROOT.remove(node);
                    fireTreeNodesRemoved(this, ROOT.getPath(), new int[]{index}, new Object[]{node});
                }
            }
        }
    }
}
