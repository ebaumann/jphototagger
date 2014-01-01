package org.jphototagger.domain.repository.event.wordsets;

import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
public final class WordsetRemovedEvent {

    private final Object source;
    private final String wordsetName;

    public WordsetRemovedEvent(Object source, String wordsetName) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        if (!StringUtil.hasContent(wordsetName)) {
            throw new IllegalArgumentException("Wordset name must have content: " + wordsetName);
        }
        this.source = source;
        this.wordsetName = wordsetName;
    }

    public Object getSource() {
        return source;
    }

    public String getWordsetName() {
        return wordsetName;
    }
}
