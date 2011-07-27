package org.jphototagger.program.model;

import java.util.List;
import javax.swing.DefaultListModel;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.domain.UserDefinedFileType;
import org.jphototagger.program.database.DatabaseUserDefinedFileTypes;
import org.jphototagger.program.event.listener.DatabaseUserDefinedFileTypesListener;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ListModelUserDefinedFileTypes extends DefaultListModel implements DatabaseUserDefinedFileTypesListener {
    private static final long serialVersionUID = -4247494897388012534L;

    public ListModelUserDefinedFileTypes() {
        addElements();
        listen();
    }

    private void addElements() {
        List<UserDefinedFileType> fileTypes = DatabaseUserDefinedFileTypes.INSTANCE.getAll();

        for (UserDefinedFileType fileType : fileTypes) {
            addElement(fileType);
        }
    }

    private void listen() {
        DatabaseUserDefinedFileTypes.INSTANCE.addListener(this);
    }

    @Override
    public void fileTypeInserted(final UserDefinedFileType fileType) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                addElement(fileType);
            }
        });
    }

    @Override
    public void fileTypeUpdated(final UserDefinedFileType oldFileType, final UserDefinedFileType newFileType) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                int index = indexOf(oldFileType);

                if (index >= 0) {
                    set(index, newFileType);
                }
            }
        });
    }

    @Override
    public void fileTypeDeleted(final UserDefinedFileType fileType) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                removeElement(fileType);
            }
        });
    }
}
