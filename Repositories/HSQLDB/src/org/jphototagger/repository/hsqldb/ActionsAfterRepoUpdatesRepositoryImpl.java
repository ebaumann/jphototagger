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

    private final ActionsAfterDbInsertionDatabase db = ActionsAfterDbInsertionDatabase.INSTANCE;

    @Override
    public boolean deleteAction(Program program) {
        return db.deleteAction(program);
    }

    @Override
    public boolean existsAction(Program action) {
        return db.existsAction(action);
    }

    @Override
    public int getActionCount() {
        return db.getActionCount();
    }

    @Override
    public List<Program> findAllActions() {
        return db.getAllActions();
    }

    @Override
    public boolean saveAction(Program program, int order) {
        return db.insertAction(program, order);
    }

    @Override
    public boolean setActionOrder(List<Program> actions, int startIndex) {
        return db.setActionOrder(actions, startIndex);
    }
}
