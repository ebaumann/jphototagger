package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.lib.io.DirectoryTreeModelFile;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * Aktiviert den Einfügen-Button für Daten eines Metadaten-Templates
 * beim Selektieren eines Verzeichnisses.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/22
 */
public class ControllerEnableInsertMetaDataTemplate extends Controller
    implements TreeSelectionListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JTree treeDirectories = appPanel.getTreeDirectories();
    private JButton buttonMetaDataTemplateInsert = appPanel.getButtonMetaDataTemplateInsert();

    public ControllerEnableInsertMetaDataTemplate() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        treeDirectories.addTreeSelectionListener(this);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (isControl()) {
            if (e.isAddedPath() && treeDirectories.getSelectionPath().getLastPathComponent() instanceof DirectoryTreeModelFile) {
                String directoryName = ((DirectoryTreeModelFile) treeDirectories.getSelectionPath().getLastPathComponent()).getAbsolutePath();
                File directory = new File(directoryName);
                buttonMetaDataTemplateInsert.setEnabled(directory.isDirectory() && directory.canWrite());
            }
        }
    }
}
