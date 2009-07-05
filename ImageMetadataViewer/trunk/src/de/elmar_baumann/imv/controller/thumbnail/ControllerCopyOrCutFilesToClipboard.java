package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.types.FileAction;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.clipboard.ClipboardUtil;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.TransferHandler;

/**
 * Copies or cuts the selected files in the thumbnails panel into the system
 * clipboard.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/26
 */
public final class ControllerCopyOrCutFilesToClipboard implements KeyListener {

    private final ImageFileThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerCopyOrCutFilesToClipboard() {
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
            transferSelectedFiles(getTransferAction(e));
        }
    }

    private void transferSelectedFiles(int action) {
        Clipboard clipboard =
                Toolkit.getDefaultToolkit().getSystemClipboard();
        ClipboardUtil.copyToClipboard(thumbnailsPanel.getSelectedFiles(),
                clipboard, null);
        // Does not work with system's file manager
//        TransferHandler transferHandler = thumbnailsPanel.getTransferHandler();
//        if (transferHandler != null) {
//            transferHandler.exportToClipboard(thumbnailsPanel, clipboard, action);
//        }
    }

    private int getTransferAction(KeyEvent e) {
        assert KeyEventUtil.isCopy(e) || KeyEventUtil.isCut(e) : e;
        return KeyEventUtil.isCopy(e)
               ? TransferHandler.COPY
               : TransferHandler.MOVE;
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
