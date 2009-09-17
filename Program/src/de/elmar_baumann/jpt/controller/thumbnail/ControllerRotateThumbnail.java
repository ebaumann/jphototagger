/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.thumbnail;

import de.elmar_baumann.jpt.cache.PersistentThumbnails;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;
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
 * ausgelöst von {@link de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-10
 */
public final class ControllerRotateThumbnail implements ActionListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final PopupMenuThumbnails popupMenu =
            PopupMenuThumbnails.INSTANCE;
    private final ThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.
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
                            PersistentThumbnails.getThumbnail(
                                    PersistentThumbnails.getMd5File(
                                    thumbnailsPanel.getFile(index.intValue()).
                                            getAbsolutePath())),
                            rotateAngle);
                    if (thumbnail != null) {
                        String filename = thumbnailsPanel.getFile(
                                index.intValue()).getAbsolutePath();
                        // should fire an update caught by cache
                        db.updateThumbnail(filename, thumbnail);
                    }
                }
            }
        });
    }
}
