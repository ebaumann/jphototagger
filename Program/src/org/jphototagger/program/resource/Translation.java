package org.jphototagger.program.resource;

import org.jphototagger.program.app.AppLogger;

import java.util.ResourceBundle;

/**
 * Übersetzt Strings. Die Übersetzungen stehen in einer locale-spezifischen
 * Properties-Datei.
 *
 * @author Elmar Baumann
 */
public final class Translation {
    private static final String PATH_PREFIX = "org/jphototagger/program/resource/properties/";
    private ResourceBundle bundle;
    private final String propertiesFilePath;

    public Translation(String propertiesFileBasename) {
        if (propertiesFileBasename == null) {
            throw new NullPointerException("propertiesFileBasename == null");
        }

        propertiesFilePath = PATH_PREFIX + propertiesFileBasename;

        try {
            bundle = ResourceBundle.getBundle(propertiesFilePath);
        } catch (Exception ex) {
            AppLogger.logSevere(Translation.class, ex);
        }
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
            AppLogger.logInfo(Translation.class, "Translation.Error.NoWordbookEntry", string, propertiesFilePath,
                              ex.getLocalizedMessage());
        }

        return string;
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
            AppLogger.logInfo(Translation.class, "Translation.Error.NoWordbookEntry", string, propertiesFilePath,
                              ex.getLocalizedMessage());
        }

        return alternate;
    }
}
