package org.jphototagger.developersupport;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import java.io.File;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Elmar Baumann
 */
final class MetaDataExtractorExif {

    private final PrintWriter printWriter;
    private final boolean printValues;

    MetaDataExtractorExif(PrintWriter printWriter, boolean printValues) {
        this.printWriter = printWriter;
        this.printValues = printValues;
    }

    void test(File file) {
        printWriter.println("Testing extracting EXIF with metadata-extractor for file " + file);
        long startTime = System.currentTimeMillis();
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    if (printValues) {
                        printWriter.println(tag);
                    }
                }
            }
            long finishedTime = System.currentTimeMillis();
            printWriter.println("Time elapsed [milliseconds]: " + (finishedTime - startTime));
        } catch (Throwable t) {
            Logger.getLogger(LibraryTest.class.getName()).log(Level.SEVERE, null, t);
        }
    }
}
