package org.jphototagger.domain.database.xmp;


import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.jphototagger.domain.database.Column;
import org.jphototagger.lib.inputverifier.InputVerifierEmpty;
import org.jphototagger.lib.inputverifier.InputVerifierStringPattern;
import org.jphototagger.lib.inputverifier.InputVerifiersOr;
import org.jphototagger.lib.resource.Bundle;

/**
 * "IPTC Core" Schema for XMP,  property name <strong>DateCreated</strong>,
 * IIM Dataset name <strong>2:55 Date Created</strong>, IIM format
 * <strong>CCYYMMDD</strong>, XMP Property Value Type <strong>Date</strong>
 * formatted as <code>YYYY</code> or <code>YYYY-MM</code> or
 * <code>YYYY-MM-DD</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpIptc4XmpCoreDateCreated extends Column {

    public static final ColumnXmpIptc4XmpCoreDateCreated INSTANCE = new ColumnXmpIptc4XmpCoreDateCreated();
    private static final InputVerifierDateCreated INPUT_VERIFIER = new InputVerifierDateCreated();

    private ColumnXmpIptc4XmpCoreDateCreated() {
        super("iptc4xmpcore_datecreated", "xmp", DataType.STRING);
        setLength(10);
        setDescription(Bundle.getString(ColumnXmpIptc4XmpCoreDateCreated.class, "ColumnXmpIptc4XmpCoreDateCreated.Description"));
        setLongerDescription(Bundle.getString(ColumnXmpIptc4XmpCoreDateCreated.class, "ColumnXmpIptc4XmpCoreDateCreated.LongerDescription"));
    }

    @Override
    public InputVerifier getInputVerifier() {
        return INPUT_VERIFIER;
    }

    private static class InputVerifierDateCreated extends InputVerifier {

        private final InputVerifiersOr inputVerifiersOr = new InputVerifiersOr();

        InputVerifierDateCreated() {
            InputVerifierEmpty inputVerifierEmpty = new InputVerifierEmpty(true);
            InputVerifierStringPattern inputVerifierY = new InputVerifierStringPattern("[0-9][0-9][0-9][0-9]");
            InputVerifierStringPattern inputVerifierYM = new InputVerifierStringPattern("[0-9][0-9][0-9][0-9]-[0-1][0-9]");
            InputVerifierStringPattern inputVerifierYMD = new InputVerifierStringPattern("[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]");

            inputVerifiersOr.addVerifier(inputVerifierEmpty);
            inputVerifiersOr.addVerifier(inputVerifierY);
            inputVerifiersOr.addVerifier(inputVerifierYM);
            inputVerifiersOr.addVerifier(inputVerifierYMD);
        }

        @Override
        public boolean verify(JComponent input) {
            boolean valid = inputVerifiersOr.verify(input);

            if (!valid) {
                errorMessage(input);
            }

            return valid;
        }

        private void errorMessage(JComponent input) {
            String message = Bundle.getString(ColumnXmpIptc4XmpCoreDateCreated.class, "ColumnXmpIptc4XmpCoreDateCreated.Rules");
            String title = Bundle.getString(ColumnXmpIptc4XmpCoreDateCreated.class, "ColumnXmpIptc4XmpCoreDateCreated.Title.ErrorInput");
            int messageType = JOptionPane.ERROR_MESSAGE;

            JOptionPane.showMessageDialog(input, message, title, messageType);
        }
    }
}
