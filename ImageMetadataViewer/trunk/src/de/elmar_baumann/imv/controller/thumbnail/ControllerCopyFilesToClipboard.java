package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.types.FileAction;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.clipboard.ClipboardUtil;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Copies the selected files in the thumbnails panel to the system clipboard.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/26
 */
public final class ControllerCopyFilesToClipboard implements KeyListener {

    private final ImageFileThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerCopyFilesToClipboard() {
        listen();
    }

    private void listen() {
        thumbnailsPanel.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isCopy(e) || KeyEventUtil.isCut(e) &&
                thumbnailsPanel.getSelectionCount() > 0) {
            setFileAction(e);
            ClipboardUtil.copyToSystemClipboard(
                    thumbnailsPanel.getSelectedFiles(), null);
        }
    }

    private void setFileAction(KeyEvent e) {
        thumbnailsPanel.setFileAction(KeyEventUtil.isCopy(e)
                                      ? FileAction.COPY
                                      : FileAction.CUT);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // nothing to do
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // nothing to do
    }
}
