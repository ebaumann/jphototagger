package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;

/**
 * Kontrolliert die Aktion: Thumbnailspanelanzeige aktualisieren.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/25
 */
public final class ControllerRefreshThumbnailsPanel implements ActionListener {

    private final ImageFileThumbnailsPanel thumbnailspanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerRefreshThumbnailsPanel() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemRefresh().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        refresh();
    }

    private void refresh() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                thumbnailspanel.refresh();
            }
        });
    }
}
