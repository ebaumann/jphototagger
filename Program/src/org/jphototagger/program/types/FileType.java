package org.jphototagger.program.types;

/**
 * Dateitypen.
 *
 * @author Elmar Baumann
 */
public final class FileType {

    /**
     * Liefert, ob eine Datei eine JPEG-Datei ist. Es wird lediglich der Name
     * herangezogen (Endungen .jpg oder .jpeg), nicht der interne Aufbau.
     *
     * @param filename Dateiname
     * @return         true, wenn die Datei eine JPEG-Datei ist
     */
    public static boolean isJpegFile(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");
        }

        String filenameLowercase = filename.toLowerCase();

        return filenameLowercase.endsWith(".jpg")
               || filenameLowercase.endsWith(".jpeg");
    }

    /**
     * Liefert, ob eine Datei eine RAW-Datei ist. Es wird lediglich der Name
     * herangezogen (Endungen), nicht der interne Aufbau.
     *
     * @param filename Dateiname
     * @return         true, wenn die Datei eine RAW-Datei ist
     */
    public static boolean isRawFile(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");
        }

        String  filenameLowerCase = filename.toLowerCase();
        boolean isCommonImageFile = filenameLowerCase.endsWith("tif")
                                    || filenameLowerCase.endsWith("tiff")
                                    || filenameLowerCase.endsWith("jpg")
                                    || filenameLowerCase.endsWith("jpeg")
                                    || filenameLowerCase.endsWith("gif")
                                    || filenameLowerCase.endsWith("png");

        return !isCommonImageFile;
    }

    private FileType() {}
}
