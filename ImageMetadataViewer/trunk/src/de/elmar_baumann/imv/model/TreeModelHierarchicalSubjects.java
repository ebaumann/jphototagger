package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.HierarchicalSubject;
import de.elmar_baumann.imv.database.DatabaseHierarchicalSubjects;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.dialogs.HierarchicalSubjectsDialog;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Model of data in {@link DatabaseHierarchicalSubjects}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/11
 */
public final class TreeModelHierarchicalSubjects extends DefaultTreeModel {

    private final DefaultMutableTreeNode ROOT;
    private final DatabaseHierarchicalSubjects db =
            DatabaseHierarchicalSubjects.INSTANCE;

    public TreeModelHierarchicalSubjects() {
        super(new DefaultMutableTreeNode(Bundle.getString(
                "TreeModelHierarchicalSubjects.DisplayName.Root")));
        ROOT = (DefaultMutableTreeNode) getRoot();
        createTree();
    }

    /**
     * Adds a subject to a node.
     *
     * @param parentNode parent node; the subject becomes a child
     * @param subject    subject to add
     */
    public synchronized void addSubject(
            DefaultMutableTreeNode parentNode, String subject) {
        if (!checkSubjectExists(parentNode, subject)) {
            return;
        }
        Object userObject = parentNode.getUserObject();
        boolean parentIsRoot = parentNode.equals(ROOT);
        assert parentIsRoot ||
                userObject instanceof HierarchicalSubject : parentNode;
        if (parentIsRoot || userObject instanceof HierarchicalSubject) {
            Long idParent = parentIsRoot
                    ? null
                    : ((HierarchicalSubject) userObject).getId();
            HierarchicalSubject child = new HierarchicalSubject(
                    null, idParent, subject);
            if (db.insert(child)) {
                DefaultMutableTreeNode childNode =
                        new DefaultMutableTreeNode(child);
                insertNodeInto(childNode, parentNode, parentNode.getChildCount());
                HierarchicalSubjectsDialog.INSTANCE.getPanel().getTree().
                        expandPath(new TreePath(parentNode.getPath()));
            } else {
                MessageDisplayer.error(
                        "TreeModelHierarchicalSubjects.Error.DbInsert", subject);
            }
        }
    }

    private boolean checkSubjectExists(
            DefaultMutableTreeNode parentNode, String subject) {
        if (childHasSubject(parentNode, subject)) {
            MessageDisplayer.error(
                    "TreeModelHierarchicalSubjects.Error.SubjectExists",
                    subject, parentNode);
            return false;
        }
        return true;
    }

    private boolean childHasSubject(
            DefaultMutableTreeNode parentNode, String subject) {
        for (Enumeration children = parentNode.children();
                children.hasMoreElements();) {
            Object o = children.nextElement();
            if (o instanceof DefaultMutableTreeNode) {
                Object userObject = ((DefaultMutableTreeNode) o).getUserObject();
                if (userObject instanceof HierarchicalSubject) {
                    String s =
                            ((HierarchicalSubject) userObject).getSubject();
                    if (s != null && s.equals(subject)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public synchronized void removeSubject(DefaultMutableTreeNode subjectNode) {
        List<HierarchicalSubject> delSubjects =
                new ArrayList<HierarchicalSubject>();
        Object o = subjectNode.getUserObject();
        assert o instanceof HierarchicalSubject : o;
        for (Enumeration e = subjectNode.preorderEnumeration();
                e.hasMoreElements();) {
            Object el = e.nextElement();
            assert el instanceof DefaultMutableTreeNode : el;
            if (el instanceof DefaultMutableTreeNode) {
                Object userObject =
                        ((DefaultMutableTreeNode) el).getUserObject();
                assert userObject instanceof HierarchicalSubject : userObject;
                if (userObject instanceof HierarchicalSubject) {
                    delSubjects.add((HierarchicalSubject) userObject);
                }
            }
        }
        if (db.delete(delSubjects)) {
            removeNodeFromParent(subjectNode);
        } else {
            MessageDisplayer.error(
                    "TreeModelHierarchicalSubjects.Error.DbRemove",
                    subjectNode.toString());
        }
    }

    /**
     * Notifies this model that a subject has been changed.
     *
     * Updates the database and fires that nodes were changed.
     *
     * @param node    node where the subject was changed
     * @param subject subject that was changed
     */
    public void changed(DefaultMutableTreeNode node, HierarchicalSubject subject) {
        assert node.getUserObject().equals(subject) : node.getUserObject();
        TreeNode parent = node.getParent();
        if (parent instanceof DefaultMutableTreeNode) {
            if (db.update(subject)) {
                DefaultMutableTreeNode parentNode =
                        (DefaultMutableTreeNode) parent;
                fireTreeNodesChanged(this, parentNode.getPath(),
                        new int[]{parentNode.getIndex(node)}, new Object[]{node});
            } else {
                MessageDisplayer.error(
                        "TreeModelHierarchicalSubjects.Error.DbUpdate", subject);
            }
        }
    }

    private void createTree() {
        Collection<HierarchicalSubject> roots = db.getRoots();
        for (HierarchicalSubject rootSubject : roots) {
            DefaultMutableTreeNode rootNode =
                    new DefaultMutableTreeNode(rootSubject);
            insertNodeInto(rootNode, ROOT, ROOT.getChildCount());
            insertChildren(rootNode);
        }
    }

    private void insertChildren(DefaultMutableTreeNode parentNode) {
        HierarchicalSubject parent =
                (HierarchicalSubject) parentNode.getUserObject();
        Collection<HierarchicalSubject> children =
                db.getChildren(parent.getId());
        for (HierarchicalSubject child : children) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
            insertNodeInto(childNode, parentNode, parentNode.getChildCount());
            insertChildren(childNode); // recursive
        }
    }
}
