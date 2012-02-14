package org.jphototagger.domain.wordsets;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jphototagger.lib.util.ObjectUtil;
import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
public final class Wordset {

    private long id = Integer.MIN_VALUE;
    private String name;
    private List<String> words = new ArrayList<String>();
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public Wordset(String name) {
        if (!StringUtil.hasContent(name)) {
            throw new IllegalArgumentException("Name must be defined and not empty: " + name);
        }
        this.name = name;
    }

    public void setName(String name) {
        Object old = this.name;
        this.name = name;
        propertyChangeSupport.firePropertyChange("name", old, this.name);
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        Object old = this.id;
        this.id = id;
        propertyChangeSupport.firePropertyChange("id", old, this.id);
    }

    public void setWords(List<? extends String> words) {
        this.words.clear();
        if (words != null) {
            this.words.addAll(words);
            propertyChangeSupport.firePropertyChange("words", null, this.words);
        }
    }

    public List<String> getWords() {
        List<String> sortedWords = new LinkedList<String>(words);
        Collections.sort(sortedWords);
        return sortedWords;
    }

    public boolean addToWords(String word) {
        if (StringUtil.hasContent(word) && !words.contains(word)) {
            boolean added = words.add(word);
            if (added) {
                propertyChangeSupport.firePropertyChange("words", null, this.words);
                return true;
            }
        }
        return false;
    }

    public boolean addToWords(Collection<? extends String> words) {
        if (words == null) {
            throw new NullPointerException("words == null");
        }
        boolean added = this.words.addAll(words);
        if (added) {
            propertyChangeSupport.firePropertyChange("words", null, this.words);
        }
        return added;
    }

    public boolean removeFromWords(String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }
        boolean removed = words.remove(word);
        if (removed) {
            propertyChangeSupport.firePropertyChange("words", null, this.words);
        }
        return removed;
    }

    public boolean removeFromWords(Collection<? extends String> words) {
        if (words == null) {
            throw new NullPointerException("words == null");
        }
        boolean removed = this.words.removeAll(words);
        if (removed) {
            propertyChangeSupport.firePropertyChange("words", null, this.words);
        }
        return removed;
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
            boolean set = words.set(oldWordIndex, newWord).equals(oldWord);
            if (set) {
                propertyChangeSupport.firePropertyChange("words", null, this.words);
                return true;
            }
        }
        return false;
    }

    public boolean containsWord(String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }
        return words.contains(word);
    }

    public int getWordCount() {
        return words.size();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return name == null ? "Wordset ?" : name;
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
        return ObjectUtil.equals(name, other.name)
                && ObjectUtil.equals(words, other.words);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 61 * hash + (this.words != null ? this.words.hashCode() : 0);
        return hash;
    }
}
