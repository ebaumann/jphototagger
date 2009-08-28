package de.elmar_baumann.imv.image;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.image.thumbnail.ThumbnailUtil;
import de.elmar_baumann.lib.image.util.ImageUtil;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Handling of raw images.
 *
 * Currently using {@link http://jrawio.tidalwave.it/} but encapsulating this
 * into this class for usage of an other library in future.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-28
 */
public final class RawImage {

    /**
     * Reads a raw image file.
     *
     * @param  imageFile file to read
     * @return           image
     */
    public static BufferedImage read(File imageFile) {
        try {
            return ImageIO.read(imageFile);
        } catch (Exception ex) {
            AppLog.logSevere(RawImage.class, ex);
        }
        return null;
    }

    /**
     * Reads a raw image and scales it.
     *
     * @param imageFile file to read
     * @param maxWidth  scaled width of the longer side
     * @return          image or null on errors
     */
    public static BufferedImage readAndScale(File imageFile, int maxWidth) {
        BufferedImage img = read(imageFile);
        if (img == null) return null;

        Dimension newDimensions = ImageUtil.getNewDimensions(img, maxWidth);
        if (newDimensions == null) return null;

        return ThumbnailUtil.scaleImage(
                newDimensions.width, newDimensions.height, img);
    }

    private RawImage() {
    }
}
