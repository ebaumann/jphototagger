package de.elmar_baumann.lib.image;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 * Bildtransformationen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/19
 */
public final class ImageTransform {

    /**
     * Rotiert ein Bild.
     * 
     * @param img   Bild
     * @param angle Winkel
     * @return      Rotiertes Bild
     */
    public static Image rotate(Image img, double angle) {
        return tilt(toBufferedImage(img), Math.toRadians(angle));
    }

    // Code von http://forums.sun.com/thread.jspa?forumID=54&threadID=5286788
    private static BufferedImage tilt(BufferedImage image, double angle) {
        double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
        int w = image.getWidth(), h = image.getHeight();
        int neww = (int) Math.floor(w * cos + h * sin), newh = (int) Math.floor(
            h * cos + w * sin);
        GraphicsConfiguration gc = getDefaultConfiguration();
        BufferedImage result = gc.createCompatibleImage(neww, newh,
            Transparency.OPAQUE);
        Graphics2D g = result.createGraphics();
        g.translate((neww - w) / 2, (newh - h) / 2);
        g.rotate(angle, w / 2, h / 2);
        g.drawRenderedImage(image, null);
        g.dispose();
        return result;
    }

    // Code von http://forums.sun.com/thread.jspa?forumID=54&threadID=5286788
    private static GraphicsConfiguration getDefaultConfiguration() {
        GraphicsEnvironment ge =
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }

    // Code von http://exampledepot.com/egs/java.awt.image/Image2Buf.html?l=rel
    private static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        image = new ImageIcon(image).getImage();
        boolean hasAlpha = hasAlpha(image);
        BufferedImage bimage = null;
        GraphicsEnvironment ge =
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException ex) {
            Logger.getLogger(ImageTransform.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (bimage == null) {
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        Graphics g = bimage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    // Code von http://exampledepot.com/egs/java.awt.image/Image2Buf.html?l=rel
    private static boolean hasAlpha(Image image) {
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage) image;
            return bimage.getColorModel().hasAlpha();
        }

        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException ex) {
            Logger.getLogger(ImageTransform.class.getName()).log(Level.SEVERE, null, ex);
        }

        ColorModel cm = pg.getColorModel();
        return cm == null ? false : cm.hasAlpha();
    }

    private ImageTransform() {
    }
}

