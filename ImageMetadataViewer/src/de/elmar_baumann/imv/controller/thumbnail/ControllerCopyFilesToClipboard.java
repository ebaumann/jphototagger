package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.clipboard.ClipboardUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Copies the selected files in the thumbnails panel to the system clipboard.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/26
 */
public class ControllerCopyFilesToClipboard extends Controller implements ActionListener {

    private ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();

    public ControllerCopyFilesToClipboard() {
        Panels.getInstance().getAppFrame().getMenuItemCopy().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl() && thumbnailsPanel.getSelectionCount() > 0) {
            ClipboardUtil.copyToSystemClipboard(thumbnailsPanel.getSelectedFiles(), null);
        }
    }
}
