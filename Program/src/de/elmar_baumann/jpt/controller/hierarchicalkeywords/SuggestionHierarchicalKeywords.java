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
public class SuggestionHierarchicalKeywords implements Suggest {

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

    private Collection<String> chooseParentKeywords(
            String keywordName,
            Collection<Collection<String>> parentKeywords) {

        List<String> keywords = new ArrayList<String>();
        if (parentKeywords.size() > 0) {
            PathSelectionDialog dlg = new PathSelectionDialog(
                    parentKeywords, PathSelectionDialog.Mode.DISTINCT_ELEMENTS);
            dlg.setInfoMessage(Bundle.getString(
                    "SuggestionHierarchicalKeywords.Info", // NOI18N
                    keywordName));
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

    private Collection<Collection<String>> toStringCollection(
            Collection<Collection<HierarchicalKeyword>> keywordCollection) {

        List<Collection<String>> strings = new ArrayList<Collection<String>>();
        for (Collection<HierarchicalKeyword> keywords : keywordCollection) {
            List<String> keywordStrings = new ArrayList<String>(keywords.size());
            for (HierarchicalKeyword keyword : keywords) {
                keywordStrings.add(keyword.getKeyword());
            }
            strings.add(keywordStrings);
        }
        return strings;

    }

    @Override
    public String getDescription() {
        return Bundle.getString("SuggestionHierarchicalKeywords.Description"); // NOI18N
    }
}
