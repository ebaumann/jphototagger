package de.elmar_baumann.imv;

import de.elmar_baumann.lib.image.icon.IconUtil;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/02/19
 */
public final class AppIcons {

    private static final String iconPath = "/de/elmar_baumann/imv/resource";
    /**
     * Pfad zum kleinen Anwendungssymbol.
     */
    public static final String pathAppIconSmall =
        iconPath + "/icon_app_small.png";  // NOI18N
    /**
     * Pfad zum mittelgroßen Anwendungssymbol.
     */
    public static final String pathAppIconMedium =
        iconPath + "/icon_app_medium.png";  // NOI18N

    private static List<String> appIconPaths = new ArrayList<String>();
    private static List<Image> appIcons = new ArrayList<Image>();
    private static final Icon mediumAppIcon = IconUtil.getImageIcon(pathAppIconMedium);


    static {
        appIconPaths.add(pathAppIconSmall);
        appIconPaths.add(pathAppIconMedium);
    }


    static {
        appIcons.add(IconUtil.getIconImage(pathAppIconSmall));
        appIcons.add(IconUtil.getIconImage(pathAppIconMedium));
    }

    /**
     * Liefert die Anwendungssymbole.
     *
     * @return Anwendungssymbole
     */
    public static List<Image> getAppIcons() {
        return appIcons;
    }

    /**
     * Liefert die Pfade der Anwendungssymbole.
     *
     * @return Pfade
     */
    public static List<String> getAppIconPaths() {
        return appIconPaths;
    }

    /**
     * Returns an icon within the app icon path.
     *
     * @param  name  name of the image file
     * @return icon
     */
    public static Icon getIcon(String name) {
        return IconUtil.getImageIcon(iconPath + "/" + name);
    }

    /**
     * Returns the path (directory name) of all icons.
     *
     * @return path
     */
    public static String getIconPath() {
        return iconPath;
    }

    /**
     * Liefert das mittelgroße Anwendungssymbol.
     *
     * @return Kleines Anwendungssymbol
     */
    public static Icon getMediumAppIcon() {
        return mediumAppIcon;
    }

    private AppIcons() {
    }

}
