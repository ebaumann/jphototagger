package org.jphototagger.domain.wordsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jphototagger.lib.util.ObjectUtil;
import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
public final class Wordset {

    private String name;
    private List<String> words = new ArrayList<String>();

    public Wordset(String name) {
        if (!StringUtil.hasContent(name)) {
            throw new IllegalArgumentException("Name must be defined and not empty: " + name);
        }
        this.name = name;
    }

    public void setName(String name) {
        if (!StringUtil.hasContent(name)) {
            throw new IllegalArgumentException("Name must be defined and not empty: " + name);
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setWords(List<? extends String> words) {
        if (words == null) {
            throw new NullPointerException("words == null");
        }
        assertNotEmptyWord(words);
        this.words.clear();
        this.words.addAll(words);
    }

    private void assertNotEmptyWord(List<? extends String> words) {
        for (String word : words) {
            if (!StringUtil.hasContent(word)) {
                throw new IllegalArgumentException("Word must be defined and not empty: " + word + ", words: " + words);
            }
        }
    }

    public List<String> getWords() {
        return Collections.unmodifiableList(words);
    }

    public boolean addToWords(String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }
        if (!words.contains(word)) {
            return words.add(word);
        }
        return false;
    }

    public boolean removeFromWords(String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }
        return words.remove(word);
    }

    public boolean updateWord(String oldWord, String newWord) {
        if (oldWord == null) {
            throw new NullPointerException("oldWord == null");
        }
        if (newWord == null) {
            throw new NullPointerException("newWord == null");
        }
        if (newWord.equals(oldWord)) {
            return false;
        }
        int oldWordIndex = words.indexOf(oldWord);
        if (oldWordIndex >= 0) {
            return (words.set(oldWordIndex, newWord).equals(oldWord));
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Wordset)) {
            return false;
        }
        Wordset other = (Wordset) obj;
        return ObjectUtil.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
