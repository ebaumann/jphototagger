package de.elmar_baumann.imv.controller.favorites;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.event.listener.RefreshListener;
import de.elmar_baumann.imv.helper.InsertImageFilesIntoDatabase;
import de.elmar_baumann.imv.io.ImageFilteredDirectory;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.InfoSettingThumbnails;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Listens for selections of items in the favorite directories tree view. A tree
 * item represents a directory. If a new item is selected, this controller sets
 * the files of the selected directory to the image file thumbnails panel.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-24
 */
public final class ControllerFavoriteSelected implements
        TreeSelectionListener, RefreshListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JTree treeFavoriteDirectories =
            appPanel.getTreeFavorites();
    private final ImageFileThumbnailsPanel thumbnailsPanel =
            appPanel.getPanelThumbnails();
    private final EditMetadataPanelsArray editPanels =
            appPanel.getEditPanelsArray();

    public ControllerFavoriteSelected() {
        listen();
    }

    private void listen() {
        treeFavoriteDirectories.getSelectionModel().addTreeSelectionListener(
                this);
        thumbnailsPanel.addRefreshListener(this, Content.FAVORITE);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (e.isAddedPath()) {
            update();
        }
    }

    @Override
    public void refresh() {
        if (treeFavoriteDirectories.getSelectionCount() > 0) {
            update();
        }
    }

    private void update() {
        SwingUtilities.invokeLater(new SetFiles());
    }

    private class SetFiles implements Runnable {

        @Override
        public void run() {
            InfoSettingThumbnails info = new InfoSettingThumbnails();
            List<File> files = getFilesOfCurrentDirectory();
            updateDatabase(files);
            thumbnailsPanel.setFiles(files, Content.FAVORITE);
            setMetadataEditable();
            info.hide();
        }

        private List<File> getFilesOfCurrentDirectory() {
            TreePath path = treeFavoriteDirectories.getSelectionPath();
            if (path != null) {
                File dir = null;
                DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode) path.getLastPathComponent();
                Object userObject = node.getUserObject();
                if (userObject instanceof FavoriteDirectory) {
                    FavoriteDirectory favoriteDirectory =
                            (FavoriteDirectory) userObject;
                    dir = favoriteDirectory.getDirectory();
                } else if (userObject instanceof File) {
                    dir = (File) userObject;
                }
                if (dir != null) {
                    return ImageFilteredDirectory.getImageFilesOfDirectory(dir);
                }
            }
            return new ArrayList<File>();
        }

        private void setMetadataEditable() {
            if (thumbnailsPanel.getSelectionCount() <= 0) {
                editPanels.setEditable(false);
            }
        }

        private void updateDatabase(List<File> files) {
            // No separate thread, it's already running in a thread
            new InsertImageFilesIntoDatabase(
                    FileUtil.getAsFilenames(files),
                    EnumSet.of(InsertImageFilesIntoDatabase.Insert.OUT_OF_DATE),
                    null).run();
        }
    }
}
