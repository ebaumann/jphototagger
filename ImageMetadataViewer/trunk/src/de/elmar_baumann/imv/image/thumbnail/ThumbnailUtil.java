package de.elmar_baumann.imv.image.thumbnail;

import com.imagero.reader.IOParameterBlock;
import com.imagero.reader.ImageProcOptions;
import com.imagero.reader.ImageReader;
import com.imagero.reader.Imagero;
import com.imagero.reader.ReaderFactory;
import com.imagero.reader.jpeg.JpegReader;
import com.imagero.reader.tiff.TiffReader;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.metadata.exif.ExifThumbnailUtil;
import de.elmar_baumann.imv.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.imv.io.IoUtil;
import de.elmar_baumann.imv.types.FileType;
import de.elmar_baumann.lib.image.util.ImageTransform;
import de.elmar_baumann.lib.image.util.ImageUtil;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.runtime.External;
import de.elmar_baumann.lib.generics.Pair;
import de.elmar_baumann.lib.io.FileLock;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Hilfsklasse für Thumbnails.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-07
 */
public final class ThumbnailUtil {

    /**
     * Returns a thumbnail of an image file. If the preferred method fails -
     * ebeddded or scaled - the other method will be used.
     * 
     * @param  file       file
     * @param  maxLength  maximum length of the image
     * @param  embedded   true if get embedded thumbnail instead of a scaled image
     * @return thumbnail or null if errors occured
     */
    public static Image getThumbnail(File file, int maxLength, boolean embedded) {
        if (!file.exists()) return null;
        Image thumbnail =
                (embedded || FileType.isRawFile(file.getName())
                 ? getRotatedThumbnail(file)
                 : getScaledImageImagero(file, maxLength));
        if (thumbnail == null) {
            thumbnail =
                    (embedded
                     ? getScaledImageImagero(file, maxLength)
                     : getRotatedThumbnail(file));
        }
        return thumbnail;
    }

    private static Pair<Image, ImageReader> getFileEmbeddedThumbnail(File file) {
        Image thumbnail = null;
        ImageReader reader = null;
        try {
            AppLog.logInfo(ThumbnailUtil.class,
                    "ThumbnailUtil.GetFileEmbeddedThumbnail.Info", file); // NOI18N
            reader = ReaderFactory.createReader(file);
            if (reader instanceof JpegReader) {
                IOParameterBlock ioParamBlock = new IOParameterBlock();
                ioParamBlock.setSource(file);
                thumbnail = Imagero.getThumbnail(ioParamBlock, 0);
            } else if (reader instanceof TiffReader) {
                TiffReader tiffReader = (TiffReader) reader;
                if (tiffReader.getThumbnailCount() > 0) {
                    thumbnail = Toolkit.getDefaultToolkit().createImage(
                            tiffReader.getThumbnail(0));
                }
            }
        } catch (Exception ex) {
            AppLog.logSevere(ThumbnailUtil.class, ex);
            return new Pair<Image, ImageReader>(null, null);
        }
        return new Pair<Image, ImageReader>(thumbnail, reader);
    }

    private static Image getScaledImageImagero(File file, int maxLength) {
        try {
            AppLog.logInfo(ThumbnailUtil.class,
                    "ThumbnailUtil.GetScaledImageImagero.Info", file, maxLength);// NOI18N
            IOParameterBlock ioParamBlock = new IOParameterBlock();
            ImageProcOptions procOptions = new ImageProcOptions();

            ioParamBlock.setSource(file);
            procOptions.setSource(ioParamBlock);
            procOptions.setScale(maxLength);

            Image image = Imagero.readImage(procOptions);
            closeReader(procOptions.getImageReader());
            return image;
        } catch (Exception ex) {
            AppLog.logSevere(ThumbnailUtil.class, ex);
        }
        return null;
    }

    private static void logExternalAppCommand(String cmd) {
        AppLog.logFinest(ThumbnailUtil.class,
                "ThumbnailUtil.Info.ExternalAppCreationCommand", cmd); // NOI18N
    }

    private static Image getRotatedThumbnail(File file) {
        Pair<Image, ImageReader> pair = getFileEmbeddedThumbnail(file);
        Image thumbnail = pair.getFirst();
        Image rotatedThumbnail = thumbnail;
        if (thumbnail != null) {
            double rotateAngle =
                    ExifThumbnailUtil.getThumbnailRotationAngle(
                    ExifMetadata.getExifEntries(file));
            if (rotateAngle != 0) {
                AppLog.logInfo(ThumbnailUtil.class,
                        "ThumbnailUtil.GetRotatedThumbnail.Information", file); // NOI18N
                rotatedThumbnail = ImageTransform.rotate(thumbnail, rotateAngle);
            }
        }
        closeReader(pair.getSecond());
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
    public static Image getThumbnailFromExternalApplication(
            File file, String command, int maxLength) {

        if (!file.exists()) {
            return null;
        }
        Image image = null;

        AppLog.logInfo(ThumbnailUtil.class,
                "ThumbnailUtil.GetThumbnailFromExternalApplication.Information", // NOI18N
                file, maxLength);
        String cmd = command.replace("%s", IoUtil.getQuotedForCommandline( // NOI18N
                Collections.singletonList(file), "")). // NOI18N
                replace("%i", new Integer(maxLength).toString()); // NOI18N
        logExternalAppCommand(cmd);
        Pair<byte[], byte[]> output =
                External.executeGetOutput(cmd,
                UserSettings.INSTANCE.getMaxSecondsToTerminateExternalPrograms() *
                1000);

        if (output == null) {
            return null;
        }

        byte[] stdout = output.getFirst();
        if (stdout != null) {
            try {
                image = javax.imageio.ImageIO.read(new ByteArrayInputStream(
                        stdout));
            } catch (Exception ex) {
                Logger.getLogger(ThumbnailUtil.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
        if (output.getSecond() != null) {
            logStderr(file, output);
        }
        return image;
    }

    private static void logStderr(File imageFile, Pair<byte[], byte[]> output) {
        byte[] stderr = output.getSecond();
        String errorMsg = (stderr == null
                           ? "" // NOI18N
                           : new String(stderr).trim());
        if (!errorMsg.isEmpty()) {
            AppLog.logWarning(ThumbnailUtil.class,
                    "ThumbnailUtil.Error.ExternalProgram", imageFile, errorMsg); // NOI18N
        }
    }

    public static Image getScaledImage(File file, int maxLength) {
        AppLog.logInfo(ThumbnailUtil.class,
                "ThumbnailUtil.GetScaledImage.Information", file, maxLength); // NOI18N
        BufferedImage image = loadImage(file);
        BufferedImage scaledImage = null;
        if (image != null) {
            scaledImage = stepScaleImage(image, maxLength, 0.5);
        }
        return scaledImage;
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
    private static BufferedImage stepScaleImage(BufferedImage image,
            int minWidth, double qfactor) {
        // Damit Assertions ausgewertet werden, muss die VM mit dem Argument -ea gestartet werden.
        assert qfactor < 1.0 : "qfactor must be < 1.0"; // NOI18N wir wollen nur verkleinern! :-)
        BufferedImage scaledImage = null;
        try {
            int origHeight = image.getHeight(); // Orignalhöhe
            int origWidth = image.getWidth(); // Originalbreite
            double factor = getScaleFactor(origWidth, origHeight, minWidth); // Skalierungsfaktor von Originalgröße auf Zielgröße
            int scaledWidth = (int) (origWidth / factor); // Zielbreite
            int scaledHeight = (int) (origHeight / factor); // Zielhöhe
            int pass = 1; // Zähler für die Durchläufe - nur für Debugging

            // Je nach qfactor läuft diese Schleife unterschiedlich oft durch. Sie prüft vor jedem Schleifendurchlauf,
            // ob die Zielgröße im folgenden Schritt unterschritten werden würde.. Wenn nein, wird ein neuer Duchlauf
            // gestartet und wieder ein wenig skaliert.
            // In jedem Schleifendurchlauf werden origHeight und origWidth auf die aktuelle Größe gesetzt.
            while (((origWidth * qfactor) > scaledWidth) || ((origHeight *
                    qfactor) > scaledHeight)) {
                int width = (int) (origWidth * qfactor); // Die Breite in diesesm Skalierungsschritt
                int height = (int) (origHeight * qfactor); // Die Höhe in diesem Skalierungsschritt

                // Skalierungsschritt
                image = scaleImage(width, height, image);

                origWidth = image.getWidth(); // Die neue Ausgangsbreite füre denm nächsten Skalierungsschritt
                origHeight = image.getHeight(); // Die neue Ausgangshöhe für den nächsten Skalierungsschritt
                pass++;
            }

            // Letzter Skalierungsschritt auf Zielgröße
            scaledImage = scaleImage(scaledWidth, scaledHeight, image);

        } catch (Exception ex) {
            AppLog.logSevere(ThumbnailUtil.class, ex);
        }
        return scaledImage;
    }

    private static double getScaleFactor(int width, int height, int maxWidth) {
        double longer = width > height
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
    private static BufferedImage scaleImage(int scaledWidth, int scaledHeight,
            BufferedImage image) {
        BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
        return scaledImage;
    }

    /**
     * Diese Methode lädt ein Bild mit Hilfe des MediaTrackers.
     * 
     * @param file Das zu ladende Bild.
     * @return Ein BufferedImage als Ergebnis.
     */
    public static BufferedImage loadImage(File file) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
            MediaTracker mediaTracker = new MediaTracker(new Container());
            mediaTracker.addImage(image, 0);
            try {
                mediaTracker.waitForID(0);
            } catch (InterruptedException ex) {
                AppLog.logSevere(ThumbnailUtil.class, ex);
            }
        } catch (IOException ex) {
            AppLog.logSevere(ThumbnailUtil.class, ex);
        }
        return image;
    }

    public static void writeThumbnail(Image thumbnail, String hash) {
        FileOutputStream fos = null;
        File tnFile = getThumbnailfile(hash);
        try {
            if (!IoUtil.lockLogWarning(tnFile, ThumbnailUtil.class)) return;
            logWriteThumbnail(tnFile);
            fos = new FileOutputStream(tnFile);
            fos.getChannel().lock();
            ByteArrayInputStream is =
                    ImageUtil.getByteArrayInputStream(thumbnail, "jpeg");
            if (is != null) {
                int nextByte;
                while ((nextByte = is.read()) != -1) {
                    fos.write(nextByte);
                }
            }
        } catch (Exception ex) {
            AppLog.logSevere(ThumbnailUtil.class, ex);
        } finally {
            FileLock.INSTANCE.unlock(tnFile, ThumbnailUtil.class);
            closeStream(fos);
        }
    }

    private static void logWriteThumbnail(File tnFile) {
        AppLog.logInfo(ThumbnailUtil.class, "ThumbnailUtil.Info.WriteThumbnail",
                tnFile); // NOI18N
    }

    public static boolean deleteThumbnail(String hash) {
        File thumbnailFile = getThumbnailfile(hash);
        if (thumbnailFile.exists()) {
            return thumbnailFile.delete();
        }
        return false;
    }

    public static Image getThumbnail(String hash) {
        Image thumbnail = null;
        FileInputStream fis = null;
        try {
            File tnFile = getThumbnailfile(hash);
            if (tnFile.exists()) {
                fis = new FileInputStream(tnFile);
                int bytecount = fis.available();
                byte[] bytes = new byte[bytecount];
                fis.read(bytes, 0, bytecount);
                ImageIcon icon = new ImageIcon(bytes);
                thumbnail = icon.getImage();
            }
        } catch (Exception ex) {
            AppLog.logSevere(ThumbnailUtil.class, ex);
        } finally {
            closeStream(fis);
        }
        return thumbnail;
    }

    private static void closeReader(ImageReader reader) {
        if (reader != null) {
            reader.close();
        }
    }

    private static void closeStream(FileInputStream fis) {
        if (fis != null) {
            try {
                fis.close();
            } catch (IOException ex) {
                AppLog.logSevere(ThumbnailUtil.class, ex);
            }
        }
    }

    private static void closeStream(FileOutputStream fos) {
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException ex) {
                AppLog.logSevere(ThumbnailUtil.class, ex);
            }
        }
    }

    /**
     * Returns the thumbnail file for an image file.
     *
     * Shortcut for: {@code getThumbnailfile(getMd5File(imageFilename))}.
     *
     * @param  imageFilename filename of the image file (full path)
     * @return               thumbnail file (may not exists, check with
     *                       {@link File#exists()})
     */
    public static File getThumbnailFileOfImageFile(String imageFilename) {
        return getThumbnailfile(getMd5File(imageFilename));
    }

    /**
     * Compute final name of thumbnail on disk.
     *
     * @param  hash hash
     * @return      file
     */
    public static File getThumbnailfile(String hash) {
        String dir = UserSettings.INSTANCE.getThumbnailsDirectoryName();
        FileUtil.ensureDirectoryExists(new File(dir));
        return new File(dir + File.separator + hash + ".jpeg");
    }

    /* Adapts the thumbnail's name to changes in the name of the original file
     */
    public static void updateThumbnailName(String oldName, String newName) {
        getThumbnailfile(getMd5File(oldName)).renameTo(
                getThumbnailfile(getMd5File(newName)));
    }


    /* Compute an MD5 hash from a file.
     */
    public static String getMd5File(File file) {
        try {
            return getMd5File("file://" + file.getCanonicalPath());
        } catch (IOException ex) {
            Logger.getLogger(ThumbnailUtil.class.getName()).log(Level.SEVERE,
                    null, ex);
            return null;
        }
    }

    /* Compute an MD5 hash from a fully canonicalized filename.
     */
    public static String getMd5File(String cFilename) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ThumbnailUtil.class.getName()).log(Level.SEVERE,
                    null, ex);
            return null;
        }
        md5.reset();
        md5.update(("file://" + cFilename).getBytes());
        byte[] result = md5.digest();

        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            if ((result[i] & 0xff) == 0) {
                hex.append("00");
            } else if ((result[i] & 0xff) < 0x10) {
                hex.append("0" + Integer.toHexString(0xFF & result[i]));
            } else {
                hex.append(Integer.toHexString(0xFF & result[i]));
            }
        }
        return hex.toString();
    }

    private ThumbnailUtil() {
    }
}
