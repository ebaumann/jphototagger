package de.elmar_baumann.imagemetadataviewer.image.thumbnail;

import com.imagero.reader.IOParameterBlock;
import com.imagero.reader.ImageProcOptions;
import com.imagero.reader.ImageReader;
import com.imagero.reader.Imagero;
import com.imagero.reader.ReaderFactory;
import com.imagero.reader.jpeg.JpegReader;
import com.imagero.reader.tiff.TiffReader;
import de.elmar_baumann.imagemetadataviewer.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.imagemetadataviewer.io.FileType;
import de.elmar_baumann.imagemetadataviewer.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.image.ImageTransform;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.runtime.External;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 * Hilfsklasse für Thumbnails.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/25
 */
public class ThumbnailUtil {

    /**
     * Liefert ein Thumbnail von einer Bilddatei. Funktioniert die favorisierte
     * Erzeugung - eingebettet oder skaliert - nicht, wird die jeweils andere
     * Erzeugungsvariante ausprobiert.
     * 
     * @param filename Dateiname
     * @param maxWidth Maximale Länge der längeren Thumbnailseite in Pixel;
     *                 wird (nur) beim Skalieren benutzt
     * @param embedded true, wenn eingebettetes Thumbnail benutzt werden soll,
     *  f              alse, wenn ein skaliertes Thumbnail berechnet werden soll
     * @return         Thumbnail oder null, falls keines erzeugt werden konnte
     */
    public static Image getThumbnail(String filename, int maxWidth,
        boolean embedded) {
        if (!FileUtil.existsFile(filename)) {
            return null;
        }
        Image thumbnail =
            (embedded || FileType.isRawFile(filename)
            ? rotateThumbnail(filename, getFileEmbeddedThumbnail(filename))
            : getScaledImage(filename, maxWidth));
        if (thumbnail == null) {
            thumbnail =
                (embedded
                ? getScaledImage(filename, maxWidth)
                : rotateThumbnail(filename, getFileEmbeddedThumbnail(filename)));
        }
        return thumbnail;
    }

    private static Image getFileEmbeddedThumbnail(String filename) {
        Image thumbnail = null;
        try {
            ImageReader reader = ReaderFactory.createReader(filename);
            if (reader instanceof JpegReader) {
                IOParameterBlock ioParamBlock = new IOParameterBlock();
                ioParamBlock.setSource(new File(filename));
                thumbnail = Imagero.getThumbnail(ioParamBlock, 0);
            } else if (reader instanceof TiffReader) {
                TiffReader tiffReader = (TiffReader) reader;
                if (tiffReader.getThumbnailCount() > 0) {
                    thumbnail = Toolkit.getDefaultToolkit().createImage(
                        tiffReader.getThumbnail(0));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ImageFileThumbnailsPanel.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (Exception ex) {
            Logger.getLogger(ImageFileThumbnailsPanel.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return thumbnail;
    }

    private static Image getScaledImage(String filename, int maxLength) {
        try {
            IOParameterBlock ioParamBlock = new IOParameterBlock();
            ImageProcOptions procOptions = new ImageProcOptions();

            ioParamBlock.setSource(new File(filename));
            procOptions.setSource(ioParamBlock);
            procOptions.setScale(maxLength);

            return Imagero.readImage(procOptions);
        } catch (IOException ex) {
            Logger.getLogger(ThumbnailUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ImageFileThumbnailsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static Image rotateThumbnail(String filename, Image thumbnail) {
        if (thumbnail != null) {
            ExifMetadata exifMetadata = new ExifMetadata();
            double rotateAngle =
                exifMetadata.getThumbnailRotationAngle(
                exifMetadata.getMetadata(filename));
            if (rotateAngle != 0) {
                return ImageTransform.rotate(thumbnail, rotateAngle);
            }
        }
        return thumbnail;
    }

    /**
     * Liefert ein ThumbnailUtil von einer externen Anwendung.
     * 
     * @param filename  Dateiname
     * @param command   Kommando zum Erzeugen des Bilds
     * @param maxLength Maximale Länge der längeren Thumbnailseite in Pixel 
     * @return          Thumbnail, null bei Fehlern
     */
    public static Image getThumbnailFromExternalApplication(String filename,
        String command, int maxLength) {
        if (!FileUtil.existsFile(filename)) {
            return null;
        }
        Image image = null;

        String cmd = command.replace("%s", filename).replace("%i", // NOI18N
            new Integer(maxLength).toString());
        byte[] output = External.executeGetOutput(cmd);
        if (output != null) {
            MediaTracker tracker = new MediaTracker(new JPanel());
            image = Toolkit.getDefaultToolkit().createImage(output);
            tracker.addImage(image, 0);
            try {
                tracker.waitForID(0);
            } catch (InterruptedException ex) {
                Logger.getLogger(ThumbnailUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return image;
    }
}
