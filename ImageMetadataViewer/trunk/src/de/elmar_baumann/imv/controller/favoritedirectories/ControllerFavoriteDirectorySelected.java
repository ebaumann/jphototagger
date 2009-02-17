package de.elmar_baumann.imv.controller.favoritedirectories;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.event.RefreshListener;
import de.elmar_baumann.imv.io.ImageFilteredDirectory;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
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
public final class ControllerFavoriteDirectorySelected implements ListSelectionListener, RefreshListener {

    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final JList listFavoriteDirectories = appPanel.getListFavoriteDirectories();
    private final ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();
    private final EditMetadataPanelsArray editPanels = appPanel.getEditPanelsArray();

    public ControllerFavoriteDirectorySelected() {
        listen();
    }

    private void listen() {
        listFavoriteDirectories.addListSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.FAVORITE_DIRECTORY);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (listFavoriteDirectories.getSelectedIndex() >= 0) {
            setFilesToThumbnailsPanel();
            checkEditPanel();
        }
    }

    @Override
    public void refresh() {
        if (listFavoriteDirectories.getSelectedIndex() >= 0) {
            setFilesToThumbnailsPanel();
            checkEditPanel();
        }
    }

    private void setFilesToThumbnailsPanel() {
        if (listFavoriteDirectories.getSelectedValue() != null) {
            thumbnailsPanel.setFiles(getFilesOfCurrentDirectory(),
                Content.FAVORITE_DIRECTORY);
        }
    }

    private List<File> getFilesOfCurrentDirectory() {
        FavoriteDirectory favorite = (FavoriteDirectory) listFavoriteDirectories.getSelectedValue();
        return ImageFilteredDirectory.getImageFilesOfDirectory(new File(favorite.getDirectoryName()));
    }

    private void checkEditPanel() {
        if (thumbnailsPanel.getSelectionCount() <= 0) {
            editPanels.setEditable(false);
        }
    }
}
