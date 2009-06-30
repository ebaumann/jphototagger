package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.comparator.ComparatorStringAscending;
import de.elmar_baumann.imv.model.ListModelImageCollections;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.tasks.ImageCollectionDatabaseUtils;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
import de.elmar_baumann.lib.componentutil.ListUtil;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;

/**
 * Kontrolliert Aktion: Erzeuge eine Bildsammlung, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails}.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public final class ControllerCreateImageCollection implements ActionListener {

    private final PopupMenuThumbnails popupMenu =
            PopupMenuThumbnails.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final ListModelImageCollections model = (ListModelImageCollections) appPanel.getListImageCollections().
            getModel();
    private final ImageFileThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.
            getAppPanel().getPanelThumbnails();

    public ControllerCreateImageCollection() {
        listen();
    }

    private void listen() {
        popupMenu.getItemCreateImageCollection().addActionListener(this);
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
                    ListUtil.insertSorted(model, collectionName,
                            ComparatorStringAscending.IGNORE_CASE);
                }
            });
        } else {
            AppLog.logWarning(ControllerCreateImageCollection.class, Bundle.
                    getString(
                    "ControllerCreateImageCollection.ErrorMessage.Create"));
        }
    }
}
