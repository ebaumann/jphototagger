/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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
