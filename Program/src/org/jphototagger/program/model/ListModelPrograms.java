package org.jphototagger.program.model;

import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.database.DatabasePrograms.Type;
import org.jphototagger.program.event.listener.DatabaseProgramsListener;


import java.util.List;

import javax.swing.DefaultListModel;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Contains {@link Program}s retrieved through
 * {@link DatabasePrograms#getAll(DatabasePrograms.Type)}.
 *
 * All programs in this model are actions, where
 * {@link org.jphototagger.program.data.Program#isAction()} is true, <em>or</em>
 * programs, where that method returns <code>false</code>.
 *
 * @author Elmar Baumann
 */
public final class ListModelPrograms extends DefaultListModel implements DatabaseProgramsListener {
    private static final long serialVersionUID = 1107244876982338977L;
    private boolean listen = true;
    private Type type;

    public ListModelPrograms(Type type) {
        if (type == null) {
            throw new NullPointerException("type == null");
        }

        this.type = type;
        addElements();
        DatabasePrograms.INSTANCE.addListener(this);
    }

    private void addElements() {
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

        List<Program> programs = DatabasePrograms.INSTANCE.getAll(type);

        for (Program program : programs) {
            addElement(program);
        }
    }

    private boolean isAppropriateProgramType(Program program) {
        return (program.isAction() && type.equals(Type.ACTION)) || (!program.isAction() && type.equals(Type.PROGRAM));
    }

    private void updateProgram(Program program) throws IllegalArgumentException {
        listen = false;
        int index = indexOf(program);

        if (index >= 0) {
            int sequenceNumber = program.getSequenceNumber();
            boolean validSeqNumber = (sequenceNumber >= 0) && (sequenceNumber <= getSize());

            if (!validSeqNumber) {
                throw new IllegalArgumentException("Invalid sequence number. Size: " + getSize()
                                                   + ". Sequence number: " + sequenceNumber);
            }

            if (index == sequenceNumber) {
                set(index, program);
            } else if (validSeqNumber) {
                remove(index);
                insertElementAt(program, sequenceNumber);
            }
        }
        listen = true;
    }

    @Override
    public void programDeleted(final Program program) {
        if (!listen) {
            return;
        }

        if (isAppropriateProgramType(program)) {
            EventQueueUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    removeElement(program);
                }
            });
        }
    }

    @Override
    public void programInserted(final Program program) {
        if (!listen) {
            return;
        }

        if (isAppropriateProgramType(program)) {
            EventQueueUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    addElement(program);
                }
            });
        }
    }

    @Override
    public void programUpdated(final Program program) {
        if (!listen) {
            return;
        }

        if (isAppropriateProgramType(program)) {
            EventQueueUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    updateProgram(program);
                }
            });
        }
    }
}
