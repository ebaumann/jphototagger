package org.jphototagger.domain.repository;

import java.util.List;

import org.jphototagger.domain.programs.Program;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface ProgramsRepository {

    boolean deleteProgram(Program program);

    boolean existsProgram(Program program);

    Program findProgram(long id);

    List<Program> getAllPrograms(ProgramType type);

    Program getDefaultImageOpenProgram();

    int getProgramCount(boolean actions);

    boolean hasAction();

    boolean hasProgram();

    boolean insertProgram(Program program);

    boolean updateProgram(Program program);
}
