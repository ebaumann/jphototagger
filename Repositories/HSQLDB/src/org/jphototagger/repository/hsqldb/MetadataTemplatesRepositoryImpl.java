package org.jphototagger.repository.hsqldb;

import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.repository.MetadataTemplatesRepository;
import org.jphototagger.domain.templates.MetadataTemplate;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = MetadataTemplatesRepository.class)
public final class MetadataTemplatesRepositoryImpl implements MetadataTemplatesRepository {

    @Override
    public boolean deleteMetadataTemplate(String name) {
        return MetadataTemplatesDatabase.INSTANCE.deleteMetadataTemplate(name);
    }

    @Override
    public boolean existsMetadataTemplate(String name) {
        return MetadataTemplatesDatabase.INSTANCE.existsMetadataTemplate(name);
    }

    @Override
    public MetadataTemplate findMetadataTemplate(String name) {
        return MetadataTemplatesDatabase.INSTANCE.findMetadataTemplate(name);
    }

    @Override
    public List<MetadataTemplate> findAllMetadataTemplates() {
        return MetadataTemplatesDatabase.INSTANCE.getAllMetadataTemplates();
    }

    @Override
    public boolean saveOrUpdateMetadataTemplate(MetadataTemplate template) {
        return MetadataTemplatesDatabase.INSTANCE.insertOrUpdateMetadataTemplate(template);
    }

    @Override
    public boolean updateMetadataTemplate(MetadataTemplate template) {
        return MetadataTemplatesDatabase.INSTANCE.updateMetadataTemplate(template);
    }

    @Override
    public boolean updateRenameMetadataTemplate(String fromName, String toName) {
        return MetadataTemplatesDatabase.INSTANCE.updateRenameMetadataTemplate(fromName, toName);
    }
}
