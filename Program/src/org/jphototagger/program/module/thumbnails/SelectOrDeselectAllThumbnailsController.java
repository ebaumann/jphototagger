package org.jphototagger.program.module.thumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class SelectOrDeselectAllThumbnailsController implements ActionListener {

    public SelectOrDeselectAllThumbnailsController() {
        listen();
    }

    private void listen() {
        getSelectAllItem().addActionListener(this);
        ThumbnailsPopupMenu.INSTANCE.getItemSelectNothing().addActionListener(this);
    }

    private JMenuItem getSelectAllItem() {
        return ThumbnailsPopupMenu.INSTANCE.getItemSelectAll();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();

        if (source == getSelectAllItem()) {
            GUI.getThumbnailsPanel().selectAll();
        } else if (source == ThumbnailsPopupMenu.INSTANCE.getItemSelectNothing()) {
            GUI.getThumbnailsPanel().clearSelection();
        }
    }
}
