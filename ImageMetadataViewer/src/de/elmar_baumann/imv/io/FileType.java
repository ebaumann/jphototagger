package de.elmar_baumann.imv.io;

/**
 * Dateitypen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/31
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
        String filenameLowercase = filename.toLowerCase();
        return filenameLowercase.endsWith(".jpg") || // NOI18N
            filenameLowercase.endsWith(".jpeg"); // NOI18N
    }

    /**
     * Liefert, ob eine Datei eine RAW-Datei ist. Es wird lediglich der Name
     * herangezogen (Endungen), nicht der interne Aufbau.
     * 
     * @param filename Dateiname
     * @return         true, wenn die Datei eine RAW-Datei ist
     */
    public static boolean isRawFile(String filename) {
        String filenameLowerCase = filename.toLowerCase();
        boolean isCommonImageFile =
            filenameLowerCase.endsWith("tif") || // NOI18N
            filenameLowerCase.endsWith("tiff") || // NOI18N
            filenameLowerCase.endsWith("jpg") || // NOI18N
            filenameLowerCase.endsWith("jpeg") || // NOI18N
            filenameLowerCase.endsWith("gif") || // NOI18N
            filenameLowerCase.endsWith("png");    // NOI18N
        return !isCommonImageFile;
    }

    private FileType() {}
}
