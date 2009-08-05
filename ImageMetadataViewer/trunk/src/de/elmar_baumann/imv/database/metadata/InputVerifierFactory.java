package de.elmar_baumann.imv.database.metadata;

import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpRating;
import de.elmar_baumann.lib.componentutil.InputVerifierNumberRange;
import java.util.HashMap;
import java.util.Map;
import javax.swing.InputVerifier;

/**
 * Contains input verifiers of columns with a limited range of valid values.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-05
 */
public final class InputVerifierFactory {

    private static final Map<Column, InputVerifier> INPUT_VERIFIER_OF_COLUMN =
            new HashMap<Column, InputVerifier>();

    static {
        INPUT_VERIFIER_OF_COLUMN.put(ColumnXmpRating.INSTANCE,
                new InputVerifierNumberRange(1, 5));
    }

    /**
     * Returns an input verifier of a column.
     *
     * @param  column column
     * @return        input verifier or null if that column doesn't have an
     *                input verifier
     */
    public static InputVerifier getInputVerifyerOf(Column column) {
        return INPUT_VERIFIER_OF_COLUMN.get(column);
    }

    private InputVerifierFactory() {
    }
}
