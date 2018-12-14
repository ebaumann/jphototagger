package org.jphototagger.lib.util;

import java.util.ListResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Elmar Baumann
 */
public final class Translation {

    private static final Logger LOGGER = Logger.getLogger(Translation.class.getName());
    private ResourceBundle bundle;
    private final String propertiesBasename;

    /**
     * @param classWithinSamePackageAsProperties
     * @param propertiesBasename name without suffix ".properties", e.g. "Bundle"
     */
    public Translation(Class<?> classWithinSamePackageAsProperties, String propertiesBasename) {
        if (classWithinSamePackageAsProperties == null) {
            throw new NullPointerException("classWithinSamePackageAsProperties == null");
        }
        if (propertiesBasename == null) {
            throw new NullPointerException("propertiesBasename == null");
        }
        String packagePath = classWithinSamePackageAsProperties.getPackage().getName();
        this.propertiesBasename = packagePath + '.' + propertiesBasename;
        try {
            LOGGER.log(Level.FINEST, "Loading resource bundle ''{0}''", this.propertiesBasename);
            this.bundle = ResourceBundle.getBundle(this.propertiesBasename);
        } catch (Throwable t) {
            this.bundle = EMPTY_BUNDLE;
            LOGGER.log(Level.SEVERE, null, t);
        }
    }

    public String translate(String string) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }
        try {
            return bundle.getString(string);
        } catch (Throwable t) {
            logMissingResource(t, string);
        }

        return string;
    }

    private void logMissingResource(Throwable t, String string) {
        String localizedMessage = t.getLocalizedMessage();
        LOGGER.log(Level.INFO, "Missing translation for ''{0}'' in dictionary [''{1}'' ''{2}'']",
                new Object[]{string, propertiesBasename, localizedMessage});
    }

    public String translate(String string, String ifNoTranslationExists) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }
        if (ifNoTranslationExists == null) {
            throw new NullPointerException("alternate == null");
        }
        try {
            return bundle.getString(string);
        } catch (Throwable t) {
            logMissingResource(t, string);
        }
        return ifNoTranslationExists;
    }

    public boolean canTranslate(String string) {
        return bundle.containsKey(string);
    }

    private static final ResourceBundle EMPTY_BUNDLE = new ListResourceBundle() {
        @Override
        protected Object[][] getContents() {
            return new Object[][]{{"", ""}};
        }
    };
}
