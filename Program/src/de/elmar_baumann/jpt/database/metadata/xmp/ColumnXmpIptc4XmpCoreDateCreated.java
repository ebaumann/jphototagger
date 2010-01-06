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

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.lib.componentutil.InputVerifierStringPattern;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 *
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

        private final InputVerifierStringPattern INPUT_VERIFIER = new InputVerifierStringPattern("[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]");

        public InputVerifierDateCreated() {
            INPUT_VERIFIER.setErrorHint(Bundle.getString("ColumnXmpIptc4XmpCoreDateCreated.Rules"));
        }

        @Override
        public boolean verify(JComponent input) {
            if (input instanceof JTextComponent) {

                String text = ((JTextComponent) input).getText().trim();

                if (text.isEmpty()) return true;

                return this.INPUT_VERIFIER.verify(input);
            }
            return true;
        }

    }
}
