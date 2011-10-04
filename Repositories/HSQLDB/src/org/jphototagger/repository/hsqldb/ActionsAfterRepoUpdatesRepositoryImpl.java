package org.jphototagger.repository.hsqldb;

import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.repository.ActionsAfterRepoUpdatesRepository;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ActionsAfterRepoUpdatesRepository.class)
public final class ActionsAfterRepoUpdatesRepositoryImpl implements ActionsAfterRepoUpdatesRepository {

    @Override
    public boolean deleteAction(Program program) {
        return ActionsAfterDbInsertionDatabase.INSTANCE.deleteAction(program);
    }

    @Override
    public boolean existsAction(Program action) {
        return ActionsAfterDbInsertionDatabase.INSTANCE.existsAction(action);
    }

    @Override
    public int getActionCount() {
        return ActionsAfterDbInsertionDatabase.INSTANCE.getActionCount();
    }

    @Override
    public List<Program> findAllActions() {
        return ActionsAfterDbInsertionDatabase.INSTANCE.getAllActions();
    }

    @Override
    public boolean saveAction(Program program, int order) {
        return ActionsAfterDbInsertionDatabase.INSTANCE.insertAction(program, order);
    }

    @Override
    public boolean setActionOrder(List<Program> actions, int startIndex) {
        return ActionsAfterDbInsertionDatabase.INSTANCE.setActionOrder(actions, startIndex);
    }
}
