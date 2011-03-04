package org.jphototagger.lib.image.util;

import org.jphototagger.lib.resource.JslBundle;

import java.awt.Image;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.filechooser.FileSystemView;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Werkzeuge für Icons.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author Elmar Baumann
 */
public final class IconUtil {

    /**
     * Liefert ein Bild eines Icons (z.B. aus einem JAR-File).
     *
     * @param  path Pfad zum Icon, z.B.
     *              <code>/org/jphototagger/program/resource/frameicon.png</code>
     * @return Bild des Icons (<code>ImageIcon</code>-Objekt) oder null,
     *         falls dieses nicht geladen wurde
     */
    public static Image getIconImage(String path) {
        if (path == null) {
            throw new NullPointerException("path == null");
        }

        java.net.URL imgURL = IconUtil.class.getResource(path);

        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        } else {
            Logger.getLogger(IconUtil.class.getName()).log(Level.SEVERE, null,
                             JslBundle.INSTANCE.getString("IconUtil.GetIconImage.Error.FileNotFound") + path);
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
        if (paths == null) {
            throw new NullPointerException("paths == null");
        }

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
     *              <code>/org/jphototagger/program/resource/frameicon.png</code>
     * @return Icon des Typs <code>ImageIcon</code> oder null, falls dieses
     *         nicht geladen wurde
     */
    public static ImageIcon getImageIcon(String path) {
        if (path == null) {
            throw new NullPointerException("path == null");
        }

        java.net.URL imgURL = IconUtil.class.getResource(path);

        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            Logger.getLogger(IconUtil.class.getName()).log(Level.SEVERE, null,
                             JslBundle.INSTANCE.getString("IconUtil.GetImageIcon.Error.FileNotFound") + path);
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
        if (paths == null) {
            throw new NullPointerException("paths == null");
        }

        List<ImageIcon> icons = new ArrayList<ImageIcon>();

        for (String path : paths) {
            ImageIcon icon = getImageIcon(path);

            if (icon != null) {
                icons.add(icon);
            }
        }

        return icons;
    }

    /**
     * Returns a system specific icon of a file.
     *
     * @param  file file
     * @return icon or null on errors
     */
    public static Icon getSystemIcon(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        Icon icon = null;
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();

        if (file.exists()) {
            synchronized (fileSystemView) {
                try {
                    icon = fileSystemView.getSystemIcon(file);
                } catch (Exception ex) {
                    Logger.getLogger(IconUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return icon;
    }

    private IconUtil() {}
}
