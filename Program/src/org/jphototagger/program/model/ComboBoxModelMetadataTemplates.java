package org.jphototagger.program.model;

import java.util.List;

import javax.swing.DefaultComboBoxModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.repository.MetadataTemplateRepository;
import org.jphototagger.domain.repository.event.metadatatemplates.MetadataTemplateDeletedEvent;
import org.jphototagger.domain.repository.event.metadatatemplates.MetadataTemplateInsertedEvent;
import org.jphototagger.domain.repository.event.metadatatemplates.MetadataTemplateRenamedEvent;
import org.jphototagger.domain.repository.event.metadatatemplates.MetadataTemplateUpdatedEvent;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.database.ConnectionPool;
import org.openide.util.Lookup;

/**
 * Elements are instances of {@link MetadataTemplate}s retrieved through
 * {@link DatabaseMetadataTemplates#getAllMetadataTemplates()}.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class ComboBoxModelMetadataTemplates extends DefaultComboBoxModel {

    private static final long serialVersionUID = 7895253533969078904L;
    private final MetadataTemplateRepository repo = Lookup.getDefault().lookup(MetadataTemplateRepository.class);

    public ComboBoxModelMetadataTemplates() {
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

        if ((getIndexOf(template) >= 0) && repo.deleteMetadataTemplate(template.getName())) {
            removeElement(template);
        } else {
            errorMessage(template.getName(), Bundle.getString(ComboBoxModelMetadataTemplates.class, "ComboBoxModelMetadataTemplates.Error.ParamDelete"));
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

        if (repo.insertOrUpdateMetadataTemplate(template)) {
            addElement(template);
            setSelectedItem(template);
        } else {
            errorMessage(template.getName(),
                    Bundle.getString(ComboBoxModelMetadataTemplates.class, "ComboBoxModelMetadataTemplates.Error.ParamInsert"));
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

        if ((index >= 0) && repo.updateMetadataTemplate(template)) {
            removeElementAt(index);
            insertElementAt(template, index);
            setSelectedItem(template);
        } else {
            errorMessage(template.getName(),
                    Bundle.getString(ComboBoxModelMetadataTemplates.class, "ComboBoxModelMetadataTemplates.Error.ParamUpdate"));
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

        if ((index >= 0) && repo.updateRenameMetadataTemplate(template.getName(), newName)) {
            template.setName(newName);
            removeElementAt(index);
            insertElementAt(template, index);
            setSelectedItem(template);
        } else {
            errorMessage(template.getName(),
                    Bundle.getString(ComboBoxModelMetadataTemplates.class, "ComboBoxModelMetadataTemplates.Error.ParamRename"));
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
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

        List<MetadataTemplate> templates = repo.getAllMetadataTemplates();

        for (MetadataTemplate template : templates) {
            addElement(template);
        }
    }

    private void errorMessage(String name, String cause) {
        String message = Bundle.getString(ComboBoxModelMetadataTemplates.class, "ComboBoxModelMetadataTemplates.Error.Template", name, cause);
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
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                removeElement(evt.getTemplate());
            }
        });
    }

    @EventSubscriber(eventClass = MetadataTemplateInsertedEvent.class)
    public void templateInserted(final MetadataTemplateInsertedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                addElement(evt.getTemplate());
            }
        });
    }

    @EventSubscriber(eventClass = MetadataTemplateUpdatedEvent.class)
    public void templateUpdated(final MetadataTemplateUpdatedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                updateTemplate(evt.getOldTemplate());
            }
        });
    }

    @EventSubscriber(eventClass = MetadataTemplateRenamedEvent.class)
    public void templateRenamed(final MetadataTemplateRenamedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                renameTemplate(evt.getFromName(), evt.getToName());
            }
        });
    }
}
