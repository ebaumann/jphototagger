package org.jphototagger.domain.repository;

import java.util.List;

import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.programs.ProgramType;

/**
 * @author Elmar Baumann
 */
public interface ProgramsRepository {

    boolean deleteProgram(Program program);

    boolean existsProgram(Program program);

    Program findProgram(long id);

    List<Program> findAllPrograms(ProgramType type);

    Program findDefaultImageOpenProgram();

    int getProgramCount(boolean actions);

    boolean hasAction();

    boolean hasProgram();

    boolean saveProgram(Program program);

    boolean updateProgram(Program program);
}
