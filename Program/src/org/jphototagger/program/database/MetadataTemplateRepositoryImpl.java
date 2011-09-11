package org.jphototagger.program.database;

import java.util.List;

import org.jphototagger.domain.repository.MetadataTemplateRepository;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = MetadataTemplateRepository.class)
public final class MetadataTemplateRepositoryImpl implements MetadataTemplateRepository {

    private final DatabaseMetadataTemplates db = DatabaseMetadataTemplates.INSTANCE;

    @Override
    public boolean deleteMetadataTemplate(String name) {
        return db.deleteMetadataTemplate(name);
    }

    @Override
    public boolean existsMetadataTemplate(String name) {
        return db.existsMetadataTemplate(name);
    }

    @Override
    public MetadataTemplate findMetadataTemplate(String name) {
        return db.findMetadataTemplate(name);
    }

    @Override
    public List<MetadataTemplate> getAllMetadataTemplates() {
        return db.getAllMetadataTemplates();
    }

    @Override
    public boolean insertOrUpdateMetadataTemplate(MetadataTemplate template) {
        return db.insertOrUpdateMetadataTemplate(template);
    }

    @Override
    public boolean updateMetadataTemplate(MetadataTemplate template) {
        return db.updateMetadataTemplate(template);
    }

    @Override
    public boolean updateRenameMetadataTemplate(String fromName, String toName) {
        return db.updateRenameMetadataTemplate(fromName, toName);
    }
}
