package org.jphototagger.program.model;

import javax.swing.DefaultListModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.repository.MetadataTemplateRepository;
import org.jphototagger.domain.repository.event.metadatatemplates.MetadataTemplateDeletedEvent;
import org.jphototagger.domain.repository.event.metadatatemplates.MetadataTemplateInsertedEvent;
import org.jphototagger.domain.repository.event.metadatatemplates.MetadataTemplateRenamedEvent;
import org.jphototagger.domain.repository.event.metadatatemplates.MetadataTemplateUpdatedEvent;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.database.ConnectionPool;
import org.openide.util.Lookup;

/**
 * Elements are {@link MetadataTemplate}s retrieved through
 * {@link DatabaseMetadataTemplates#getAllMetadataTemplates()}.
 *
 * @author Elmar Baumann
 */
public final class ListModelMetadataTemplates extends DefaultListModel {

    private static final long serialVersionUID = -1726658041913008196L;
    private final MetadataTemplateRepository repo = Lookup.getDefault().lookup(MetadataTemplateRepository.class);

    public ListModelMetadataTemplates() {
        addElements();
        AnnotationProcessor.process(this);
    }

    private void addElements() {
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

        for (MetadataTemplate t : repo.getAllMetadataTemplates()) {
            addElement(t);
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

    private void renameTemplate(String fromName, String toName) {
        final int index = indexOfTemplate(fromName);

        if (index >= 0) {
            MetadataTemplate template = (MetadataTemplate) getElementAt(index);

            template.setName(toName);
            fireContentsChanged(this, index, index);
        }
    }

    private void updateTemplate(MetadataTemplate template) {
        int index = indexOfTemplate(template.getName());

        if (index >= 0) {
            fireContentsChanged(this, index, index);
        }
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
