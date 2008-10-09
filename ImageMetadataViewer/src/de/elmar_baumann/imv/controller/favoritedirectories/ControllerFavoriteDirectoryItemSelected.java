package de.elmar_baumann.imv.controller.favoritedirectories;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.io.ImageFilteredDirectory;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    private List<String> getSortedFilenamesOfCurrentDirectory() {
        FavoriteDirectory favorite = (FavoriteDirectory) listFavoriteDirectories.getSelectedValue();
        List<String> filenames =
            ImageFilteredDirectory.getImageFilenamesOfDirectory(favorite.getDirectoryName());
        Collections.sort(filenames);
        return filenames;
    }
}
