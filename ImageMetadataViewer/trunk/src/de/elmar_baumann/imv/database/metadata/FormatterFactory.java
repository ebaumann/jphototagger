package de.elmar_baumann.imv.database.metadata;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-28
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
            MaskFormatter doubleFormatter = new MaskFormatter("####.##"); // NOI18N
            doubleFormatter.setAllowsInvalid(false);

            integerFormatterFactory = new DefaultFormatterFactory(integerFormatter);
            doubleFormatterFactory = new DefaultFormatterFactory(doubleFormatter);
            dateFormatterFactory = new DefaultFormatterFactory(new MaskFormatter("####-##-##")); // NOI18N
        } catch (ParseException ex) {
            AppLog.logSevere(FormatterFactory.class, ex);
        }
    }

    public static DefaultFormatterFactory getFormatterFactory(Column column) {
        DataType type = column.getDataType();
        if (type.equals(DataType.DATE)) {
            return dateFormatterFactory;
        } else if (type.equals(DataType.BIGINT) || type.equals(DataType.INTEGER) ||
            type.equals(DataType.SMALLINT)) {
            return integerFormatterFactory;
        } else if (type.equals(DataType.REAL)) {
            return doubleFormatterFactory;
        } else {
            return defaultFormatterFactory;
        }
    }

    private FormatterFactory() {
    }
}
