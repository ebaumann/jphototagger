package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.ListenerProvider;
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
public final class ControllerRenameFiles implements ActionListener, RenameFileListener {

    private final ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();
    private final DatabaseImageFiles db = DatabaseImageFiles.getInstance();

    public ControllerRenameFiles() {
        listen();
    }

    private void listen() {
        PopupMenuPanelThumbnails.getInstance().addActionListenerFileSystemRenameFiles(this);
        Panels.getInstance().getAppFrame().getMenuItemRename().addActionListener(this);
        ListenerProvider.getInstance().addRenameFileListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        renameSelectedFiles();
    }

    private void renameSelectedFiles() {
        RenameDialog dialog = new RenameDialog();
        List<File> files = thumbnailsPanel.getSelectedFiles();
        if (files.size() > 0) {
            Collections.sort(files);
            dialog.setFiles(files);
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
