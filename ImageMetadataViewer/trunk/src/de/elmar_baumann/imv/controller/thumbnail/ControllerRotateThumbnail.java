package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.resource.Panels;
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
public final class ControllerRotateThumbnail extends Controller
    implements ActionListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.getInstance();
    private final PopupMenuPanelThumbnails popup = PopupMenuPanelThumbnails.getInstance();
    private final ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();

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
        if (isControl()) {
            rotateSelectedImages(
                popup.getRotateAngle(e.getSource()));
        }
    }

    private void rotateSelectedImages(float rotateAngle) {
        List<Integer> selectedIndices = thumbnailsPanel.getSelected();
        for (Integer index : selectedIndices) {
            Image thumbnail = ImageTransform.rotate(
                thumbnailsPanel.getThumbnail(index.intValue()), rotateAngle);
            if (thumbnail != null) {
                String filename = thumbnailsPanel.getFile(index.intValue()).getAbsolutePath();
                if (db.updateThumbnail(filename, thumbnail)) {
                    thumbnailsPanel.set(index.intValue(), thumbnail);
                }
            }
        }
    }
}
