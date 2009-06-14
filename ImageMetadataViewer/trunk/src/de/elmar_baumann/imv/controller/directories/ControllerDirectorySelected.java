package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.event.RefreshListener;
import de.elmar_baumann.imv.io.ImageFilteredDirectory;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.InfoSetThumbnails;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeDirectories;
import java.io.File;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 * Kontrolliert die Aktion: Thumbnails eines selektierten Verzeichnisses
 * anzeigen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerDirectorySelected implements TreeSelectionListener,
                                                          RefreshListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JTree treeDirectories = appPanel.getTreeDirectories();
    private final EditMetadataPanelsArray editPanels = appPanel.
            getEditPanelsArray();
    private final ImageFileThumbnailsPanel thumbnailsPanel = appPanel.
            getPanelThumbnails();
    private final ImageFilteredDirectory imageFilteredDirectory =
            new ImageFilteredDirectory();

    public ControllerDirectorySelected() {
        listen();
    }

    private void listen() {
        treeDirectories.addTreeSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.DIRECTORY);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (e.isAddedPath() &&
                !PopupMenuTreeDirectories.INSTANCE.isTreeSelected()) {
            setFilesToThumbnailsPanel();
        }
    }

    @Override
    public void refresh() {
        setFilesToThumbnailsPanel();
    }

    private void setFilesToThumbnailsPanel() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                if (treeDirectories.getSelectionCount() > 0) {
                    InfoSetThumbnails info = new InfoSetThumbnails();
                    File selectedDirectory =
                            new File(getDirectorynameFromTree());
                    imageFilteredDirectory.setDirectory(selectedDirectory);
                    thumbnailsPanel.setFiles(
                            ImageFilteredDirectory.getImageFilesOfDirectory(
                            selectedDirectory),
                            Content.DIRECTORY);
                    setMetadataEditable();
                    info.hide();
                }
            }
        });
        thread.setName("Directory selected" + " @ " + getClass().getName()); // NOI18N
        thread.start();
    }

    private String getDirectorynameFromTree() {
        TreePath treePath = treeDirectories.getSelectionPath();
        if (treePath.getLastPathComponent() instanceof File) {
            return ((File) treePath.getLastPathComponent()).getAbsolutePath();
        } else {
            return treePath.getLastPathComponent().toString();
        }
    }

    private void setMetadataEditable() {
        if (thumbnailsPanel.getSelectionCount() <= 0) {
            editPanels.setEditable(false);
        }
    }
}
