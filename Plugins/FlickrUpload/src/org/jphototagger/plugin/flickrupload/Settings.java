package org.jphototagger.plugin.flickrupload;

import org.jphototagger.lib.util.ServiceLookup;
import org.jphototagger.services.Storage;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class Settings {

    private static final String KEY_DC_DESCRIPTION = "org.jphototagger.plugin.flickrupload.AddDcDescription";
    private static final String KEY_PHOTOSHOP_HEADLINE = "org.jphototagger.plugin.flickrupload.AddPhotoshopHeadline";
    private static final String KEY_DC_SUBJECTS = "org.jphototagger.plugin.flickrupload.AddDcSubjects";
    private static final String VALUE_BOOLEAN_TRUE = "1";
    private static final String VALUE_BOOLEAN_FALSE = "0";
    private final Storage storage = ServiceLookup.lookup(Storage.class);

    public void setAddDcDescription(boolean add) {
        setBoolean(KEY_DC_DESCRIPTION, add);
    }

    public void setAddPhotoshopHeadline(boolean add) {
        setBoolean(KEY_PHOTOSHOP_HEADLINE, add);
    }

    public void setAddDcSubjects(boolean add) {
        setBoolean(KEY_DC_SUBJECTS, add);
    }

    public boolean isAddDcDescription() {
        return isTrue(KEY_DC_DESCRIPTION);
    }

    public boolean isAddPhotoshopHeadline() {
        return isTrue(KEY_PHOTOSHOP_HEADLINE);
    }

    public boolean isAddDcSubjects() {
        return isTrue(KEY_DC_SUBJECTS);
    }

    private boolean isTrue(String key) {
        if (storage == null) {
            return false;
        }

        String value = storage.getString(key);

        return value != null && value.equals(VALUE_BOOLEAN_TRUE);
    }

    private void setBoolean(String key, boolean b) {
        if (storage != null) {

            storage.setString(key, b
                    ? VALUE_BOOLEAN_TRUE
                    : VALUE_BOOLEAN_FALSE);
        }
    }
}
