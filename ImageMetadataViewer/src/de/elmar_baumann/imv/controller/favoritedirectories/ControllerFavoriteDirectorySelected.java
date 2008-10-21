package de.elmar_baumann.imv.controller.favoritedirectories;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.event.RefreshListener;
import de.elmar_baumann.imv.io.ImageFilteredDirectory;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.Content;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import java.io.File;
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
public class ControllerFavoriteDirectorySelected extends Controller
    implements ListSelectionListener, RefreshListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JList listFavoriteDirectories = appPanel.getListFavoriteDirectories();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();

    public ControllerFavoriteDirectorySelected() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        listFavoriteDirectories.addListSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.FavoriteDirectory);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        showThumbnails();
    }

    @Override
    public void refresh() {
        showThumbnails();
    }

    private void showThumbnails() {
        if (isControl() && listFavoriteDirectories.getSelectedValue() != null) {
            thumbnailsPanel.setFiles(getFilesOfCurrentDirectory(),
                Content.FavoriteDirectory);
        }
    }

    private List<File> getFilesOfCurrentDirectory() {
        FavoriteDirectory favorite = (FavoriteDirectory) listFavoriteDirectories.getSelectedValue();
        return ImageFilteredDirectory.getImageFilesOfDirectory(new File(favorite.getDirectoryName()));
    }
}
