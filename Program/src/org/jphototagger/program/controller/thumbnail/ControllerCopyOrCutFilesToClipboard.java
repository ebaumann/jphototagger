package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.lib.clipboard.ClipboardUtil;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.types.FileAction;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Toolkit;

import javax.swing.JMenuItem;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Listens to {@link PopupMenuThumbnails#getItemCopyToClipboard()},
 * {@link PopupMenuThumbnails#getItemCutToClipboard()} and on action
 * performed this class copies or cuts the selected files into the clipboard.
 *
 * Enables or disables that menu items based on selection.
 *
 * @author Elmar Baumann
 */
public final class ControllerCopyOrCutFilesToClipboard implements ActionListener, KeyListener, ThumbnailsPanelListener {
    private final ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();
    private final PopupMenuThumbnails popup = PopupMenuThumbnails.INSTANCE;

    public ControllerCopyOrCutFilesToClipboard() {
        listen();
    }

    private void listen() {
        getCopyItem().addActionListener(this);
        getCutItem().addActionListener(this);
        tnPanel.addThumbnailsPanelListener(this);
        tnPanel.addKeyListener(this);
    }

    private JMenuItem getCopyItem() {
        return PopupMenuThumbnails.INSTANCE.getItemCopyToClipboard();
    }

    private JMenuItem getCutItem() {
        return PopupMenuThumbnails.INSTANCE.getItemCutToClipboard();
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (!tnPanel.isAFileSelected()) {
            return;
        }

        if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_C)) {
            perform(FileAction.COPY);
        } else if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_X)) {
            perform(FileAction.CUT);
        }
    }

    private void perform(FileAction fa) {
        tnPanel.setFileAction(fa);
        transferSelectedFiles();
        popup.getItemPasteFromClipboard().setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (tnPanel.isAFileSelected()) {
            setFileAction(evt.getSource());
            transferSelectedFiles();
            popup.getItemPasteFromClipboard().setEnabled(true);
        }
    }

    public void setFileAction(Object source) {
        if (source == getCopyItem()) {
            tnPanel.setFileAction(FileAction.COPY);
        } else if (source == getCutItem()) {
            tnPanel.setFileAction(FileAction.CUT);
        } else {
            assert false : "Invalid source: " + source;
        }
    }

    private void transferSelectedFiles() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        ClipboardUtil.copyToClipboard(tnPanel.getSelectedFiles(), clipboard, null);
    }

    @Override
    public void thumbnailsSelectionChanged() {
        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                final boolean imagesSelected = tnPanel.isAFileSelected();

                getCopyItem().setEnabled(imagesSelected);

                // ignore possibility of write protected files
                getCutItem().setEnabled(imagesSelected);
            }
        });
    }

    @Override
    public void thumbnailsChanged() {

        // ignore
    }

    @Override
    public void keyTyped(KeyEvent evt) {

        // ignore
    }

    @Override
    public void keyReleased(KeyEvent evt) {

        // ignore
    }
}
