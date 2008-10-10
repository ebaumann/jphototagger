package de.elmar_baumann.lib.componentutil;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * The application's Look and Feel.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/14
 */
public class LookAndFeelUtil {

    private static final String propertyColorDelim = ",";

    /**
     * Set's the application's Look and Feel. Must be called before the first
     * GUI element will be created.
     */
    public static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(LookAndFeelUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LookAndFeelUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(LookAndFeelUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(LookAndFeelUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Loads a properties file with the class loader and calls
     * {@link #setCustomLookAndFeel(java.io.InputStream)}. Typically used
     * within ar JAR file.
     * 
     * @param propertyFilename Name of the properties file, e.g.
     *        <code>/de/elmar_baumann/lib/resource/LookAndFeel.properties</code>
     */
    public static void setCustomLookAndFeel(String propertyFilename) {
        setCustomLookAndFeel(LookAndFeelUtil.class.getClassLoader().getResourceAsStream(propertyFilename));
    }

    /**
     * Sets a custom Look and Feel. Uses a properties file which defines the
     * colors. A sample file is in
     * <code>de.elmar_baumann.lib.resource.LookAndFeel.properties</code>
     * 
     * @param is Input stream with the key - value pairs
     */
    public static void setCustomLookAndFeel(InputStream is) {
        try {
            Properties properties = new Properties();
            properties.load(is);
            Set<Object> keys = properties.keySet();
            for (Object key : keys) {
                UIManager.put(key, getColorFromProperty(properties.get(key).toString()));
            }
        } catch (IOException ex) {
            Logger.getLogger(LookAndFeelUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Color getColorFromProperty(String property) {
        StringTokenizer tokenizer = new StringTokenizer(property, propertyColorDelim);
        if (tokenizer.countTokens() == 3) {
            String red = tokenizer.nextToken().trim();
            String green = tokenizer.nextToken().trim();
            String blue = tokenizer.nextToken().trim();
            int r = 0,  g = 0, b = 0;
            try {
                r = Integer.parseInt(red);
                g = Integer.parseInt(green);
                b = Integer.parseInt(blue);
            } catch (NumberFormatException ex) {
                Logger.getLogger(LookAndFeelUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
            return new Color(r, g, b);
        } else {
            return null;
        }
    }
}
