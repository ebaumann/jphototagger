package de.elmar_baumann.imv.app;

import de.elmar_baumann.lib.image.util.IconUtil;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;

/**
 * Icons used in the application.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-02-19
 */
public final class AppIcons {

    /**
     * Path where all icons stored
     */
    private static final String PATH_ICONS =
            "/de/elmar_baumann/imv/resource/icons"; // NOI18N
    /**
     * Path to the small application's icon (16 x 16 pixels)
     */
    private static final String PATH_APP_ICON_SMALL =
            PATH_ICONS + "/icon_app_small.png";  // NOI18N
    /**
     * Path to the medium sized application's icon (32 x 32 pixels)
     */
    private static final String PATH_APP_ICON_MEDIUM =
            PATH_ICONS + "/icon_app_medium.png";  // NOI18N
    private static List<String> appIconPaths = new ArrayList<String>();
    private static List<Image> appIcons = new ArrayList<Image>();

    static {
        appIconPaths.add(PATH_APP_ICON_SMALL);
        appIconPaths.add(PATH_APP_ICON_MEDIUM);
    }

    static {
        appIcons.add(IconUtil.getIconImage(PATH_APP_ICON_SMALL));
        appIcons.add(IconUtil.getIconImage(PATH_APP_ICON_MEDIUM));
    }

    /**
     * Returns the application's icons (small and medium sized).
     *
     * @return icons
     */
    public static List<Image> getAppIcons() {
        return appIcons;
    }

    /**
     * Returns the paths to the application's icons (small and medium sized).
     *
     * @return Pfade
     */
    public static List<String> getAppIconPaths() {
        return appIconPaths;
    }

    /**
     * Returns an icon located in the application's icon path.
     *
     * @param  name  name of the icon file
     * @return icon
     */
    public static Icon getIcon(String name) {
        return IconUtil.getImageIcon(PATH_ICONS + "/" + name); // NOI18N
    }

    private AppIcons() {
    }
}
