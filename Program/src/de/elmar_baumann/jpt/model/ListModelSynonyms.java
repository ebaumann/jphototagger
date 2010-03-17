/*
 * @(#)ListModelSynonyms.java    2010-02-07
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

package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.database.DatabaseSynonyms;
import de.elmar_baumann.jpt.event.DatabaseSynonymsEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseSynonymsListener;

import javax.swing.DefaultListModel;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class ListModelSynonyms extends DefaultListModel
        implements DatabaseSynonymsListener {
    private static final long serialVersionUID = -7595224452344062647L;
    private boolean           listen           = true;

    public enum Role { WORDS, SYNONYMS }

    private final Role role;
    private String     word;

    public ListModelSynonyms(Role role) {
        this.role = role;
        addElements();
        listen();
    }

    private void listen() {
        DatabaseSynonyms.INSTANCE.addListener(this);
    }

    @Override
    public void actionPerformed(DatabaseSynonymsEvent event) {
        if (!listen) {
            return;
        }

        if (event.isSynonymInserted()) {
            String w = event.getWord();
            String s = event.getSynonym();

            if (role.equals(Role.WORDS) &&!contains(w)) {
                addElement(w);
            } else if (isRoleSynonymForWord(w) &&!contains(s)) {
                addElement(s);
            }
        } else if (event.isSynonymDeleted()) {
            String w = event.getWord();
            String s = event.getSynonym();

            if (isRoleSynonymForWord(w) && contains(s)) {
                removeElement(s);
            } else if (role.equals(Role.WORDS) && contains(w)
                       &&!DatabaseSynonyms.INSTANCE.existsWord(w)) {
                removeElement(w);
            }
        } else if (event.isSynonymUpdated()) {
            String w  = event.getWord();
            String s  = event.getSynonym();
            String os = event.getOldSynonym();

            if (isRoleSynonymForWord(w) && contains(os)) {
                setElementAt(s, indexOf(os));
            }
        } else if (event.isWordUpdated()) {
            String w  = event.getWord();
            String ow = event.getOldWord();

            if (role.equals(Role.WORDS) && contains(ow)) {
                setElementAt(w, indexOf(ow));
            }
        } else if (event.isWordDeleted()) {
            String w = event.getWord();

            if (role.equals(Role.WORDS) && contains(w)) {
                removeElement(w);
            }
        }
    }

    private boolean isRoleSynonymForWord(String word) {
        return role.equals(Role.SYNONYMS) && (this.word != null)
               && this.word.equals(word);
    }

    public void addWord(String word) {
        assert role.equals(Role.WORDS);

        if (role.equals(Role.WORDS) &&!contains(word)) {
            addElement(word);
        }
    }

    public void removeWord(String word) {
        assert role.equals(Role.WORDS);

        if (role.equals(Role.WORDS) && contains(word)) {
            listen = false;
            DatabaseSynonyms.INSTANCE.deleteWord(word);
            listen = true;
            removeElement(word);
        }
    }

    public void changeWord(String oldWord, String newWord) {
        assert role.equals(Role.WORDS) &&!oldWord.equals(newWord);

        if (role.equals(Role.WORDS) && contains(oldWord)) {
            listen = false;
            DatabaseSynonyms.INSTANCE.updateWord(oldWord, newWord);
            listen = true;
            setElementAt(newWord, indexOf(oldWord));
        }
    }

    public void addSynonym(String synonym) {
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
        assert role.equals(Role.SYNONYMS);

        if (role.equals(Role.SYNONYMS)
                && ((this.word == null) ||!this.word.equals(word))) {
            this.word = word;
            addElements();
        }
    }

    private void addElements() {
        removeAllElements();
        listen = false;

        if (role.equals(Role.WORDS)) {
            for (String w : DatabaseSynonyms.INSTANCE.getAllWords()) {
                addElement(w);
            }
        } else if (role.equals(Role.SYNONYMS)) {
            for (String s : DatabaseSynonyms.INSTANCE.getSynonymsOf(word)) {
                addElement(s);
            }
        }

        listen = true;
    }
}
