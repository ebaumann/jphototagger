package de.elmar_baumann.lib.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads and writes to an {@link java.util.Properties} instance locations and
 * sizes of components.
 * 
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/08/08
 */
public final class ComponentSizesFromProperties {

    private static final String postfixKeyWidth = ".Width"; // NOI18N
    private static final String postfixKeyHeight = ".Height"; // NOI18N
    private static final String postfixKeyLocationX = ".LocationX"; // NOI18N
    private static final String postfixKeyLocationY = ".LocationY"; // NOI18N
    private final Properties properties;

    public ComponentSizesFromProperties(Properties properties) {
        if (properties == null)
            throw new NullPointerException("properties == null");

        this.properties = properties;
    }

    /**
     * Sets to a component the persistent written size and location. Uses the
     * class name as key. If the key does not exist, nothing will be done.
     * 
     * @param component component
     * @see             #getSize(java.awt.Component)
     * @see             #getLocation(java.awt.Component)
     */
    public void getSizeAndLocation(Component component) {
        if (component == null)
            throw new NullPointerException("component == null");

        getSize(component);
        getLocation(component);
    }

    /**
     * Sets to a component the persistent written size. Uses the class name as
     * key. If the key does not exist, nothing will be done.
     * 
     * @param component component
     */
    public void getSize(Component component) {
        if (component == null)
            throw new NullPointerException("component == null");

        getSize(component, component.getClass().getName());
    }

    /**
     * Sets to a component the persistent written size. If the key does not
     * exist, nothing will be done.
     * 
     * @param component component
     * @param key       key
     */
    public void getSize(Component component, String key) {
        if (component == null)
            throw new NullPointerException("component == null");
        if (key == null)
            throw new NullPointerException("key == null");

        String keyWidth = getKeyWidth(key);
        String keyHeight = getKeyHeight(key);

        try {
            if (properties.containsKey(keyWidth) && properties.containsKey(keyHeight)) {
                Integer width = new Integer(properties.getProperty(keyWidth));
                Integer height = new Integer(properties.getProperty(keyHeight));
                component.setPreferredSize(new Dimension(width, height));
                component.setSize(new Dimension(width, height));
            }
        } catch (NumberFormatException ex) {
            Logger.getLogger(ComponentSizesFromProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sets to a component the persistent written location. Uses the class name
     * as key. If the key does not exist, nothing will be done.
     *
     * @param component component
     */
    public void getLocation(Component component) {
        if (component == null)
            throw new NullPointerException("component == null");

        getLocation(component, component.getClass().getName());
    }

    /**
     * Sets to a component the persistent written location. If the key does not
     * exist, nothing will be done.
     *
     * @param component component
     * @param key       key
     */
    public void getLocation(Component component, String key) {
        if (component == null)
            throw new NullPointerException("component == null");
        if (key == null)
            throw new NullPointerException("key == null");

        String keyLocationX = getKeyLocationX(key);
        String keyLocationY = getKeyLocationY(key);

        if (properties.containsKey(keyLocationX) && properties.containsKey(keyLocationY)) {
            try {
                Integer locationX = new Integer(properties.getProperty(keyLocationX));
                Integer locationY = new Integer(properties.getProperty(keyLocationY));
                component.setLocation(new Point(locationX, locationY));
            } catch (NumberFormatException ex) {
                Logger.getLogger(ComponentSizesFromProperties.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Writes persistent the size and location of a component. Uses the
     * class name as key.
     * 
     * @param component component
     * @see             #setSize(java.awt.Component)
     * @see             #setLocation(java.awt.Component)
     */
    public void setSizeAndLocation(Component component) {
        if (component == null)
            throw new NullPointerException("component == null");

        setSize(component);
        setLocation(component);
    }

    /**
     * Writes persistent the size of a component. Uses the class name as key.
     * 
     * @param component component
     */
    public void setSize(Component component) {
        if (component == null)
            throw new NullPointerException("component == null");

        setSize(component, component.getClass().getName());
    }

    /**
     * Writes persistent the size of a component.
     * 
     * @param component component
     * @param key       key
     */
    public void setSize(Component component, String key) {
        if (component == null)
            throw new NullPointerException("component == null");
        if (key == null)
            throw new NullPointerException("key == null");

        Dimension size = component.getSize();

        properties.setProperty(getKeyWidth(key), Integer.toString(size.width));
        properties.setProperty(getKeyHeight(key), Integer.toString(size.height));
    }

    /**
     * Writes persistent the size of a component. Uses the class name as key.
     * 
     * @param component component
     */
    public void setLocation(Component component) {
        if (component == null)
            throw new NullPointerException("component == null");

        setLocation(component, component.getClass().getName());
    }

    /**
     * Writes persistent the size of a component.
     * 
     * @param component component
     * @param key       key
     */
    public void setLocation(Component component, String key) {
        if (component == null)
            throw new NullPointerException("component == null");
        if (key == null)
            throw new NullPointerException("key == null");

        Point location = component.getLocation();

        properties.setProperty(getKeyLocationX(key), Integer.toString(location.x));
        properties.setProperty(getKeyLocationY(key), Integer.toString(location.y));
    }

    private static String getKeyHeight(String key) {
        return key + postfixKeyHeight;
    }

    private static String getKeyWidth(String key) {
        return key + postfixKeyWidth;
    }

    private static String getKeyLocationX(String key) {
        return key + postfixKeyLocationX;
    }

    private static String getKeyLocationY(String key) {
        return key + postfixKeyLocationY;
    }
}
