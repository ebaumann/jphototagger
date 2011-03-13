package org.jphototagger.program.model;

import java.awt.EventQueue;
import java.util.List;
import javax.swing.DefaultListModel;
import org.jphototagger.program.data.UserDefinedFileType;
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
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                addElement(fileType);
            }
        });
    }

    @Override
    public void fileTypeUpdated(final UserDefinedFileType oldFileType, final UserDefinedFileType newFileType) {
        EventQueue.invokeLater(new Runnable() {

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
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                removeElement(fileType);
            }
        });
    }
}
