package org.jphototagger.domain.metadata.xmp;

import java.util.Calendar;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.lib.swing.inputverifier.EmptyInputVerifier;
import org.jphototagger.lib.swing.inputverifier.InputVerifiersOr;
import org.jphototagger.lib.swing.inputverifier.StringPatternInputVerifier;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.DateUtil;
import org.jphototagger.lib.util.NumberUtil;
import org.jphototagger.lib.util.StringUtil;

/**
 * "IPTC Core" Schema for XMP,  property name <strong>DateCreated</strong>,
 * IIM Dataset name <strong>2:55 Date Created</strong>, IIM format
 * <strong>CCYYMMDD</strong>, XMP Property Value Type <strong>Date</strong>
 * formatted as <code>YYYY</code> or <code>YYYY-MM</code> or
 * <code>YYYY-MM-DD</code>.
 *
 * @author Elmar Baumann
 */
public final class XmpIptc4XmpCoreDateCreatedMetaDataValue extends MetaDataValue {

    public static final XmpIptc4XmpCoreDateCreatedMetaDataValue INSTANCE = new XmpIptc4XmpCoreDateCreatedMetaDataValue();
    private static final InputVerifierDateCreated INPUT_VERIFIER = new InputVerifierDateCreated();

    private XmpIptc4XmpCoreDateCreatedMetaDataValue() {
        super("iptc4xmpcore_datecreated", "xmp", ValueType.STRING);
        setValueLength(10);
        setDescription(Bundle.getString(XmpIptc4XmpCoreDateCreatedMetaDataValue.class, "XmpIptc4XmpCoreDateCreatedMetaDataValue.Description"));
        setLongerDescription(Bundle.getString(XmpIptc4XmpCoreDateCreatedMetaDataValue.class, "XmpIptc4XmpCoreDateCreatedMetaDataValue.LongerDescription"));
    }

    @Override
    public InputVerifier getInputVerifier() {
        return INPUT_VERIFIER;
    }

    private static class InputVerifierDateCreated extends InputVerifier {

        private final InputVerifiersOr inputVerifiersOr = new InputVerifiersOr();

        InputVerifierDateCreated() {
            EmptyInputVerifier inputVerifierEmpty = new EmptyInputVerifier(true);
            StringPatternInputVerifier inputVerifierY = new StringPatternInputVerifier("[0-9][0-9][0-9][0-9]");
            StringPatternInputVerifier inputVerifierYM = new StringPatternInputVerifier("[0-9][0-9][0-9][0-9]-[0-1][0-9]");
            StringPatternInputVerifier inputVerifierYMD = new StringPatternInputVerifier("[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]");
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
            String message = Bundle.getString(XmpIptc4XmpCoreDateCreatedMetaDataValue.class, "XmpIptc4XmpCoreDateCreatedMetaDataValue.Rules");
            String title = Bundle.getString(XmpIptc4XmpCoreDateCreatedMetaDataValue.class, "XmpIptc4XmpCoreDateCreatedMetaDataValue.Title.ErrorInput");
            int messageType = JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog(input, message, title, messageType);
        }
    }

    /**
     * @param dateCreated maybe null
     * @return month will be June, day the 15th if not contained in dateCreated, time always 12:00 	at noon.
     * null if date created is null or invalid
     */
    public static Long createTimestamp(String dateCreated) {
        if (!StringUtil.hasContent(dateCreated)) {
            return null;
        }
        String[] dateParts = dateCreated.split("-");
        if (dateParts == null || dateParts.length > 3) {
            return null;
        }
        for (String datePart : dateParts) {
            if (!NumberUtil.isInteger(datePart)) {
                return null;
            }
        }
        int year = Integer.parseInt(dateParts[0]);
        int month = dateParts.length >= 2 ? Integer.parseInt(dateParts[1]) : 6;
        int day = dateParts.length == 3 ? Integer.parseInt(dateParts[2]) : 15;
        if (!DateUtil.isValidGregorianDate(year, month, day)) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        return cal.getTime().getTime();
    }
}
