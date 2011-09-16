package org.jphototagger.repository.hsqldb;

import java.util.Set;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.repository.RenameTemplatesRepository;
import org.jphototagger.domain.templates.RenameTemplate;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = RenameTemplatesRepository.class)
public final class RenameTemplatesRepositoryImpl implements RenameTemplatesRepository {

    private final RenameTemplatesDatabase db = RenameTemplatesDatabase.INSTANCE;

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
    public Set<RenameTemplate> findAllRenameTemplates() {
        return db.getAllRenameTemplates();
    }

    @Override
    public boolean saveRenameTemplate(RenameTemplate template) {
        return db.insertRenameTemplate(template);
    }

    @Override
    public boolean updateRenameTemplate(RenameTemplate template) {
        return db.updateRenameTemplate(template);
    }
}
