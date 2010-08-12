/*
 * @(#)ListModelSynonyms.java    Created on 2010-02-07
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.model;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.database.DatabaseSynonyms;
import org.jphototagger.program.event.listener.DatabaseSynonymsListener;

import javax.swing.DefaultListModel;
import org.jphototagger.program.database.ConnectionPool;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class ListModelSynonyms extends DefaultListModel
        implements DatabaseSynonymsListener {
    private static final long serialVersionUID = -7595224452344062647L;
    private boolean           listen           = true;
    private final Role        role;
    private String            word;

    public enum Role { WORDS, SYNONYMS }

    public ListModelSynonyms(Role role) {
        if (role == null) {
            throw new NullPointerException("role == null");
        }

        this.role = role;
        addElements();
        listen();
    }

    private void listen() {
        DatabaseSynonyms.INSTANCE.addListener(this);
    }

    private boolean isRoleSynonymForWord(String word) {
        return role.equals(Role.SYNONYMS) && (this.word != null)
               && this.word.equals(word);
    }

    public void addWord(String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }

        assert role.equals(Role.WORDS);

        if (role.equals(Role.WORDS) &&!contains(word)) {
            addElement(word);
        }
    }

    public void removeWord(String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }

        assert role.equals(Role.WORDS);

        if (role.equals(Role.WORDS) && contains(word)) {
            listen = false;
            DatabaseSynonyms.INSTANCE.deleteWord(word);
            listen = true;
            removeElement(word);
        }
    }

    public void changeWord(String oldWord, String newWord) {
        if (oldWord == null) {
            throw new NullPointerException("oldWord == null");
        }

        if (newWord == null) {
            throw new NullPointerException("newWord == null");
        }

        assert role.equals(Role.WORDS) &&!oldWord.equals(newWord);

        if (role.equals(Role.WORDS) && contains(oldWord)) {
            listen = false;
            DatabaseSynonyms.INSTANCE.updateWord(oldWord, newWord);
            listen = true;
            setElementAt(newWord, indexOf(oldWord));
        }
    }

    public void addSynonym(String synonym) {
        if (synonym == null) {
            throw new NullPointerException("synonym == null");
        }

        assert role.equals(Role.SYNONYMS) && (word != null);

        if (role.equals(Role.SYNONYMS) && (word != null) &&!contains(synonym)) {
            listen = false;

            if (DatabaseSynonyms.INSTANCE.insert(word, synonym) == 1) {
                addElement(synonym);
            } else {
                MessageDisplayer.error(null,
                                       "ListModelSynonyms.Error.AddSynonym",
                                       word, synonym);
            }

            listen = true;
        }
    }

    public void removeSynonym(String synonym) {
        if (synonym == null) {
            throw new NullPointerException("synonym == null");
        }

        assert role.equals(Role.SYNONYMS) && (word != null);

        if (role.equals(Role.SYNONYMS) && (word != null) && contains(synonym)) {
            listen = false;

            if (DatabaseSynonyms.INSTANCE.delete(word, synonym) == 1) {
                removeElement(synonym);
            }

            listen = true;
        }
    }

    public void changeSynonym(String oldSynonym, String newSynonym) {
        if (newSynonym == null) {
            throw new NullPointerException("newSynonym == null");
        }

        if (newSynonym == null) {
            throw new NullPointerException("newSynonym == null");
        }

        assert role.equals(Role.SYNONYMS) && (word != null)
               &&!oldSynonym.equals(newSynonym);

        if (role.equals(Role.SYNONYMS) && (word != null)
                && contains(oldSynonym)) {
            listen = false;

            if (DatabaseSynonyms.INSTANCE.updateSynonymOf(word, oldSynonym,
                    newSynonym) == 1) {
                setElementAt(newSynonym, indexOf(oldSynonym));
            }

            listen = true;
        }
    }

    public void setWord(String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }

        assert role.equals(Role.SYNONYMS);

        if (role.equals(Role.SYNONYMS)
                && ((this.word == null) ||!this.word.equals(word))) {
            this.word = word;
            addElements();
        }
    }

    @Override
    public void synonymOfWordDeleted(String word, String synonym) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }

        if (synonym == null) {
            throw new NullPointerException("synonym == null");
        }

        if (listen && isRoleSynonymForWord(word) && contains(synonym)) {
            removeElement(synonym);
        } else if (listen && role.equals(Role.WORDS) && contains(word)
                   &&!DatabaseSynonyms.INSTANCE.existsWord(word)) {
            removeElement(word);
        }
    }

    @Override
    public void synonymInserted(String word, String synonym) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }

        if (synonym == null) {
            throw new NullPointerException("synonym == null");
        }

        if (listen && role.equals(Role.WORDS) &&!contains(word)) {
            addElement(word);
        } else if (listen && isRoleSynonymForWord(word) &&!contains(synonym)) {
            addElement(synonym);
        }
    }

    @Override
    public void synonymOfWordRenamed(String word, String oldSynonymName,
                                     String newSynonymName) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }

        if (oldSynonymName == null) {
            throw new NullPointerException("oldSynonymName == null");
        }

        if (newSynonymName == null) {
            throw new NullPointerException("newSynonymName == null");
        }

        if (listen && isRoleSynonymForWord(word) && contains(oldSynonymName)) {
            setElementAt(newSynonymName, indexOf(oldSynonymName));
        }
    }

    @Override
    public void synonymRenamed(String oldSynonymName, String newSynonymName) {
        if (oldSynonymName == null) {
            throw new NullPointerException("oldSynonymName == null");
        }

        if (newSynonymName == null) {
            throw new NullPointerException("newSynonymName == null");
        }

        if (listen && role.equals(Role.SYNONYMS) && contains(oldSynonymName)) {
            setElementAt(newSynonymName, indexOf(oldSynonymName));
        }
    }

    @Override
    public void wordDeleted(String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }

        if (listen && role.equals(Role.WORDS) && contains(word)) {
            removeElement(word);
        } else if (listen && isRoleSynonymForWord(word)) {
            removeAllElements();
        }
    }

    @Override
    public void wordRenamed(String fromName, String toName) {
        if (fromName == null) {
            throw new NullPointerException("fromName == null");
        }

        if (toName == null) {
            throw new NullPointerException("toName == null");
        }

        if (listen && role.equals(Role.WORDS) && contains(fromName)) {
            setElementAt(toName, indexOf(fromName));
        }
    }

    private void addElements() {
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

        removeAllElements();
        listen = false;

        if (role.equals(Role.WORDS)) {
            for (String w : DatabaseSynonyms.INSTANCE.getAllWords()) {
                addElement(w);
            }
        } else if (role.equals(Role.SYNONYMS) && word != null) {
            for (String s : DatabaseSynonyms.INSTANCE.getSynonymsOf(word)) {
                addElement(s);
            }
        }

        listen = true;
    }
}
