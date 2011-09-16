package org.jphototagger.program.controller.imagecollection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;

import javax.swing.JMenuItem;

import org.jdesktop.swingx.JXList;

import org.openide.util.Lookup;

import org.jphototagger.domain.repository.ImageCollectionsRepository;
import org.jphototagger.domain.thumbnails.TypeOfDisplayedImages;
import org.jphototagger.lib.componentutil.MessageLabel;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.model.ImageCollectionsListModel;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.ThumbnailsPopupMenu;

/**
 *
 * @author Elmar Baumann
 */
public final class PickRejectController implements ActionListener, KeyListener {

    private final ImageCollectionsRepository repo = Lookup.getDefault().lookup(ImageCollectionsRepository.class);

    public PickRejectController() {
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

        if (panelThumbnails.isAFileSelected()) {
            List<File> selFiles = panelThumbnails.getSelectedFiles();

            GUI.getAppPanel().setStatusbarText(getPopupMessage(pick), MessageLabel.MessageType.INFO, 1000);
            addToCollection(pick
                    ? ImageCollectionsListModel.NAME_IMAGE_COLLECTION_PICKED
                    : ImageCollectionsListModel.NAME_IMAGE_COLLECTION_REJECTED, selFiles);

            if ((pick && isRejectCollection()) || (!pick && isPickCollection())) {
                deleteFromCollection(pick
                        ? ImageCollectionsListModel.NAME_IMAGE_COLLECTION_REJECTED
                        : ImageCollectionsListModel.NAME_IMAGE_COLLECTION_PICKED, selFiles);
                panelThumbnails.removeFiles(selFiles);
            }
        }
    }

    private String getPopupMessage(boolean pick) {
        return pick
                ? Bundle.getString(PickRejectController.class, "PickRejectController.Info.Pick")
                : Bundle.getString(PickRejectController.class, "PickRejectController.Info.Reject");
    }

    private boolean isPickCollection() {
        return isCollection(ImageCollectionsListModel.NAME_IMAGE_COLLECTION_PICKED);
    }

    private boolean isRejectCollection() {
        return isCollection(ImageCollectionsListModel.NAME_IMAGE_COLLECTION_REJECTED);
    }

    private boolean isCollection(String collection) {
        if (!GUI.getThumbnailsPanel().getContent().equals(TypeOfDisplayedImages.IMAGE_COLLECTION)) {
            return false;
        }

        JXList list = GUI.getAppPanel().getListImageCollections();

        if (list.getSelectedIndex() < 0) {
            return false;
        }

        return list.getSelectedValue().toString().equals(collection);
    }

    private void addToCollection(String collection, List<File> files) {
        repo.insertImagesIntoImageCollection(collection, files);
    }

    private void deleteFromCollection(String collection, List<File> files) {
        repo.deleteImagesFromImageCollection(collection, files);
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
        return ThumbnailsPopupMenu.INSTANCE.getItemPick();
    }

    private JMenuItem getRejectItem() {
        return ThumbnailsPopupMenu.INSTANCE.getItemReject();
    }
}
