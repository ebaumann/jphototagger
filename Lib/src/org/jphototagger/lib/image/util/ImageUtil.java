package org.jphototagger.lib.image.util;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Image utils.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author Elmar Baumann
 */
public final class ImageUtil {

    /**
     * Returns a <code>ByteArrayInputStream</code> of an image.
     *
     * @param image      image
     * @param formatName a String containg the informal name of the format
     * @return           stream oder null on errors
     */
    public static ByteArrayInputStream getByteArrayInputStream(Image image, String formatName) {
        if (image == null) {
            throw new NullPointerException("image == null");
        }

        if (formatName == null) {
            throw new NullPointerException("formatName == null");
        }

        ByteArrayInputStream stream = null;

        try {
            BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
                                              BufferedImage.TYPE_INT_RGB);
            Graphics graphics = bufferedImage.getGraphics();

            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ImageIO.write(bufferedImage, formatName, outputStream);

            byte[] byteArray = outputStream.toByteArray();

            stream = new ByteArrayInputStream(byteArray);
        } catch (Exception ex) {
            Logger.getLogger(ImageUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return stream;
    }

    /**
     * Returns the new dimensions of an image if the image shall be scaled.
     *
     * @param img      image to scale
     * @param maxWidth the new width of the longer image side
     * @return         new dimensions or null if they couldn't be calculated
     */
    public static Dimension getNewDimensions(BufferedImage img, int maxWidth) {
        if (img == null) {
            throw new NullPointerException("img == null");
        }

        if (maxWidth < 0) {
            throw new IllegalArgumentException("Negative width: " + maxWidth);
        }

        int width = img.getWidth();
        int height = img.getHeight();

        assert(width > 0) && (height > 0) : "Width " + width + " height " + height + " have to be > 0!";

        if ((width <= 0) || (height <= 0)) {
            return null;
        }

        boolean isLandscape = width > height;
        double aspectRatio = (double) width / (double) height;
        int lenOtherSide = isLandscape
                           ? (int) ((double) maxWidth / aspectRatio + 0.5)
                           : (int) ((double) maxWidth * aspectRatio + 0.5);
        int newWidth = isLandscape
                       ? maxWidth
                       : lenOtherSide;
        int newHeight = isLandscape
                        ? lenOtherSide
                        : maxWidth;

        assert(newWidth > 0) && (newHeight > 0) : "Width " + newWidth + " height " + newHeight + " have to be > 0!";

        if ((newWidth <= 0) || (newHeight <= 0)) {
            return null;
        }

        return new Dimension(newWidth, newHeight);
    }

    /**
     * Returns a scaled instance of an image with a maximum length of the longer
     * image dimension.
     *
     * @param image     image
     * @param maxLength maximum length of the maximum dimension
     * @return          scaled instance
     */
    public static Image getScaledInstance(Image image, int maxLength) {
        if (image == null) {
            throw new NullPointerException("image == null");
        }

        if (maxLength <= 0) {
            throw new IllegalArgumentException("Illegal length: " + maxLength);
        }

        int width = image.getWidth(null);
        int height = image.getHeight(null);
        double scaleFactor = getScaleFactor(width, height, maxLength);

        if ((scaleFactor == 1) || (scaleFactor == 0)) {
            return image;
        } else {
            return image.getScaledInstance((int) (width / scaleFactor + 0.5), (int) (height / scaleFactor + 0.5),
                                           Image.SCALE_DEFAULT);
        }
    }

    /**
     * Returns a thumbnail from an image file.
     *
     * @param  imageFile image file readable through the Java Imaging I/O
     * @param  maxLength length in pixel of the longer image dimension (width or
     *                   height)
     * @return           image or null
     */
    public static Image getThumbnail(File imageFile, int maxLength) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (maxLength <= 0) {
            throw new IllegalArgumentException("Illegal length: " + maxLength);
        }

        BufferedImage image = loadImage(imageFile);
        BufferedImage scaledImage = null;

        if (image != null) {
            scaledImage = stepScaleImage(image, maxLength, 0.5);
        }

        return scaledImage;
    }

    private static BufferedImage stepScaleImage(BufferedImage image, int minWidth, double qfactor) {
        assert qfactor < 1.0 : "qfactor must be < 1.0";

        BufferedImage scaledImage = null;

        try {
            int origHeight = image.getHeight();
            int origWidth = image.getWidth();
            double factor = getScaleFactor(origWidth, origHeight, minWidth);
            int scaledWidth = (int) (origWidth / factor);
            int scaledHeight = (int) (origHeight / factor);
            int pass = 1;
            BufferedImage img = image;

            while (((origWidth * qfactor) > scaledWidth) || ((origHeight * qfactor) > scaledHeight)) {
                int width = (int) (origWidth * qfactor);
                int height = (int) (origHeight * qfactor);

                img = scaleImage(width, height, img);
                origWidth = img.getWidth();
                origHeight = img.getHeight();
                pass++;
            }

            scaledImage = scaleImage(scaledWidth, scaledHeight, img);
        } catch (Exception ex) {
            Logger.getLogger(ImageUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return scaledImage;
    }

    private static double getScaleFactor(int width, int height, int maxWidth) {
        double longer = (width > height)
                        ? width
                        : height;

        return longer / (double) maxWidth;
    }

    private static BufferedImage scaleImage(int scaledWidth, int scaledHeight, BufferedImage image) {
        BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = scaledImage.createGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);

        return scaledImage;
    }

    private static BufferedImage loadImage(File file) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(file);

            MediaTracker mediaTracker = new MediaTracker(new Container());

            mediaTracker.addImage(image, 0);
            mediaTracker.waitForID(0);
        } catch (Exception ex) {
            Logger.getLogger(ImageUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return image;
    }

    private ImageUtil() {}
}
