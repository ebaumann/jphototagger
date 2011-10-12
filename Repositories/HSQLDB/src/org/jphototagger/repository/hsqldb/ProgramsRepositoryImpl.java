package org.jphototagger.repository.hsqldb;

import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.programs.ProgramType;
import org.jphototagger.domain.repository.ProgramsRepository;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ProgramsRepository.class)
public final class ProgramsRepositoryImpl implements ProgramsRepository {

    @Override
    public boolean deleteProgram(Program program) {
        return ProgramsDatabase.INSTANCE.deleteProgram(program);
    }

    @Override
    public boolean existsProgram(Program program) {
        return ProgramsDatabase.INSTANCE.existsProgram(program);
    }

    @Override
    public Program findProgram(long id) {
        return ProgramsDatabase.INSTANCE.findProgram(id);
    }

    @Override
    public List<Program> findAllPrograms(ProgramType type) {
        return ProgramsDatabase.INSTANCE.getAllPrograms(type);
    }

    @Override
    public Program findDefaultImageOpenProgram() {
        return ProgramsDatabase.INSTANCE.getDefaultImageOpenProgram();
    }

    @Override
    public int getProgramCount(boolean actions) {
        return ProgramsDatabase.INSTANCE.getProgramCount(actions);
    }

    @Override
    public boolean hasAction() {
        return ProgramsDatabase.INSTANCE.hasAction();
    }

    @Override
    public boolean hasProgram() {
        return ProgramsDatabase.INSTANCE.hasProgram();
    }

    @Override
    public boolean saveProgram(Program program) {
        return ProgramsDatabase.INSTANCE.insertProgram(program);
    }

    @Override
    public boolean updateProgram(Program program) {
        return ProgramsDatabase.INSTANCE.updateProgram(program);
    }
}
