package org.jphototagger.domain.repository.event.wordsets;

import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
public final class WordsetWordRenamedEvent {

    private final Object source;
    private final String wordsetName;
    private final String oldWord;
    private final String newWord;

    public WordsetWordRenamedEvent(Object source, String wordsetName, String oldWord, String newWord) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        if (!StringUtil.hasContent(wordsetName)) {
            throw new IllegalArgumentException("Wordset name must have content: " + wordsetName);
        }
        if (!StringUtil.hasContent(oldWord)) {
            throw new IllegalArgumentException("Old word must have content: " + oldWord);
        }
        if (!StringUtil.hasContent(newWord)) {
            throw new IllegalArgumentException("New word name must have content: " + newWord);
        }
        this.source = source;
        this.wordsetName = wordsetName;
        this.oldWord = oldWord;
        this.newWord = newWord;
    }

    public String getNewWord() {
        return newWord;
    }

    public String getOldWord() {
        return oldWord;
    }

    public Object getSource() {
        return source;
    }

    public String getWordsetName() {
        return wordsetName;
    }
}
