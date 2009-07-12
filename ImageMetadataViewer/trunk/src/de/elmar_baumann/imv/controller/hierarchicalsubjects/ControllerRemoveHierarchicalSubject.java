package de.elmar_baumann.imv.controller.hierarchicalsubjects;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.HierarchicalSubject;
import de.elmar_baumann.imv.model.TreeModelHierarchicalSubjects;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.dialogs.HierarchicalSubjectsDialog;
import de.elmar_baumann.imv.view.panels.HierarchicalSubjectsPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Listens to the menu item {@link HierarchicalSubjectsPanel#getMenuItemRemove()}
 * and on action removes from the tree the selected hierarchical subject.
 *
 * Also listens to key events into the tree and removes the selected
 * hierarchical subject if the delete key was pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/12
 */
public class ControllerRemoveHierarchicalSubject
        implements ActionListener, KeyListener {

    private final HierarchicalSubjectsPanel panel =
            HierarchicalSubjectsDialog.INSTANCE.getPanel();

    public ControllerRemoveHierarchicalSubject() {
        listen();
    }

    private void listen() {
        panel.getMenuItemRemove().addActionListener(this);
        panel.getTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            delete();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        delete();
    }

    private void delete() {
        JTree tree = panel.getTree();
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            MessageDisplayer.error(
                    "ControllerDeleteHierarchicalSubject.Error.NoPathSelected");
        } else {
            Object node = path.getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode subjectNode =
                        (DefaultMutableTreeNode) node;
                Object userObject = subjectNode.getUserObject();
                if (userObject instanceof HierarchicalSubject) {
                    delete(subjectNode, (HierarchicalSubject) userObject);
                } else {
                    MessageDisplayer.error(
                            "ControllerDeleteHierarchicalSubject.Error.Node",
                            node);
                }
            }
        }
    }

    private void delete(
            DefaultMutableTreeNode node, HierarchicalSubject subject) {
        TreeModel tm = panel.getTree().getModel();
        if (tm instanceof TreeModelHierarchicalSubjects) {
            if (MessageDisplayer.confirm(
                    "ControllerDeleteHierarchicalSubject.Confirm.Delete",
                    false, subject) == JOptionPane.YES_OPTION) {
                ((TreeModelHierarchicalSubjects) tm).removeSubject(node);
            }
        } else {
            AppLog.logWarning(ControllerRemoveHierarchicalSubject.class,
                    Bundle.getString(
                    "ControllerDeleteHierarchicalSubject.Error.Model"));
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}
