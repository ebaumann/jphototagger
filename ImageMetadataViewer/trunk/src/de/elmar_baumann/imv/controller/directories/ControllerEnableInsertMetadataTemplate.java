package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * Listens for selections of items in the directory tree view. A tree item
 * represents a directory. If a new item is selected, this controller enables or
 * disables the button {@link AppPanel#getButtonMetadataTemplateInsert()} 
 * depending on whether the directory is writable or not (when a directory is
 * not writable, no XMP metadata files can be written into this directory).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/22
 */
public final class ControllerEnableInsertMetadataTemplate implements
        TreeSelectionListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JTree treeDirectories = appPanel.getTreeDirectories();
    private final JButton buttonMetadataTemplateInsert =
            appPanel.getButtonMetadataTemplateInsert();

    public ControllerEnableInsertMetadataTemplate() {
        listen();
    }

    private void listen() {
        treeDirectories.addTreeSelectionListener(this);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (e.isAddedPath()) {
            setEnabledButtonInsert();
        }
    }

    private void setEnabledButtonInsert() {
        if (treeDirectories.getSelectionPath().getLastPathComponent() instanceof File) {
            String fileName = ((File) treeDirectories.getSelectionPath().
                    getLastPathComponent()).getAbsolutePath();
            File file = new File(fileName);

            buttonMetadataTemplateInsert.setEnabled(
                    file.isDirectory() && file.canWrite());
        }
    }
}
