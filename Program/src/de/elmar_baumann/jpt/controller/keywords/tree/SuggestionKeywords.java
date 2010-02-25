package de.elmar_baumann.jpt.controller.keywords.tree;

import de.elmar_baumann.jpt.data.Keyword;
import de.elmar_baumann.jpt.database.DatabaseKeywords;
import de.elmar_baumann.jpt.resource.JptBundle;
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
public class SuggestionKeywords implements Suggest {

    @Override
    public Collection<String> suggest(String keywordName) {

        List<String> parentKeywordNames = new ArrayList<String>();
        Collection<Collection<Keyword>> parentKeywords =
                DatabaseKeywords.INSTANCE.getParents(
                keywordName,
                DatabaseKeywords.Select.REAL_KEYWORDS);
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
            dlg.setInfoMessage(JptBundle.INSTANCE.getString("SuggestKeywords.Info", keywordName));
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
            Collection<Collection<Keyword>> keywordCollection) {

        List<Collection<String>> strings = new ArrayList<Collection<String>>();
        for (Collection<Keyword> keywords : keywordCollection) {
            List<String> keywordStrings = new ArrayList<String>(keywords.size());
            for (Keyword keyword : keywords) {
                keywordStrings.add(keyword.getName());
            }
            strings.add(keywordStrings);
        }
        return strings;

    }

    @Override
    public String getDescription() {
        return JptBundle.INSTANCE.getString("SuggestKeywords.Description");
    }
}
