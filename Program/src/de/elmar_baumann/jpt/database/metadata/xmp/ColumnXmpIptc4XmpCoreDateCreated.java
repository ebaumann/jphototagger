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
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.lib.componentutil.InputVerifierEmpty;
import de.elmar_baumann.lib.componentutil.InputVerifierStringPattern;
import de.elmar_baumann.lib.componentutil.InputVerifiersOr;
import javax.swing.InputVerifier;
import javax.swing.JComponent;

/**
 * "IPTC Core" Schema for XMP,  property name <strong>DateCreated</strong>,
 * IIM Dataset name <strong>2:55 Date Created</strong>, IIM format
 * <strong>CCYYMMDD</strong>, XMP Property Value Type <strong>Date</strong>
 * formatted as <code>YYYY</code> or <code>YYYY-MM</code> or
 * <code>YYYY-MM-DD</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-06
 */
public final class ColumnXmpIptc4XmpCoreDateCreated extends Column {

    public static final  ColumnXmpIptc4XmpCoreDateCreated INSTANCE       = new ColumnXmpIptc4XmpCoreDateCreated();
    private static final InputVerifierDateCreated         INPUT_VERIFIER = new InputVerifierDateCreated();

    private ColumnXmpIptc4XmpCoreDateCreated() {
        super(
            TableXmp.INSTANCE,
            "iptc4xmpcore_datecreated",
            DataType.STRING);

        setLength(10);
        setDescription(Bundle.getString("ColumnXmpIptc4XmpCoreDateCreated.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpIptc4XmpCoreDateCreated.LongerDescription"));
    }

    @Override
    public InputVerifier getInputVerifier() {
        return INPUT_VERIFIER;
    }

    private static class InputVerifierDateCreated extends InputVerifier {

        private final InputVerifiersOr inputVerifiersOr = new InputVerifiersOr();

        public InputVerifierDateCreated() {
            InputVerifierEmpty         inputVerifierEmpty = new InputVerifierEmpty(true);
            InputVerifierStringPattern inputVerifierY     = new InputVerifierStringPattern("[0-9][0-9][0-9][0-9]");
            InputVerifierStringPattern inputVerifierYM    = new InputVerifierStringPattern("[0-9][0-9][0-9][0-9]-[0-1][0-9]");
            InputVerifierStringPattern inputVerifierYMD   = new InputVerifierStringPattern("[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]");

            inputVerifiersOr.addVerifier(inputVerifierEmpty);
            inputVerifiersOr.addVerifier(inputVerifierY);
            inputVerifiersOr.addVerifier(inputVerifierYM);
            inputVerifiersOr.addVerifier(inputVerifierYMD);
        }

        @Override
        public boolean verify(JComponent input) {
            boolean valid = inputVerifiersOr.verify(input);
            if (!valid) errorMessage(input);
            return valid;
        }

        private void errorMessage(JComponent input) {
            MessageDisplayer.error(input, "ColumnXmpIptc4XmpCoreDateCreated.Rules");
        }
   }
}
