package de.elmar_baumann.imagemetadataviewer.controller.thumbnail;

import de.elmar_baumann.imagemetadataviewer.UserSettings;
import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.io.IoUtil;
import de.elmar_baumann.imagemetadataviewer.view.panels.ImageFileThumbnailsPanel;

/**
 * Kontroller für die Aktion: Doppelklick auf ein Thumbnail ausgelöst von
 * {@link de.elmar_baumann.imagemetadataviewer.view.panels.ImageFileThumbnailsPanel}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public class ControllerDoubleklickThumbnail extends Controller {

    private ImageFileThumbnailsPanel panel;

    public ControllerDoubleklickThumbnail(ImageFileThumbnailsPanel panel) {
        this.panel = panel;
    }

    public void doubleClickAtIndex(int index) {
        if (isStarted()) {
            openImage(index);
        }
    }

    private void openImage(int index) {
        if (panel.isThumbnailIndex(index)) {
            IoUtil.startApplication(UserSettings.getInstance().getDefaultImageOpenApp(),
                panel.getThumbnailFilenameAtIndex(index));
        }
    }
}
