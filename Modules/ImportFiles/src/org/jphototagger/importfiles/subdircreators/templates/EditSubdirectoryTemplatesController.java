package org.jphototagger.importfiles.subdircreators.templates;

import java.util.Objects;

/**
 * Let the user edit subdirectory creation templates via a GUI.
 *
 * @author Elmar Baumann
 */
public final class EditSubdirectoryTemplatesController {

    private final SubdirectoryTemplates templates;

    /**
     * @param templates instance to modify; after editing the instances contains
     *                  modified / deleted / added {@link SubdirectoryTemplate}
     *                  instances.
     */
    public EditSubdirectoryTemplatesController(SubdirectoryTemplates templates) {
        this.templates = Objects.requireNonNull(templates, "templates == null");
    }

    public boolean execute() {
        // TODO
        throw new UnsupportedOperationException();
    }
}
