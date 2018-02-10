package org.jphototagger.resources;

import java.awt.Image;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 * @author Elmar Baumann
 */
public final class ResourcesCommon {

    /**
     * Converts a path to a localized path.
     * <p>
     * A localized path is the path where before the last path component the
     * default locale's language code will be added as path component. If the
     * language code is <code>"de"</code> and the path is
     * <code>"/org/jphototagger/program/resoure/images/image.png"</code>, the
     * localized path will be
     * <code>"/org/jphototagger/program/resoure/images/de/image.png"</code>.
     *
     * @param  path path
     * @return      localized path
     * @throws      NullPointerException if <code>path</code> is null
     * @throws      IllegalArgumentException if the trimmed path is empty
     */
    public static String toLocalizedPath(String path) {
        if (path == null) {
            throw new NullPointerException("path == null");
        }
        if (path.trim().isEmpty()) {
            throw new IllegalArgumentException("path is empty!");
        }
        String lang = Locale.getDefault().getLanguage();
        int lastPathDelim = path.lastIndexOf('/');
        return (lastPathDelim >= 0) ? path.substring(0, lastPathDelim + 1) + lang + "/" + path.substring(lastPathDelim + 1) : lang + "/" + path;
    }

    static ImageIcon getImageIcon(String path) {
        URL imgURL = Icons.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            Logger.getLogger(Icons.class.getName()).log(Level.SEVERE, null, "Image path not found: " + path);
        }
        return null;
    }

    static Image getImage(String path) {
        ImageIcon imageIcon = ResourcesCommon.getImageIcon(path);
        return imageIcon == null
                ? null
                : imageIcon.getImage();
    }
}
