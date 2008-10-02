package de.elmar_baumann.imagemetadataviewer.controller.directories;

import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.io.ImageFilteredDirectory;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import de.elmar_baumann.imagemetadataviewer.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.io.DirectoryTreeModelFile;
import java.util.Collections;
import java.util.Vector;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 * Kontrolliert die Aktion: Thumbnails eines selektierten Verzeichnisses
 * anzeigen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/00/11
 */
public class ControllerShowThumbnailsOfSelectedDirectory extends Controller
    implements TreeSelectionListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JTree treeDirectories = appPanel.getTreeDirectories();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelImageFileThumbnails();
    private ImageFilteredDirectory imageFilteredDirectory = new ImageFilteredDirectory();
    private String selectedDirectory;

    public ControllerShowThumbnailsOfSelectedDirectory() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        treeDirectories.addTreeSelectionListener(this);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (isStarted() && e.isAddedPath()) {
            selectedDirectory = getDirectorynameFromTree(treeDirectories.getSelectionPath());
            imageFilteredDirectory.setDirectoryname(selectedDirectory);
            setFilenamesToThumbnailsPanel();
        }
    }

    private void setFilenamesToThumbnailsPanel() {
        thumbnailsPanel.setFilenames(getSortedFilenamesOfCurrentDirectory());
        PopupMenuPanelThumbnails.getInstance().setIsImageCollection(false);
    }

    private String getDirectorynameFromTree(TreePath treePath) {
        if (treePath.getLastPathComponent() instanceof DirectoryTreeModelFile) {
            return ((DirectoryTreeModelFile) treePath.getLastPathComponent()).getAbsolutePath();
        } else {
            return treePath.getLastPathComponent().toString();
        }
    }

    private Vector<String> getSortedFilenamesOfCurrentDirectory() {
        Vector<String> filenames =
            ImageFilteredDirectory.getImageFilenamesOfDirectory(selectedDirectory);
        Collections.sort(filenames);
        return filenames;
    }
}
