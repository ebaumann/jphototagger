package org.jphototagger.resources;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.jphototagger.api.preferences.CommonPreferences;

/**
 * @author Elmar Baumann
 */
public final class Icons {

    private static final float FONT_SCALE = CommonPreferences.getFontScale();
    private static final String ICONS_PATH = "/org/jphototagger/resources/icons";

    private static ImageIcon getImageIcon(String path) {
        java.net.URL imgURL = Icons.class.getResource(path);

        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            Logger.getLogger(Icons.class.getName()).log(Level.SEVERE, null, "Image path not found: " + path);
        }

        return null;
    }

    public static ImageIcon getIcon(String name) {
        Objects.requireNonNull(name, "name == null");

        ImageIcon icon = getImageIcon(ICONS_PATH + "/" + getScaledName(name));

        return icon == null
                ? getImageIcon(ICONS_PATH + "/" + name) // Trying to get unscaled icon
                : icon;
    }

    // All image names are assumed to be in lowercase due performance (not
    // creating an uppercase or lowercase name and compare with ".PNG" or ".png"
    private static String getScaledName(String name) {
        if (FONT_SCALE < 1.5) {
            return name;
        }

        if (!name.endsWith(".png")) {
            return name;
        }

        int index = name.lastIndexOf(".png");
        if (index < 1) {
            return name;
        }

        String sizePostfix = FONT_SCALE > 3
                ? "-64.png"
                : FONT_SCALE > 2.0
                ? "-48.png"
                : FONT_SCALE > 1.5
                ? "-32.png"
                : FONT_SCALE > 1.0
                ? "-24.png"
                : "";

        return name.substring(0, index) + sizePostfix;
    }

    private Icons() {
    }
}
