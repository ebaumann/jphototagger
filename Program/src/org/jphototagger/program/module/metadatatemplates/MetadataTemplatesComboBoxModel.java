package org.jphototagger.program.module.metadatatemplates;

import java.util.List;

import javax.swing.DefaultComboBoxModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.domain.repository.MetadataTemplatesRepository;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.domain.repository.event.metadatatemplates.MetadataTemplateDeletedEvent;
import org.jphototagger.domain.repository.event.metadatatemplates.MetadataTemplateInsertedEvent;
import org.jphototagger.domain.repository.event.metadatatemplates.MetadataTemplateRenamedEvent;
import org.jphototagger.domain.repository.event.metadatatemplates.MetadataTemplateUpdatedEvent;
import org.jphototagger.domain.templates.MetadataTemplate;

/**
 * @author Elmar Baumann, Tobias Stening
 */
public final class MetadataTemplatesComboBoxModel extends DefaultComboBoxModel {

    private static final long serialVersionUID = 1L;
    private final MetadataTemplatesRepository templateRepo = Lookup.getDefault().lookup(MetadataTemplatesRepository.class);

    public MetadataTemplatesComboBoxModel() {
        addElements();
        listen();
    }

    private void addElements() {
        Repository repo = Lookup.getDefault().lookup(Repository.class);

        if (repo == null || !repo.isInit()) {
            return;
        }

        List<MetadataTemplate> templates = templateRepo.findAllMetadataTemplates();

        for (MetadataTemplate template : templates) {
            addElement(template);
        }
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = MetadataTemplateDeletedEvent.class)
    public void templateDeleted(final MetadataTemplateDeletedEvent evt) {
        removeElement(evt.getTemplate());
    }

    @EventSubscriber(eventClass = MetadataTemplateInsertedEvent.class)
    public void templateInserted(final MetadataTemplateInsertedEvent evt) {
        addElement(evt.getTemplate());
    }

    @EventSubscriber(eventClass = MetadataTemplateUpdatedEvent.class)
    public void templateUpdated(final MetadataTemplateUpdatedEvent evt) {
        updateTemplate(evt.getUpdatedTemplate());
    }

    @EventSubscriber(eventClass = MetadataTemplateRenamedEvent.class)
    public void templateRenamed(final MetadataTemplateRenamedEvent evt) {
        renameTemplate(evt.getFromName(), evt.getToName());
    }

    private void updateTemplate(MetadataTemplate updatedTemplate) {
        int index = indexOfTemplate(updatedTemplate.getName());

        if (index >= 0) {
            MetadataTemplate template = (MetadataTemplate) getElementAt(index);
            template.updateValuesWithTemplate(updatedTemplate);
            fireContentsChanged(this, index, index);
        }
    }

    private void renameTemplate(String fromName, String toName) {
        int index = indexOfTemplate(fromName);

        if (index >= 0) {
            MetadataTemplate template = (MetadataTemplate) getElementAt(index);
            template.setName(toName);
            fireContentsChanged(this, index, index);
        }
    }

    private int indexOfTemplate(String name) {
        int size = getSize();

        for (int i = 0; i < size; i++) {
            Object o = getElementAt(i);

            if (o instanceof MetadataTemplate) {
                if (((MetadataTemplate) o).getName().equals(name)) {
                    return i;
                }
            }
        }

        return -1;
    }
}
