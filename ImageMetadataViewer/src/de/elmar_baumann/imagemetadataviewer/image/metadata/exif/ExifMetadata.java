package de.elmar_baumann.imagemetadataviewer.image.metadata.exif;

import com.imagero.reader.MetadataUtils;
import com.imagero.reader.iptc.IPTCEntry;
import com.imagero.reader.iptc.IPTCEntryCollection;
import com.imagero.reader.jpeg.JpegReader;
import com.imagero.reader.tiff.IFDEntry;
import com.imagero.reader.tiff.ImageFileDirectory;
import com.imagero.reader.tiff.TiffReader;
import de.elmar_baumann.imagemetadataviewer.data.Exif;
import de.elmar_baumann.imagemetadataviewer.tasks.ImageMetadataToDatabase;
import de.elmar_baumann.imagemetadataviewer.event.ErrorEvent;
import de.elmar_baumann.imagemetadataviewer.event.listener.ErrorListeners;
import de.elmar_baumann.imagemetadataviewer.io.FileType;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utils für EXIF.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/28
 */
public class ExifMetadata {

    private static HashMap<String, Double> rotationAngleOfString = new HashMap<String, Double>();
    private static List<Integer> tagsToDisplay = new ArrayList<Integer>();

    /**
     * Liefert die EXIF-Metadaten einer Datei.
     * 
     * @param filename Dateiname
     * @return         Metadaten oder null bei Lesefehlern
     */
    public List<IFDEntry> getMetadata(String filename) {
        if (!FileUtil.existsFile(filename)) {
            return null;
        }
        List<IFDEntry> metadata = new ArrayList<IFDEntry>();
        try {
            addIFDEntries(filename, metadata);
        } catch (IOException ex) {
            Logger.getLogger(ExifMetadata.class.getName()).log(Level.SEVERE, null, ex);
            notifyErrorListener(ex.toString());
        } catch (Exception ex) {
            Logger.getLogger(ExifMetadata.class.getName()).log(Level.SEVERE, null, ex);
            notifyErrorListener(ex.toString());
        }
        return metadata;
    }

    private static void addIFDEntries(String filename, List<IFDEntry> metadata) throws IOException {
        File file = new File(filename);
        if (FileType.isJpegFile(filename)) {
            JpegReader reader = new JpegReader(file);
            IFDEntry[][] allEntries = MetadataUtils.getExif(reader);
            if (allEntries != null) {
                for (int i = 0; i < allEntries.length; i++) {
                    IFDEntry[] currentEntries = allEntries[i];
                    for (int j = 0; j < currentEntries.length; j++) {
                        metadata.add(currentEntries[j]);
                    }
                }
            }
        } else {
            TiffReader reader = new TiffReader(file);
            int count = reader.getIFDCount();
            for (int i = 0; i < count; i++) {
                addIfdEntriesOfDirectory(reader.getIFD(i), metadata);
            }
        }
    }

    private static void addIfdEntriesOfDirectory(ImageFileDirectory ifd,
        List<IFDEntry> metadata) {
        int entryCount = ifd.getEntryCount();
        for (int i = 0; i < entryCount; i++) {
            IFDEntry ifdEntry = ifd.getEntryAt(i);
            if (ifdEntry != null) {
                metadata.add(ifdEntry);
            }
        }
        for (int i = 0; i < ifd.getIFDCount(); i++) {
            ImageFileDirectory ifd0 = ifd.getIFDAt(i);
            addIfdEntriesOfDirectory(ifd0, metadata);
        }
        ImageFileDirectory exifIFD = ifd.getExifIFD();
        if (exifIFD != null) {
            addIfdEntriesOfDirectory(exifIFD, metadata);
        }
        ImageFileDirectory gpsIFD = ifd.getGpsIFD();
        if (gpsIFD != null) {
            addIfdEntriesOfDirectory(gpsIFD, metadata);
        }
        ImageFileDirectory interoperabilityIFD = ifd.getInteroperabilityIFD();
        if (interoperabilityIFD != null) {
            addIfdEntriesOfDirectory(interoperabilityIFD, metadata);
        }
    }

    /**
     * Findet einen Entry mit bestimmten Tag.
     * 
     * @param entries Zu durchsuchende Entries
     * @param tag     Tag
     * @return        Erster passender Entry oder null, falls nicht gefunden
     */
    public static IFDEntry findEntryWithTag(List<IFDEntry> entries, int tag) {
        for (IFDEntry entry : entries) {
            if (entry.getTag() == tag) {
                return entry;
            }
        }
        return null;
    }

    /**
     * Liefert den Drehwinkel für die richtige Ausrichtung des eingebetteten 
     * Thumbnails.
     * 
     * @param entries EXIF-Metadaten eines Bilds
     * @return        Drehwinkel im Uhrzeigersinn
     */
    public double getThumbnailRotationAngle(List<IFDEntry> entries) {
        IFDEntry entry = findEntryWithTag(entries, 274);
        if (entry != null) {
            initRotationAngleMap();
            Double angle = rotationAngleOfString.get(entry.toString());
            if (angle == null) {
                return 0;
            }
            return angle.doubleValue();
        }
        return 0;
    }

    private static void initRotationAngleMap() {
        if (rotationAngleOfString.isEmpty()) {
            rotationAngleOfString.put("(0, 0) is top-left", new Double(0)); // 1 // NOI18N
            rotationAngleOfString.put("(0, 0) is top-right", new Double(0)); // 2 // NOI18N
            rotationAngleOfString.put("0, 0) is bottom-right", new Double(180)); // 3 // NOI18N
            rotationAngleOfString.put("(0, 0) is bottom-left", new Double(180)); // 4 // NOI18N
            rotationAngleOfString.put("(0, 0) is left-top", new Double(90)); // 5 // NOI18N
            rotationAngleOfString.put("(0, 0) is right-top", new Double(90)); // 6 // NOI18N
            rotationAngleOfString.put("(0, 0) is right-bottom", new Double(270)); // 7 // NOI18N
            rotationAngleOfString.put("(0, 0) is left-bottom", new Double(270)); // 8 // NOI18N
        }
    }

    /**
     * Liefert Metadaten, die eine informative View anzeigen soll.
     * 
     * @param entries Beliebige Entries
     * @return        Entries aus entries, die angezeigt werden sollen
     */
    public static List<IFDEntry> getDisplayableMetadata(List<IFDEntry> entries) {
        List<IFDEntry> displayableEntries = new ArrayList<IFDEntry>(entries.size());
        initTagsToDisplay();
        for (IFDEntry entry : entries) {
            if (isTagToDisplay(entry.getEntryMeta().getTag())) {
                if (!contains(displayableEntries, entry)) {
                    displayableEntries.add(entry);
                }
            }
        }
        return displayableEntries;
    }

    private static boolean isTagToDisplay(int tag) {
        return tagsToDisplay.contains(tag);
    }

    private static void initTagsToDisplay() {
        if (tagsToDisplay.isEmpty()) {
            tagsToDisplay.add(ExifTag.Make.getId());
            tagsToDisplay.add(ExifTag.Model.getId());
            tagsToDisplay.add(ExifTag.Software.getId());
            tagsToDisplay.add(ExifTag.ExposureTime.getId());
            tagsToDisplay.add(ExifTag.FNumber.getId());
            tagsToDisplay.add(ExifTag.ExposureProgram.getId());
            tagsToDisplay.add(ExifTag.ISOSpeedRatings.getId());
            tagsToDisplay.add(ExifTag.DateTimeOriginal.getId());
            tagsToDisplay.add(ExifTag.MeteringMode.getId());
            tagsToDisplay.add(ExifTag.Flash.getId());
            tagsToDisplay.add(ExifTag.FocalLength.getId());
            tagsToDisplay.add(ExifTag.UserComment.getId());
            tagsToDisplay.add(ExifTag.FileSource.getId());
            tagsToDisplay.add(ExifTag.WhiteBalance.getId());
            tagsToDisplay.add(ExifTag.FocalLengthIn35mmFilm.getId());
            tagsToDisplay.add(ExifTag.Contrast.getId());
            tagsToDisplay.add(ExifTag.Saturation.getId());
            tagsToDisplay.add(ExifTag.Sharpness.getId());
        }
    }

    private static boolean contains(List<IFDEntry> entries, IFDEntry entry) {
        ExifIfdEntryComparator comparator = new ExifIfdEntryComparator();
        for (IFDEntry e : entries) {
            if (comparator.compare(e, entry) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt die EXIF-Daten einer Datei auf die Standardausgabe aus.
     * 
     * @param filename Dateiname
     */
    public static void dumpExif(String filename) {
        // Code inklusive aufgerufener Operation von Andrey Kuznetsov <imagero@gmx.de>
        // E-Mail v. 22.08.2008
        try {
            File f = new File(filename);
            TiffReader reader = new TiffReader(f);
            int cnt = reader.getIFDCount();
            System.out.println("Count IFDs: " + cnt); // NOI18N
            for (int i = 0; i < cnt; i++) {
                dumpPrintDirectory(reader.getIFD(i), "IFD#" + i, System.out); // NOI18N
            }

            IPTCEntryCollection collection = MetadataUtils.getIPTC(reader);
            dumpPrintIptc(collection, System.out);
        } catch (IOException ex) {
            Logger.getLogger(ExifMetadata.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void dumpPrintDirectory(ImageFileDirectory ifd, String name,
        PrintStream out)
        throws IOException {
        out.println("\n-----------------------------------------"); // NOI18N
        out.println();
        out.println(name);
        out.println("Entry count " + ifd.getEntryCount()); // NOI18N
        out.println("name: tag: valueOffset: {description}: value:"); // NOI18N

        int entryCount = ifd.getEntryCount();
        for (int i = 0; i < entryCount; i++) {
            IFDEntry ifdEntry = ifd.getEntryAt(i);
            if (ifdEntry != null) {
                dumpPrintEntry(ifdEntry, out);
            }
        }
        for (int i = 0; i < ifd.getIFDCount(); i++) {
            ImageFileDirectory ifd0 = ifd.getIFDAt(i);
            dumpPrintDirectory(ifd0, "", out); // NOI18N
        }
        ImageFileDirectory exifIFD = ifd.getExifIFD();
        if (exifIFD != null) {
            dumpPrintDirectory(exifIFD, "ExifIFD", out); // NOI18N
        }
        ImageFileDirectory gpsIFD = ifd.getGpsIFD();
        if (gpsIFD != null) {
            dumpPrintDirectory(gpsIFD, "GpsIFD", out); // NOI18N
        }
        ImageFileDirectory interoperabilityIFD = ifd.getInteroperabilityIFD();
        if (interoperabilityIFD != null) {
            dumpPrintDirectory(interoperabilityIFD, "InteroperabilityIFD", out); // NOI18N
        }
        out.println();
    }

    static void dumpPrintEntry(IFDEntry e, PrintStream out) {
        out.println();
        int tag = e.getTag();
        out.print(tag);
        out.print("\t"); // NOI18N
        out.print(e.getEntryMeta().getName());
        out.print("\t"); // NOI18N
        out.print(e);
    }

    static void dumpPrintIptc(IPTCEntryCollection entries, PrintStream out) {
        out.println();
        Enumeration keys = entries.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            int count = entries.getCount(key);
            for (int j = 0; j < count; j++) {
                IPTCEntry entry = entries.getEntry(key, j);
                out.print(entry.getRecordNumber() + ":" + entry.getDataSetNumber()); // NOI18N
                out.print(" ("); // NOI18N
                out.print(entry.getEntryMeta().getName());
                out.print(") "); // NOI18N
                out.println(entries.toString(entry).replace((char) 0, (char) 32).trim());
            }
        }
    }

    private void notifyErrorListener(String message) {
        ErrorListeners.getInstance().notifyErrorListener(new ErrorEvent(message, this));
    }

    /**
     * Liefert die EXIF-Daten einer Bilddatei.
     * 
     * @param  filename  Dateiname
     * @return EXIF-Daten oder null bei Fehlern
     */
    public static Exif getExif(String filename) {
        Exif exif = null;
        ExifMetadata exifMetadata = new ExifMetadata();
        List<IFDEntry> exifEntries = exifMetadata.getMetadata(filename);
        if (exifEntries != null) {
            exif = new Exif();
            IFDEntry dateTimeOriginalEntry = ExifMetadata.findEntryWithTag(exifEntries, ExifTag.DateTimeOriginal.getId());
            IFDEntry focalLengthEntry = ExifMetadata.findEntryWithTag(exifEntries, ExifTag.FocalLength.getId());
            IFDEntry isoSpeedRatingsEntry = ExifMetadata.findEntryWithTag(exifEntries, ExifTag.ISOSpeedRatings.getId());
            IFDEntry modelEntry = ExifMetadata.findEntryWithTag(exifEntries, ExifTag.Model.getId());
            if (dateTimeOriginalEntry != null) {
                setExifDateTimeOriginal(exif, dateTimeOriginalEntry);
            }
            if (focalLengthEntry != null) {
                setExifFocalLength(exif, focalLengthEntry);
            }
            if (isoSpeedRatingsEntry != null) {
                setExifIsoSpeedRatings(exif, isoSpeedRatingsEntry);
            }
            setExifEquipment(exif, modelEntry);
        }
        return exif;
    }

    private static void setExifDateTimeOriginal(Exif exifData,
        IFDEntry dateTimeOriginalEntry) {
        String datestring = dateTimeOriginalEntry.toString();
        if (datestring.length() >= 11) {
            try {
                int year = new Integer(datestring.substring(0, 4)).intValue();
                int month = new Integer(datestring.substring(5, 7)).intValue();
                int day = new Integer(datestring.substring(8, 10)).intValue();
                Calendar calendar = new GregorianCalendar();
                calendar.set(year, month - 1, day);
                try {
                    exifData.setDateTimeOriginal(new Date(calendar.getTimeInMillis()));
                } catch (Exception ex) {
                    Logger.getLogger(ImageMetadataToDatabase.class.getName()).log(Level.WARNING, ex.getMessage());
                }
            } catch (NumberFormatException ex) {
                Logger.getLogger(ImageMetadataToDatabase.class.getName()).log(Level.WARNING, ex.getMessage());
                ErrorListeners.getInstance().notifyErrorListener(new ErrorEvent(ex.toString(), ExifMetadata.class));
            }
        }
    }

    private static void setExifEquipment(Exif exifData, IFDEntry modelEntry) {
        if (modelEntry != null) {
            exifData.setRecordingEquipment(modelEntry.toString().trim());
        }
    }

    private static void setExifFocalLength(Exif exifData, IFDEntry focalLengthEntry) {
        try {
            String lengthString = focalLengthEntry.toString().trim();
            StringTokenizer tokenizer = new StringTokenizer(lengthString, "/:"); // NOI18N
            if (tokenizer.countTokens() >= 1) {
                String dividentString = tokenizer.nextToken();
                String divisorString = null;
                if (tokenizer.hasMoreTokens()) {
                    divisorString = tokenizer.nextToken();
                }
                Double divident = new Double(dividentString);
                double focalLength = divident;
                if (divisorString != null) {
                    Double divisor = new Double(divisorString);
                    focalLength = divident / divisor;
                }
                exifData.setFocalLength(focalLength);
            }
        } catch (Exception ex) {
            Logger.getLogger(ExifMetadata.class.getName()).log(Level.WARNING, ex.getMessage());
            ErrorListeners.getInstance().notifyErrorListener(new ErrorEvent(ex.toString(), ExifMetadata.class));
        }
    }

    private static void setExifIsoSpeedRatings(Exif exifData,
        IFDEntry isoSpeedRatingsEntry) {
        try {
            exifData.setIsoSpeedRatings(new Short(isoSpeedRatingsEntry.toString().trim()).shortValue());
        } catch (Exception ex) {
            Logger.getLogger(ExifMetadata.class.getName()).log(Level.WARNING, ex.getMessage());
            ErrorListeners.getInstance().notifyErrorListener(new ErrorEvent(ex.toString(), ExifMetadata.class));
        }
    }
}
