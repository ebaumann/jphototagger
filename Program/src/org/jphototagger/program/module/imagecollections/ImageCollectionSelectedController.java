package org.jphototagger.program.module.imagecollections;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.domain.repository.ImageCollectionsRepository;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.ThumbnailsPanelSettings;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.thumbnails.ThumbnailsPanel;
import org.jphototagger.program.resource.GUI;

/**
 * Kontrolliert die Aktion: Eine Bildsammlung wurde ausgewählt.
 * Ausgelöst wird dies durch Selektieren des Treeitems mit dem
 * Namen der gespeicherten Suche.
 *
 * @author Elmar Baumann
 */
public final class ImageCollectionSelectedController implements ListSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(ImageCollectionSelectedController.class.getName());
    private final ImageCollectionsRepository repo = Lookup.getDefault().lookup(ImageCollectionsRepository.class);

    public ImageCollectionSelectedController() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
        GUI.getImageCollectionsList().addListSelectionListener(this);
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (GUI.getImageCollectionsList().getSelectedIndex() >= 0) {
            showImageCollection(null);
        }
    }

    @EventSubscriber(eventClass = ThumbnailsPanelRefreshEvent.class)
    public void refresh(ThumbnailsPanelRefreshEvent evt) {
        if (GUI.getImageCollectionsList().getSelectedIndex() >= 0) {
            OriginOfDisplayedThumbnails typeOfDisplayedImages = evt.getTypeOfDisplayedImages();

            if (OriginOfDisplayedThumbnails.FILES_OF_AN_IMAGE_COLLECTION.equals(typeOfDisplayedImages)) {
                showImageCollection(evt.getThumbnailsPanelSettings());
            }
        }
    }

    private void showImageCollection(final ThumbnailsPanelSettings settings) {
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

    private void showImageCollection(final String collectionName, final ThumbnailsPanelSettings settings) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
                waitDisplayer.show();
                List<File> imageFiles = repo.findImageFilesOfImageCollection(collectionName);
                ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();
                setTitle();
                tnPanel.setFiles(imageFiles, OriginOfDisplayedThumbnails.FILES_OF_AN_IMAGE_COLLECTION);
                tnPanel.applyThumbnailsPanelSettings(settings);
                waitDisplayer.hide();
            }

            private void setTitle() {
                String title = Bundle.getString(ImageCollectionSelectedController.class, "ImageCollectionSelectedController.AppFrame.Title.Collection", collectionName);
                MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
                mainWindowManager.setMainWindowTitle(title);
            }
        });
    }

    private void setMetadataEditable() {
        if (!GUI.getThumbnailsPanel().isAFileSelected()) {
            GUI.getEditPanel().setEditable(false);
        }
    }
}
