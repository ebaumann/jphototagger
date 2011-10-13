package org.jphototagger.program.module.thumbnails;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Comparator;

import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.app.ui.WaitDisplay;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class SortThumbnailsController implements ItemListener {

    public SortThumbnailsController() {
        listen();
    }

    private void listen() {
        GUI.getAppPanel().getComboBoxThumbnailsSort().addItemListener(this);
    }

    private void sortThumbnails(final Comparator<File> fileSortComparator) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                WaitDisplay.INSTANCE.show();
                ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();
                tnPanel.setFileSortComparator(fileSortComparator);
                tnPanel.sort();
                WaitDisplay.INSTANCE.hide();
            }
        });
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object item = e.getItem();
        if (item instanceof ThumbnailsSortComboBoxModel.FileSorter) {
            ThumbnailsSortComboBoxModel.FileSorter fileSorter = (ThumbnailsSortComboBoxModel.FileSorter) item;
            Comparator<File> comparator = fileSorter.getComparator();
            sortThumbnails(comparator);
        }
    }
}
