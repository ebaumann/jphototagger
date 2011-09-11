package org.jphototagger.program.database;

import java.util.Set;

import org.jphototagger.domain.repository.RenameTemplatesRepository;
import org.jphototagger.domain.templates.RenameTemplate;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = RenameTemplatesRepository.class)
public final class RenameTemplatesRepositoryImpl implements RenameTemplatesRepository {

    private final DatabaseRenameTemplates db = DatabaseRenameTemplates.INSTANCE;

    @Override
    public int deleteRenameTemplate(String name) {
        return db.deleteRenameTemplate(name);
    }

    @Override
    public boolean existsRenameTemplate(String name) {
        return db.existsRenameTemplate(name);
    }

    @Override
    public RenameTemplate findRenameTemplate(String name) {
        return db.findRenameTemplate(name);
    }

    @Override
    public Set<RenameTemplate> getAllRenameTemplates() {
        return db.getAllRenameTemplates();
    }

    @Override
    public boolean insertRenameTemplate(RenameTemplate template) {
        return db.insertRenameTemplate(template);
    }

    @Override
    public boolean updateRenameTemplate(RenameTemplate template) {
        return db.updateRenameTemplate(template);
    }
}
