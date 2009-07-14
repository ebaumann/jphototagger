package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.AppTexts;
import de.elmar_baumann.imv.comparator.ComparatorStringAscending;
import de.elmar_baumann.imv.model.ListModelImageCollections;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.tasks.ImageCollectionDatabaseUtils;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
import de.elmar_baumann.lib.componentutil.ListUtil;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

/**
 * Kontrolliert Aktion: Erzeuge eine Bildsammlung, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails}.
 *
 * Also listens to the {@link JList}'s key events and creates a new image
 * collection when the keys <code>Ctrl+N</code> were pressed.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public final class ControllerCreateImageCollection
        implements ActionListener, KeyListener {

    private final PopupMenuThumbnails popupMenu =
            PopupMenuThumbnails.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JList list = appPanel.getListImageCollections();
    private final ImageFileThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerCreateImageCollection() {
        listen();
    }

    private void listen() {
        popupMenu.getItemCreateImageCollection().addActionListener(this);
        list.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isControl(e, KeyEvent.VK_N)) {
            createImageCollectionOfSelectedFiles();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        createImageCollectionOfSelectedFiles();
    }

    private void createImageCollectionOfSelectedFiles() {
        final String collectionName = ImageCollectionDatabaseUtils.
                insertImageCollection(
                FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles()));
        if (collectionName != null) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ListModel model = list.getModel();
                    if (model instanceof ListModelImageCollections) {
                        ListUtil.insertSorted((ListModelImageCollections) model,
                                collectionName,
                                ComparatorStringAscending.IGNORE_CASE,
                                getSortIndex(model));
                    }
                }
            });
        } else {
            AppLog.logWarning(ControllerCreateImageCollection.class, Bundle.
                    getString(
                    "ControllerCreateImageCollection.Error.Create")); // NOI18N
        }
    }

    private int getSortIndex(ListModel model) {
        Object firstElement = model.getElementAt(0);
        return firstElement == null
               ? 0
               : firstElement.toString().equalsIgnoreCase(
                AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PREV_IMPORT)
                 ? 1
                 : 0;
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
