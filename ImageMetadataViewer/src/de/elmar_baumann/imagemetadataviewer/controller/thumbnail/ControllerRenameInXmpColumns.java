package de.elmar_baumann.imagemetadataviewer.controller.thumbnail;

import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.tasks.UpdaterRenameInXmpColumnsArray;
import de.elmar_baumann.imagemetadataviewer.view.dialogs.RenameInXmpColumnsDialog;
import de.elmar_baumann.imagemetadataviewer.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuPanelThumbnails;
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
            List<String> filenames = thumbnailsPanel.getSelectedFilenames();
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
