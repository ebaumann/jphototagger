package org.jphototagger.lib.util;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Elmar Baumann
 */
public final class Translation {

    private static final Logger LOGGER = Logger.getLogger(Translation.class.getName());
    private final ResourceBundle bundle;
    private final String propertiesBasename;

    /**
     *
     * @param propertiesBasename e.g. "org/jphototagger/mymodule/mypackage/Bundle"
     */
    public Translation(String propertiesBasename) {
        if (propertiesBasename == null) {
            throw new NullPointerException("propertiesBasename == null");
        }

        this.propertiesBasename = propertiesBasename;
        this.bundle = ResourceBundle.getBundle(propertiesBasename);
    }

    /**
     * Übersetzt einen String.
     *
     * @param string Fremdsprachiger String (Key eines Propertys)
     * @return       Übersetzter String (Value eines Propertys) oder zu
     *               übersetzender String, falls keine Übersetzung möglich ist
     *               (Key nicht vorhanden in Properties-Datei)
     */
    public String translate(String string) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }

        try {
            return bundle.getString(string);
        } catch (Exception ex) {
            logMissingResource(ex, string);
        }

        return string;
    }

    private void logMissingResource(Exception ex, String string) {
        String localizedMessage = ex.getLocalizedMessage();

        LOGGER.log(Level.INFO, "Missing translation for ''{0}'' in dictionary [''{1}'' ''{2}'']",
                new Object[]{string, propertiesBasename, localizedMessage});
    }

    /**
     * Übersetzt einen String.
     *
     * @param string    Fremdsprachiger String (Key eines Propertys)
     * @param alternate Alternative Übersetzung, die geliefert wird, wenn
     *                  keine Übersetzung gefunden wurde
     * @return          Übersetzter String (Value eines Propertys) oder
     *                  <code>alternate</code>, falls keine Übersetzung möglich
     *                  ist (Key nicht vorhanden in Properties-Datei)
     */
    public String translate(String string, String alternate) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }

        if (alternate == null) {
            throw new NullPointerException("alternate == null");
        }

        try {
            return bundle.getString(string);
        } catch (Exception ex) {
            logMissingResource(ex, string);
        }

        return alternate;
    }

    public boolean canTranslate(String string) {
        return bundle.containsKey(string);
    }
}
