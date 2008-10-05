package de.elmar_baumann.lib.image.icon;

import de.elmar_baumann.lib.resource.Bundle;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 * Werkzeuge für Icons.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class IconUtil {

    /**
     * Liefert ein Bild eines Icons (z.B. aus einem JAR-File).
     * 
     * @param  path Pfad zum Icon, z.B.
     *              <code>/de/elmar_baumann/imagemetadataviewer/resource/frameicon.png</code>
     * @return Bild des Icons (<code>ImageIcon</code>-Objekt) oder null,
     *         falls dieses nicht geladen wurde
     */
    public static Image getIconImage(String path) {
        java.net.URL imgURL = new IconUtil().getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        } else {
            Logger.getLogger(IconUtil.class.getName()).log(Level.SEVERE, null, Bundle.getString("IconUtil.GetIconImage.ErrorMessage.FileNotFound") + path);
        }
        return null;
    }

    /**
     * Liefert mehrere Bilder von Icons.
     * 
     * @param  paths Pfade zu den Bildern
     * @return Bilder, die geladen werden konnten (ungleich null sind)
     * @see    #getIconImage(java.lang.String)
     */
    public static List<Image> getIconImages(List<String> paths) {
        List<Image> images = new ArrayList<Image>();
        for (String path : paths) {
            Image image = getIconImage(path);
            if (image != null) {
                images.add(image);
            }
        }
        return images;
    }

    /**
     * Liefert ein Icon (z.B. aus einem JAR-File).
     * 
     * @param  path Pfad zum Icon, z.B.
     *              <code>/de/elmar_baumann/imagemetadataviewer/resource/frameicon.png</code>
     * @return Icon des Typs <code>ImageIcon</code> oder null, falls dieses
     *         nicht geladen wurde
     */
    public static ImageIcon getImageIcon(String path) {
        java.net.URL imgURL = new IconUtil().getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            Logger.getLogger(IconUtil.class.getName()).log(Level.SEVERE, null, Bundle.getString("IconUtil.GetImageIcon.ErrorMessage.FileNotFound") + path);
        }
        return null;
    }

    /**
     * Liefert mehrere Icons.
     * 
     * @param  paths Pfade zu den Icons
     * @return Icons des Typs <code>ImageIcon</code>, die geladen werden konnten
     *         (ungleich null sind)
     * @see    #getImageIcon(java.lang.String)
     */
    public static List<ImageIcon> getImageIcons(List<String> paths) {
        List<ImageIcon> icons = new ArrayList<ImageIcon>();
        for (String path : paths) {
            ImageIcon icon = getImageIcon(path);
            if (icon != null) {
                icons.add(icon);
            }
        }
        return icons;
    }
}
