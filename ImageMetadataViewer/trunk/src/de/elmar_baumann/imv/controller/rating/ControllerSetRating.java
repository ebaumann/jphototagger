package de.elmar_baumann.imv.controller.rating;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JMenuItem;

/**
 * Listens to key events in {@link ImageFileThumbnailsPanel} and if a key
 * between the range 0..5 was pressed set's the rating to the
 * {@link EditMetadataPanelsArray}.
 * <p>
 * Also listens to the rating items in the {@link PopupMenuThumbnails} and
 * rates an action performed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-03
 */
public final class ControllerSetRating
        implements ActionListener, KeyListener {

    private final PopupMenuThumbnails popup = PopupMenuThumbnails.INSTANCE;
    private static final Map<Integer, Long> RATING_OF_KEY_CODE =
            new HashMap<Integer, Long>();

    static {
        RATING_OF_KEY_CODE.put(KeyEvent.VK_0, Long.valueOf(0));
        RATING_OF_KEY_CODE.put(KeyEvent.VK_1, Long.valueOf(1));
        RATING_OF_KEY_CODE.put(KeyEvent.VK_2, Long.valueOf(2));
        RATING_OF_KEY_CODE.put(KeyEvent.VK_3, Long.valueOf(3));
        RATING_OF_KEY_CODE.put(KeyEvent.VK_4, Long.valueOf(4));
        RATING_OF_KEY_CODE.put(KeyEvent.VK_5, Long.valueOf(5));
    }

    public ControllerSetRating() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppPanel().getPanelThumbnails().addKeyListener(this);
        popup.getItemRating0().addActionListener(this);
        popup.getItemRating1().addActionListener(this);
        popup.getItemRating2().addActionListener(this);
        popup.getItemRating3().addActionListener(this);
        popup.getItemRating4().addActionListener(this);
        popup.getItemRating5().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setRating(popup.getRatingOfItem((JMenuItem) e.getSource()));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (isRatingKey(e)) {
            setRating(RATING_OF_KEY_CODE.get(e.getKeyCode()));
        }
    }

    public void setRating(Long rating) {
        EditMetadataPanelsArray editPanel =
                GUI.INSTANCE.getAppPanel().getMetadataEditPanelsArray();
        if (editPanel.isEditable()) {
            editPanel.setRating(rating);
        }
    }

    private boolean isRatingKey(KeyEvent e) {
        return RATING_OF_KEY_CODE.containsKey(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}
