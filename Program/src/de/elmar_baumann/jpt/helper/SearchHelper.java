package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.controller.thumbnail.ControllerSortThumbnails;
import de.elmar_baumann.jpt.data.SavedSearch;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.comparator.FileSort;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-20
 */
public final class SearchHelper {

    /**
     * Calls {@link ThumbnailsPanel#setFileSortComparator(java.util.Comparator)}
     * with no sort order if the search uses custom SQL or the last used sort
     * order.
     *
     * @param search saved search
     */
    public static void setSort(SavedSearch search) {
        if (search.isCustomSql() ) {
            GUI.INSTANCE.getAppPanel().getPanelThumbnails().setFileSortComparator(FileSort.NO_SORT.getComparator());
            GUI.INSTANCE.getAppFrame().selectMenuItemUnsorted();
        } else {
            ControllerSortThumbnails.setLastSort();
        }
    }

    private SearchHelper() {
    }
}
