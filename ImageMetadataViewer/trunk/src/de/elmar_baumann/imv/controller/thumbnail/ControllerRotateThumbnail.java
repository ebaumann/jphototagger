package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
import de.elmar_baumann.lib.image.util.ImageTransform;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

/**
 * Kontrolliert die Aktion: Rotiere ein Thumbnail,
 * ausgelöst von {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public final class ControllerRotateThumbnail implements ActionListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final PopupMenuThumbnails popupMenu =
            PopupMenuThumbnails.INSTANCE;
    private final ImageFileThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.
            getAppPanel().getPanelThumbnails();
    private final Map<JMenuItem, Float> angleOfItem =
            new HashMap<JMenuItem, Float>();

    public ControllerRotateThumbnail() {
        initAngleOfItem();
        listen();
    }

    private void initAngleOfItem() {
        angleOfItem.put(popupMenu.getItemRotateThumbnai90(), new Float(90));
        angleOfItem.put(popupMenu.getItemRotateThumbnai180(), new Float(180));
        angleOfItem.put(popupMenu.getItemRotateThumbnai270(), new Float(270));
    }

    private void listen() {
        popupMenu.getItemRotateThumbnai90().addActionListener(this);
        popupMenu.getItemRotateThumbnai180().addActionListener(this);
        popupMenu.getItemRotateThumbnai270().addActionListener(this);
    }

    private float getRotateAngle(Object obj) {
        Float angle = new Float(0);

        if (obj instanceof JMenuItem) {
            JMenuItem menuItem = (JMenuItem) obj;
            if (angleOfItem.containsKey(menuItem)) {
                angle = angleOfItem.get(menuItem);
            }
        }

        return angle.floatValue();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        rotateSelectedImages(getRotateAngle(e.getSource()));
    }

    private void rotateSelectedImages(final float rotateAngle) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                List<Integer> selectedIndices = thumbnailsPanel.
                        getSelectedIndices();
                for (Integer index : selectedIndices) {
                    Image thumbnail = ImageTransform.rotate(
                            thumbnailsPanel.getThumbnail(index.intValue()),
                            rotateAngle);
                    if (thumbnail != null) {
                        String filename = thumbnailsPanel.getFile(
                                index.intValue()).getAbsolutePath();
                        if (db.updateThumbnail(filename, thumbnail)) {
                            thumbnailsPanel.set(index.intValue(), thumbnail);
                        }
                    }
                }
            }
        });
    }
}
