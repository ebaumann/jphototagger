package org.jphototagger.program.model;

import org.jphototagger.domain.database.RenameTemplate;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseRenameTemplates;
import org.jphototagger.domain.event.listener.DatabaseRenameTemplatesListener;
import javax.swing.DefaultComboBoxModel;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ComboBoxModelRenameTemplates extends DefaultComboBoxModel
        implements DatabaseRenameTemplatesListener {
    private static final long serialVersionUID = -5081726761734936168L;

    public ComboBoxModelRenameTemplates() {
        addElements();
        DatabaseRenameTemplates.INSTANCE.addListener(this);
    }

    private void addElements() {
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

        for (RenameTemplate template : DatabaseRenameTemplates.INSTANCE.getAll()) {
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

    @Override
    public void templateDeleted(final RenameTemplate template) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                deleteTemplate(template);
            }
        });
    }

    @Override
    public void templateInserted(final RenameTemplate template) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                insertTemplate(template);
            }
        });
    }

    @Override
    public void templateUpdated(final RenameTemplate template) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                updateTemplate(template);
            }
        });
    }
}
