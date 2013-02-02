package org.jphototagger.lib.swing;

import java.awt.Image;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;


/**
 * @author Elmar Baumann
 */
public final class IconUtil {

    /**
     * @param  path e.g. <code>/org/jphototagger/program/resource/frameicon.png</code>
     * @return image or null
     */
    public static Image getIconImage(String path) {
        if (path == null) {
            throw new NullPointerException("path == null");
        }
        java.net.URL imgURL = IconUtil.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        } else {
            Logger.getLogger(IconUtil.class.getName()).log(Level.SEVERE, null, "Image path not found: " + path);
        }
        return null;
    }

    /**
     * @param  path e.g. <code>/org/jphototagger/program/resource/frameicon.png</code>
     * @return Icon or null
     */
    public static ImageIcon getImageIcon(String path) {
        if (path == null) {
            throw new NullPointerException("path == null");
        }
        java.net.URL imgURL = IconUtil.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            Logger.getLogger(IconUtil.class.getName()).log(Level.SEVERE, null, "Image path not found: " + path);
        }
        return null;
    }

    /**
     * @param clazz Class within the same package as the icon
     * @param iconName e.g. "file.png"
     * @return i
     */
    public static ImageIcon getImageIcon(Class<?> clazz, String iconName) {
        if (clazz == null) {
            throw new NullPointerException("clazz == null");
        }
        if (iconName == null) {
            throw new NullPointerException("iconName == null");
        }
        String packagePath = resolvePackagePathForResource(clazz);
        return getImageIcon('/' + packagePath + '/' + iconName);
    }

    private static String resolvePackagePathForResource(Class<?> clazz) {
        String className = clazz.getName();
        int indexLastDot = className.lastIndexOf('.');
        if (indexLastDot < 1) {
            return "";
        }
        String packagePath = className.substring(0, indexLastDot);
        return packagePath.replace(".", "/");
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
                } catch (Throwable t) {
                    Logger.getLogger(IconUtil.class.getName()).log(Level.SEVERE, null, t);
                }
            }
        }
        return icon;
    }

    private IconUtil() {
    }
}
