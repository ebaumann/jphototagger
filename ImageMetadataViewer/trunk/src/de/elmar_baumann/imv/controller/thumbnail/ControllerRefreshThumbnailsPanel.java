package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Thumbnailspanelanzeige aktualisieren.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/25
 */
public final class ControllerRefreshThumbnailsPanel implements ActionListener {

    private final ImageFileThumbnailsPanel thumbnailspanel;

    public ControllerRefreshThumbnailsPanel(ImageFileThumbnailsPanel thumbnailspanel) {
        this.thumbnailspanel = thumbnailspanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        thumbnailspanel.refresh();
    }
}
