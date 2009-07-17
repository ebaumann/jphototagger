package de.elmar_baumann.imv.controller.metadata;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.tasks.UpdaterRenameInXmpColumnsArray;
import de.elmar_baumann.imv.view.dialogs.RenameInXmpColumnsDialog;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Listens to the menu item {@link AppFrame#getMenuItemRenameInXmp()} and if
 * action was performed shows the {@link RenameInXmpColumnsDialog}. If
 * {@link RenameInXmpColumnsDialog#accepted} is true, this controller renames
 * XMP metadata via {@link UpdaterRenameInXmpColumnsArray}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/30
 */
public final class ControllerRenameInXmpColumns implements ActionListener {

    private final UpdaterRenameInXmpColumnsArray updater =
            new UpdaterRenameInXmpColumnsArray();
    private final ImageFileThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerRenameInXmpColumns() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemRenameInXmp().addActionListener(
                this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        renameSelectedThumbnails();
    }

    private void renameSelectedThumbnails() {
        List<String> filenames =
                FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles());
        if (!filenames.isEmpty()) {
            renameFiles(filenames);
        }
    }

    private void renameFiles(List<String> filenames) {
        RenameInXmpColumnsDialog dialog = new RenameInXmpColumnsDialog();
        dialog.setVisible(true);
        if (dialog.accepted()) {
            updater.update(filenames, dialog.getColumn(), dialog.getOldString(),
                    dialog.getNewString());
        }
    }
}
