package org.jphototagger.repository.hsqldb;

import java.util.Set;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.repository.RenameTemplatesRepository;
import org.jphototagger.domain.templates.RenameTemplate;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RenameTemplatesRepository.class)
public final class RenameTemplatesRepositoryImpl implements RenameTemplatesRepository {

    @Override
    public int deleteRenameTemplate(String name) {
        return RenameTemplatesDatabase.INSTANCE.deleteRenameTemplate(name);
    }

    @Override
    public boolean existsRenameTemplate(String name) {
        return RenameTemplatesDatabase.INSTANCE.existsRenameTemplate(name);
    }

    @Override
    public RenameTemplate findRenameTemplate(String name) {
        return RenameTemplatesDatabase.INSTANCE.findRenameTemplate(name);
    }

    @Override
    public Set<RenameTemplate> findAllRenameTemplates() {
        return RenameTemplatesDatabase.INSTANCE.getAllRenameTemplates();
    }

    @Override
    public boolean saveRenameTemplate(RenameTemplate template) {
        return RenameTemplatesDatabase.INSTANCE.insertRenameTemplate(template);
    }

    @Override
    public boolean updateRenameTemplate(RenameTemplate template) {
        return RenameTemplatesDatabase.INSTANCE.updateRenameTemplate(template);
    }
}
