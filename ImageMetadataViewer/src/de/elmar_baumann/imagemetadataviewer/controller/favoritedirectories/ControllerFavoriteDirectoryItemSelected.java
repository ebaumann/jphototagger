package de.elmar_baumann.imagemetadataviewer.controller.favoritedirectories;

import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.data.FavoriteDirectory;
import de.elmar_baumann.imagemetadataviewer.io.ImageFilteredDirectory;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import de.elmar_baumann.imagemetadataviewer.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuPanelThumbnails;
import java.util.Collections;
import java.util.Vector;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/24
 */
public class ControllerFavoriteDirectoryItemSelected extends Controller
    implements ListSelectionListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JList listFavoriteDirectories = appPanel.getListFavoriteDirectories();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelImageFileThumbnails();

    public ControllerFavoriteDirectoryItemSelected() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        listFavoriteDirectories.addListSelectionListener(this);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (isStarted() && listFavoriteDirectories.getSelectedValue() != null) {
            showThumbnails();
        }
    }

    private void showThumbnails() {
        setFilenamesToThumbnailsPanel();
    }

    private void setFilenamesToThumbnailsPanel() {
        thumbnailsPanel.setFilenames(getSortedFilenamesOfCurrentDirectory());
        PopupMenuPanelThumbnails.getInstance().setIsImageCollection(false);
    }

    private Vector<String> getSortedFilenamesOfCurrentDirectory() {
        FavoriteDirectory favorite = (FavoriteDirectory) listFavoriteDirectories.getSelectedValue();
        Vector<String> filenames =
            ImageFilteredDirectory.getImageFilenamesOfDirectory(favorite.getDirectoryName());
        Collections.sort(filenames);
        return filenames;
    }
}
