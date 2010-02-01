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
package de.elmar_baumann.jpt.database.metadata.xmp;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.Column.DataType;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.lib.componentutil.InputVerifierForbiddenCharacters;
import de.elmar_baumann.lib.componentutil.InputVerifierMaxLength;
import javax.swing.InputVerifier;
import javax.swing.JComponent;

/**
 * Spalte <code>subject</code> der Tabelle <code>xmp_dc_subjects</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-23
 */
public final class ColumnXmpDcSubjectsSubject extends Column {

    private static final int                        COL_LENGTH     = 128;
    public static final  ColumnXmpDcSubjectsSubject INSTANCE       = new ColumnXmpDcSubjectsSubject();
    private static final InputVerifierDcSubjects      INPUT_VERIFIER = new InputVerifierDcSubjects(COL_LENGTH);

    private ColumnXmpDcSubjectsSubject() {
        super(
            TableXmpDcSubjects.INSTANCE,
            "subject",
            DataType.STRING);

        setLength(COL_LENGTH);
        setDescription(Bundle.getString("ColumnXmpDcSubjectsSubject.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpDcSubjectsSubject.LongerDescription"));
    }

    @Override
    public InputVerifier getInputVerifier() {
        return INPUT_VERIFIER;
    }

    private static class InputVerifierDcSubjects extends InputVerifier {

        private final InputVerifierMaxLength           verifierMaxLen;
        private final InputVerifierForbiddenCharacters verifierForbiddenChars = new InputVerifierForbiddenCharacters(Xmp.HIER_SUBJECTS_DELIM.charAt(0));

        public InputVerifierDcSubjects(int maxLength) {
            verifierMaxLen = new InputVerifierMaxLength(maxLength);
        }

        @Override
        public boolean verify(JComponent input) {
            if (!verifierMaxLen.verify(input)) return false;
            if (!verifierForbiddenChars.verify(input)) {
                errorMessage();
                return false;
            }
            return true;
        }

        private void errorMessage() {
            MessageDisplayer.error(null,
                    "ColumnXmpDcSubjectsSubject.Error.FrobiddenChars",
                    verifierForbiddenChars.getChars());
        }

    }
}
