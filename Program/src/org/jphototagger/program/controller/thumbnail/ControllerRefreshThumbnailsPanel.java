package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jphototagger.program.resource.GUI;

/**
 * Kontrolliert die Aktion: Thumbnailspanelanzeige aktualisieren.
 *
 * @author Elmar Baumann
 */
public final class ControllerRefreshThumbnailsPanel implements ActionListener {

    public ControllerRefreshThumbnailsPanel() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemRefresh().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        GUI.refreshThumbnailsPanel();
    }
}
