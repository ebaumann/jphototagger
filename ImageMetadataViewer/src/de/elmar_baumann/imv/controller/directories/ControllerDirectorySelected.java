package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.event.RefreshListener;
import de.elmar_baumann.imv.io.ImageFilteredDirectory;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.io.DirectoryTreeModelFile;
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
public class ControllerDirectorySelected extends Controller
    implements TreeSelectionListener, RefreshListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JTree treeDirectories = appPanel.getTreeDirectories();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();
    private ImageFilteredDirectory imageFilteredDirectory = new ImageFilteredDirectory();

    public ControllerDirectorySelected() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        treeDirectories.addTreeSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, ImageFileThumbnailsPanel.Content.Directory);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (isStarted() && e.isAddedPath()) {
            showThumbnails();
        }
    }

    @Override
    public void refresh() {
        if (isStarted()) {
            showThumbnails();
        }
    }

    private void showThumbnails() {
        File selectedDirectory = new File(getDirectorynameFromTree(treeDirectories.getSelectionPath()));
        imageFilteredDirectory.setDirectory(selectedDirectory);
        thumbnailsPanel.setFiles(ImageFilteredDirectory.getImageFilesOfDirectory(selectedDirectory), ImageFileThumbnailsPanel.Content.Directory);
    }

    private String getDirectorynameFromTree(TreePath treePath) {
        if (treePath.getLastPathComponent() instanceof DirectoryTreeModelFile) {
            return ((DirectoryTreeModelFile) treePath.getLastPathComponent()).getAbsolutePath();
        } else {
            return treePath.getLastPathComponent().toString();
        }
    }
}
