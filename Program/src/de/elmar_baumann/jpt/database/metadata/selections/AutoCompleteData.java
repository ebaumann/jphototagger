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
package de.elmar_baumann.jpt.database.metadata.selections;

import de.elmar_baumann.jpt.database.DatabaseContent;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.lib.util.CollectionUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Contains autocomplete data (words, terms).
 *
 * @author  Elmar Baumann
 * @version 2008-09-10
 */
public final class AutoCompleteData {

    private final DatabaseContent    db     = DatabaseContent.INSTANCE;
    private final Set<Column>        columns;
    private final LinkedList<String> words  = new LinkedList<String>();

    /**
     * Creates a new instance of this class.
     *
     * @param columns columns. All words of
     *               {@link DatabaseContent#getDistinctValuesOf(de.elmar_baumann.jpt.database.metadata.Column)}
     *               will be added to the autocomplete data.
     */
    AutoCompleteData(Collection<? extends Column> columns) {
        this.columns = new LinkedHashSet<Column>(columns);
        words.addAll(wordsOf(db.getDistinctValuesOf(this.columns)));
        Collections.sort(words);
    }

    public boolean add(String word) {
        synchronized (words) {
            for (String wd : wordsOf(word)) {
                if (Collections.binarySearch(words, wd) < 0) {
                    CollectionUtil.binaryInsert(words, wd);
                    return true;
                }
            }
        }
        return false;
    }

    private List<String> wordsOf(Collection<String> strings) {
        List<String> wordsOf = new ArrayList<String>(strings.size());
        for (String string : strings) {
            wordsOf.addAll(wordsOf(string));
        }
        return wordsOf;
    }

    private List<String> wordsOf(String s) {
        List<String>    wordsOf = new ArrayList<String>();
        StringTokenizer st      = new StringTokenizer(s, " \t");

        while (st.hasMoreTokens()) {
            wordsOf.add(st.nextToken().trim());
        }
        return wordsOf;
    }

    /**
     * Returns a <strong>reference</strong> to the list with the autocomplete
     * data.
     *
     * @return autocomplete data
     */
    public List<String> get() {
        return words;
    }
}
