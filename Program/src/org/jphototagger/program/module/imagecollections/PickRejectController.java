package org.jphototagger.program.module.imagecollections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;
import javax.swing.JMenuItem;
import org.jdesktop.swingx.JXList;
import org.jphototagger.api.messages.MessageType;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.domain.imagecollections.ImageCollection;
import org.jphototagger.domain.repository.ImageCollectionsRepository;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.thumbnails.ThumbnailsPanel;
import org.jphototagger.program.module.thumbnails.ThumbnailsPopupMenu;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
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
            MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
            mainWindowManager.setMainWindowStatusbarText(getPopupMessage(pick), MessageType.INFO, 1000);
            addToCollection(pick
                    ? ImageCollection.PICKED_NAME
                    : ImageCollection.REJECTED_NAME, selFiles);
            if ((pick && isRejectCollection()) || (!pick && isPickCollection())) {
                deleteFromCollection(pick
                        ? ImageCollection.REJECTED_NAME
                        : ImageCollection.PICKED_NAME, selFiles);
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
        return isCollection(ImageCollection.PICKED_NAME);
    }

    private boolean isRejectCollection() {
        return isCollection(ImageCollection.REJECTED_NAME);
    }

    private boolean isCollection(String collection) {
        if (!GUI.getThumbnailsPanel().getOriginOfDisplayedThumbnails().isFilesOfAnImageCollection()) {
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
