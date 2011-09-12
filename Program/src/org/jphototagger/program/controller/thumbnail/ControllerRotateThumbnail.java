package org.jphototagger.program.controller.thumbnail;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;

import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.repository.ThumbnailsRepository;
import org.jphototagger.image.util.ImageTransform;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;
import org.openide.util.Lookup;

/**
 * Kontrolliert die Aktion: Rotiere ein Thumbnail,
 * ausgelöst von
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuThumbnails}.
 *
 * @author Elmar Baumann
 */
public final class ControllerRotateThumbnail implements ActionListener {

    private final Map<JMenuItem, Float> angleOfItem = new HashMap<JMenuItem, Float>();
    private final ThumbnailsRepository tnRepo = Lookup.getDefault().lookup(ThumbnailsRepository.class);

    public ControllerRotateThumbnail() {
        initAngleOfItem();
        listen();
    }

    private void initAngleOfItem() {
        PopupMenuThumbnails popup = PopupMenuThumbnails.INSTANCE;

        angleOfItem.put(popup.getItemRotateThumbnail90(), new Float(90));
        angleOfItem.put(popup.getItemRotateThumbnai180(), new Float(180));
        angleOfItem.put(popup.getItemRotateThumbnail270(), new Float(270));
    }

    private void listen() {
        PopupMenuThumbnails popup = PopupMenuThumbnails.INSTANCE;

        popup.getItemRotateThumbnail90().addActionListener(this);
        popup.getItemRotateThumbnai180().addActionListener(this);
        popup.getItemRotateThumbnail270().addActionListener(this);
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
    public void actionPerformed(ActionEvent evt) {
        rotateSelectedImages(getRotateAngle(evt.getSource()));
    }

    private void rotateSelectedImages(final float rotateAngle) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

            @Override
            public void run() {
                List<File> selFiles = GUI.getSelectedImageFiles();

                for (File imageFile : selFiles) {
                    final Image unrotatedTn = tnRepo.findThumbnail(imageFile);

                    if (unrotatedTn != null) {
                        Image rotatedTn = ImageTransform.rotate(unrotatedTn, rotateAngle);

                        if (rotatedTn != null) {

                            // should fire an updateImageFile caught by cache
                            repo.updateThumbnail(imageFile, rotatedTn);
                        }
                    }
                }
            }
        });
    }
}
