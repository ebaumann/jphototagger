package org.jphototagger.domain.repository;

import java.util.List;

import org.jphototagger.domain.programs.Program;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface ActionsAfterRepoUpdatesRepository {

    boolean deleteAction(Program program);

    boolean existsAction(Program action);

    int getActionCount();

    List<Program> findAllActions();

    boolean saveAction(Program program, int order);

    boolean setActionOrder(List<Program> actions, int startIndex);
}
