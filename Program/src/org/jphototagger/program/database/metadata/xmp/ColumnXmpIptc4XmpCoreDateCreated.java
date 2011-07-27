package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.lib.inputverifier.InputVerifierEmpty;
import org.jphototagger.lib.inputverifier.InputVerifiersOr;
import org.jphototagger.lib.inputverifier.InputVerifierStringPattern;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.domain.Column;
import org.jphototagger.program.resource.JptBundle;
import javax.swing.InputVerifier;
import javax.swing.JComponent;

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
        setDescription(JptBundle.INSTANCE.getString("ColumnXmpIptc4XmpCoreDateCreated.Description"));
        setLongerDescription(JptBundle.INSTANCE.getString("ColumnXmpIptc4XmpCoreDateCreated.LongerDescription"));
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
            InputVerifierStringPattern inputVerifierYM =
                new InputVerifierStringPattern("[0-9][0-9][0-9][0-9]-[0-1][0-9]");
            InputVerifierStringPattern inputVerifierYMD =
                new InputVerifierStringPattern("[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]");

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
            MessageDisplayer.error(input, "ColumnXmpIptc4XmpCoreDateCreated.Rules");
        }
    }
}
