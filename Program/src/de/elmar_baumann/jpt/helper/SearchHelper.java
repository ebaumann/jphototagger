/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
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
