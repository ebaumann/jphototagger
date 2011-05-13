package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerThumbnailsSelectAllOrNothing implements ActionListener {
    public ControllerThumbnailsSelectAllOrNothing() {
        listen();
    }

    private void listen() {
        getSelectAllItem().addActionListener(this);
        PopupMenuThumbnails.INSTANCE.getItemSelectNothing().addActionListener(this);
    }

    private JMenuItem getSelectAllItem() {
        return PopupMenuThumbnails.INSTANCE.getItemSelectAll();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();

        if (source == getSelectAllItem()) {
            GUI.getThumbnailsPanel().selectAll();
        } else if (source == PopupMenuThumbnails.INSTANCE.getItemSelectNothing()) {
            GUI.getThumbnailsPanel().clearSelection();
        }
    }
}
