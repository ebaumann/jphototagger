package de.elmar_baumann.lib.image.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-07-24
 */
public final class ImageUtil {

    /**
     * Returns a <code>ByteArrayInputStream</code> of an image.
     * 
     * @param image      image
     * @param formatName a String containg the informal name of the format
     * @return           stream oder null on errors
     */
    public static ByteArrayInputStream getByteArrayInputStream(
            Image image, String formatName) {

        if (image == null)
            throw new NullPointerException("image == null"); // NOI18N
        if (formatName == null)
            throw new NullPointerException("formatName == null"); // NOI18N

        ByteArrayInputStream stream = null;
        try {
            BufferedImage bufferedImage =
                    new BufferedImage(image.getWidth(null),
                    image.getHeight(null),
                    BufferedImage.TYPE_INT_RGB);
            Graphics graphics = bufferedImage.getGraphics();
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, formatName, outputStream); // NOI18N
            byte[] byteArray = outputStream.toByteArray();
            stream = new ByteArrayInputStream(byteArray);
        } catch (Exception ex) {
            Logger.getLogger(
                    ImageUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stream;
    }

    private ImageUtil() {
    }
}
