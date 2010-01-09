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
package de.elmar_baumann.jpt.controller.hierarchicalkeywords;

import de.elmar_baumann.jpt.data.HierarchicalKeyword;
import de.elmar_baumann.jpt.database.DatabaseHierarchicalKeywords;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.types.Suggest;
import de.elmar_baumann.jpt.view.dialogs.PathSelectionDialog;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Suggest keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-12
 */
public class SuggestHierarchicalKeywords implements Suggest {

    @Override
    public Collection<String> suggest(String keywordName) {

        List<String> parentKeywordNames = new ArrayList<String>();
        Collection<Collection<HierarchicalKeyword>> parentKeywords =
                DatabaseHierarchicalKeywords.INSTANCE.getParents(
                keywordName,
                DatabaseHierarchicalKeywords.Select.REAL_KEYWORDS);
        parentKeywordNames.addAll(
                chooseParentKeywords(
                keywordName, toStringCollection(parentKeywords)));
        return new HashSet<String>(parentKeywordNames); // make them unique
    }

    private Collection<Collection<String>> toStringCollection(
            Collection<Collection<HierarchicalKeyword>> keywordCollection) {

        List<Collection<String>> strings = new ArrayList<Collection<String>>();
        for (Collection<HierarchicalKeyword> keywords : keywordCollection) {
            List<String> keywordStrings = new ArrayList<String>(keywords.size());
            for (HierarchicalKeyword keyword : keywords) {
                keywordStrings.add(keyword.getName());
            }
            strings.add(keywordStrings);
        }
        return strings;

    }

    private Collection<String> chooseParentKeywords(
            String keywordName,
            Collection<Collection<String>> parentKeywords) {

        List<String> keywords = new ArrayList<String>();
        if (parentKeywords.size() > 0) {
            PathSelectionDialog dlg = new PathSelectionDialog(
                    parentKeywords, PathSelectionDialog.Mode.DISTINCT_ELEMENTS);
            dlg.setInfoMessage(
                    Bundle.getString("SuggestionHierarchicalKeywords.Info", keywordName));
            dlg.setVisible(true);
            if (dlg.isAccepted()) {
                addToKeywords(keywords, dlg.getSelPaths());
            }
        }
        return keywords;
    }

    private void addToKeywords(
            Collection<String> keywords,
            Collection<Collection<String>> parentKeywords) {

        for (Collection<String> collection : parentKeywords) {
            keywords.addAll(collection);
        }
    }

    @Override
    public String getDescription() {
        return Bundle.getString("SuggestionHierarchicalKeywords.Description");
    }
}
