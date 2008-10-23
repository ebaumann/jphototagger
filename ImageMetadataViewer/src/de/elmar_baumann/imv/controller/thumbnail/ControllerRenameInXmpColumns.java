package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.tasks.UpdaterRenameInXmpColumnsArray;
import de.elmar_baumann.imv.view.dialogs.RenameInXmpColumnsDialog;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
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
    private ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();

    public ControllerRenameInXmpColumns() {
        Panels.getInstance().getAppFrame().getMenuItemRenameInXmp().addActionListener(this);
    }

    @Override
    public void setControl(boolean control) {
        super.setControl(control);
        if (!control) {
            updater.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            renameSelectedThumbnails();
        }
    }

    private void renameSelectedThumbnails() {
        List<String> filenames = FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles());
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
