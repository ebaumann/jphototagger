package org.jphototagger.program.resource;

import org.jphototagger.lib.resource.Bundle;

/**
 *
 * @author Elmar Baumann
 */
public final class JptBundle extends Bundle {
    public static final JptBundle INSTANCE = new JptBundle();

    private JptBundle() {
        super("org/jphototagger/program/resource/properties/Bundle");
    }
}
