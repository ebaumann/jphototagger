package org.jphototagger.importfiles.subdircreators.templates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.file.SubdirectoryCreateStrategy;
import org.jphototagger.api.file.UserDefinedSubdirectoryCreateStrategies;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Provides user defined subdirectory creation strategies based on templates.
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = UserDefinedSubdirectoryCreateStrategies.class)
public final class TemplatesSubdirectoryCreateStrategiesImpl implements UserDefinedSubdirectoryCreateStrategies {

    @Override
    public Collection<SubdirectoryCreateStrategy> getStrageties() {
        SubdirectoryTemplatesRepository repo = new SubdirectoryTemplatesRepository();

        try {
            SubdirectoryTemplates fileTemplates = repo.load();

            if (fileTemplates == null) {
                return Collections.emptyList();
            }

            return createStrategies(fileTemplates.getTemplates());
        } catch (Exception ex) {
            Logger.getLogger(TemplatesSubdirectoryCreateStrategiesImpl.class.getName()).log(Level.SEVERE, null, ex);
            return Collections.emptyList();
        }
    }

    private Collection<SubdirectoryCreateStrategy> createStrategies(Collection<SubdirectoryTemplate> templates) throws Exception {
        Objects.requireNonNull(templates, "templates == null");

        Collection<SubdirectoryCreateStrategy> result = new ArrayList<>(templates.size());

        for (SubdirectoryTemplate template : templates) {
            result.add(new TemplateSubdirectoryCreateStrategy(template));
        }

        return result;
    }

    @Override
    public boolean edit() {
        SubdirectoryTemplatesRepository repo = new SubdirectoryTemplatesRepository();

        try {
            SubdirectoryTemplates templates = repo.load();

            if (templates == null) { // File does not exist
                templates = new SubdirectoryTemplates();
            }

            EditSubdirectoryTemplatesController ctrl = new EditSubdirectoryTemplatesController(templates);

            if (ctrl.execute()) {
                repo.save(templates);
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            Logger.getLogger(TemplatesSubdirectoryCreateStrategiesImpl.class.getName()).log(Level.SEVERE, null, ex);
            MessageDisplayer.thrown(Bundle.getString(TemplatesSubdirectoryCreateStrategiesImpl.class, "TemplatesSubdirectoryCreateStrategiesImpl.Edit.Exception"), ex);
            return false;
        }
    }
}
