package org.jphototagger.program.module.rating;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JMenuItem;
import org.jphototagger.domain.metadata.SelectedFilesMetaDataEditor;
import org.jphototagger.program.module.thumbnails.ThumbnailsPopupMenu;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * Listens to key events in {@code ThumbnailsPanel} and if a key
 * between the range 0..5 was pressed set's the rating to the
 * {@code SelectedFilesMetaDataEditor}.
 * <p>
 * Also listens to the rating items in the {@code ThumbnailsPopupMenu} and
 * rates an action performed.
 *
 * @author Elmar Baumann
 */
public final class SetRatingController implements ActionListener, KeyListener {

    private static final Map<Integer, Long> RATING_OF_KEY_CODE = new HashMap<>();

    static {
        RATING_OF_KEY_CODE.put(KeyEvent.VK_0, Long.valueOf(0));
        RATING_OF_KEY_CODE.put(KeyEvent.VK_1, Long.valueOf(1));
        RATING_OF_KEY_CODE.put(KeyEvent.VK_2, Long.valueOf(2));
        RATING_OF_KEY_CODE.put(KeyEvent.VK_3, Long.valueOf(3));
        RATING_OF_KEY_CODE.put(KeyEvent.VK_4, Long.valueOf(4));
        RATING_OF_KEY_CODE.put(KeyEvent.VK_5, Long.valueOf(5));
    }

    public SetRatingController() {
        listen();
    }

    private void listen() {
        ThumbnailsPopupMenu popup = ThumbnailsPopupMenu.INSTANCE;

        GUI.getThumbnailsPanel().addKeyListener(this);
        popup.getItemRating0().addActionListener(this);
        popup.getItemRating1().addActionListener(this);
        popup.getItemRating2().addActionListener(this);
        popup.getItemRating3().addActionListener(this);
        popup.getItemRating4().addActionListener(this);
        popup.getItemRating5().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        setRating(ThumbnailsPopupMenu.INSTANCE.getRatingOfItem((JMenuItem) evt.getSource()));
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (isRatingKey(evt)) {
            setRating(RATING_OF_KEY_CODE.get(evt.getKeyCode()));
        }
    }

    public void setRating(Long rating) {
        SelectedFilesMetaDataEditor editor = Lookup.getDefault().lookup(SelectedFilesMetaDataEditor.class);

        if (editor.isEditable()) {
            editor.setRating(rating);
        }
    }

    private boolean isRatingKey(KeyEvent evt) {
        return evt.getModifiers() == 0
                && RATING_OF_KEY_CODE.containsKey(evt.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent evt) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent evt) {
        // ignore
    }
}
