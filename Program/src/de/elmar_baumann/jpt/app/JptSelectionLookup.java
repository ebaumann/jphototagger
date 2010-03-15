package de.elmar_baumann.jpt.app;

import de.elmar_baumann.lib.util.Lookup;

/**
 * Lookup containing the current selection.
 *
 * @author  Elmar Baumann
 * @version 2010-03-15
 */
public final class JptSelectionLookup extends Lookup {

    public static final JptSelectionLookup INSTANCE = new JptSelectionLookup();

    private JptSelectionLookup() {
    }

}
