package org.jphototagger.program.controller.metadata;

import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.EventQueue;

import javax.swing.JMenuItem;

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
            MessageDisplayer.error(null, "ControllerCopyPasteMetadata.Error.NoSelection");

            return false;
        }

        return true;
    }

    private boolean checkCanEdit(EditMetadataPanels editPanel) {
        if (!editPanel.isEditable()) {
            MessageDisplayer.error(null, "ControllerCopyPasteMetadata.Error.NotEditable");

            return false;
        }

        return true;
    }

    @Override
    public void thumbnailsSelectionChanged() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                getCopyItem().setEnabled(GUI.getThumbnailsPanel().isFileSelected());
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
