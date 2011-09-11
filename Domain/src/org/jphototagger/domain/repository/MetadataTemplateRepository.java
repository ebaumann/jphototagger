package org.jphototagger.domain.repository;

import java.util.List;

import org.jphototagger.domain.templates.MetadataTemplate;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface MetadataTemplateRepository {

    boolean deleteMetadataTemplate(String name);

    boolean existsMetadataTemplate(String name);

    MetadataTemplate findMetadataTemplate(String name);

    List<MetadataTemplate> getAllMetadataTemplates();

    boolean insertOrUpdateMetadataTemplate(MetadataTemplate template);

    boolean updateMetadataTemplate(MetadataTemplate template);

    boolean updateRenameMetadataTemplate(String fromName, String toName);
}
