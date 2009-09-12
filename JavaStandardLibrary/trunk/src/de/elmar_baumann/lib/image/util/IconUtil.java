/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.lib.image.util;

import de.elmar_baumann.lib.resource.Bundle;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

/**
 * Werkzeuge für Icons.
 * 
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class IconUtil {

    /**
     * Liefert ein Bild eines Icons (z.B. aus einem JAR-File).
     * 
     * @param  path Pfad zum Icon, z.B.
     *              <code>/de/elmar_baumann/imv/resource/frameicon.png</code>
     * @return Bild des Icons (<code>ImageIcon</code>-Objekt) oder null,
     *         falls dieses nicht geladen wurde
     */
    public static Image getIconImage(String path) {
        if (path == null)
            throw new NullPointerException("path == null"); // NOI18N

        java.net.URL imgURL = IconUtil.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        } else {
            Logger.getLogger(IconUtil.class.getName()).log(Level.SEVERE, null,
                    Bundle.getString(
                    "IconUtil.GetIconImage.Error.FileNotFound") + path); // NOI18N
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
        if (paths == null)
            throw new NullPointerException("paths == null"); // NOI18N

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
     *              <code>/de/elmar_baumann/imv/resource/frameicon.png</code>
     * @return Icon des Typs <code>ImageIcon</code> oder null, falls dieses
     *         nicht geladen wurde
     */
    public static ImageIcon getImageIcon(String path) {
        if (path == null)
            throw new NullPointerException("path == null"); // NOI18N

        java.net.URL imgURL = IconUtil.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            Logger.getLogger(IconUtil.class.getName()).log(Level.SEVERE, null,
                    Bundle.getString(
                    "IconUtil.GetImageIcon.Error.FileNotFound") + path); // NOI18N
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
        if (paths == null)
            throw new NullPointerException("paths == null"); // NOI18N

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
        if (file == null)
            throw new NullPointerException("file == null"); // NOI18N

        Icon icon = null;
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        if (file.exists()) {
            synchronized (fileSystemView) {
                try {
                    icon = fileSystemView.getSystemIcon(file);
                } catch (Exception ex) {
                    Logger.getLogger(IconUtil.class.getName()).log(Level.SEVERE,
                            null, ex);
                }
            }
        }
        return icon;
    }

    private IconUtil() {
    }
}
