package org.jphototagger.domain.database.file;

/**
 *
 *
 * @author Elmar Baumann
 */
final class Bundle {

    public static final Bundle INSTANCE = new Bundle();
    private org.jphototagger.lib.resource.Bundle bundle = org.jphototagger.lib.resource.Bundle.getBundle(Bundle.class);

    String getString(String key) {
        return bundle.getString(key);
    }

    private Bundle() {
    }
}
