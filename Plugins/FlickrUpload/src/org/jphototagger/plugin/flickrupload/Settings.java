package org.jphototagger.plugin.flickrupload;

import java.util.Properties;

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
    private final Properties properties;

    public Settings(Properties properties) {
        if (properties == null) {
            throw new NullPointerException("properties == null");
        }

        this.properties = properties;
    }

    public void setAddDcDescription(boolean add) {
        setBoolean(add, KEY_DC_DESCRIPTION);
    }

    public void setAddPhotoshopHeadline(boolean add) {
        setBoolean(add, KEY_PHOTOSHOP_HEADLINE);
    }

    public void setAddDcSubjects(boolean add) {
        setBoolean(add, KEY_DC_SUBJECTS);
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
        String value = properties.getProperty(key);

        return (value != null) && value.equals(VALUE_BOOLEAN_TRUE);
    }

    private void setBoolean(boolean b, String key) {
        properties.setProperty(key, b
                                    ? VALUE_BOOLEAN_TRUE
                                    : VALUE_BOOLEAN_FALSE);
    }
}
