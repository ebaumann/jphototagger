package de.elmar_baumann.imv.resource;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Übersetzt Strings. Die Übersetzungen stehen in einer locale-spezifischen
 * Properties-Datei.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/05
 */
public final class Translation {

    private static final String pathPrefix = "de/elmar_baumann/imv/resource/"; // NOI18N
    private ResourceBundle bundle;

    public Translation(String propertiesFileBasename) {
        try {
            bundle = ResourceBundle.getBundle(pathPrefix + propertiesFileBasename);
        } catch (MissingResourceException ex) {
            de.elmar_baumann.imv.app.AppLog.logWarning(getClass(), ex);
        }
    }

    /**
     * Übersetzt einen String.
     * 
     * @param string Fremdsprachiger String (Key eines Propertys)
     * @return       Übersetzter String (Value eines Propertys) oder zu
     *               übersetzender String, falls keine Übersetzung möglich ist
     *               (Key nicht vorhanden in Properties-Datei)
     * @throws NullPointerException wenn der Schlüssel null ist
     */
    public String translate(String string) {
        try {
            return bundle.getString(string);
        } catch (MissingResourceException ex) {
            de.elmar_baumann.imv.app.AppLog.logWarning(getClass(), ex);
        } catch (NullPointerException ex) {
            throw ex;
        } catch (Exception ex) {
            de.elmar_baumann.imv.app.AppLog.logWarning(getClass(), ex);
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
        try {
            return bundle.getString(string);
        } catch (MissingResourceException ex) {
            de.elmar_baumann.imv.app.AppLog.logWarning(getClass(), ex);
        } catch (Exception ex) {
            de.elmar_baumann.imv.app.AppLog.logWarning(getClass(), ex);
        }
        return alternate;
    }
}
