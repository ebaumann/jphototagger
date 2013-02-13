package org.jphototagger.developersupport;

import java.io.File;
import java.io.PrintWriter;

/**
 * Testing which library (Imagero, metadata-extractor or in future ohter) can extract which metadata in which time of a
 * specific file (format).
 *
 * @author Elmar Baumann
 */
public class LibraryTest {

    public static void main(String[] args) {
        PrintWriter printWriter = new PrintWriter(System.out);
        ensureArgs(args, printWriter);
        File file = new File(args[0]);
        boolean printValues = true;
        ImageroExif imageroExif = new ImageroExif(printWriter, printValues);
        imageroExif.test(file);
        printSeparator(printWriter);
        MetaDataExtractorExif mdeExif = new MetaDataExtractorExif(printWriter, printValues);
        mdeExif.test(file);
        printWriter.flush();
    }

    private static void printSeparator(PrintWriter printWriter) {
        int length = 120;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append('-');
        }
        printWriter.println(sb.toString());
    }

    private static void ensureArgs(String[] args, PrintWriter printWriter) {
        if (args.length != 1) {
            printUsage(args, printWriter);
            System.exit(1);
        }
        if (!(new File(args[0]).isFile())) {
            printUsage(args, printWriter);
            System.exit(2);
        }
    }

    private static void printUsage(String[] args, PrintWriter printWriter) {
        printWriter.println("Tests metadata extracting libraries. The parameter has to be an existing image file! Got"
                + (args.length == 0
                ? " no parameter"
                : args.length == 1
                ? args[0]
                : args.length + " parameters: " + getParameters(args)));
    }

    private static String getParameters(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(" \"").append(arg).append("\"");
        }
        return sb.toString();
    }
}
