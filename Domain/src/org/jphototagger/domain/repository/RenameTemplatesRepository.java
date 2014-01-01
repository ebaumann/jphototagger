package org.jphototagger.domain.repository;

import java.util.Set;
import org.jphototagger.domain.templates.RenameTemplate;

/**
 * @author Elmar Baumann
 */
public interface RenameTemplatesRepository {

    int deleteRenameTemplate(String name);

    boolean existsRenameTemplate(String name);

    RenameTemplate findRenameTemplate(String name);

    Set<RenameTemplate> findAllRenameTemplates();

    boolean saveRenameTemplate(RenameTemplate template);

    boolean updateRenameTemplate(RenameTemplate template);
}
