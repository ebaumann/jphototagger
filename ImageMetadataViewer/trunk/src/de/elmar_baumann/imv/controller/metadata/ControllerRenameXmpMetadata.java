package de.elmar_baumann.imv.controller.metadata;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.helper.RenameXmpMetadata;
import de.elmar_baumann.imv.view.dialogs.RenameXmpMetadataDialog;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Listens to the menu item {@link AppFrame#getMenuItemRenameInXmp()} and if
 * action was performed shows the {@link RenameXmpMetadataDialog}. If
 * {@link RenameXmpMetadataDialog#accepted} is true, this controller renames
 * XMP metadata via {@link RenameXmpMetadata}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-30
 */
public final class ControllerRenameXmpMetadata implements ActionListener {

    private final ImageFileThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerRenameXmpMetadata() {
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
        RenameXmpMetadataDialog dialog = new RenameXmpMetadataDialog();
        dialog.setVisible(true);
        if (dialog.accepted()) {
            RenameXmpMetadata.update(
                    filenames,
                    dialog.getColumn(),
                    dialog.getOldString(),
                    dialog.getNewString());
        }
    }
}
