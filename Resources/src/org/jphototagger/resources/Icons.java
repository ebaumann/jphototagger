package org.jphototagger.resources;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.swing.ImageIcon;
import org.jphototagger.api.preferences.CommonPreferences;

/**
 * @author Elmar Baumann
 */
public final class Icons {

    private static final float FONT_SCALE = CommonPreferences.getFontScale();
    private static final String ICONS_PATH = "/org/jphototagger/resources/icons";
    private static final String SMALL_APP_ICON_PATH = ICONS_PATH + "/icon_app_small.png";
    private static final String MEDIUM_APP_ICON_PATH = ICONS_PATH + "/icon_app_medium.png";
    private static final String LARGE_APP_ICON_PATH = ICONS_PATH + "/icon_app-128.png";
    private static final String HUGE_APP_ICON_PATH = ICONS_PATH + "/icon_app-256.png";
    private static final List<Image> APP_ICONS = new ArrayList<>();

    static {
        APP_ICONS.add(ResourcesCommon.getImage(SMALL_APP_ICON_PATH));
        APP_ICONS.add(ResourcesCommon.getImage(MEDIUM_APP_ICON_PATH));
        APP_ICONS.add(ResourcesCommon.getImage(LARGE_APP_ICON_PATH));
        APP_ICONS.add(ResourcesCommon.getImage(HUGE_APP_ICON_PATH));
    }

    public static List<Image> getAppIcons() {
        return Collections.unmodifiableList(APP_ICONS);
    }

    public static Image getSmallAppIcon() {
        return ResourcesCommon.getImage(SMALL_APP_ICON_PATH);
    }

    public static Image getMediumAppIcon() {
        return ResourcesCommon.getImage(MEDIUM_APP_ICON_PATH);
    }

    public static ImageIcon getIcon(String name) {
        Objects.requireNonNull(name, "name == null");

        ImageIcon icon = ResourcesCommon.getImageIcon(ICONS_PATH + "/" + getScaledName(name));

        return icon == null
                ? ResourcesCommon.getImageIcon(ICONS_PATH + "/" + name) // Trying to get unscaled icon
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
