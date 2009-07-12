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
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Listens to the menu item {@link HierarchicalSubjectsPanel#getMenuItemAdd()}
 * and on action adds a new subject below the selected subject.
 *
 * Also listens to key events into the tree and adds a new subject below the
 * selected subject if the keys Ctrl+N were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/12
 */
public class ControllerAddHierarchicalSubject
        implements ActionListener, KeyListener {

    private final HierarchicalSubjectsPanel panel =
            HierarchicalSubjectsDialog.INSTANCE.getPanel();

    public ControllerAddHierarchicalSubject() {
        listen();
    }

    private void listen() {
        panel.getMenuItemAdd().addActionListener(this);
        panel.getTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isControl(e, KeyEvent.VK_N)) {
            add();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        add();
    }

    private void add() {
        JTree tree = panel.getTree();
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            MessageDisplayer.error(
                    "ControllerAddHierarchicalSubject.Error.NoPathSelected");
        } else {
            Object node = path.getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode subjectNode =
                        (DefaultMutableTreeNode) node;
                Object userObject = subjectNode.getUserObject();
                if (userObject instanceof HierarchicalSubject) {
                    add(subjectNode, (HierarchicalSubject) userObject);
                } else if (isRootNode(node)) {
                    add((DefaultMutableTreeNode) panel.getTree().getModel().
                            getRoot(), null);
                }
            }
        }
    }

    private boolean isRootNode(Object node) {
        return panel.getTree().getModel().getRoot().equals(node);
    }

    private void add(
            DefaultMutableTreeNode parentNode, HierarchicalSubject parentSubject) {
        HierarchicalSubject newSubject =
                new HierarchicalSubject(
                null, parentSubject == null
                ? null
                : parentSubject.getId(),
                Bundle.getString("ControllerAddHierarchicalSubject.DefaultName"));
        String name = ControllerRenameHierarchicalSubject.getName(newSubject,
                DatabaseHierarchicalSubjects.INSTANCE);
        if (name != null && !name.trim().isEmpty()) {
            TreeModel tm = panel.getTree().getModel();
            if (tm instanceof TreeModelHierarchicalSubjects) {
                ((TreeModelHierarchicalSubjects) tm).addSubject(parentNode, name);
            } else {
                AppLog.logWarning(ControllerAddHierarchicalSubject.class,
                        Bundle.getString(
                        "ControllerAddHierarchicalSubject.Error.Model"));
            }
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
