package de.elmar_baumann.lib.resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Settings that will be used by the classes of this Java library.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class Settings {

    public static final Settings INSTANCE = new Settings();
    private List<String> iconImagesPaths = new ArrayList<String>();

    /**
     * Returns the paths to icon images. Each path is a single icon image,
     * e.g. <code>"/de/elmar_baumann/imv/resource/help.png"</code>.
     * 
     * @return paths. Is empty if not set through
     *         {@link #setIconImagesPath(java.util.List)}
     */
    public List<String> getIconImagesPaths() {
        return iconImagesPaths;
    }

    /**
     * Sets the paths to icon images. Each path is a single icon image,
     * e.g. <code>"/de/elmar_baumann/imv/resource/help.png"</code>.
     * 
     * @param iconImagesPaths paths
     */
    public void setIconImagesPath(List<String> iconImagesPaths) {
        if (iconImagesPaths == null)
            throw new NullPointerException("iconImagesPaths == null");

        this.iconImagesPaths = new ArrayList<String>(iconImagesPaths);
    }

    /**
     * Returns, whether at least one path to an icon image is defined.
     * 
     * @return true, if one ore more paths to icon images are defined
     */
    public boolean hasIconImages() {
        return !iconImagesPaths.isEmpty();
    }

    private Settings() {
    }
}
