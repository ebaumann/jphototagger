package org.jphototagger.program.model;

import java.util.List;

import javax.swing.DefaultListModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.filetypes.UserDefinedFileType;
import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;
import org.jphototagger.domain.repository.event.userdefinedfiletypes.UserDefinedFileTypeDeletedEvent;
import org.jphototagger.domain.repository.event.userdefinedfiletypes.UserDefinedFileTypeInsertedEvent;
import org.jphototagger.domain.repository.event.userdefinedfiletypes.UserDefinedFileTypeUpdatedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.openide.util.Lookup;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ListModelUserDefinedFileTypes extends DefaultListModel {

    private static final long serialVersionUID = -4247494897388012534L;
    private final UserDefinedFileTypesRepository repo = Lookup.getDefault().lookup(UserDefinedFileTypesRepository.class);

    public ListModelUserDefinedFileTypes() {
        addElements();
        listen();
    }

    private void addElements() {
        if (repo == null) {
            return;
        }

        List<UserDefinedFileType> fileTypes = repo.findAllUserDefinedFileTypes();

        for (UserDefinedFileType fileType : fileTypes) {
            addElement(fileType);
        }
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = UserDefinedFileTypeInsertedEvent.class)
    public void fileTypeInserted(final UserDefinedFileTypeInsertedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                addElement(evt.getFileType());
            }
        });
    }

    @EventSubscriber(eventClass = UserDefinedFileTypeUpdatedEvent.class)
    public void fileTypeUpdated(final UserDefinedFileTypeUpdatedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                int index = indexOf(evt.getOldFileType());

                if (index >= 0) {
                    set(index, evt.getNewFileType());
                }
            }
        });
    }

    @EventSubscriber(eventClass = UserDefinedFileTypeDeletedEvent.class)
    public void fileTypeDeleted(final UserDefinedFileTypeDeletedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                removeElement(evt.getFileType());
            }
        });
    }
}
