package org.jphototagger.program.controller.thumbnail;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JMenuItem;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.clipboard.ClipboardUtil;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.types.FileAction;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.ThumbnailsPopupMenu;

/**
 * Listens to {@link ThumbnailsPopupMenu#getItemCopyToClipboard()},
 * {@link ThumbnailsPopupMenu#getItemCutToClipboard()} and on action
 * performed this class copies or cuts the selected files into the clipboard.
 *
 * Enables or disables that menu items based on selection.
 *
 * @author Elmar Baumann
 */
public final class CopyOrCutFilesToClipboardController implements ActionListener, KeyListener {

    private final ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();
    private final ThumbnailsPopupMenu popup = ThumbnailsPopupMenu.INSTANCE;

    public CopyOrCutFilesToClipboardController() {
        listen();
    }

    private void listen() {
        getCopyItem().addActionListener(this);
        getCutItem().addActionListener(this);
        tnPanel.addKeyListener(this);
        AnnotationProcessor.process(this);
    }

    private JMenuItem getCopyItem() {
        return ThumbnailsPopupMenu.INSTANCE.getItemCopyToClipboard();
    }

    private JMenuItem getCutItem() {
        return ThumbnailsPopupMenu.INSTANCE.getItemCutToClipboard();
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

    @EventSubscriber(eventClass = ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(final ThumbnailsSelectionChangedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                final boolean imagesSelected = evt.isAFileSelected();

                getCopyItem().setEnabled(imagesSelected);

                // ignore possibility of write protected files
                getCutItem().setEnabled(imagesSelected);
            }
        });
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
