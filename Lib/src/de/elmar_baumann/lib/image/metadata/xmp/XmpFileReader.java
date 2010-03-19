/*
 * @(#)XmpFileReader.java    Created on 2007-11-08
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.lib.image.metadata.xmp;

import java.io.BufferedReader;
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
 * @author  Elmar Baumann
 */
public final class XmpFileReader {
    private static final byte[] XMP_PACKET_MARKER = {
        0x3C, 0x3F, 0x78, 0x70, 0x61, 0x63, 0x6B, 0x65, 0x74, 0x20, 0x62, 0x65,
        0x67, 0x69, 0x6E, 0x3D
    };    // "<?xpacket begin="
    private static final byte[] XMP_BEGIN_MARKER = {
        0x3C, 0x78, 0x3A, 0x78, 0x6D, 0x70, 0x6D, 0x65, 0x74, 0x61
    };    // "<x:xmpmeta"
    private static final byte[] XMP_END_MARKER   = {
        0x3C, 0x2F, 0x78, 0x3A, 0x78, 0x6D, 0x70, 0x6D, 0x65, 0x74, 0x61, 0x3E
    };    // "</x:xmpmeta>"

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
    public static String readFile(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");
        }

        RandomAccessFile file = null;

        try {
            file = new RandomAccessFile(filename, "r");
            file.getChannel().lock(0, Long.MAX_VALUE, true);

            int xmpPacketStartIndex = getMatchIndex(file, 0, XMP_PACKET_MARKER);

            if (xmpPacketStartIndex >= 0) {
                int xmpStartIndex =
                    getMatchIndex(file,
                                  xmpPacketStartIndex
                                  + XMP_PACKET_MARKER.length, XMP_BEGIN_MARKER);

                if (xmpStartIndex > 0) {
                    int xmpEndIndex =
                        getMatchIndex(file,
                                      xmpStartIndex + XMP_BEGIN_MARKER.length,
                                      XMP_END_MARKER);

                    if (xmpEndIndex > 0) {

                        // file will be closed in finally
                        return getXmp(filename, xmpStartIndex, xmpEndIndex);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(XmpFileReader.class.getName()).log(Level.SEVERE,
                             null, ex);
        } finally {
            closeFile(file);
        }

        return null;
    }

    private static String getXmp(String filename, int xmpStartIndex,
                                 int xmpEndIndex) {
        assert (xmpStartIndex >= 0) && (xmpStartIndex <= xmpEndIndex) :
                xmpStartIndex;
        assert xmpEndIndex >= xmpStartIndex : xmpEndIndex;

        RandomAccessFile file = null;

        try {
            file = new RandomAccessFile(filename, "r");
            file.seek(xmpStartIndex);

            int    count     = xmpEndIndex - xmpStartIndex
                               + XMP_END_MARKER.length;
            byte[] bytes     = new byte[count];
            int    bytesRead = file.read(bytes, 0, count);

            // file will be closed in finally
            return new String(bytes, 0, bytesRead, "UTF-8");
        } catch (Exception ex) {
            Logger.getLogger(XmpFileReader.class.getName()).log(Level.SEVERE,
                             null, ex);
        } finally {
            closeFile(file);
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
        if (filename == null) {
            throw new NullPointerException("filename == null");
        }

        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(filename));

            String line;

            do {
                line = bufferedReader.readLine();

                if ((line != null) && (line.indexOf("<?xpacket begin") >= 0)) {

                    // reader will be closed in finally
                    return true;
                }
            } while (line != null);
        } catch (Exception ex) {
            Logger.getLogger(XmpFileReader.class.getName()).log(Level.SEVERE,
                             null, ex);
        } finally {
            closeReader(bufferedReader);
        }

        return false;
    }

    private static int getMatchIndex(RandomAccessFile file, int startAtOffset,
                                     byte[] pattern)
            throws IOException {
        assert startAtOffset >= 0;

        long    fileLength     = file.length();
        byte    byteRead       = 0;
        int     startOffset    = -1;
        int     matchCount     = 0;
        boolean patternMatches = false;
        int     bufferSize     = 1024 * 512;
        byte[]  buffer         = new byte[bufferSize];
        long    bytesToRead    = fileLength - startAtOffset + 1;

        for (int offset = startAtOffset; offset < fileLength; ) {
            bytesToRead = (offset + bufferSize > fileLength)
                          ? fileLength - offset + 1
                          : bufferSize;
            file.seek(offset);

            int bytesRead = file.read(buffer, 0, (int) bytesToRead);

            for (int index = 0; index < bytesRead; index++) {
                byteRead = buffer[index];

                if (!patternMatches && (matchCount <= 0)
                        && (byteRead == pattern[0])) {
                    matchCount     = 1;
                    startOffset    = offset;
                    patternMatches = matchCount == pattern.length;
                }

                if (!patternMatches && (matchCount > 0)
                        && (offset > startOffset)) {
                    if (byteRead == pattern[matchCount]) {
                        matchCount++;
                        patternMatches = matchCount == pattern.length;
                    } else {
                        matchCount  = -1;
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

    private static void closeFile(RandomAccessFile file) {
        if (file != null) {
            try {
                file.close();
            } catch (Exception ex) {
                Logger.getLogger(XmpFileReader.class.getName()).log(
                    Level.SEVERE, null, ex);
            }
        }
    }

    private static void closeReader(BufferedReader bufferedReader) {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (Exception ex) {
                Logger.getLogger(XmpFileReader.class.getName()).log(
                    Level.SEVERE, null, ex);
            }
        }
    }

    private XmpFileReader() {}
}
