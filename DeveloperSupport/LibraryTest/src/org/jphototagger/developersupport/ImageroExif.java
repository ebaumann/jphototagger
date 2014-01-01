package org.jphototagger.developersupport;

import com.imagero.reader.ImageReader;
import com.imagero.reader.MetadataUtils;
import com.imagero.reader.jpeg.JpegReader;
import com.imagero.reader.tiff.IFDEntry;
import com.imagero.reader.tiff.ImageFileDirectory;
import com.imagero.reader.tiff.TiffReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Elmar Baumann
 */
public final class ImageroExif {

    private final PrintWriter printWriter;
    private final boolean printValues;

    ImageroExif(PrintWriter printWriter, boolean printValues) {
        this.printWriter = printWriter;
        this.printValues = printValues;
    }

    void test(File file) {
        printWriter.println("Testing extracting EXIF with imagero for file " + file);
        long startTime = System.currentTimeMillis();
        String filenameLowerCase = file.getName().toLowerCase();
        ImageReader imageReader = null;
        try {
            if (filenameLowerCase.endsWith(".jpg")) {
                imageReader = new JpegReader(file);
                printExifTags((JpegReader) imageReader);
            } else {
                imageReader = new TiffReader(file);
                TiffReader tiffReader = (TiffReader) imageReader;
                int count = tiffReader.getIFDCount();
                for (int i = 0; i < count; i++) {
                    ImageFileDirectory iFD = tiffReader.getIFD(i);
                    printExifTagsRecursive(iFD);
                }
            }
            long finishedTime = System.currentTimeMillis();
            printWriter.println("Time elapsed [milliseconds]: " + (finishedTime - startTime));
        } catch (Throwable t) {
            Logger.getLogger(LibraryTest.class.getName()).log(Level.SEVERE, null, t);
        } finally {
            if (imageReader != null) {
                imageReader.close();
            }
        }
    }

    private void printExifTags(JpegReader jpegReader) {
        IFDEntry[][] allIfdEntries = MetadataUtils.getExif(jpegReader);
        if (allIfdEntries != null) {
            for (int i = 0; i < allIfdEntries.length; i++) {
                IFDEntry[] currentIfdEntry = allIfdEntries[i];
                for (int j = 0; j < currentIfdEntry.length; j++) {
                    IFDEntry entry = currentIfdEntry[j];
                    if (printValues) {
                        printWriter.println(entry);
                    }
                }
            }
        }
    }

    private void printExifTagsRecursive(ImageFileDirectory ifd) {
        printExifTags(ifd);
        for (int i = 0; i < ifd.getIFDCount(); i++) {
            ImageFileDirectory subIfd = ifd.getIFDAt(i);
            printExifTagsRecursive(subIfd);
        }
        ImageFileDirectory exifIFD = ifd.getExifIFD();
        if (exifIFD != null) {
            printExifTags(exifIFD);
        }
        ImageFileDirectory gpsIFD = ifd.getGpsIFD();
        if (gpsIFD != null) {
            printExifTags(gpsIFD);
        }
        ImageFileDirectory interoperabilityIFD = ifd.getInteroperabilityIFD();
        if (interoperabilityIFD != null) {
            printExifTags(interoperabilityIFD);
        }
    }

    private void printExifTags(ImageFileDirectory ifd) {
        int entryCount = ifd.getEntryCount();
        for (int i = 0; i < entryCount; i++) {
            IFDEntry ifdEntry = ifd.getEntryAt(i);
            if (printValues && ifdEntry != null) {
                printWriter.println(ifdEntry);
            }
        }
    }
}
