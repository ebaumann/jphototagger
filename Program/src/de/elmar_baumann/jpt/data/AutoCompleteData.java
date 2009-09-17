/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.data;

import de.elmar_baumann.jpt.database.DatabaseContent;
import de.elmar_baumann.jpt.database.metadata.Column;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Contains autocomplete data (words, terms).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-10
 */
public final class AutoCompleteData {

    private final DatabaseContent db = DatabaseContent.INSTANCE;
    private final Set<Column> columns;
    private final List<String> content =
            Collections.synchronizedList(new LinkedList<String>());

    /**
     * Creates a new instance of this class.
     * 
     * @param columns columns. All words of
     *               {@link DatabaseContent#getContent(de.elmar_baumann.jpt.database.metadata.Column)}
     *               will be added to the autocomplete data.
     */
    AutoCompleteData(Collection<? extends Column> columns) {
        this.columns = new LinkedHashSet<Column>(columns);
        content.addAll(db.getContent(this.columns));
    }

    /**
     * Adds a string to the autocomplete data if it does not already exist.
     * 
     * @param string new string
     */
    void addString(String string) {
        synchronized (content) {
            if (!content.contains(string)) {
                content.add(string);
            }
        }
    }

    /**
     * Returns a <strong>reference</strong> to the list with the autocomplete
     * data.
     *
     * @return autocomplete data
     */
    public List<String> getData() {
        return content;
    }
}
