package org.jphototagger.program.module.thumbnails;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Comparator;

import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.comparator.FileSort;
import org.jphototagger.program.app.ui.WaitDisplay;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class SortThumbnailsController implements ItemListener {

    private static volatile Comparator<File> currentSortComparator = ThumbnailsPanelPersistenceController.getFileSortComparator();

    public SortThumbnailsController() {
        sortThumbnailsWithCurrentSortOrder();
        listen();
    }

    private void listen() {
        GUI.getAppPanel().getComboBoxThumbnailsSort().addItemListener(this);
    }

    public static void sortThumbnailsWithCurrentSortOrder() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                GUI.getThumbnailsPanel().setFileSortComparator(currentSortComparator);
            }
        });
    }

    private void sortThumbnails(final Comparator<File> fileSortComparator) {
        currentSortComparator = fileSortComparator;
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                WaitDisplay.INSTANCE.show();

                ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

                ControllerFactory.INSTANCE.getController(
                        ThumbnailsPanelPersistenceController.class).setFileSortComparator(fileSortComparator);
                tnPanel.setFileSortComparator(fileSortComparator);
                tnPanel.sort();
                WaitDisplay.INSTANCE.hide();
            }
        });
    }

    public static void setUnsorted() {
        SortThumbnailsController controller = ControllerFactory.INSTANCE.getController(SortThumbnailsController.class);
        if (controller != null) {
            controller.sortThumbnails(FileSort.NO_SORT.getComparator());
        }
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
