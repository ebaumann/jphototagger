package de.elmar_baumann.lib.image.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Utils für Bilder.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/24
 */
public class ImageUtil {

    /**
     * Liefert von einem Bild ein ByteArrayInputStream-Objekt.
     * 
     * @param image Bild
     * @return      Stream oder null, wenn der Stream nicht erzeugt werden konnte
     */
    public static ByteArrayInputStream getByteArrayInputStream(Image image) {
        ByteArrayInputStream stream = null;
        try {
            BufferedImage bufferedImage =
                new BufferedImage(image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_INT_RGB);
            Graphics graphics = bufferedImage.getGraphics();
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpeg", outputStream); // NOI18N
            byte[] bytebuffer = outputStream.toByteArray();
            stream = new ByteArrayInputStream(bytebuffer);
        } catch (IOException ex) {
            Logger.getLogger(ImageUtil.class.getName()).log(Level.SEVERE, null,
                ex);
        }
        return stream;
    }
}
