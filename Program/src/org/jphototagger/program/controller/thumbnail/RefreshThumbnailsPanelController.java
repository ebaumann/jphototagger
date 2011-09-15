package org.jphototagger.program.controller.thumbnail;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.ThumbnailsPopupMenu;

/**
 * Kontrolliert die Aktion: Thumbnailspanelanzeige aktualisieren.
 *
 * @author Elmar Baumann
 */
public final class RefreshThumbnailsPanelController implements ActionListener {

    public RefreshThumbnailsPanelController() {
        listen();
    }

    private void listen() {
        ThumbnailsPopupMenu.INSTANCE.getItemRefresh().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        GUI.refreshThumbnailsPanel();
    }
}
