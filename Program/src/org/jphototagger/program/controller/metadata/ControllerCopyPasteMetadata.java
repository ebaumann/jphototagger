package org.jphototagger.program.controller.metadata;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JMenuItem;

import org.jphototagger.domain.event.listener.ThumbnailsPanelListener;
import org.jphototagger.domain.xmp.Xmp;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

/**
 * Listens to the menu items {@link PopupMenuThumbnails#getItemCopyMetadata()} and
 * {@link PopupMenuThumbnails#getItemPasteMetadata()} and on action performed copies
 * XMP metadata of the {@link EditMetadataPanels} or paste it via
 * {@link EditMetadataPanels#getXmp()} or
 * {@link EditMetadataPanels#setXmp(org.jphototagger.program.data.Xmp)}.
 *
 * @author Elmar Baumann
 */
public final class ControllerCopyPasteMetadata implements ActionListener, KeyListener, ThumbnailsPanelListener {
    private Xmp xmp;

    public ControllerCopyPasteMetadata() {
        listen();
    }

    private void listen() {
        getCopyItem().addActionListener(this);
        getPasteItem().addActionListener(this);
        GUI.getThumbnailsPanel().addThumbnailsPanelListener(this);
        GUI.getThumbnailsPanel().addKeyListener(this);
    }

    private JMenuItem getCopyItem() {
        return PopupMenuThumbnails.INSTANCE.getItemCopyMetadata();
    }

    private JMenuItem getPasteItem() {
        return PopupMenuThumbnails.INSTANCE.getItemPasteMetadata();
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (KeyEventUtil.isMenuShortcutWithShiftDown(evt, KeyEvent.VK_C)) {
            copy();
        } else if (KeyEventUtil.isMenuShortcutWithShiftDown(evt, KeyEvent.VK_V)) {
            paste();
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == getCopyItem()) {
            copy();
        } else if (evt.getSource() == getPasteItem()) {
            paste();
        }
    }

    private void copy() {
        this.xmp = new Xmp(GUI.getAppPanel().getEditMetadataPanels().getXmp());
        getPasteItem().setEnabled(true);
    }

    private void paste() {
        if (xmp == null) {
            return;
        }

        EditMetadataPanels editPanel = GUI.getAppPanel().getEditMetadataPanels();

        if (!checkSelected() ||!checkCanEdit(editPanel)) {
            return;
        }

        editPanel.setXmp(xmp);
        getPasteItem().setEnabled(false);
        xmp = null;
    }

    private boolean checkSelected() {
        int selCount = GUI.getThumbnailsPanel().getSelectionCount();

        if (selCount <= 0) {
            String message = Bundle.getString(ControllerCopyPasteMetadata.class, "ControllerCopyPasteMetadata.Error.NoSelection");
            MessageDisplayer.error(null, message);

            return false;
        }

        return true;
    }

    private boolean checkCanEdit(EditMetadataPanels editPanel) {
        if (!editPanel.isEditable()) {
            String message = Bundle.getString(ControllerCopyPasteMetadata.class, "ControllerCopyPasteMetadata.Error.NotEditable");
            MessageDisplayer.error(null, message);

            return false;
        }

        return true;
    }

    @Override
    public void thumbnailsSelectionChanged() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                getCopyItem().setEnabled(GUI.getThumbnailsPanel().isAFileSelected());
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
