package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.database.DatabaseHierarchicalKeywords;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.TextModifyer;
import de.elmar_baumann.imv.view.dialogs.PathSelectionDialog;
import de.elmar_baumann.imv.view.panels.EditRepeatableTextEntryPanel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Modifies text returned from an {@link EditRepeatableTextEntryPanel}. Searches
 * for parent keywords and adds them.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/12
 */
public class TextModifierHierarchicalKeywords implements TextModifyer {

    private final String DELIM = XmpMetadata.getXmpTokenDelimiter();

    @Override
    @SuppressWarnings("unchecked")
    public String modify(String text, Collection<String> ignoreWords) {
        StringTokenizer strToken = new StringTokenizer(text, DELIM);
        List<String> keywords = new ArrayList<String>();
        while (strToken.hasMoreTokens()) {
            String keywordName = strToken.nextToken();
            Collection parentKeywords =
                    DatabaseHierarchicalKeywords.INSTANCE.getParentNames(
                    keywordName);
            keywords.add(keywordName);
            if (!ignoreWords.contains(keywordName)) {
                keywords.addAll(getUniquePath(keywordName, parentKeywords));
            }
        }
        return toKeywordString(new HashSet<String>(keywords)); // make them unique
    }

    private String toKeywordString(Collection<String> collection) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (String s : collection) {
            sb.append((index++ == 0
                    ? "" // NOI18N
                    : DELIM) + s);
        }
        return sb.toString();
    }

    private Collection<String> getUniquePath(
            String keywordName, Collection<Collection<String>> parentKeywords) {
        List<String> keywords = new ArrayList<String>();
        if (parentKeywords.size() <= 1) {
            addToKeywords(keywords, parentKeywords);
        } else {
            PathSelectionDialog dlg = new PathSelectionDialog(parentKeywords);
            dlg.setInfoMessage(Bundle.getString(
                    "TextModifierHierarchicalKeywords.Error.MultiplePaths", // NOI18N
                    keywordName));
            dlg.setVisible(true);
            if (dlg.isAccepted()) {
                addToKeywords(keywords, dlg.getSelPaths());
            }
        }
        return keywords;
    }

    private void addToKeywords(Collection<String> keywords,
            Collection<Collection<String>> parentKeywords) {
        for (Collection<String> collection : parentKeywords) {
            keywords.addAll(collection);
        }
    }
}
