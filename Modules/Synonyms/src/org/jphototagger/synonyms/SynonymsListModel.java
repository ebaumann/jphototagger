package org.jphototagger.synonyms;

import javax.swing.DefaultListModel;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.domain.repository.SynonymsRepository;
import org.jphototagger.domain.repository.event.synonyms.SynonymInsertedEvent;
import org.jphototagger.domain.repository.event.synonyms.SynonymOfWordDeletedEvent;
import org.jphototagger.domain.repository.event.synonyms.SynonymOfWordRenamedEvent;
import org.jphototagger.domain.repository.event.synonyms.SynonymRenamedEvent;
import org.jphototagger.domain.repository.event.synonyms.WordDeletedEvent;
import org.jphototagger.domain.repository.event.synonyms.WordRenamedEvent;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class SynonymsListModel extends DefaultListModel<Object> {

    private static final long serialVersionUID = 1L;
    private boolean listen = true;
    private final Role role;
    private String word;
    private final SynonymsRepository synonymsRepo = Lookup.getDefault().lookup(SynonymsRepository.class);

    public enum Role {

        WORDS, SYNONYMS
    }

    public SynonymsListModel(Role role) {
        if (role == null) {
            throw new NullPointerException("role == null");
        }

        this.role = role;
        addElements();
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    private boolean isRoleSynonymForWord(String word) {
        return role.equals(Role.SYNONYMS) && (this.word != null) && this.word.equals(word);
    }

    public void addWord(final String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }

        assert role.equals(Role.WORDS);

        if (role.equals(Role.WORDS) && !contains(word)) {
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
            synonymsRepo.deleteWord(word);
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

        assert role.equals(Role.WORDS) && !oldWord.equals(newWord);

        if (role.equals(Role.WORDS) && contains(oldWord)) {
            listen = false;
            synonymsRepo.updateWord(oldWord, newWord);
            listen = true;
            setElementAt(newWord, indexOf(oldWord));
        }
    }

    public void addSynonym(final String synonym) {
        if (synonym == null) {
            throw new NullPointerException("synonym == null");
        }

        assert role.equals(Role.SYNONYMS) && (word != null);

        if (role.equals(Role.SYNONYMS) && (word != null) && !contains(synonym)) {
            listen = false;

            if (synonymsRepo.saveSynonym(word, synonym) == 1) {
                addElement(synonym);
            } else {
                String message = Bundle.getString(SynonymsListModel.class, "SynonymsListModel.Error.AddSynonym", word, synonym);

                MessageDisplayer.error(null, message);
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

            if (synonymsRepo.deleteSynonym(word, synonym) == 1) {
                removeElement(synonym);
            }

            listen = true;
        }
    }

    public void changeSynonym(final String oldSynonym, final String newSynonym) {
        if (newSynonym == null) {
            throw new NullPointerException("newSynonym == null");
        }

        if (newSynonym == null) {
            throw new NullPointerException("newSynonym == null");
        }

        assert role.equals(Role.SYNONYMS) && (word != null) && !oldSynonym.equals(newSynonym);

        if (role.equals(Role.SYNONYMS) && (word != null) && contains(oldSynonym)) {
            listen = false;

            if (synonymsRepo.updateSynonymOfWord(word, oldSynonym, newSynonym) == 1) {
                setElementAt(newSynonym, indexOf(oldSynonym));
            }

            listen = true;
        }
    }

    public void setWord(final String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }

        final SynonymsListModel model = this;

        assert role.equals(Role.SYNONYMS);

        if (role.equals(Role.SYNONYMS) && ((model.word == null) || !model.word.equals(word))) {
            model.word = word;
            addElements();
        }
    }

    private void addElements() {
        Repository repo = Lookup.getDefault().lookup(Repository.class);

        if (repo == null || !repo.isInit()) {
            return;
        }

        removeAllElements();
        listen = false;

        if (role.equals(Role.WORDS)) {
            for (String w : synonymsRepo.findAllWords()) {
                addElement(w);
            }
        } else if (role.equals(Role.SYNONYMS) && (word != null)) {
            for (String s : synonymsRepo.findSynonymsOfWord(word)) {
                addElement(s);
            }
        }

        listen = true;
    }

    private void insertSynonym(String word, String synonym) {
        if (listen && role.equals(Role.WORDS) && !contains(word)) {
            addElement(word);
        } else if (listen && isRoleSynonymForWord(word) && !contains(synonym)) {
            addElement(synonym);
        }
    }

    private void deleteSynonymOfWord(String word, String synonym) {
        if (listen && isRoleSynonymForWord(word) && contains(synonym)) {
            removeElement(synonym);
        } else if (listen && role.equals(Role.WORDS) && contains(word) && !synonymsRepo.existsWord(word)) {
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

    private void renameSynonymOfWord(String word, String oldSynonymName, String newSynonymName) {
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

    @EventSubscriber(eventClass = SynonymOfWordDeletedEvent.class)
    public void synonymOfWordDeleted(final SynonymOfWordDeletedEvent evt) {
        deleteSynonymOfWord(word, evt.getSynonym());
    }

    @EventSubscriber(eventClass = SynonymInsertedEvent.class)
    public void synonymInserted(final SynonymInsertedEvent evt) {
        insertSynonym(word, evt.getSynonym());
    }

    @EventSubscriber(eventClass = SynonymOfWordRenamedEvent.class)
    public void synonymOfWordRenamed(final SynonymOfWordRenamedEvent evt) {
        renameSynonymOfWord(word, evt.getOldSynonymName(), evt.getNewSynonymName());
    }

    @EventSubscriber(eventClass = SynonymRenamedEvent.class)
    public void synonymRenamed(final SynonymRenamedEvent evt) {
        renameSynonym(evt.getOldSynonymName(), evt.getNewSynonymName());
    }

    @EventSubscriber(eventClass = WordDeletedEvent.class)
    public void wordDeleted(final WordDeletedEvent evt) {
        deleteWord(word);
    }

    @EventSubscriber(eventClass = WordRenamedEvent.class)
    public void wordRenamed(final WordRenamedEvent evt) {
        renameWord(evt.getFromName(), evt.getToName());
    }
}
