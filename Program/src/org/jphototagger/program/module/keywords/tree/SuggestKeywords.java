package org.jphototagger.program.module.keywords.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.jphototagger.api.text.Suggest;
import org.jphototagger.domain.metadata.keywords.Keyword;
import org.jphototagger.domain.metadata.keywords.KeywordType;
import org.jphototagger.domain.repository.KeywordsRepository;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class SuggestKeywords implements Suggest {

    private final KeywordsRepository repo = Lookup.getDefault().lookup(KeywordsRepository.class);
    private boolean accepted;

    @Override
    public Collection<String> suggest(String keywordName) {
        if (keywordName == null) {
            throw new NullPointerException("keywordName == null");
        }
        List<String> parentKeywordNames = new ArrayList<String>();
        Collection<Collection<Keyword>> parentKeywords = repo.findParentKeywords(keywordName, KeywordType.REAL_KEYWORD);

        parentKeywordNames.addAll(chooseParentKeywords(keywordName, toStringCollection(parentKeywords)));

        return new HashSet<String>(parentKeywordNames);    // make them unique
    }

    private Collection<Collection<String>> toStringCollection(Collection<Collection<Keyword>> keywordCollection) {
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

    private Collection<String> chooseParentKeywords(String keywordName, Collection<Collection<String>> allParentKeywords) {
        List<String> choosenParentKeywords = new ArrayList<String>();
        accepted = true;
        if (allParentKeywords.size() > 0) {
            PathSelectionDialog dlg = new PathSelectionDialog(allParentKeywords,
                    PathSelectionDialog.Mode.DISTINCT_ELEMENTS);

            dlg.setInfoMessage(Bundle.getString(SuggestKeywords.class, "SuggestKeywords.Info", keywordName));
            dlg.setVisible(true);

            if (dlg.isAccepted()) {
                addToKeywords(choosenParentKeywords, dlg.getSelPaths());
            }
            accepted = dlg.isAccepted();
        }

        return choosenParentKeywords;
    }

    private void addToKeywords(Collection<String> keywords, Collection<Collection<String>> parentKeywords) {
        for (Collection<String> collection : parentKeywords) {
            keywords.addAll(collection);
        }
    }

    @Override
    public String getDescription() {
        return Bundle.getString(SuggestKeywords.class, "SuggestKeywords.Description");
    }

    @Override
    public String getRequiresDescription() {
        return Bundle.getString(SuggestKeywords.class, "SuggestKeywords.RequiresDescription");
    }

    @Override
    public boolean isAccepted() {
        return accepted;
    }
}
