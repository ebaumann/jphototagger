package org.jphototagger.program.module.wordsets;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.repository.WordsetsRepository;
import org.jphototagger.domain.repository.event.wordsets.WordsetInsertedEvent;
import org.jphototagger.domain.repository.event.wordsets.WordsetRemovedEvent;
import org.jphototagger.domain.repository.event.wordsets.WordsetRenamedEvent;
import org.jphototagger.domain.repository.event.wordsets.WordsetUpdatedEvent;
import org.jphototagger.lib.util.ObjectUtil;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class WordsetNamesComboBoxModel extends DefaultComboBoxModel {

    private static final long serialVersionUID = 1L;

    public WordsetNamesComboBoxModel() {
        addElements();
        listen();
    }

    private void addElements() {
        addElement(WordsetPreferences.AUTOMATIC_WORDSET_NAME);
        WordsetsRepository repository = Lookup.getDefault().lookup(WordsetsRepository.class);
        if (repository != null) {
            List<String> wordsetNames = repository.findAllWordsetNames();
            for (String wordsetname : wordsetNames) {
                addElement(wordsetname);
            }
        }
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = WordsetInsertedEvent.class)
    public void wordsetAdded(WordsetInsertedEvent evt) {
        String wordsetName = evt.getWordset().getName();
        addElement(wordsetName);
    }

    @EventSubscriber(eventClass = WordsetRemovedEvent.class)
    public void wordsetRemoved(WordsetRemovedEvent evt) {
        removeElement(evt.getWordsetName());
    }

    @EventSubscriber(eventClass = WordsetUpdatedEvent.class)
    public void wordsetUpdated(WordsetUpdatedEvent evt) {
        String oldName = evt.getOldWordset().getName();
        String newName = evt.getNewWordset().getName();
        if (!ObjectUtil.equals(oldName, newName)) {
            wordsetRenamed(new WordsetRenamedEvent(this, oldName, newName));
        }
    }

    @EventSubscriber(eventClass = WordsetRenamedEvent.class)
    public void wordsetRenamed(WordsetRenamedEvent evt) {
        String oldName = evt.getOldName();
        int indexOfOldName = getIndexOf(oldName);
        if (indexOfOldName >= 0) {
            Object selectedItem = getSelectedItem();
            boolean selected = oldName.equals(selectedItem);
            String newName = evt.getNewName();
            removeElementAt(indexOfOldName);
            insertElementAt(newName, indexOfOldName);
            if (selected) {
                setSelectedItem(newName);
            }
        }
    }
}
