package org.jphototagger.program.model;

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
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;

/**
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class MetadataTemplatesComboBoxModel extends DefaultComboBoxModel {

    private static final long serialVersionUID = 7895253533969078904L;
    private final MetadataTemplatesRepository templateRepo = Lookup.getDefault().lookup(MetadataTemplatesRepository.class);

    public MetadataTemplatesComboBoxModel() {
        addElements();
        AnnotationProcessor.process(this);
    }

    /**
     * Löscht ein Template.
     *
     * @param template  Template
     */
    public void delete(final MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        if ((getIndexOf(template) >= 0) && templateRepo.deleteMetadataTemplate(template.getName())) {
            removeElement(template);
        } else {
            errorMessage(template.getName(), Bundle.getString(MetadataTemplatesComboBoxModel.class, "MetadataTemplatesComboBoxModel.Error.ParamDelete"));
        }
    }

    /**
     * Fügt ein Template hinzu.
     *
     * @param template  Template
     */
    public void insert(final MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        if (getIndexOf(template) >= 0) {
            return;
        }

        if (templateRepo.saveOrUpdateMetadataTemplate(template)) {
            addElement(template);
            setSelectedItem(template);
        } else {
            errorMessage(template.getName(),
                    Bundle.getString(MetadataTemplatesComboBoxModel.class, "MetadataTemplatesComboBoxModel.Error.ParamInsert"));
        }
    }

    /**
     * Aktualisiert die Daten eines Templates.
     *
     * @param template  Template
     */
    public void update(final MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        int index = getIndexOf(template);

        if ((index >= 0) && templateRepo.updateMetadataTemplate(template)) {
            removeElementAt(index);
            insertElementAt(template, index);
            setSelectedItem(template);
        } else {
            errorMessage(template.getName(),
                    Bundle.getString(MetadataTemplatesComboBoxModel.class, "MetadataTemplatesComboBoxModel.Error.ParamUpdate"));
        }
    }

    /**
     * Benennt ein Template um.
     *
     * @param template  Template
     * @param newName   Neuer Name
     */
    public void rename(final MetadataTemplate template, final String newName) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        if (newName == null) {
            throw new NullPointerException("newName == null");
        }

        int index = getIndexOf(template);

        if ((index >= 0) && templateRepo.updateRenameMetadataTemplate(template.getName(), newName)) {
            template.setName(newName);
            removeElementAt(index);
            insertElementAt(template, index);
            setSelectedItem(template);
        } else {
            errorMessage(template.getName(),
                    Bundle.getString(MetadataTemplatesComboBoxModel.class, "MetadataTemplatesComboBoxModel.Error.ParamRename"));
        }
    }

    private void updateTemplate(MetadataTemplate template) {
        int index = indexOfTemplate(template.getName());

        if (index >= 0) {
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

    private void errorMessage(String name, String cause) {
        String message = Bundle.getString(MetadataTemplatesComboBoxModel.class, "MetadataTemplatesComboBoxModel.Error.Template", name, cause);
        MessageDisplayer.error(null, message);
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
        updateTemplate(evt.getOldTemplate());
    }

    @EventSubscriber(eventClass = MetadataTemplateRenamedEvent.class)
    public void templateRenamed(final MetadataTemplateRenamedEvent evt) {
        renameTemplate(evt.getFromName(), evt.getToName());
    }
}
