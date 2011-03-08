package org.jphototagger.program.image.thumbnail;

import com.imagero.reader.ImageProcOptions;
import com.imagero.reader.ImageReader;
import com.imagero.reader.Imagero;
import com.imagero.reader.IOParameterBlock;
import com.imagero.reader.jpeg.JpegReader;
import com.imagero.reader.ReaderFactory;
import com.imagero.reader.tiff.TiffReader;

import org.jphototagger.lib.generics.Pair;
import org.jphototagger.lib.image.util.ImageTransform;
import org.jphototagger.lib.runtime.External;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.database.metadata.exif.ExifThumbnailUtil;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import org.jphototagger.program.image.metadata.exif.ExifTags;
import org.jphototagger.program.types.FileType;
import org.jphototagger.program.UserSettings;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import java.io.ByteArrayInputStream;
import java.io.File;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import org.jphototagger.program.cache.ExifCache;

/**
 * Hilfsklasse für Thumbnails.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class ThumbnailUtil {

    private static final Logger LOGGER = Logger.getLogger(ThumbnailUtil.class.getName());

    /**
     * Returns a thumbnail created with
     * {@link UserSettings#setThumbnailCreator(org.jphototagger.program.image.thumbnail.ThumbnailCreator)}.
     * <p>
     * If the creator did not create a thumbnail, this method tries to get an
     * embedded thumbnail.
     *
     * @param  file file
     * @return      thumbnail or null on errors
     */
    public static Image getThumbnail(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (!file.exists()) {
            return null;
        }

        ThumbnailCreator creator = UserSettings.INSTANCE.getThumbnailCreator();
        int maxLength = UserSettings.INSTANCE.getMaxThumbnailWidth();
        boolean isRawImage = FileType.isRawFile(file.getName());
        boolean canCreateImage = !isRawImage || (isRawImage && creator.equals(ThumbnailCreator.EXTERNAL_APP));
        Image thumbnail = null;

        if (creator.equals(ThumbnailCreator.EXTERNAL_APP)) {    // has to be 1st.
            thumbnail = getThumbnailFromExternalApplication(file,
                    UserSettings.INSTANCE.getExternalThumbnailCreationCommand(), maxLength);
        } else if (!canCreateImage || creator.equals(ThumbnailCreator.EMBEDDED)) {
            thumbnail = ThumbnailUtil.getEmbeddedThumbnailRotated(file);
        } else if (creator.equals(ThumbnailCreator.IMAGERO)) {
            thumbnail = getScaledImageImagero(file, maxLength);
        } else if (creator.equals(ThumbnailCreator.JAVA_IMAGE_IO)) {
            thumbnail = getThumbnailFromJavaImageIo(file, maxLength);
        } else {
            assert false : "Not handled enum type (thumbnail create option)";
        }

        if (thumbnail == null) {
            thumbnail = getEmbeddedThumbnailRotated(file);
        }

        return thumbnail;
    }

    /**
     * Returns a thumbnail of an image file. If the preferred method fails -
     * ebeddded or scaled - the other method will be used.
     *
     * @param  file       file
     * @param  maxLength  maximum length of the image
     * @return            thumbnail or null if errors occured
     */
    public static Image getThumbnailFromImagero(File file, int maxLength) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (maxLength < 0) {
            throw new IllegalArgumentException("Invalid length: " + maxLength);
        }

        return getScaledImageImagero(file, maxLength);
    }

    public static Image getThumbnailFromJavaImageIo(File file, int maxLength) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (maxLength < 0) {
            throw new IllegalArgumentException("Invalid length: " + maxLength);
        }

        AppLogger.logInfo(ThumbnailUtil.class, "ThumbnailUtil.CreateImage.Information.JavaIo", file, maxLength);
        LOGGER.log(Level.INFO, "Creating thumbnail from image file ''{0}'', size {1} Bytes", new Object[]{file, file.length()});

        BufferedImage image = loadImage(file);
        BufferedImage scaledImage = null;

        if (image != null) {
            scaledImage = stepScaleImage(image, maxLength, 0.5);
        }

        return scaledImage;
    }

    /**
     * Returns in files embedded thumbnails.
     *
     * @param file file
     * @return     thumbnail or null on errors
     */
    public static Image getEmbeddedThumbnail(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        return getEmbeddedThumbnailRotated(file);
    }

    private static Pair<Image, ImageReader> getEmbeddedThumbnailWithReader(File file) {
        Image thumbnail = null;
        ImageReader reader = null;

        try {
            AppLogger.logInfo(ThumbnailUtil.class, "ThumbnailUtil.GetFileEmbeddedThumbnail.Info", file);
            LOGGER.log(Level.INFO, "Reading embedded thumbnail from image file ''{0}'', size {1} Bytes", new Object[]{file, file.length()});
            reader = ReaderFactory.createReader(file);

            if (reader instanceof JpegReader) {
                IOParameterBlock ioParamBlock = new IOParameterBlock();

                ioParamBlock.setSource(file);
                thumbnail = Imagero.getThumbnail(ioParamBlock, 0);
            } else if (reader instanceof TiffReader) {
                TiffReader tiffReader = (TiffReader) reader;

                if (tiffReader.getThumbnailCount() > 0) {
                    thumbnail = Toolkit.getDefaultToolkit().createImage(tiffReader.getThumbnail(0));
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(ThumbnailUtil.class, ex);

            return new Pair<Image, ImageReader>(null, null);
        }

        return new Pair<Image, ImageReader>(thumbnail, reader);
    }

    private static Image getScaledImageImagero(File file, int maxLength) {
        try {
            AppLogger.logInfo(ThumbnailUtil.class, "ThumbnailUtil.GetScaledImageImagero.Info", file, maxLength);
            LOGGER.log(Level.INFO, "Creating thumbnail from image file ''{0}'', size {1} Bytes", new Object[]{file, file.length()});

            IOParameterBlock ioParamBlock = new IOParameterBlock();
            ImageProcOptions procOptions = new ImageProcOptions();

            ioParamBlock.setSource(file);
            procOptions.setSource(ioParamBlock);
            procOptions.setScale(maxLength);

            Image image = Imagero.readImage(procOptions);

            closeReader(procOptions.getImageReader());

            return image;
        } catch (Exception ex) {
            AppLogger.logSevere(ThumbnailUtil.class, ex);
        }

        return null;
    }

    private static void logExternalAppCommand(String cmd) {
        AppLogger.logFinest(ThumbnailUtil.class, "ThumbnailUtil.Info.ExternalAppCreationCommand", cmd);
    }

    private static Image getEmbeddedThumbnailRotated(File file) {
        Pair<Image, ImageReader> pair = getEmbeddedThumbnailWithReader(file);
        Image thumbnail = pair.getFirst();
        Image rotatedThumbnail = thumbnail;

        if (thumbnail != null) {
            ExifTags exifTags = ExifCache.INSTANCE.getExifTags(file);
            double rotateAngle = 0.0;

            if (exifTags != null) {
                ExifTag exifTag = exifTags.exifTagById(274);

                if (exifTag != null) {
                    rotateAngle = ExifThumbnailUtil.getThumbnailRotationAngle(exifTag);
                }
            }

            AppLogger.logInfo(ThumbnailUtil.class, "ThumbnailUtil.GetRotatedThumbnail.Information", file);
            rotatedThumbnail = ImageTransform.rotate(thumbnail, rotateAngle);
        }

        closeReader(pair.getSecond());    // Needs to be open for calling ImageTransform.rotate()

        return rotatedThumbnail;
    }

    /**
     * Returns a thumbnail created by an external application to stdout.
     *
     * @param file      file
     * @param command   command to create the thumbnail
     * @param maxLength maximum length of the image in pixel
     * @return          thumbnail or null if errors occured
     */
    public static Image getThumbnailFromExternalApplication(File file, String command, int maxLength) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (command == null) {
            throw new NullPointerException("command == null");
        }

        if (maxLength < 0) {
            throw new IllegalArgumentException("Invalid length: " + maxLength);
        }

        if (!file.exists()) {
            return null;
        }

        AppLogger.logInfo(ThumbnailUtil.class, "ThumbnailUtil.GetThumbnailFromExternalApplication.Information", file,
                          maxLength);

        String cmd = command.replace("%s", file.getAbsolutePath()).replace("%i", Integer.toString(maxLength));
        Image image = null;

        logExternalAppCommand(cmd);

        Pair<byte[], byte[]> output = External.executeGetOutput(cmd,
                                          UserSettings.INSTANCE.getMaxSecondsToTerminateExternalPrograms() * 1000);

        if (output == null) {
            return null;
        }

        byte[] stdout = output.getFirst();

        if (stdout != null) {
            try {
                image = javax.imageio.ImageIO.read(new ByteArrayInputStream(stdout));
            } catch (Exception ex) {
                Logger.getLogger(ThumbnailUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (output.getSecond() != null) {
            logStderr(file, output);
        }

        return image;
    }

    /**
     * Diese Methode skaliert ein Bild in mehreren Schritten. Die Idee dahinter:
     * Anstatt einen großen Skalierungsschritt auf die Zielgröße zu machen,
     * werden mehrere kleine unternommen. Der Vorteil dabei ist ein besseres Ergebnis.
     * Beispiel:
     * Ausgangsgröße ist 1172 x 1704 Pixel
     * Zielgröße ist 150 x 112 Pixel
     * maxWidth = 150
     * qfactor = 0.75
     *
     * Durchläufe:
     * In jedem Durchgang wird das Bild auf 75% seine vorherigen Größe skaliert.
     * Der letzte Schritt Pass 10 in diesem Fall skaliert es auf die Zielgröße.
     *
     * Scaling image F:\Digicam\2008\05 - Mai\BMW 320i Coupe\IMG_1386.JPG
     * Pass 1: Scaling 2272 x 1704 - > 1704 x 1278
     * Pass 2: Scaling 1704 x 1278 - > 1278 x 958
     * Pass 3: Scaling 1278 x 958 - > 958 x 718
     * Pass 4: Scaling 958 x 718 - > 718 x 538
     * Pass 5: Scaling 718 x 538 - > 538 x 403
     * Pass 6: Scaling 538 x 403 - > 403 x 302
     * Pass 7: Scaling 403 x 302 - > 302 x 226
     * Pass 8: Scaling 302 x 226 - > 226 x 169
     * Pass 9: Scaling 226 x 169 - > 169 x 126
     * Pass 10: Scaling 169 x 126 - > 150 x 112
     *
     * @param image Das Bild, welches skaliert werden soll.
     * @param minWidth Die Breite des skalierten Bildes.
     * @param qfactor Ein Wert zwichen 0 und 1. Je kleiner die Zahl, desto mehr Duchgänge wird der Skalierungsprozess machen. Empfohlener Wert ist 0.5.
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
            AppLogger.logSevere(ThumbnailUtil.class, ex);
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

    /**
     * Diese Methode lädt ein Bild mit Hilfe des MediaTrackers.
     *
     * @param file Das zu ladende Bild.
     * @return Ein BufferedImage als Ergebnis.
     */
    private static BufferedImage loadImage(File file) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(file);

            MediaTracker mediaTracker = new MediaTracker(new Container());

            mediaTracker.addImage(image, 0);
            mediaTracker.waitForID(0);
        } catch (Exception ex) {
            AppLogger.logSevere(ThumbnailUtil.class, ex);
        }

        return image;
    }

    private static void closeReader(ImageReader reader) {
        if (reader != null) {
            reader.close();
        }
    }

    private static void logStderr(File imageFile, Pair<byte[], byte[]> output) {
        byte[] stderr = output.getSecond();
        String errorMsg = ((stderr == null)
                           ? ""
                           : new String(stderr).trim());

        if (!errorMsg.isEmpty()) {
            AppLogger.logWarning(ThumbnailUtil.class, "ThumbnailUtil.Error.ExternalProgram", imageFile, errorMsg);
        }
    }

    private ThumbnailUtil() {}
}
