package org.jphototagger.repository.hsqldb;

import java.util.List;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.programs.ProgramType;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ProgramsRepository.class)
public final class ProgramsRepositoryImpl implements ProgramsRepository {

    private final ProgramsDatabase db = ProgramsDatabase.INSTANCE;

    @Override
    public boolean deleteProgram(Program program) {
        return db.deleteProgram(program);
    }

    @Override
    public boolean existsProgram(Program program) {
        return db.existsProgram(program);
    }

    @Override
    public Program findProgram(long id) {
        return db.findProgram(id);
    }

    @Override
    public List<Program> findAllPrograms(ProgramType type) {
        return db.getAllPrograms(type);
    }

    @Override
    public Program findDefaultImageOpenProgram() {
        return db.getDefaultImageOpenProgram();
    }

    @Override
    public int getProgramCount(boolean actions) {
        return db.getProgramCount(actions);
    }

    @Override
    public boolean hasAction() {
        return db.hasAction();
    }

    @Override
    public boolean hasProgram() {
        return db.hasProgram();
    }

    @Override
    public boolean saveProgram(Program program) {
        return db.insertProgram(program);
    }

    @Override
    public boolean updateProgram(Program program) {
        return db.updateProgram(program);
    }
}
