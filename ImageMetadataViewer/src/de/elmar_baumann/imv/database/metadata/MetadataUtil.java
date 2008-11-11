package de.elmar_baumann.imv.database.metadata;

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
 * @version 2008/10/28
 */
public class MetadataUtil {

    private static DefaultFormatterFactory integerFormatterFactory;
    private static DefaultFormatterFactory doubleFormatterFactory;
    private static DefaultFormatterFactory dateFormatterFactory;
    private static DefaultFormatterFactory defaultFormatterFactory = new DefaultFormatterFactory(new DefaultFormatter());
    

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
        } catch (ParseException ex) {
            de.elmar_baumann.imv.Logging.logWarning(MetadataUtil.class, ex);
        }
    }

    public static DefaultFormatterFactory getFormatterFactory(Column column) {
        DataType type = column.getDataType();
        if (type.equals(DataType.Date)) {
            return dateFormatterFactory;
        } else if (type.equals(DataType.Bigint) || type.equals(DataType.Integer) ||
            type.equals(DataType.Smallint)) {
            return integerFormatterFactory;
        } else if (type.equals(DataType.Real)) {
            return doubleFormatterFactory;
        } else {
            return defaultFormatterFactory;
        }
    }
}
