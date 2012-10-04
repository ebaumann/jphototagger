package org.jphototagger.program.module.programs;

import java.util.List;
import javax.swing.DefaultListModel;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.programs.ProgramType;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.domain.repository.event.programs.ProgramDeletedEvent;
import org.jphototagger.domain.repository.event.programs.ProgramInsertedEvent;
import org.jphototagger.domain.repository.event.programs.ProgramUpdatedEvent;
import org.openide.util.Lookup;

/**
 *
 * All programs in this model are actions, where
 * {@code org.jphototagger.program.data.Program#isAction()} is true, <em>or</em>
 * programs, where that method returns <code>false</code>.
 *
 * @author Elmar Baumann
 */
public final class ProgramsListModel extends DefaultListModel {

    private static final long serialVersionUID = 1L;
    private boolean listen = true;
    private ProgramType type;
    private final ProgramsRepository programsRepo = Lookup.getDefault().lookup(ProgramsRepository.class);

    public ProgramsListModel(ProgramType type) {
        if (type == null) {
            throw new NullPointerException("type == null");
        }

        this.type = type;
        addElements();
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    private void addElements() {
        Repository repo = Lookup.getDefault().lookup(Repository.class);

        if (repo == null || !repo.isInit()) {
            return;
        }

        List<Program> programs = programsRepo.findAllPrograms(type);

        for (Program program : programs) {
            addElement(program);
        }
    }

    private boolean isAppropriateProgramType(Program program) {
        return (program.isAction() && type.equals(ProgramType.ACTION)) || (!program.isAction() && type.equals(ProgramType.PROGRAM));
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

    @EventSubscriber(eventClass = ProgramDeletedEvent.class)
    public void programDeleted(final ProgramDeletedEvent evt) {
        if (!listen) {
            return;
        }

        final Program program = evt.getProgram();

        if (isAppropriateProgramType(program)) {
            removeElement(program);
        }
    }

    @EventSubscriber(eventClass = ProgramInsertedEvent.class)
    public void programInserted(final ProgramInsertedEvent evt) {
        if (!listen) {
            return;
        }

        final Program program = evt.getProgram();

        if (isAppropriateProgramType(program)) {
            addElement(program);
        }
    }

    @EventSubscriber(eventClass = ProgramUpdatedEvent.class)
    public void programUpdated(final ProgramUpdatedEvent evt) {
        if (!listen) {
            return;
        }

        final Program program = evt.getProgram();

        if (isAppropriateProgramType(program)) {
            updateProgram(program);
        }
    }
}
