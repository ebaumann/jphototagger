package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.app.AppTexts;
import de.elmar_baumann.imv.database.DatabaseImageCollections;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;
import javax.swing.JList;

/**
 * Listens to the menu item {@link ImageFileThumbnailsPanel} to key events
 * and on action adds a new keyword below the selected keyword.
 * <p>
 * If the key <strong>P</strong> was pressed, this class adds the selected
 * thumbnails to the <strong>Pick</strong> collection and if the key
 * <strong>R</strong> was pressed to the <strong>Reject</strong> collection.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-21
 */
public final class ControllerPickReject implements KeyListener {

    private ImageFileThumbnailsPanel panelThumbnails =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerPickReject() {
        listen();
    }

    private void listen() {
        panelThumbnails.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) {
            addRemove(true);
        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            addRemove(false);
        }
    }

    private void addRemove(boolean pick) {
        if (pick && isPickCollection() || !pick && isRejectCollection()) return;
        if (panelThumbnails.getSelectionCount() > 0) {
            List<File> selFiles = panelThumbnails.getSelectedFiles();
            addToCollection(
                    pick
                    ? AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PICKED
                    : AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_REJECTED,
                    selFiles);
            if (pick && isRejectCollection() || !pick && isPickCollection()) {
                deleteFromCollection(
                        pick
                        ? AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_REJECTED
                        : AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PICKED,
                        selFiles);
                panelThumbnails.remove(selFiles);
            }
        }
    }

    private boolean isPickCollection() {
        return isCollection(AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PICKED);
    }

    private boolean isRejectCollection() {
        return isCollection(
                AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_REJECTED);
    }

    private boolean isCollection(String collection) {
        if (!panelThumbnails.getContent().equals(Content.IMAGE_COLLECTION))
            return false;
        JList list = GUI.INSTANCE.getAppPanel().getListImageCollections();
        if (list.getSelectedIndex() < 0) return false;
        return list.getSelectedValue().toString().equals(collection);
    }

    private void addToCollection(String collection, List<File> files) {
        DatabaseImageCollections.INSTANCE.insertImagesIntoCollection(
                collection, FileUtil.getAsFilenames(files));
    }

    private void deleteFromCollection(String collection, List<File> files) {
        DatabaseImageCollections.INSTANCE.deleteImagesFromCollection(
                collection, FileUtil.getAsFilenames(files));
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}
