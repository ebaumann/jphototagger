package org.jphototagger.domain.repository.event.wordsets;

/**
 * @author Elmar Baumann
 */
public final class WordsetWordUpdatedEvent {

    private final Object source;
    private final String wordsetName;
    private final String oldWord;
    private final String newWord;

    public WordsetWordUpdatedEvent(Object source, String wordsetName, String oldWord, String newWord) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        if (wordsetName == null) {
            throw new NullPointerException("wordsetName == null");
        }
        if (oldWord == null) {
            throw new NullPointerException("oldWord == null");
        }
        if (newWord == null) {
            throw new NullPointerException("newWord == null");
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
