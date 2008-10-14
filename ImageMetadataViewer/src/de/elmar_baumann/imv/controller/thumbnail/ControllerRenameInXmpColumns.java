package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.tasks.UpdaterRenameInXmpColumnsArray;
import de.elmar_baumann.imv.view.dialogs.RenameInXmpColumnsDialog;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/30
 */
public class ControllerRenameInXmpColumns extends Controller
    implements ActionListener {

    private UpdaterRenameInXmpColumnsArray updater = new UpdaterRenameInXmpColumnsArray();
    private ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelImageFileThumbnails();

    public ControllerRenameInXmpColumns() {
        PopupMenuPanelThumbnails.getInstance().addActionListenerRenameInXmpColumns(this);
    }

    @Override
    public void stop() {
        updater.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            List<String> filenames = FileUtil.getAsFilenames(
                thumbnailsPanel.getSelectedFiles());
            if (!filenames.isEmpty()) {
                rename(filenames);
            }
        }
    }

    private void rename(List<String> filenames) {
        RenameInXmpColumnsDialog dialog = new RenameInXmpColumnsDialog();
        dialog.setVisible(true);
        if (dialog.accepted()) {
            updater.update(filenames, dialog.getColumn(), dialog.getOldString(),
                dialog.getNewString());
        }
    }
}
