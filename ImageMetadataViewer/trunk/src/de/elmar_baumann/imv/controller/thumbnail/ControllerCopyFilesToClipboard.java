package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.types.FileAction;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.clipboard.ClipboardUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

/**
 * Copies the selected files in the thumbnails panel to the system clipboard.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/26
 */
public final class ControllerCopyFilesToClipboard implements ActionListener {

    private final ImageFileThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final JMenuItem itemCopy = GUI.INSTANCE.getAppFrame().getMenuItemCopy();
    private final JMenuItem itemCut = GUI.INSTANCE.getAppFrame().getMenuItemCut();

    public ControllerCopyFilesToClipboard() {
        listen();
    }

    private void listen() {
        itemCopy.addActionListener(this);
        itemCut.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (thumbnailsPanel.getSelectionCount() > 0) {
            setFileAction(e.getSource());
            ClipboardUtil.copyToSystemClipboard(thumbnailsPanel.getSelectedFiles(), null);
        }
    }

    private void setFileAction(Object source) {
        thumbnailsPanel.setFileAction(source == itemCopy ? FileAction.COPY : FileAction.CUT);
    }
}
