package org.jphototagger.program.model;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.MetadataTemplate;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseMetadataTemplates;
import org.jphototagger.program.event.listener.DatabaseMetadataTemplatesListener;
import org.jphototagger.program.resource.JptBundle;


import java.util.List;

import javax.swing.DefaultComboBoxModel;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Elements are instances of {@link MetadataTemplate}s retrieved through
 * {@link DatabaseMetadataTemplates#getAll()}.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class ComboBoxModelMetadataTemplates extends DefaultComboBoxModel
        implements DatabaseMetadataTemplatesListener {
    private static final long serialVersionUID = 7895253533969078904L;

    public ComboBoxModelMetadataTemplates() {
        addElements();
        DatabaseMetadataTemplates.INSTANCE.addListener(this);
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

        if ((getIndexOf(template) >= 0) && DatabaseMetadataTemplates.INSTANCE.delete(template.getName())) {
            removeElement(template);
        } else {
            errorMessage(template.getName(),
                         JptBundle.INSTANCE.getString("ComboBoxModelMetadataTemplates.Error.ParamDelete"));
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

        if (DatabaseMetadataTemplates.INSTANCE.insertOrUpdate(template)) {
            addElement(template);
            setSelectedItem(template);
        } else {
            errorMessage(template.getName(),
                         JptBundle.INSTANCE.getString("ComboBoxModelMetadataTemplates.Error.ParamInsert"));
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

        if ((index >= 0) && DatabaseMetadataTemplates.INSTANCE.update(template)) {
            removeElementAt(index);
            insertElementAt(template, index);
            setSelectedItem(template);
        } else {
            errorMessage(template.getName(),
                         JptBundle.INSTANCE.getString("ComboBoxModelMetadataTemplates.Error.ParamUpdate"));
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

        if ((index >= 0) && DatabaseMetadataTemplates.INSTANCE.updateRename(template.getName(), newName)) {
            template.setName(newName);
            removeElementAt(index);
            insertElementAt(template, index);
            setSelectedItem(template);
        } else {
            errorMessage(template.getName(),
                         JptBundle.INSTANCE.getString("ComboBoxModelMetadataTemplates.Error.ParamRename"));
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

        List<MetadataTemplate> templates = DatabaseMetadataTemplates.INSTANCE.getAll();

        for (MetadataTemplate template : templates) {
            addElement(template);
        }
    }

    private void errorMessage(String name, String cause) {
        MessageDisplayer.error(null, "ComboBoxModelMetadataTemplates.Error.Template", name, cause);
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
