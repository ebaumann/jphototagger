package org.jphototagger.image.thumbnail;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.exif.ExifInfo;
import org.jphototagger.image.util.ImageTransform;

import com.imagero.reader.IOParameterBlock;
import com.imagero.reader.ImageProcOptions;
import com.imagero.reader.ImageReader;
import com.imagero.reader.Imagero;
import com.imagero.reader.ReaderFactory;
import com.imagero.reader.tiff.TiffReader;

/**
 * @author Elmar Baumann, Tobias Stening
 */
final class ThumbnailUtil {

    private static final Logger LOGGER = Logger.getLogger(ThumbnailUtil.class.getName());

    private static class ImageAndReader {

        private final Image image;
        private final ImageReader imageReader;

        private ImageAndReader(Image image, ImageReader imageReader) {
            this.image = image;
            this.imageReader = imageReader;
        }
    }

    static Image getEmbeddedThumbnail(File file) {
        ImageAndReader imageAndReader = getEmbeddedThumbnailWithReader(file);
        Image thumbnail = imageAndReader.image;
        Image rotatedThumbnail = thumbnail;
        if (thumbnail != null) {
            ExifInfo exifInfo = Lookup.getDefault().lookup(ExifInfo.class);
            double rotateAngle = exifInfo.getRotationAngleOfEmbeddedThumbnail(file);
            LOGGER.log(Level.INFO, "Rotating extracted thumbnail that was embedded file ''{0}''", file);
            rotatedThumbnail = ImageTransform.rotate(thumbnail, rotateAngle);
        }
        closeReader(imageAndReader.imageReader);    // Needs to be open for calling ImageTransform.rotate()
        return rotatedThumbnail;
    }

    private static ImageAndReader getEmbeddedThumbnailWithReader(File file) {
        Image thumbnail = null;
        ImageReader reader = null;
        try {
            LOGGER.log(Level.INFO, "Reading embedded thumbnail from image file ''{0}'', size {1} Bytes", new Object[]{file, file.length()});
            reader = ReaderFactory.createReader(file);
            if (reader instanceof TiffReader) {
                TiffReader tiffReader = (TiffReader) reader;
                if (tiffReader.getThumbnailCount() > 0) {
                    ImageProducer thumbnailProducer = tiffReader.getThumbnail(0);
                    thumbnail = Toolkit.getDefaultToolkit().createImage(thumbnailProducer);
                }
            } else {
                IOParameterBlock ioParamBlock = new IOParameterBlock();
                ioParamBlock.setSource(file);
                thumbnail = Imagero.getThumbnail(ioParamBlock, 0);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new ImageAndReader(null, null);
        }
        return new ImageAndReader(thumbnail, reader);
    }

    static Image createThumbnailWithImagero(File file, int maxLength) {
        try {
            LOGGER.log(Level.INFO, "Creating thumbnail from image file ''{0}'', size of image file is {1} Bytes", new Object[]{file, file.length()});
            IOParameterBlock ioParamBlock = new IOParameterBlock();
            ImageProcOptions procOptions = new ImageProcOptions();
            ioParamBlock.setSource(file);
            procOptions.setSource(ioParamBlock);
            procOptions.setScale(maxLength);
            Image image = Imagero.readImage(procOptions);
            closeReader(procOptions.getImageReader());
            return image;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    static Image createThumbnailWithJavaImageIO(File file, int maxLength) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        if (maxLength < 0) {
            throw new IllegalArgumentException("Invalid length: " + maxLength);
        }
        LOGGER.log(Level.INFO, "Creating thumbnail from image file ''{0}'', size {1} Bytes", new Object[]{file, file.length()});
        BufferedImage image = loadImage(file);
        BufferedImage scaledImage = null;
        if (image != null) {
            scaledImage = stepScaleImage(image, maxLength, 0.5);
        }
        return scaledImage;
    }

    /**
     * Diese Methode skaliert ein Bild in mehreren Schritten. Die Idee dahinter: Anstatt einen großen Skalierungsschritt
     * auf die Zielgröße zu machen, werden mehrere kleine unternommen. Der Vorteil dabei ist ein besseres Ergebnis.
     * Beispiel: Ausgangsgröße ist 1172 x 1704 Pixel Zielgröße ist 150 x 112 Pixel maxWidth = 150 qfactor = 0.75
     *
     * Durchläufe: In jedem Durchgang wird das Bild auf 75% seine vorherigen Größe skaliert. Der letzte Schritt Pass 10
     * in diesem Fall skaliert es auf die Zielgröße.
     *
     * Scaling image F:\Digicam\2008\05 - Mai\BMW 320i Coupe\IMG_1386.JPG Pass 1: Scaling 2272 x 1704 - > 1704 x 1278
     * Pass 2: Scaling 1704 x 1278 - > 1278 x 958 Pass 3: Scaling 1278 x 958 - > 958 x 718 Pass 4: Scaling 958 x 718 - >
     * 718 x 538 Pass 5: Scaling 718 x 538 - > 538 x 403 Pass 6: Scaling 538 x 403 - > 403 x 302 Pass 7: Scaling 403 x
     * 302 - > 302 x 226 Pass 8: Scaling 302 x 226 - > 226 x 169 Pass 9: Scaling 226 x 169 - > 169 x 126 Pass 10:
     * Scaling 169 x 126 - > 150 x 112
     *
     * @param image Das Bild, welches skaliert werden soll.
     * @param minWidth Die Breite des skalierten Bildes.
     * @param qfactor Ein Wert zwichen 0 und 1. Je kleiner die Zahl, desto mehr Duchgänge wird der Skalierungsprozess
     * machen. Empfohlener Wert ist 0.5.
     * @return Das skalierte Bild.
     */
    private static BufferedImage stepScaleImage(BufferedImage image, int minWidth, double qfactor) {

        // Damit Assertions ausgewertet werden, muss die VM mit dem Argument -ea gestartet werden.
        assert qfactor < 1.0 : "qfactor must be < 1.0";    // wir wollen nur verkleinern! :-)

        BufferedImage scaledImage = null;

        try {
            int origHeight = image.getHeight();    // Orignalhöhe
            int origWidth = image.getWidth();    // Originalbreite
            double factor = getScaleFactor(origWidth, origHeight,
                    minWidth);    // Skalierungsfaktor von Originalgröße auf Zielgröße
            int scaledWidth = (int) (origWidth / factor);    // Zielbreite
            int scaledHeight = (int) (origHeight / factor);    // Zielhöhe
            int pass = 1;    // Zähler für die Durchläufe - nur für Debugging

            // Je nach qfactor läuft diese Schleife unterschiedlich oft durch. Sie prüft vor jedem Schleifendurchlauf,
            // ob die Zielgröße im folgenden Schritt unterschritten werden würde.. Wenn nein, wird ein neuer Duchlauf
            // gestartet und wieder ein wenig skaliert.
            // In jedem Schleifendurchlauf werden origHeight und origWidth auf die aktuelle Größe gesetzt.
            BufferedImage img = image;

            while (((origWidth * qfactor) > scaledWidth) || ((origHeight * qfactor) > scaledHeight)) {
                int width = (int) (origWidth * qfactor);    // Die Breite in diesesm Skalierungsschritt
                int height = (int) (origHeight * qfactor);    // Die Höhe in diesem Skalierungsschritt

                // Skalierungsschritt
                img = scaleImage(width, height, img);
                origWidth = img.getWidth();    // Die neue Ausgangsbreite füre denm nächsten Skalierungsschritt
                origHeight = img.getHeight();    // Die neue Ausgangshöhe für den nächsten Skalierungsschritt
                pass++;
            }

            // Letzter Skalierungsschritt auf Zielgröße
            scaledImage = scaleImage(scaledWidth, scaledHeight, img);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return scaledImage;
    }

    private static double getScaleFactor(int width, int height, int maxWidth) {
        double longer = (width > height)
                ? width
                : height;
        return longer / (double) maxWidth;
    }

    /**
     * Skaliert ein Image auf eine definierte Zielgröße.
     *
     * @param scaledWidth Breite des skalierten Images.
     * @param scaledHeight Höhe des skalierten Images.
     * @param image Das zu skalierende Image.
     * @return Das skalierte Image.
     */
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
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return image;
    }

    private static void closeReader(ImageReader reader) {
        if (reader != null) {
            reader.close();
        }
    }

    private ThumbnailUtil() {
    }
}
