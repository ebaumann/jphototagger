package org.jphototagger.domain.metadata;

import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

import org.jphototagger.domain.metadata.MetaDataValue.ValueType;

/**
 * @author Elmar Baumann
 */
final class MetaDataValueFormatterFactory {

    private static DefaultFormatterFactory integerFormatterFactory;
    private static DefaultFormatterFactory doubleFormatterFactory;
    private static DefaultFormatterFactory dateFormatterFactory;
    private static DefaultFormatterFactory defaultFormatterFactory =
            new DefaultFormatterFactory(new DefaultFormatter());

    static {
        try {
            NumberFormat integerFormat = NumberFormat.getIntegerInstance();

            integerFormat.setGroupingUsed(false);

            NumberFormatter integerFormatter = new NumberFormatter(integerFormat);

            integerFormatter.setAllowsInvalid(false);

            MaskFormatter doubleFormatter = new MaskFormatter("####.##");

            doubleFormatter.setAllowsInvalid(false);
            integerFormatterFactory = new DefaultFormatterFactory(integerFormatter);
            doubleFormatterFactory = new DefaultFormatterFactory(doubleFormatter);
            dateFormatterFactory = new DefaultFormatterFactory(new MaskFormatter("####-##-##"));
        } catch (Exception ex) {
            Logger.getLogger(MetaDataValueFormatterFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static DefaultFormatterFactory getFormatterFactory(MetaDataValue mdValue) {
        if (mdValue == null) {
            throw new NullPointerException("mdValue == null");
        }

        ValueType type = mdValue.getValueType();

        if (type.equals(ValueType.DATE)) {
            return dateFormatterFactory;
        } else if (type.equals(ValueType.BIGINT) || type.equals(ValueType.INTEGER) || type.equals(ValueType.SMALLINT)) {
            return integerFormatterFactory;
        } else if (type.equals(ValueType.REAL)) {
            return doubleFormatterFactory;
        } else {
            return defaultFormatterFactory;
        }
    }

    private MetaDataValueFormatterFactory() {
    }
}
