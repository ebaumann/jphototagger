package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.WaitDisplay;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.EventQueue;

import java.io.File;

import java.util.Comparator;

import javax.swing.JRadioButtonMenuItem;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerSortThumbnails implements ActionListener {
    public ControllerSortThumbnails() {
        listen();
        GUI.getAppFrame().getMenuItemOfSortCmp(
            GUI.getThumbnailsPanel().getFileSortComparator()).setSelected(true);
    }

    private void listen() {
        for (JRadioButtonMenuItem item : GUI.getAppFrame().getSortMenuItems()) {
            item.addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        sortThumbnails(evt);
    }

    public static void setLastSort() {
        Comparator<File> cmp =
            ControllerThumbnailsPanelPersistence.getFileSortComparator();

        GUI.getThumbnailsPanel().setFileSortComparator(cmp);
        GUI.getAppFrame().getMenuItemOfSortCmp(cmp).setSelected(true);
    }

    private void sortThumbnails(final ActionEvent evt) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                WaitDisplay.show();

                JRadioButtonMenuItem item =
                    (JRadioButtonMenuItem) evt.getSource();
                Comparator<File> sortCmp =
                    GUI.getAppFrame().getSortCmpOfMenuItem(item);
                ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

                ControllerFactory.INSTANCE
                    .getController(ControllerThumbnailsPanelPersistence.class)
                    .setFileSortComparator(sortCmp);
                item.setSelected(true);
                tnPanel.setFileSortComparator(sortCmp);
                tnPanel.sort();
                WaitDisplay.hide();
            }
        });
    }
}
