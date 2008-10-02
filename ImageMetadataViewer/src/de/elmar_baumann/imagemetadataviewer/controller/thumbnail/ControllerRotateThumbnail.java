package de.elmar_baumann.imagemetadataviewer.controller.thumbnail;

import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.database.Database;
import de.elmar_baumann.imagemetadataviewer.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.image.ImageTransform;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * Kontrolliert die Aktion: Rotiere ein Thumbnail,
 * ausgelöst von {@link de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuPanelThumbnails}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public class ControllerRotateThumbnail extends Controller
    implements ActionListener {

    private Database db = Database.getInstance();
    private PopupMenuPanelThumbnails popup = PopupMenuPanelThumbnails.getInstance();

    public ControllerRotateThumbnail() {
        listenToActionSources();
    }

    private void listenToActionSources() {
        popup.addActionListenerRotateThumbnail180(this);
        popup.addActionListenerRotateThumbnail270(this);
        popup.addActionListenerRotateThumbnail90(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            rotateSelectedImages(
                popup.getRotateAngle(e.getActionCommand()));
        }
    }

    private void rotateSelectedImages(float rotateAngle) {
        ImageFileThumbnailsPanel panel = popup.getThumbnailsPanel();
        Vector<Integer> selectedIndices = panel.getIndicesSelectedThumbnails();
        for (Integer index : selectedIndices) {
            Image thumbnail = ImageTransform.rotate(
                panel.getThumbnailAtIndex(index.intValue()), rotateAngle);
            if (thumbnail != null) {
                String filename = panel.getThumbnailFilenameAtIndex(index.intValue());
                if (db.isConnected() && db.updateThumbnail(filename, thumbnail)) {
                    panel.setThumbnailAtIndex(index.intValue(), thumbnail);
                }
            }
        }
    }
}
