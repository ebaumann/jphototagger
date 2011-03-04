package org.jphototagger.program.database.metadata;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.database.metadata.Column.DataType;

import java.text.NumberFormat;

import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class FormatterFactory {
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
            AppLogger.logSevere(FormatterFactory.class, ex);
        }
    }

    public static DefaultFormatterFactory getFormatterFactory(Column column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        DataType type = column.getDataType();

        if (type.equals(DataType.DATE)) {
            return dateFormatterFactory;
        } else if (type.equals(DataType.BIGINT) || type.equals(DataType.INTEGER) || type.equals(DataType.SMALLINT)) {
            return integerFormatterFactory;
        } else if (type.equals(DataType.REAL)) {
            return doubleFormatterFactory;
        } else {
            return defaultFormatterFactory;
        }
    }

    private FormatterFactory() {}
}
