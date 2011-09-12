package org.jphototagger.domain.repository;

import java.util.List;

import org.jphototagger.domain.templates.MetadataTemplate;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface MetadataTemplatesRepository {

    boolean deleteMetadataTemplate(String name);

    boolean existsMetadataTemplate(String name);

    MetadataTemplate findMetadataTemplate(String name);

    List<MetadataTemplate> findAllMetadataTemplates();

    boolean saveOrUpdateMetadataTemplate(MetadataTemplate template);

    boolean updateMetadataTemplate(MetadataTemplate template);

    boolean updateRenameMetadataTemplate(String fromName, String toName);
}
