package de.elmar_baumann.imv.controller.hierarchicalsubjects;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.HierarchicalSubject;
import de.elmar_baumann.imv.database.DatabaseHierarchicalSubjects;
import de.elmar_baumann.imv.model.TreeModelHierarchicalSubjects;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.dialogs.HierarchicalSubjectsDialog;
import de.elmar_baumann.imv.view.panels.HierarchicalSubjectsPanel;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
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
 * Listens to the menu item {@link HierarchicalSubjectsPanel#getMenuItemRename()}
 * and on action renames in the tree the selected hierarchical subject.
 *
 * Also listens to key events into the tree and renames the selected
 * hierarchical subject if the keys F2 or Ctrl+R were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/12
 */
public class ControllerRenameHierarchicalSubject
        implements ActionListener, KeyListener {

    private final HierarchicalSubjectsPanel panel =
            HierarchicalSubjectsDialog.INSTANCE.getPanel();
    private final DatabaseHierarchicalSubjects db =
            DatabaseHierarchicalSubjects.INSTANCE;

    public ControllerRenameHierarchicalSubject() {
        listen();
    }

    private void listen() {
        panel.getMenuItemRename().addActionListener(this);
        panel.getTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F2 ||
                KeyEventUtil.isControl(e, KeyEvent.VK_R)) {
            rename();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        rename();
    }

    private void rename() {
        JTree tree = panel.getTree();
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            MessageDisplayer.error(
                    "ControllerRenameHierarchicalSubject.Error.NoPathSelected");
        } else {
            Object node = path.getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode subjectNode =
                        (DefaultMutableTreeNode) node;
                Object userObject = subjectNode.getUserObject();
                if (userObject instanceof HierarchicalSubject) {
                    renameSubject(subjectNode, (HierarchicalSubject) userObject);
                } else {
                    MessageDisplayer.error(
                            "ControllerRenameHierarchicalSubject.Error.Node",
                            node);
                }
            }
        }
    }

    private void renameSubject(
            DefaultMutableTreeNode node, HierarchicalSubject subject) {
        TreeModel tm = panel.getTree().getModel();
        if (tm instanceof TreeModelHierarchicalSubjects) {
            String newName = getName(subject, db);
            if (newName != null && !newName.trim().isEmpty()) {
                subject.setSubject(newName);
                ((TreeModelHierarchicalSubjects) tm).changed(node, subject);
            }
        } else {
            AppLog.logWarning(ControllerRenameHierarchicalSubject.class,
                    Bundle.getString(
                    "ControllerRenameHierarchicalSubject.Error.Model"));
        }
    }

    static String getName(
            HierarchicalSubject subject, DatabaseHierarchicalSubjects database) {
        String newName = null;
        String oldName = subject.getSubject();
        boolean confirmed = true;
        while (newName == null && confirmed) {
            newName = JOptionPane.showInputDialog(Bundle.getString(
                    "ControllerRenameHierarchicalSubject.Input.Name", oldName),
                    oldName);
            confirmed = newName != null;
            if (newName != null && !newName.trim().isEmpty()) {
                HierarchicalSubject s = new HierarchicalSubject(
                        subject.getId(), subject.getIdParent(), newName.trim());
                if (database.parentHasChild(s)) {
                    newName = null;
                    confirmed = MessageDisplayer.confirm(
                            "ControllerRenameHierarchicalSubject.Confirm.Exists",
                            false, subject) == JOptionPane.YES_OPTION;
                }
            }
        }
        return newName;
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
