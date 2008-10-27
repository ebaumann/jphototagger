package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/27
 */
public class ControllerDirecotriesFileSystemActions extends Controller implements ActionListener {

    private PopupMenuTreeDirectories popup = PopupMenuTreeDirectories.getInstance();
    private JTree tree = Panels.getInstance().getAppPanel().getTreeDirectories();

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl() && tree.getSelectionCount() == 1) {
            TreePath path = tree.getSelectionPath();
            if (popup.isActionFilesystemAddDirectory(e.getSource())) {
                addDirectory(path);
            } else if (popup.isActionFilesystemDeleteDirectory(e.getSource())) {
                deleteDirectory(path);
            } else if (popup.isActionFilesystemRenameDirectory(e.getSource())) {
                renameDirectory(path);
            }
        }
    }

    private void addDirectory(TreePath path) {
        // TODO: In DB
    }

    private void deleteDirectory(TreePath path) {
        if (confirmed(path, "Ordner {0} und gesamten Inhalt darunter unwiderruflich löschen?")) {
            if (((File) path.getLastPathComponent()).delete()) {
                // TODO: In DB
            } else {
                errorMessage(path, "Ordner {0} konnte nicht gelöscht werden!");
            }
        }
    }

    private void renameDirectory(TreePath path) {
        // TODO: In DB
    }

    private boolean confirmed(TreePath path, String messageFormat) {
        MessageFormat msg = new MessageFormat(messageFormat);
        Object[] params = {path};
        return JOptionPane.showConfirmDialog(
            tree,
            msg.format(params),
            "Frage",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            AppSettings.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }

    private void errorMessage(TreePath path, String messageFormat) {
        MessageFormat msg = new MessageFormat(messageFormat);
        Object[] params = {path};
        JOptionPane.showMessageDialog(
            tree,
            msg.format(params),
            "Fehler",
            JOptionPane.ERROR_MESSAGE,
            AppSettings.getMediumAppIcon());
    }
}
