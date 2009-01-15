package de.elmar_baumann.imv.image.metadata.exif;

import com.imagero.reader.ImageReader;
import com.imagero.reader.MetadataUtils;
import com.imagero.reader.tiff.IFDEntry;
import com.imagero.reader.tiff.ImageFileDirectory;
import de.elmar_baumann.imv.data.Exif;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Utils für EXIF.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ExifMetadata {

    private static final Map<String, Double> rotationAngleOfString = new HashMap<String, Double>();
    private static final List<Integer> tagsToDisplay = new ArrayList<Integer>();


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
    public static List<IdfEntryProxy> getMetadata(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        List<IdfEntryProxy> metadata = new ArrayList<IdfEntryProxy>();
        try {
            addIFDEntries(file, metadata);
        } catch (IOException ex) {
            de.elmar_baumann.imv.Log.logWarning(ExifMetadata.class, ex);
        } catch (Exception ex) {
            de.elmar_baumann.imv.Log.logWarning(ExifMetadata.class, ex);
        }
        return metadata;
    }

    private static void addIFDEntries(File file, List<IdfEntryProxy> metadata) throws IOException {
        ImageFileDirectory[] ifds = MetadataUtils.getExif(file);
        if (ifds != null) {
            IFDEntry[][] allEntries = MetadataUtils.getEntries(ifds);
            if (allEntries != null) {
                for (int i = 0; i < allEntries.length; i++) {
                    IFDEntry[] currentEntries = allEntries[i];
                    for (int j = 0; j < currentEntries.length; j++) {
                        metadata.add(new IdfEntryProxy(currentEntries[j]));
                    }
                }
            }
        }
    }

    private static void close(ImageReader reader) {
        // causes exceptions e.g. in TableModelExif#setExifData(): entry.toString() ?
        if (reader != null) {
            reader.close();
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
    public static double getThumbnailRotationAngle(List<IdfEntryProxy> entries) {
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
        for (IdfEntryProxy e : entries) {
            if (ExifIfdEntryComparator.INSTANCE.compare(e, entry) == 0) {
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
        List<IdfEntryProxy> exifEntries = ExifMetadata.getMetadata(file);
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
                de.elmar_baumann.imv.Log.logWarning(ExifMetadata.class, ex);
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
                    de.elmar_baumann.imv.Log.logWarning(ExifMetadata.class, ex);
                }
            } catch (NumberFormatException ex) {
                de.elmar_baumann.imv.Log.logWarning(ExifMetadata.class, ex);
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
            de.elmar_baumann.imv.Log.logWarning(ExifMetadata.class, ex);
        }
    }

    private static void setExifIsoSpeedRatings(Exif exifData, IdfEntryProxy enry) {
        try {
            exifData.setIsoSpeedRatings(new Short(enry.toString().trim()).shortValue());
        } catch (Exception ex) {
            de.elmar_baumann.imv.Log.logWarning(ExifMetadata.class, ex);
        }
    }

    private ExifMetadata() {
    }
}
