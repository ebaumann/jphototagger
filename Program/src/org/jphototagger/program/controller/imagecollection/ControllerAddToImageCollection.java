package org.jphototagger.program.controller.imagecollection;

import org.jphototagger.program.helper.ImageCollectionsHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.ImageCollectionsDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Füge Bilder einer Bildsammlung hinzu, ausgelöst von
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuThumbnails}.
 *
 * @author Elmar Baumann
 */
public final class ControllerAddToImageCollection implements ActionListener {
    public ControllerAddToImageCollection() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemAddToImageCollection().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        addSelectedFilesToImageCollection();
    }

    private void addSelectedFilesToImageCollection() {
        String collectionName = selectCollectionName();

        if (collectionName != null) {
            ImageCollectionsHelper.addImagesToCollection(collectionName, GUI.getSelectedImageFiles());
        }
    }

    private String selectCollectionName() {
        ImageCollectionsDialog dlg = new ImageCollectionsDialog();

        dlg.setVisible(true);

        return dlg.getSelectedCollectionName();
    }
}
