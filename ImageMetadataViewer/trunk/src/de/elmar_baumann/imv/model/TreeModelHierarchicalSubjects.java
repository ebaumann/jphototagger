package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.data.HierarchicalSubject;
import de.elmar_baumann.imv.database.DatabaseHierarchicalSubjects;
import de.elmar_baumann.imv.resource.Bundle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

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
        super(new DefaultMutableTreeNode("Hierarchical Subjects"));
        ROOT = (DefaultMutableTreeNode) getRoot();
        createTree();
    }

    /**
     * Adds a subject to a node.
     *
     * @param parentNode parent node; the subject becomes a child
     * @param subject    subject to add
     */
    public synchronized void addSubject(DefaultMutableTreeNode parentNode,
            String subject) {
        Object userObject = parentNode.getUserObject();
        assert userObject instanceof HierarchicalSubject : userObject;
        if (userObject instanceof HierarchicalSubject) {
            HierarchicalSubject child = new HierarchicalSubject(
                    null, ((HierarchicalSubject) userObject).getId(), subject);
            if (db.insert(child)) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(
                        child);
                insertNodeInto(childNode, parentNode, parentNode.getChildCount());
            } else {
                errorMessageDbInsert(subject);
            }
        }
    }

    public synchronized void removeSubject(DefaultMutableTreeNode subjectNode) {
        List<HierarchicalSubject> delSubjects =
                new ArrayList<HierarchicalSubject>();
        Object o = subjectNode.getUserObject();
        assert o instanceof HierarchicalSubject : o;
        if (o instanceof HierarchicalSubject) {
            delSubjects.add((HierarchicalSubject) o);
        }
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
            errorMessageDbRemove(subjectNode.toString());
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

    private void errorMessageDbRemove(String subject) {
        JOptionPane.showMessageDialog(null,
                Bundle.getString(
                "TreeModelHierarchicalSubjects.Error.DbRemove", subject),
                Bundle.getString(
                "TreeModelHierarchicalSubjects.Error.DbRemove.Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    private void errorMessageDbInsert(String subject) {
        JOptionPane.showMessageDialog(null,
                Bundle.getString(
                "TreeModelHierarchicalSubjects.Error.DbInsert", subject),
                Bundle.getString(
                "TreeModelHierarchicalSubjects.Error.DbInsert.Title"),
                JOptionPane.ERROR_MESSAGE);
    }
}
