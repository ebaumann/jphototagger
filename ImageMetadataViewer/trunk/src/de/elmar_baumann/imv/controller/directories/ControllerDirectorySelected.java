package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.event.RefreshListener;
import de.elmar_baumann.imv.io.ImageFilteredDirectory;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.types.Content;
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
public final class ControllerDirectorySelected extends Controller
    implements TreeSelectionListener, RefreshListener {

    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final JTree treeDirectories = appPanel.getTreeDirectories();
    private final EditMetadataPanelsArray editPanels = appPanel.getEditPanelsArray();
    private final ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();
    private final ImageFilteredDirectory imageFilteredDirectory = new ImageFilteredDirectory();

    public ControllerDirectorySelected() {
        listenToActionSources();
    }

    private void listenToActionSources() {
        treeDirectories.addTreeSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.DIRECTORY);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (isControl() && e.isAddedPath() && !PopupMenuTreeDirectories.getInstance().isTreeSelected()) {
            setFilesToThumbnailsPanel();
            checkEditPanel();
        }
    }

    @Override
    public void refresh() {
        if (isControl() && treeDirectories.getSelectionCount() > 0) {
            setFilesToThumbnailsPanel();
            checkEditPanel();
        }
    }

    private void setFilesToThumbnailsPanel() {
        File selectedDirectory = new File(
            getDirectorynameFromTree(treeDirectories.getSelectionPath()));
        imageFilteredDirectory.setDirectory(selectedDirectory);
        thumbnailsPanel.setFiles(
            ImageFilteredDirectory.getImageFilesOfDirectory(selectedDirectory),
            Content.DIRECTORY);
    }

    private String getDirectorynameFromTree(TreePath treePath) {
        if (treePath.getLastPathComponent() instanceof File) {
            return ((File) treePath.getLastPathComponent()).getAbsolutePath();
        } else {
            return treePath.getLastPathComponent().toString();
        }
    }

    private void checkEditPanel() {
        if (thumbnailsPanel.getSelectionCount() <= 0) {
            editPanels.setEditable(false);
        }
    }
}
