package org.jphototagger.program.controller.imagecollection;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.comparator.FileSort;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.database.DatabaseImageCollections;
import org.jphototagger.program.event.RefreshEvent;
import org.jphototagger.program.event.listener.RefreshListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.WaitDisplay;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

/**
 * Kontrolliert die Aktion: Eine Bildsammlung wurde ausgewählt.
 * Ausgelöst wird dies durch Selektieren des Treeitems mit dem
 * Namen der gespeicherten Suche.
 *
 * @author Elmar Baumann
 */
public final class ControllerImageCollectionSelected implements ListSelectionListener, RefreshListener {

    private static final Logger LOGGER = Logger.getLogger(ControllerImageCollectionSelected.class.getName());

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
                    LOGGER.log(Level.WARNING, "Photo album item selected: Couldn't find the album's name (Item value == null)!");
                }

                setMetadataEditable();
            }
        }, "JPhotoTagger: Displaying selected image collection");

        thread.start();
    }

    private void showImageCollection(final String collectionName, final ThumbnailsPanel.Settings settings) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
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
                String title = Bundle.getString(ControllerImageCollectionSelected.class ,  "ControllerImageCollectionSelected.AppFrame.Title.Collection", collectionName);

                GUI.getAppFrame().setTitle(title);
            }
        });
    }

    private void setMetadataEditable() {
        if (!GUI.getThumbnailsPanel().isAFileSelected()) {
            GUI.getEditPanel().setEditable(false);
        }
    }
}
