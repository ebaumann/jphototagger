package org.jphototagger.program.controller.imagecollection;

import org.jdesktop.swingx.JXList;

import org.jphototagger.lib.componentutil.MessageLabel;
import org.jphototagger.program.database.DatabaseImageCollections;
import org.jphototagger.program.model.ListModelImageCollections;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.File;

import java.util.List;

import javax.swing.JMenuItem;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerPickReject implements ActionListener, KeyListener {
    public ControllerPickReject() {
        listen();
    }

    private void listen() {
        GUI.getThumbnailsPanel().addKeyListener(this);
        getPickItem().addActionListener(this);
        getRejectItem().addActionListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_P) {
            addOrRemove(true);
        } else if (evt.getKeyCode() == KeyEvent.VK_R) {
            addOrRemove(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(getPickItem())) {
            addOrRemove(true);
        } else if (evt.getSource().equals(getRejectItem())) {
            addOrRemove(false);
        }
    }

    private void addOrRemove(boolean pick) {
        if ((pick && isPickCollection()) || (!pick && isRejectCollection())) {
            return;
        }

        ThumbnailsPanel panelThumbnails = GUI.getThumbnailsPanel();

        if (panelThumbnails.isFileSelected()) {
            List<File> selFiles = panelThumbnails.getSelectedFiles();

            GUI.getAppPanel().setStatusbarText(getPopupMessage(pick), MessageLabel.MessageType.INFO, 1000);
            addToCollection(pick
                            ? ListModelImageCollections.NAME_IMAGE_COLLECTION_PICKED
                            : ListModelImageCollections.NAME_IMAGE_COLLECTION_REJECTED, selFiles);

            if ((pick && isRejectCollection()) || (!pick && isPickCollection())) {
                deleteFromCollection(pick
                                     ? ListModelImageCollections.NAME_IMAGE_COLLECTION_REJECTED
                                     : ListModelImageCollections.NAME_IMAGE_COLLECTION_PICKED, selFiles);
                panelThumbnails.remove(selFiles);
            }
        }
    }

    private String getPopupMessage(boolean pick) {
        return pick
               ? JptBundle.INSTANCE.getString("ControllerPickReject.Info.Pick")
               : JptBundle.INSTANCE.getString("ControllerPickReject.Info.Reject");
    }

    private boolean isPickCollection() {
        return isCollection(ListModelImageCollections.NAME_IMAGE_COLLECTION_PICKED);
    }

    private boolean isRejectCollection() {
        return isCollection(ListModelImageCollections.NAME_IMAGE_COLLECTION_REJECTED);
    }

    private boolean isCollection(String collection) {
        if (!GUI.getThumbnailsPanel().getContent().equals(Content.IMAGE_COLLECTION)) {
            return false;
        }

        JXList list = GUI.getAppPanel().getListImageCollections();

        if (list.getSelectedIndex() < 0) {
            return false;
        }

        return list.getSelectedValue().toString().equals(collection);
    }

    private void addToCollection(String collection, List<File> files) {
        DatabaseImageCollections.INSTANCE.insertImagesInto(collection, files);
    }

    private void deleteFromCollection(String collection, List<File> files) {
        DatabaseImageCollections.INSTANCE.deleteImagesFrom(collection, files);
    }

    @Override
    public void keyTyped(KeyEvent evt) {

        // ignore
    }

    @Override
    public void keyReleased(KeyEvent evt) {

        // ignore
    }

    private JMenuItem getPickItem() {
        return PopupMenuThumbnails.INSTANCE.getItemPick();
    }

    private JMenuItem getRejectItem() {
        return PopupMenuThumbnails.INSTANCE.getItemReject();
    }
}
