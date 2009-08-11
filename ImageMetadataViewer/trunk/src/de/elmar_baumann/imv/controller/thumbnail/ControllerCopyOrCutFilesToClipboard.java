package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.types.FileAction;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.clipboard.ClipboardUtil;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

/**
 * Listens to {@link AppFrame#getMenuItemCopyToClipboard()},
 * {@link AppFrame#getMenuItemCutToClipboard()} and on action
 * performed this class copies or cuts the selected files into the clipboard.
 * 
 * Enables or disables that menu items based on selection.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-26
 */
public final class ControllerCopyOrCutFilesToClipboard
        implements ActionListener, ThumbnailsPanelListener {

    private final ImageFileThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final JMenuItem menuItemCopy =
            GUI.INSTANCE.getAppFrame().getMenuItemCopyToClipboard();
    private final JMenuItem menuItemCut =
            GUI.INSTANCE.getAppFrame().getMenuItemCutToClipboard();

    public ControllerCopyOrCutFilesToClipboard() {
        listen();
    }

    private void listen() {
        menuItemCopy.addActionListener(this);
        menuItemCut.addActionListener(this);
        thumbnailsPanel.addThumbnailsPanelListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (thumbnailsPanel.getSelectionCount() > 0) {
            setFileAction(e.getSource());
            transferSelectedFiles();
            GUI.INSTANCE.getAppFrame().getMenuItemPasteFromClipboard().
                    setEnabled(
                    true);
        }
    }

    public void setFileAction(Object source) {
        if (source == menuItemCopy) {
            thumbnailsPanel.setFileAction(FileAction.COPY);
        } else if (source == menuItemCut) {
            thumbnailsPanel.setFileAction(FileAction.CUT);
        } else {
            assert false : "Invalid source: " + source;
        }
    }

    private void transferSelectedFiles() {
        Clipboard clipboard =
                Toolkit.getDefaultToolkit().getSystemClipboard();
        ClipboardUtil.copyToClipboard(thumbnailsPanel.getSelectedFiles(),
                clipboard, null);
    }

    @Override
    public void thumbnailsSelectionChanged() {
        final boolean imagesSelected = thumbnailsPanel.getSelectionCount() > 0;
        menuItemCopy.setEnabled(imagesSelected);
        menuItemCut.setEnabled(imagesSelected); // ignore possibility of write protected files
    }

    @Override
    public void thumbnailsChanged() {
        // ignore
    }
}
