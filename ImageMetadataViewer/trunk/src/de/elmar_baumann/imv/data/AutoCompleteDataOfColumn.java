package de.elmar_baumann.imv.data;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.listener.UserSettingsChangeListener;
import de.elmar_baumann.imv.event.listener.impl.ListenerProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains autocomplete data of specific columns.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-09-01
 */
public final class AutoCompleteDataOfColumn
        implements UserSettingsChangeListener {

    public static final AutoCompleteDataOfColumn INSTANCE =
            new AutoCompleteDataOfColumn();
    private static final Map<Column, AutoCompleteData> DATA_OF_COLUMN =
            Collections.synchronizedMap(new HashMap<Column, AutoCompleteData>());
    private AutoCompleteData fastSearchData;

    /**
     * Returns the autocomplete data of a specific column.
     *
     * @param  column column
     * @return        autocomplete data of that column
     */
    public synchronized AutoCompleteData get(Column column) {
        AutoCompleteData data = DATA_OF_COLUMN.get(column);
        if (data == null) {
            data = new AutoCompleteData(Arrays.asList(column));
            DATA_OF_COLUMN.put(column, data);
        }
        return data;
    }

    public synchronized AutoCompleteData getFastSearchData() {
        if (fastSearchData == null) {
            fastSearchData = new AutoCompleteData(
                    UserSettings.INSTANCE.getFastSearchColumns());
        }
        return fastSearchData;
    }

    /**
     * Adds autocomplete data of a specific column.
     *
     * @param data   data to add
     * @param column column
     */
    public synchronized void addData(Column column, Object data) {
        AutoCompleteData autoComplete = get(column);
        if (data != null) {
            if (data instanceof String) {
                autoComplete.addString((String) data);
            } else if (data instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> list = (List) data;
                for (String string : list) {
                    autoComplete.addString(string);
                }
            } else {
                assert false : "Value is neither string nor list: " + data; // NOI18N
            }
        }
    }

    private AutoCompleteDataOfColumn() {
        ListenerProvider.INSTANCE.addUserSettingsChangeListener(this);
    }

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        if (evt.getType().equals(
                UserSettingsChangeEvent.Type.FAST_SEARCH_COLUMNS)) {
            synchronized (this) {
                fastSearchData = new AutoCompleteData(
                        UserSettings.INSTANCE.getFastSearchColumns());
            }
        }
    }
}
