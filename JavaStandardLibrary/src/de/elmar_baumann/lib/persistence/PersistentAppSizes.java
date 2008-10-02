package de.elmar_baumann.lib.persistence;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Persistent gespeicherte Größe und Position der Anwendung.
 * Benutzt dazu den Singleton {@link PersistentSettings}. <em>Das heißt, die
 * Einstellungen werden nur persistent gespeichert durch Aufruf von
 * {@link PersistentSettings#writeToFile()}!</em>
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/08/08
 * @see     PersistentSettings
 */
public class PersistentAppSizes {

    private static final String postfixKeyWidth = ".Width"; // NOI18N
    private static final String postfixKeyHeight = ".Height"; // NOI18N
    private static final String postfixKeyLocationX = ".LocationX"; // NOI18N
    private static final String postfixKeyLocationY = ".LocationY"; // NOI18N

    /**
     * Setzt die Größe und Position einer Komponente.
     * 
     * @param component Komponente
     * @see             #getSize(java.awt.Component)
     * @see             #getLocation(java.awt.Component)
     */
    public static void getSizeAndLocation(Component component) {
        getSize(component);
        getLocation(component);
    }

    /**
     * Setzt die Größe einer Komponente.
     * 
     * Benutzt als Schlüssel den Klassennamen der Komponente.
     * 
     * @param component Komponente
     */
    public static void getSize(Component component) {
        getSize(component, component.getClass().getName());
    }

    /**
     * Setzt die Größe einer Komponente.
     * 
     * @param component Komponente
     * @param key       Schlüssel
     */
    public static void getSize(Component component, String key) {
        PersistentSettings settings = PersistentSettings.getInstance();
        Properties properties = settings.getProperties();
        String keyWidth = key + postfixKeyWidth;
        String keyHeight = key + postfixKeyHeight;

        try {
            if (properties.containsKey(keyWidth) && properties.containsKey(keyHeight)) {
                Integer storedWidth = new Integer(properties.getProperty(keyWidth));
                Integer storedHeight = new Integer(properties.getProperty(keyHeight));
                int width = storedWidth.intValue();
                int height = storedHeight.intValue();
                component.setPreferredSize(new Dimension(width, height));
                component.setSize(new Dimension(width, height));
            }
        } catch (NumberFormatException ex) {
            Logger.getLogger(PersistentAppSizes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Speichert Größe und Position einer Komponente.
     * 
     * @param component Komponente
     * @see             #setSize(java.awt.Component)
     * @see             #setLocation(java.awt.Component)
     */
    public static void setSizeAndLocation(Component component) {
        setSize(component);
        setLocation(component);
    }

    /**
     * Speichert die Größe einer Komponente.
     * 
     * Benutzt als Schlüssel den Klassennamen der Komponente.
     * 
     * @param component Komponente
     */
    public static void setSize(Component component) {
        setSize(component, component.getClass().getName());
    }

    /**
     * Speichert die Größe einer Komponente.
     * 
     * @param component Komponente
     * @param key       Schlüssel
     */
    public static void setSize(Component component, String key) {
        Dimension size = component.getSize();
        Properties properties = PersistentSettings.getInstance().getProperties();
        String keyWidth = key + postfixKeyWidth;
        String keyHeight = key + postfixKeyHeight;

        properties.setProperty(keyWidth, Integer.toString(size.width));
        properties.setProperty(keyHeight, Integer.toString(size.height));
    }

    /**
     * Setzt die Position einer Komponente.
     * 
     * Benutzt als Schlüssel den Klassennamen der Komponente.
     * 
     * @param component Komponente
     */
    public static void getLocation(Component component) {
        getLocation(component, component.getClass().getName());
    }

    /**
     * Setzt die Position einer Komponente.
     * 
     * @param component Komponente
     * @param key       Schlüssel
     */
    public static void getLocation(Component component, String key) {
        PersistentSettings settings = PersistentSettings.getInstance();
        Properties properties = settings.getProperties();
        String keyLocationX = key + postfixKeyLocationX;
        String keyLocationY = key + postfixKeyLocationY;

        if (properties.containsKey(keyLocationX) && properties.containsKey(
            keyLocationY)) {
            try {
                Integer storedPosX = new Integer(properties.getProperty(keyLocationX));
                Integer storedPosY = new Integer(properties.getProperty(keyLocationY));
                int locationX = storedPosX.intValue();
                int locationY = storedPosY.intValue();
                component.setLocation(new Point(locationX, locationY));
            } catch (NumberFormatException ex) {
                Logger.getLogger(PersistentAppSizes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Speichert die Position einer Komponente.
     * 
     * Benutzt als Schlüssel den Klassennamen der Komponente.
     * 
     * @param component Komponente
     */
    public static void setLocation(Component component) {
        setLocation(component, component.getClass().getName());
    }

    /**
     * Speichert die Position einer Komponente.
     * 
     * @param component Komponente
     * @param key       Schlüssel
     */
    public static void setLocation(Component component, String key) {
        Point location = component.getLocation();
        Properties properties = PersistentSettings.getInstance().getProperties();
        String keyLocationX = key + postfixKeyLocationX;
        String keyLocationY = key + postfixKeyLocationY;

        properties.setProperty(keyLocationX, Integer.toString(location.x));
        properties.setProperty(keyLocationY, Integer.toString(location.y));
    }
}
