package org.jphototagger.program.controller.imagecollection;

import org.jphototagger.lib.comparator.FileSort;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.database.DatabaseImageCollections;
import org.jphototagger.program.event.listener.RefreshListener;
import org.jphototagger.program.event.RefreshEvent;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.WaitDisplay;


import java.io.File;

import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Kontrolliert die Aktion: Eine Bildsammlung wurde ausgewählt.
 * Ausgelöst wird dies durch Selektieren des Treeitems mit dem
 * Namen der gespeicherten Suche.
 *
 * @author Elmar Baumann
 */
public final class ControllerImageCollectionSelected implements ListSelectionListener, RefreshListener {
    public ControllerImageCollectionSelected() {
        listen();
    }

    private void listen() {
        GUI.getImageCollectionsList().addListSelectionListener(this);
        GUI.getThumbnailsPanel().addRefreshListener(this, Content.IMAGE_COLLECTION);
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (GUI.getImageCollectionsList().getSelectedIndex() >= 0) {
            showImageCollection(null);
        }
    }

    @Override
    public void refresh(RefreshEvent evt) {
        if (GUI.getImageCollectionsList().getSelectedIndex() >= 0) {
            showImageCollection(evt.getSettings());
        }
    }

    private void showImageCollection(final ThumbnailsPanel.Settings settings) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Object selValue = GUI.getImageCollectionsList().getSelectedValue();

                if (selValue != null) {
                    showImageCollection(selValue.toString(), settings);
                } else {
                    AppLogger.logWarning(ControllerImageCollectionSelected.class,
                                         "ControllerImageCollectionSelected.Error.SelectedValueIsNull");
                }

                setMetadataEditable();
            }
        }, "JPhotoTagger: Displaying selected image collection");

        thread.start();
    }

    private void showImageCollection(final String collectionName, final ThumbnailsPanel.Settings settings) {
        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                WaitDisplay.show();

                List<File> imageFiles = DatabaseImageCollections.INSTANCE.getImageFilesOf(collectionName);
                ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

                setTitle();
                tnPanel.setFileSortComparator(FileSort.NO_SORT.getComparator());
                tnPanel.setFiles(imageFiles, Content.IMAGE_COLLECTION);
                tnPanel.apply(settings);
                WaitDisplay.hide();
            }
            private void setTitle() {
                GUI.getAppFrame().setTitle(
                    JptBundle.INSTANCE.getString(
                        "ControllerImageCollectionSelected.AppFrame.Title.Collection", collectionName));
            }
        });
    }

    private void setMetadataEditable() {
        if (!GUI.getThumbnailsPanel().isAFileSelected()) {
            GUI.getEditPanel().setEditable(false);
        }
    }
}
