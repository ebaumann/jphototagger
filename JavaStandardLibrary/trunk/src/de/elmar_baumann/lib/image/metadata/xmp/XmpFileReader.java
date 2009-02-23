package de.elmar_baumann.lib.image.metadata.xmp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Liest aus einer <em>Nicht</em>-XMP-Datei die XMP-Daten. Voraussetzung ist,
 * dass diese in einen <strong>XMP Packet Wrapper</strong> eingeschlossen sind.
 * 
 * Motivation: Das Adobe XMP SDK hat für Java (noch) nicht XMPFiles
 * implementiert.
 * 
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * <strong>Bug:</strong> Liest nur UTF-8-kodierte XMP-Pakete richtig.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/11/08
 */
public final class XmpFileReader {

    /** Marker für den Beginn eines XMP-Packets (<?xpacket begin=) */
    private static final byte[] xmpPacketMarker = {0x3C, 0x3F, 0x78, 0x70,
        0x61, 0x63, 0x6B, 0x65, 0x74, 0x20, 0x62, 0x65, 0x67, 0x69, 0x6E,
        0x3D
    };
    private static final byte[] xmpBeginMarker = new String("<x:xmpmeta").getBytes(); // NOI18N
    private static final byte[] xmpEndMarker = new String("</x:xmpmeta>").getBytes(); // NOI18N

    /**
     * Liest eine Datei und liefert einen String mit den XMP-Informationen.
     * 
     * Es wird alles geliefert was dem Tag '<?xpacket ... ?>' folgt bis
     * einschließlich '</x:xmpmeta>'. Nach weiteren XMP-Paketen wird nicht
     * gesucht.
     * 
     * Die Validität des Packets wird nicht geprüft (Sind die required-Attribute
     * begin und id definiert? Sind ihre Werte gültig? Ist das gesamte Tag
     * gültig?).
     * 
     * @param filename Name der Datei
     * @return         String mit den XMP-Informationen aus der Datei oder null, wenn
     *                 die Datei nicht existiert, keine XMP-Informationen enthält oder
     *                 ein Eingabefehler aufgetreten ist
     */
    public static synchronized String readFile(String filename) {
        if (filename == null)
            throw new NullPointerException("filename == null");

        try {
            RandomAccessFile file = new RandomAccessFile(filename, "r"); // NOI18N
            int xmpPacketStartIndex = getMatchIndex(file, 0, xmpPacketMarker);
            if (xmpPacketStartIndex >= 0) {
                int xmpStartIndex = getMatchIndex(file, xmpPacketStartIndex +
                    xmpPacketMarker.length, xmpBeginMarker);
                if (xmpStartIndex > 0) {
                    int xmpEndIndex = getMatchIndex(file, xmpStartIndex +
                        xmpBeginMarker.length, xmpEndMarker);
                    if (xmpEndIndex > 0) {
                        file.close();
                        return getXmp(filename, xmpStartIndex, xmpEndIndex);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XmpFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XmpFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static String getXmp(String filename, int xmpStartIndex, int xmpEndIndex) {
        assert filename != null : filename;
        assert xmpStartIndex >= 0 && xmpStartIndex <= xmpEndIndex : xmpStartIndex;
        assert xmpEndIndex >= xmpStartIndex : xmpEndIndex;

        try {
            RandomAccessFile file = new RandomAccessFile(filename, "r"); // NOI18N
            file.seek(xmpStartIndex);
            int count = xmpEndIndex - xmpStartIndex + xmpEndMarker.length;
            byte[] bytes = new byte[count];
            file.read(bytes, 0, count);
            return new String(bytes, 0, count, "UTF-8"); // NOI18N
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XmpFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XmpFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Liefert, ob eine Datei XMP-Informationen enthält.
     * 
     * @param filename Dateiname
     * @return         true, wenn die Datei XMP-Informationen enthält
     */
    public static boolean existsXmp(String filename) {
        if (filename == null)
            throw new NullPointerException("filename == null");

        try {
            BufferedReader inputStream = new BufferedReader(new FileReader(
                filename));
            String line;
            do {
                line = inputStream.readLine();
                if (line != null && line.indexOf("<?xpacket begin") >= 0) { // NOI18N
                    return true;
                }
            } while (line != null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XmpFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XmpFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private static int getMatchIndex(RandomAccessFile file, int startAtOffset, byte[] pattern) throws IOException {
        assert file != null : file;
        assert startAtOffset >= 0;
        assert pattern != null : pattern;

        long fileLength = file.length();
        byte byteRead = 0;
        int startOffset = -1;
        int matchCount = 0;
        boolean patternMatches = false;
        int bufferSize = 1024 * 512;
        byte[] buffer = new byte[bufferSize];
        long bytesToRead = fileLength - startAtOffset + 1;

        for (int offset = startAtOffset; offset < fileLength;) {
            bytesToRead = offset + bufferSize > fileLength ? fileLength - offset + 1
                : bufferSize;
            file.seek(offset);
            file.read(buffer, 0, (int) bytesToRead);
            for (int index = 0; index < bytesToRead; index++) {
                byteRead = buffer[index];
                if (!patternMatches && matchCount <= 0 && byteRead == pattern[0]) {
                    matchCount = 1;
                    startOffset = offset;
                    patternMatches = matchCount == pattern.length;
                }
                if (!patternMatches && matchCount > 0 && offset > startOffset) {
                    if (byteRead == pattern[matchCount]) {
                        matchCount++;
                        patternMatches = matchCount == pattern.length;
                    } else {
                        matchCount = -1;
                        startOffset = -1;
                    }
                }
                if (patternMatches) {
                    return startOffset;
                }
                offset++;
            }
        }
        return -1;
    }

    private XmpFileReader() {
    }
}
