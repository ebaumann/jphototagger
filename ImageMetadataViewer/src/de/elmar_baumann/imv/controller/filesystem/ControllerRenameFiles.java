package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.event.RenameFileAction;
import de.elmar_baumann.imv.event.RenameFileListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.RenameDialog;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Renames files in the file system.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/13
 */
public class ControllerRenameFiles extends Controller
    implements ActionListener, RenameFileListener {

    private ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelImageFileThumbnails();
    private Database db = Database.getInstance();

    public ControllerRenameFiles() {
        PopupMenuPanelThumbnails.getInstance().addActionListenerFileSystemRenameFiles(this);
        Panels.getInstance().getAppFrame().getMenuItemRename().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            renameFiles();
        }
    }

    private void renameFiles() {
        RenameDialog dialog = new RenameDialog();
        List<File> files = thumbnailsPanel.getSelectedFiles();
        if (files.size() > 0) {
            Collections.sort(files);
            dialog.setFilenames(files);
            dialog.addRenameFileListener(this);
            dialog.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(RenameFileAction action) {
        db.updateRenameImageFilename(action.getOldFile().getAbsolutePath(),
            action.getNewFile().getAbsolutePath());
        thumbnailsPanel.rename(action.getOldFile(), action.getNewFile());
    }
}
