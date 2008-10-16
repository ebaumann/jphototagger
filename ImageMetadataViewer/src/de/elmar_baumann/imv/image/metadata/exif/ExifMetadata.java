package de.elmar_baumann.imv.image.metadata.exif;

import com.imagero.reader.ImageReader;
import com.imagero.reader.MetadataUtils;
import com.imagero.reader.iptc.IPTCEntry;
import com.imagero.reader.iptc.IPTCEntryCollection;
import com.imagero.reader.jpeg.JpegReader;
import com.imagero.reader.tiff.IFDEntry;
import com.imagero.reader.tiff.ImageFileDirectory;
import com.imagero.reader.tiff.TiffReader;
import de.elmar_baumann.imv.data.Exif;
import de.elmar_baumann.imv.tasks.ImageMetadataToDatabase;
import de.elmar_baumann.imv.event.ErrorEvent;
import de.elmar_baumann.imv.event.listener.ErrorListeners;
import de.elmar_baumann.imv.io.FileType;
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
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utils für EXIF.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ExifMetadata {

    private static Map<String, Double> rotationAngleOfString = new HashMap<String, Double>();
    private static List<Integer> tagsToDisplay = new ArrayList<Integer>();
    

    static {
        rotationAngleOfString.put("(0, 0) is top-left", new Double(0)); // 1 // NOI18N
        rotationAngleOfString.put("(0, 0) is top-right", new Double(0)); // 2 // NOI18N
        rotationAngleOfString.put("0, 0) is bottom-right", new Double(180)); // 3 // NOI18N
        rotationAngleOfString.put("(0, 0) is bottom-left", new Double(180)); // 4 // NOI18N
        rotationAngleOfString.put("(0, 0) is left-top", new Double(90)); // 5 // NOI18N
        rotationAngleOfString.put("(0, 0) is right-top", new Double(90)); // 6 // NOI18N
        rotationAngleOfString.put("(0, 0) is right-bottom", new Double(270)); // 7 // NOI18N
        rotationAngleOfString.put("(0, 0) is left-bottom", new Double(270)); // 8 // NOI18N
    }
    

    static {
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

    /**
     * Returns the EXIF metadata of a file.
     * 
     * @param  file  file
     * @return metadata oder null if errors occured
     */
    public List<IdfEntryProxy> getMetadata(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        List<IdfEntryProxy> metadata = new ArrayList<IdfEntryProxy>();
        try {
            addIFDEntries(file, metadata);
        } catch (IOException ex) {
            handleException(ex);
        } catch (Exception ex) {
            handleException(ex);
        }
        return metadata;
    }

    private static void addIFDEntries(File file, List<IdfEntryProxy> metadata) throws IOException {
        ImageReader reader = null;
        if (FileType.isJpegFile(file.getName())) {
            reader = new JpegReader(file);
            IFDEntry[][] allEntries = MetadataUtils.getExif((JpegReader) reader);
            if (allEntries != null) {
                for (int i = 0; i < allEntries.length; i++) {
                    IFDEntry[] currentEntries = allEntries[i];
                    for (int j = 0; j < currentEntries.length; j++) {
                        metadata.add(new IdfEntryProxy(currentEntries[j]));
                    }
                }
            }
        } else {
            reader = new TiffReader(file);
            int count = ((TiffReader) reader).getIFDCount();
            for (int i = 0; i < count; i++) {
                addIfdEntriesOfDirectory(((TiffReader) reader).getIFD(i), metadata);
            }
        }
        close(reader);
    }

    private static void close(ImageReader reader) {
        // causes exceptions e.g. in TableModelExif#setExifData(): entry.toString() ?
        if (reader != null) {
            reader.close();
        }
    }

    private static void addIfdEntriesOfDirectory(ImageFileDirectory ifd, List<IdfEntryProxy> metadata) {
        int entryCount = ifd.getEntryCount();
        for (int i = 0; i < entryCount; i++) {
            IFDEntry ifdEntry = ifd.getEntryAt(i);
            if (ifdEntry != null) {
                metadata.add(new IdfEntryProxy(ifdEntry));
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
    public static IdfEntryProxy findEntryWithTag(List<IdfEntryProxy> entries, int tag) {
        for (IdfEntryProxy entry : entries) {
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
    public double getThumbnailRotationAngle(List<IdfEntryProxy> entries) {
        IdfEntryProxy entry = findEntryWithTag(entries, 274);
        if (entry != null) {
            Double angle = rotationAngleOfString.get(entry.toString());
            if (angle == null) {
                return 0;
            }
            return angle.doubleValue();
        }
        return 0;
    }

    /**
     * Liefert Metadaten, die eine informative View anzeigen soll.
     * 
     * @param entries Beliebige Entries
     * @return        Entries aus entries, die angezeigt werden sollen
     */
    public static List<IdfEntryProxy> getDisplayableMetadata(List<IdfEntryProxy> entries) {
        List<IdfEntryProxy> displayableEntries = new ArrayList<IdfEntryProxy>(entries.size());
        for (IdfEntryProxy entry : entries) {
            if (isTagToDisplay(entry.getTag())) {
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

    private static boolean contains(List<IdfEntryProxy> entries, IdfEntryProxy entry) {
        ExifIfdEntryComparator comparator = new ExifIfdEntryComparator();
        for (IdfEntryProxy e : entries) {
            if (comparator.compare(e, entry) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the EXIF metadata of a file.
     * 
     * @param  file  file
     * @return EXIF metadata or null if errors occured
     */
    public static Exif getExif(File file) {
        Exif exif = null;
        ExifMetadata exifMetadata = new ExifMetadata();
        List<IdfEntryProxy> exifEntries = exifMetadata.getMetadata(file);
        if (exifEntries != null) {
            exif = new Exif();
            try {
                IdfEntryProxy dateTimeOriginalEntry = ExifMetadata.findEntryWithTag(exifEntries, ExifTag.DateTimeOriginal.getId());
                IdfEntryProxy focalLengthEntry = ExifMetadata.findEntryWithTag(exifEntries, ExifTag.FocalLength.getId());
                IdfEntryProxy isoSpeedRatingsEntry = ExifMetadata.findEntryWithTag(exifEntries, ExifTag.ISOSpeedRatings.getId());
                IdfEntryProxy modelEntry = ExifMetadata.findEntryWithTag(exifEntries, ExifTag.Model.getId());
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
            } catch (Exception ex) {
                Logger.getLogger(ImageMetadataToDatabase.class.getName()).log(Level.WARNING, ex.getMessage());
                ErrorListeners.getInstance().notifyErrorListener(new ErrorEvent(ex.toString(), ExifMetadata.class));
                exif = null;
            }
        }
        return exif;
    }

    private static void setExifDateTimeOriginal(Exif exifData, IdfEntryProxy entry) {
        String datestring = null;
        datestring = entry.toString(); // had thrown a null pointer exception
        if (datestring != null && datestring.length() >= 11) {
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

    private static void setExifEquipment(Exif exifData, IdfEntryProxy entry) {
        if (entry != null) {
            exifData.setRecordingEquipment(entry.toString().trim());
        }
    }

    private static void setExifFocalLength(Exif exifData, IdfEntryProxy entry) {
        try {
            String lengthString = entry.toString().trim();
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
            handleException(ex);
        }
    }

    private static void setExifIsoSpeedRatings(Exif exifData, IdfEntryProxy enry) {
        try {
            exifData.setIsoSpeedRatings(new Short(enry.toString().trim()).shortValue());
        } catch (Exception ex) {
            handleException(ex);
        }
    }

    private static void handleException(Exception ex) {
        Logger.getLogger(ExifMetadata.class.getName()).log(Level.WARNING, null, ex);
        ErrorListeners.getInstance().notifyErrorListener(new ErrorEvent(ex.toString(), ExifMetadata.class));
    }

    /**
     * Dumps the exif metadata of a file to stdout.
     * 
     * @param file file
     */
    public static void dumpExif(File file) {
        // Code inklusive aufgerufener Operation von Andrey Kuznetsov <imagero@gmx.de>
        // E-Mail v. 22.08.2008
        try {
            TiffReader reader = new TiffReader(file);
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
}
