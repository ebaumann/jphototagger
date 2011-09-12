package org.jphototagger.program.model;

import javax.swing.DefaultComboBoxModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.repository.RenameTemplatesRepository;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.domain.repository.event.renametemplates.RenameTemplateDeletedEvent;
import org.jphototagger.domain.repository.event.renametemplates.RenameTemplateInsertedEvent;
import org.jphototagger.domain.repository.event.renametemplates.RenameTemplateUpdatedEvent;
import org.jphototagger.domain.templates.RenameTemplate;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.openide.util.Lookup;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ComboBoxModelRenameTemplates extends DefaultComboBoxModel {

    private static final long serialVersionUID = -5081726761734936168L;
    private final RenameTemplatesRepository renameTemplatesRepo = Lookup.getDefault().lookup(RenameTemplatesRepository.class);

    public ComboBoxModelRenameTemplates() {
        addElements();
        AnnotationProcessor.process(this);
    }

    private void addElements() {
        Repository repo = Lookup.getDefault().lookup(Repository.class);

        if (repo == null || !repo.isInit()) {
            return;
        }

        for (RenameTemplate template : renameTemplatesRepo.findAllRenameTemplates()) {
            addElement(template);
        }
    }

    private void updateTemplate(RenameTemplate template) {
        int index = getIndexOf(template);

        if (index >= 0) {
            ((RenameTemplate) getElementAt(index)).set(template);
            fireContentsChanged(this, index, index);
        }
    }

    private void insertTemplate(RenameTemplate template) {
        addElement(template);
        setSelectedItem(template);
    }

    private void deleteTemplate(RenameTemplate template) {
        removeElement(template);
    }

    @EventSubscriber(eventClass = RenameTemplateDeletedEvent.class)
    public void templateDeleted(final RenameTemplateDeletedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                deleteTemplate(evt.getTemplate());
            }
        });
    }

    @EventSubscriber(eventClass = RenameTemplateInsertedEvent.class)
    public void templateInserted(final RenameTemplateInsertedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                insertTemplate(evt.getTemplate());
            }
        });
    }

    @EventSubscriber(eventClass = RenameTemplateUpdatedEvent.class)
    public void templateUpdated(final RenameTemplateUpdatedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                updateTemplate(evt.getTemplate());
            }
        });
    }
}
