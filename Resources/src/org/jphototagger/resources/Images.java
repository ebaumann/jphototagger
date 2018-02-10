package org.jphototagger.resources;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * @author Elmar Baumann
 */
public final class Images {

    /**
     * Returns a localized image if exists.
     * <p>
     * A localized icon has the same path plus the default locale's language
     * code before the last path component.
     *
     * @param  path not localized path, e.g.
     *         <code>"/org/jphototagger/program/resoure/images/image.png"</code>
     * @return      localized icon, e.g. if the path is the same as in the
     *              the parameter doc obove, the icon of the path
     *     <code>"/org/jphototagger/program/resoure/images/de/image.png"</code>.
     *              If a localized icon does not exist, the icon of the path
     *              will be returned or null if the icon of the path does not
     *              exist.
     */
    public static Image getLocalizedImage(String path) {
        if (path == null) {
            throw new NullPointerException("path == null");
        }
        URL imgURL = Images.class.getResource(ResourcesCommon.toLocalizedPath(path));
        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        } else {
            return ResourcesCommon.getImage(path);
        }
    }

    private Images() {
    }

}
