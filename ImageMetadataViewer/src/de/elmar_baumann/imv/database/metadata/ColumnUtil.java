package de.elmar_baumann.imv.database.metadata;

import de.elmar_baumann.imv.model.ListModelSelectedColumns;
import de.elmar_baumann.lib.component.CheckList;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/02
 */
public class ColumnUtil {

    /**
     * Creates via Reflection columns from the keys of the columns
     * ({@link Column#getKey()}).
     * 
     * @param  columnKeys  column keys
     * @return columns of valid keys
     */
    public static List<Column> columnKeysToColumns(List<String> columnKeys) {
        List<Column> columns = new ArrayList<Column>();
        for (String key : columnKeys) {
            try {
                Class cl = Class.forName(key);
                @SuppressWarnings("unchecked")
                Method method = cl.getMethod("getInstance", new Class[0]); // NOI18N
                Object o = method.invoke(null, new Object[0]);
                if (o instanceof Column) {
                    columns.add((Column) o);
                }
            } catch (Exception ex) {
                de.elmar_baumann.imv.Log.logWarning(ColumnUtil.class, ex);
            }
        }
        return columns;
    }

    /**
     * Returns the descriptions of columns ({@link Column#getDescription()}).
     * 
     * @param  columns  columns
     * @return descriptions in the same order as in <code>columns</code>
     */
    public static List<String> getDescriptionsOfColumns(List<Column> columns) {
        List<String> text = new ArrayList<String>();
        for (Column column : columns) {
            text.add(column.getDescription());
        }
        return text;
    }

    /**
     * Returns all selected columns in a
     * {@link de.elmar_baumann.lib.component.CheckList} with a
     * {@link de.elmar_baumann.imv.model.ListModelSelectedColumns}.
     * 
     * @param  list  check list
     * @return selected columns
     */
    public static List<Column> getSelectedColumns(CheckList list) {
        List<Column> columns = new ArrayList<Column>();
        ListModelSelectedColumns model = (ListModelSelectedColumns) list.getModel();
        List<Integer> indices = list.getSelectedItemIndices();
        for (Integer index : indices) {
            columns.add(model.getColumnAtIndex(index));
        }
        return columns;
    }
}
