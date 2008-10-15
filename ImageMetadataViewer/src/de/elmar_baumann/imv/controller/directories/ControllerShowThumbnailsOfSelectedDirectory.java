package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.io.ImageFilteredDirectory;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.io.DirectoryTreeModelFile;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.util.Collections;
import java.util.List;
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
public class ControllerShowThumbnailsOfSelectedDirectory extends Controller
    implements TreeSelectionListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JTree treeDirectories = appPanel.getTreeDirectories();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelImageFileThumbnails();
    private ImageFilteredDirectory imageFilteredDirectory = new ImageFilteredDirectory();

    public ControllerShowThumbnailsOfSelectedDirectory() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        treeDirectories.addTreeSelectionListener(this);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (isStarted() && e.isAddedPath()) {
            File selectedDirectory = new File(getDirectorynameFromTree(treeDirectories.getSelectionPath()));
            imageFilteredDirectory.setDirectory(selectedDirectory);
            thumbnailsPanel.setFiles(ImageFilteredDirectory.getImageFilesOfDirectory(selectedDirectory));
            PopupMenuPanelThumbnails.getInstance().setIsImageCollection(false);
        }
    }

    private String getDirectorynameFromTree(TreePath treePath) {
        if (treePath.getLastPathComponent() instanceof DirectoryTreeModelFile) {
            return ((DirectoryTreeModelFile) treePath.getLastPathComponent()).getAbsolutePath();
        } else {
            return treePath.getLastPathComponent().toString();
        }
    }
}
