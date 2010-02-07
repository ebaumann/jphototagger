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
package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.database.DatabaseSynonyms;
import javax.swing.DefaultListModel;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-02-07
 */
public final class ListModelSynonyms extends DefaultListModel {

    private static final long serialVersionUID = -7595224452344062647L;

    public enum Role {
        WORDS,
        SYNONYMS
    }

    private final Role role;
    private String word;

    public ListModelSynonyms(Role role) {
        this.role = role;
        addElements();
    }

    public void addWord(String word) {
        assert role.equals(Role.WORDS);
        if (role.equals(Role.WORDS) && !contains(word)) {
            addElement(word);
        }
    }

    public void removeWord(String word) {
        assert role.equals(Role.WORDS);
        if (role.equals(Role.WORDS) && contains(word)) {
            DatabaseSynonyms.INSTANCE.deleteWord(word);
            removeElement(word);
        }
    }

    public void changeWord(String oldWord, String newWord) {
        assert role.equals(Role.WORDS) && !oldWord.equals(newWord);
        if (role.equals(Role.WORDS) && contains(oldWord)) {
            DatabaseSynonyms.INSTANCE.updateWord(oldWord, newWord);
            setElementAt(newWord, indexOf(oldWord));
        }
    }

    public void addSynonym(String synonym) {
        assert role.equals(Role.SYNONYMS) && word != null;
        if (role.equals(Role.SYNONYMS) && word != null && !contains(synonym)) {
            if (DatabaseSynonyms.INSTANCE.insert(word, synonym)) {
                addElement(synonym);
            } else {
                MessageDisplayer.error(null, "ListModelSynonyms.Error.AddSynonym", word, synonym);
            }
        }
    }

    public void removeSynonym(String synonym) {
        assert role.equals(Role.SYNONYMS) && word != null;
        if (role.equals(Role.SYNONYMS) && word != null && contains(synonym)) {
            if (DatabaseSynonyms.INSTANCE.delete(word, synonym) == 1) {
                removeElement(synonym);
            }
        }
    }

    public void changeSynonym(String oldSynonym, String newSynonym) {
        assert role.equals(Role.SYNONYMS) && word != null && !oldSynonym.equals(newSynonym);
        if (role.equals(Role.SYNONYMS) && word != null && contains(oldSynonym)) {
            if (DatabaseSynonyms.INSTANCE.updateSynonymOf(word, oldSynonym, newSynonym) == 1) {
                setElementAt(newSynonym, indexOf(oldSynonym));
            }
        }
    }

    public void setWord(String word) {
        assert role.equals(Role.SYNONYMS);
        if (role.equals(Role.SYNONYMS) &&
           (this.word == null || !this.word.equals(word))) {
            this.word = word;
            addElements();
        }
    }

    private void addElements() {
        removeAllElements();
        if (role.equals(Role.WORDS)) {
            for (String w : DatabaseSynonyms.INSTANCE.getAllWords()) {
                addElement(w);
            }
        } else if (role.equals(Role.SYNONYMS)) {
            for (String s : DatabaseSynonyms.INSTANCE.getSynonymsOf(word)) {
                addElement(s);
            }
        }
    }
}
