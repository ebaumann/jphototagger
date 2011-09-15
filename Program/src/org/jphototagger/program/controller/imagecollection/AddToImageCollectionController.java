package org.jphototagger.program.controller.imagecollection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jphototagger.program.helper.ImageCollectionsHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.ImageCollectionsDialog;
import org.jphototagger.program.view.popupmenus.ThumbnailsPopupMenu;

/**
 * Kontrolliert die Aktion: Füge Bilder einer Bildsammlung hinzu, ausgelöst von
 * {@link org.jphototagger.program.view.popupmenus.ThumbnailsPopupMenu}.
 *
 * @author Elmar Baumann
 */
public final class AddToImageCollectionController implements ActionListener {

    public AddToImageCollectionController() {
        listen();
    }

    private void listen() {
        ThumbnailsPopupMenu.INSTANCE.getItemAddToImageCollection().addActionListener(this);
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
