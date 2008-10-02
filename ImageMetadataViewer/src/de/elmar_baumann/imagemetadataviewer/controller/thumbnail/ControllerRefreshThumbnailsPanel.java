package de.elmar_baumann.imagemetadataviewer.controller.thumbnail;

import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.view.panels.ImageFileThumbnailsPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Thumbnailspanelanzeige aktualisieren.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/25
 */
public class ControllerRefreshThumbnailsPanel extends Controller
    implements ActionListener {

    private ImageFileThumbnailsPanel thumbnailspanel;

    public ControllerRefreshThumbnailsPanel(ImageFileThumbnailsPanel thumbnailspanel) {
        this.thumbnailspanel = thumbnailspanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            thumbnailspanel.repaint();
        }
    }
}
