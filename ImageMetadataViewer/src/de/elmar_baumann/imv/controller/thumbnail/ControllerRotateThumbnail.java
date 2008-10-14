package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.image.ImageTransform;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Kontrolliert die Aktion: Rotiere ein Thumbnail,
 * ausgelöst von {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails}.
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
        List<Integer> selectedIndices = panel.getSelected();
        for (Integer index : selectedIndices) {
            Image thumbnail = ImageTransform.rotate(
                panel.getThumbnail(index.intValue()), rotateAngle);
            if (thumbnail != null) {
                String filename = panel.geFilename(index.intValue());
                if (db.updateThumbnail(filename, thumbnail)) {
                    panel.set(index.intValue(), thumbnail);
                }
            }
        }
    }
}
