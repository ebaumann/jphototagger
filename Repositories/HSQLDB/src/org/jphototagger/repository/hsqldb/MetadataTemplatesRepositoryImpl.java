package org.jphototagger.repository.hsqldb;

import java.util.List;

import org.jphototagger.domain.repository.MetadataTemplatesRepository;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = MetadataTemplatesRepository.class)
public final class MetadataTemplatesRepositoryImpl implements MetadataTemplatesRepository {

    private final MetadataTemplatesDatabase db = MetadataTemplatesDatabase.INSTANCE;

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
    public List<MetadataTemplate> findAllMetadataTemplates() {
        return db.getAllMetadataTemplates();
    }

    @Override
    public boolean saveOrUpdateMetadataTemplate(MetadataTemplate template) {
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
