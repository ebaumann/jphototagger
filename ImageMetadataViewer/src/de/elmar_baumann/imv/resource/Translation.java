package de.elmar_baumann.imv.resource;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Übersetzt Strings. Die Übersetzungen stehen in einer locale-spezifischen
 * Properties-Datei.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/05
 */
public class Translation {

    private static final String pathPrefix = "de/elmar_baumann/imagemetadataviewer/resource/"; // NOI18N
    ResourceBundle bundle;

    public Translation(String propertiesFileBasename) {
        try {
            bundle = ResourceBundle.getBundle(pathPrefix + propertiesFileBasename);
        } catch (MissingResourceException ex) {
            Logger.getLogger(Translation.class.getName()).log(Level.FINE, null, ex);
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
        try {
            return bundle.getString(string);
        } catch (MissingResourceException ex) {
            Logger.getLogger(Translation.class.getName()).log(Level.FINE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Translation.class.getName()).log(Level.FINE, null, ex);
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
            Logger.getLogger(Translation.class.getName()).log(Level.FINE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Translation.class.getName()).log(Level.FINE, null, ex);
        }
        return alternate;
    }
}
