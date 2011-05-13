package org.jphototagger.program.model;

import org.jphototagger.program.data.MetadataTemplate;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseMetadataTemplates;
import org.jphototagger.program.event.listener.DatabaseMetadataTemplatesListener;
import javax.swing.DefaultListModel;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Elements are {@link MetadataTemplate}s retrieved through
 * {@link DatabaseMetadataTemplates#getAll()}.
 *
 * @author Elmar Baumann
 */
public final class ListModelMetadataTemplates extends DefaultListModel implements DatabaseMetadataTemplatesListener {
    private static final long serialVersionUID = -1726658041913008196L;

    public ListModelMetadataTemplates() {
        addElements();
        DatabaseMetadataTemplates.INSTANCE.addListener(this);
    }

    private void addElements() {
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

        for (MetadataTemplate t : DatabaseMetadataTemplates.INSTANCE.getAll()) {
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

    @Override
    public void templateDeleted(final MetadataTemplate template) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                removeElement(template);
            }
        });
    }

    @Override
    public void templateInserted(final MetadataTemplate template) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                addElement(template);
            }
        });
    }

    @Override
    public void templateUpdated(final MetadataTemplate oldTemplate, MetadataTemplate updatedTemplate) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                updateTemplate(oldTemplate);
            }
        });
    }

    @Override
    public void templateRenamed(final String fromName, final String toName) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                renameTemplate(fromName, toName);
            }
        });
    }
}
