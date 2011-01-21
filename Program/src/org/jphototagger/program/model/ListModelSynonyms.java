package org.jphototagger.program.model;

import java.awt.EventQueue;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseSynonyms;
import org.jphototagger.program.event.listener.DatabaseSynonymsListener;

import javax.swing.DefaultListModel;

/**
 *
 *
 * @author Elmar Baumann
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

    public void addWord(final String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }

        assert role.equals(Role.WORDS);

        if (role.equals(Role.WORDS) &&!contains(word)) {
            addElement(word);
        }
    }

    public void removeWord(final String word) {
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

    public void changeWord(final String oldWord, final String newWord) {
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

    public void addSynonym(final String synonym) {
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

    public void removeSynonym(final String synonym) {
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

    public void changeSynonym(final String oldSynonym,
                              final String newSynonym) {
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

    public void setWord(final String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }

        final ListModelSynonyms model = this;

        assert role.equals(Role.SYNONYMS);

        if (role.equals(Role.SYNONYMS)
                && ((model.word == null) ||!model.word.equals(word))) {
            model.word = word;
            addElements();
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
        } else if (role.equals(Role.SYNONYMS) && (word != null)) {
            for (String s : DatabaseSynonyms.INSTANCE.getSynonymsOf(word)) {
                addElement(s);
            }
        }

        listen = true;
    }

    private void insertSynonym(String word, String synonym) {
        if (listen && role.equals(Role.WORDS) &&!contains(word)) {
            addElement(word);
        } else if (listen && isRoleSynonymForWord(word) &&!contains(synonym)) {
            addElement(synonym);
        }
    }

    private void deleteSynonymOfWord(String word, String synonym) {
                if (listen && isRoleSynonymForWord(word) && contains(synonym)) {
                    removeElement(synonym);
                } else if (listen && role.equals(Role.WORDS) && contains(word)
                           &&!DatabaseSynonyms.INSTANCE.existsWord(word)) {
                    removeElement(word);
                }
            }

    private void deleteWord(String word) {
        if (listen && role.equals(Role.WORDS) && contains(word)) {
            removeElement(word);
        } else if (listen && isRoleSynonymForWord(word)) {
            removeAllElements();
    }
    }

    private void renameSynonymOfWord(String word, String oldSynonymName,
                                     String newSynonymName) {
        if (listen && isRoleSynonymForWord(word) && contains(oldSynonymName)) {
            setElementAt(newSynonymName, indexOf(oldSynonymName));
        }
    }

    private void renameSynonym(String oldSynonymName, String newSynonymName) {
        if (listen && role.equals(Role.SYNONYMS) && contains(oldSynonymName)) {
            setElementAt(newSynonymName, indexOf(oldSynonymName));
        }
    }

    private void renameWord(String fromName, String toName) {
        if (listen && role.equals(Role.WORDS) && contains(fromName)) {
            setElementAt(toName, indexOf(fromName));
        }
    }

            @Override
    public void synonymOfWordDeleted(final String word, final String synonym) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                deleteSynonymOfWord(word, synonym);
                }
        });
            }

    @Override
    public void synonymInserted(final String word, final String synonym) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                insertSynonym(word, synonym);
            }
        });
    }

    @Override
    public void synonymOfWordRenamed(final String word,
                                     final String oldSynonymName,
                                     final String newSynonymName) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                renameSynonymOfWord(word, oldSynonymName, newSynonymName);
                }
        });
    }

    @Override
    public void synonymRenamed(final String oldSynonymName,
                               final String newSynonymName) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                renameSynonym(oldSynonymName, newSynonymName);
                }
        });
    }

    @Override
    public void wordDeleted(final String word) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                deleteWord(word);
                }
        });
    }

    @Override
    public void wordRenamed(final String fromName, final String toName) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                renameWord(fromName, toName);
                }
        });
    }
        }
