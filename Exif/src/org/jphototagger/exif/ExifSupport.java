package org.jphototagger.exif;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.exif.cache.ExifCache;
import org.jphototagger.exif.datatype.ExifAscii;
import org.jphototagger.exif.formatter.ExifFormatterAscii;
import org.jphototagger.exif.tag.ExifGpsLatitude;
import org.jphototagger.exif.tag.ExifGpsLongitude;
import org.jphototagger.exif.tag.ExifGpsMetadata;
import org.jphototagger.exif.tag.ExifGpsUtil;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.NumberUtil;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ExifSupport {

    private static final Logger LOGGER = Logger.getLogger(ExifSupport.class.getName());
    public static final ExifSupport INSTANCE = new ExifSupport();
    public static final String PREF_KEY_EXCLUDE_FROM_READ_SUFFIXES = "ExifSettings.ExcludeFromReadSuffixes";
    private static final Set<String> SUPPORTED_FILENAME_SUFFIXES_LOWERCASE = new HashSet<>();
    private static final Set<String> EXCLUDE_FILENAME_SUFFIXES_LOWERCASE = new CopyOnWriteArraySet<>();
    private static final Map<String, List<ExifTagsProvider>> EXIF_TAGS_PROVIDERS_OF_FILENAMESUFFIX = new HashMap<>();

    static {
        for (ExifTagsProvider provider : Lookup.getDefault().lookupAll(ExifTagsProvider.class)) {
            Set<String> suffixes = provider.getSupportedFilenameSuffixes();
            SUPPORTED_FILENAME_SUFFIXES_LOWERCASE.addAll(suffixes);
            for (String suffix : suffixes) {
                String suffixLowerCase = suffix.toLowerCase();
                List<ExifTagsProvider> providers = EXIF_TAGS_PROVIDERS_OF_FILENAMESUFFIX.get(suffixLowerCase);
                if (providers == null) {
                    providers = new ArrayList<>();
                    EXIF_TAGS_PROVIDERS_OF_FILENAMESUFFIX.put(suffixLowerCase, providers);
                }
                providers.add(provider);
            }
        }
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        setExcludeFilenameSuffixes(prefs.getStringCollection(PREF_KEY_EXCLUDE_FROM_READ_SUFFIXES));
        AnnotationProcessor.process(INSTANCE);
    }

    private static void setExcludeFilenameSuffixes(Collection<? extends String> suffixes) {
        EXCLUDE_FILENAME_SUFFIXES_LOWERCASE.clear();
        for (String suffix : suffixes) {
            EXCLUDE_FILENAME_SUFFIXES_LOWERCASE.add(suffix.toLowerCase());
        }
    }

    public Set<String> getSupportedSuffixes() {
        return Collections.unmodifiableSet(SUPPORTED_FILENAME_SUFFIXES_LOWERCASE);
    }

    public boolean canReadExif(File file) {
        String suffix = FileUtil.getSuffix(file);
        String suffixLowerCase = suffix.toLowerCase();
        return !EXCLUDE_FILENAME_SUFFIXES_LOWERCASE.contains(suffixLowerCase)
                && SUPPORTED_FILENAME_SUFFIXES_LOWERCASE.contains(suffixLowerCase);
    }

    @EventSubscriber(eventClass=PreferencesChangedEvent.class)
    @SuppressWarnings("unchecked")
    public void preferencesChanged(PreferencesChangedEvent evt) {
        if (PREF_KEY_EXCLUDE_FROM_READ_SUFFIXES.equals(evt.getKey())) {
            setExcludeFilenameSuffixes((Collection<String>) evt.getNewValue());
        }
    }

    public Collection<? extends String> getExludeFilenameSuffixes() {
        return Collections.unmodifiableCollection(EXCLUDE_FILENAME_SUFFIXES_LOWERCASE);
    }

    Exif getExifPreferCached(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        return createExifFromTags(getExifTagsPreferCached(imageFile));
    }

    /**
     * Returns EXIF tags of an image file from the cache if up to date. If the
     * tags are not up to date, they will be created from the image file and cached.
     *
     * @param  imageFile image file
     * @return           tags or null if the tags neither in the cache nor could be
     *                   created from the image file
     */
    public ExifTags getExifTagsPreferCached(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        if (ExifCache.INSTANCE.containsUpToDateExifTags(imageFile)) {
            return ExifCache.INSTANCE.getCachedExifTags(imageFile);
        } else {
            ExifTags exifTags = null;
            if (canReadExif(imageFile)) {
                exifTags = getExifTags(imageFile);
            }
            if (exifTags == null) {
                ExifCache.INSTANCE.cacheExifTags(imageFile, new ExifTags());
            } else {
                ExifCache.INSTANCE.cacheExifTags(imageFile, exifTags);
            }
            return exifTags;
        }
    }

    Exif getExif(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        ExifTags exifTags = getExifTags(imageFile);
        return createExifFromTags(exifTags);
    }

    ExifTags getExifTags(File file) {
        if (file == null || !file.exists() || !canReadExif(file)) {
            return null;
        }
        ExifTags exifTags = new ExifTags();
        try {
            String suffix = FileUtil.getSuffix(file).toLowerCase();
            for (ExifTagsProvider provider : EXIF_TAGS_PROVIDERS_OF_FILENAMESUFFIX.get(suffix)) {
                try {
                    LOGGER.log(Level.INFO, "Reading EXIF from image file ''{0}'', size {1} Bytes. Using {2}.",
                            new Object[]{file, file.length(), provider.getClass()});
                    int countAdded = provider.addToExifTags(file, exifTags);
                    if (countAdded > 0) {
                        return exifTags;
                    }
                } catch (Throwable t) {
                    LOGGER.log(Level.SEVERE, null, t);
                }
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        }
        ExifMakerNotesAdder.addMakerNotesToExifTags(file, exifTags);
        return exifTags;
    }

    long getTimeTakenInMillis(File file) {
        ExifTags exifTags = getExifTags(file);
        if (exifTags == null) {
            return file.lastModified();
        }
        ExifTag dateTimeTag = exifTags.findExifTagByTagId(ExifTag.Properties.DATE_TIME_ORIGINAL.getTagId());
        if (dateTimeTag == null) {
            dateTimeTag = exifTags.findExifTagByTagId(ExifTag.Properties.DATE_TIME_DIGITIZED.getTagId());
        }
        if (dateTimeTag == null) {
            dateTimeTag = exifTags.findExifTagByTagId(ExifTag.Properties.DATE_TIME.getTagId());
        }
        if (dateTimeTag == null) {
            return file.lastModified();
        }
        String dateTimeString = dateTimeTag.getStringValue().trim();
        int dateTimeStringLength = dateTimeString.length();
        if (dateTimeStringLength < 19) {
            return file.lastModified();
        }
        long timestamp = exifDateTimeStringToTimestamp(dateTimeString);
        return timestamp < 0 ? file.lastModified() : timestamp;
    }

    long exifDateTimeStringToTimestamp(String dateTimeString) {
        if (dateTimeString.length() < 19) {
            return -1;
        }
        try {
            String yearString = dateTimeString.substring(0, 4);
            String monthString = dateTimeString.substring(5, 7);
            String dayString = dateTimeString.substring(8, 10);
            String hoursString = dateTimeString.substring(11, 13);
            String minutesString = dateTimeString.substring(14, 16);
            String secondsString = dateTimeString.substring(17, 19);
            if (!NumberUtil.allStringsAreIntegers(Arrays.asList(yearString, monthString, dayString, hoursString, minutesString, secondsString))) {
                return -1;
            }
            int year = Integer.parseInt(yearString);
            int month = Integer.parseInt(monthString);
            int day = Integer.parseInt(dayString);
            int hours = Integer.parseInt(hoursString);
            int minutes = Integer.parseInt(minutesString);
            int seconds = Integer.parseInt(secondsString);
            Calendar calendar = new GregorianCalendar();
            if (year < 1839) {
                LOGGER.log(Level.WARNING, "Year {0} is not plausible and EXIF date time taken will not be set!", year);
                return -1;
            }
            calendar.set(year, month - 1, day, hours, minutes, seconds);
            return calendar.getTimeInMillis();
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        }
        return -1;
    }

    /**
     * @return EXIF metadata or null
     */
    Exif createExifFromTags(ExifTags exifTags) {
        if (exifTags == null) {
            return null;
        }
        try {
            Exif exif = new Exif();
            ExifTag dateTimeOriginalTag = findDateTimeTag(exifTags);
            ExifTag focalLengthTag = exifTags.findExifTagByTagId(ExifTag.Properties.FOCAL_LENGTH.getTagId());
            ExifTag isoSpeedRatingsTag = exifTags.findExifTagByTagId(ExifTag.Properties.ISO_SPEED_RATINGS.getTagId());
            ExifTag modelTag = exifTags.findExifTagByTagId(ExifTag.Properties.MODEL.getTagId());
            ExifTag lensTag = exifTags.findExifTagByTagId(ExifTag.Properties.MAKER_NOTE_LENS.getTagId());
            if (dateTimeOriginalTag != null) {
                setExifDateTimeOriginal(exif, dateTimeOriginalTag);
            }
            if (focalLengthTag != null) {
                setExifFocalLength(exif, focalLengthTag);
            }
            if (isoSpeedRatingsTag != null) {
                setExifIsoSpeedRatings(exif, isoSpeedRatingsTag);
            }
            if (modelTag != null) {
                setExifEquipment(exif, modelTag);
            }
            if (lensTag != null) {
                exif.setLens(lensTag.getStringValue());
            }
            setExifGps(exifTags, exif);
            return exif;
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
            return null;
        }
    }

    private ExifTag findDateTimeTag(ExifTags exifTags) {
        ExifTag dateTimeTag = exifTags.findExifTagByTagId(ExifTag.Properties.DATE_TIME_ORIGINAL.getTagId());
        if (dateTimeTag == null) {
            dateTimeTag = exifTags.findExifTagByTagId(ExifTag.Properties.DATE_TIME_DIGITIZED.getTagId());
        }
        if (dateTimeTag == null) {
            dateTimeTag = exifTags.findExifTagByTagId(ExifTag.Properties.DATE_TIME.getTagId());
        }
        return dateTimeTag;
    }

    private void setExifDateTimeOriginal(Exif exif, ExifTag dateTimeOriginalTag) {
        byte[] rawValue = dateTimeOriginalTag.getRawValue();
        if (rawValue == null || rawValue.length < ExifTag.Properties.DATE_TIME_ORIGINAL.getValueCount()) {
            return;
        }
        String dateTimeString = ExifAscii.convertRawValueToString(rawValue).trim();
        if (dateTimeString.length() == 19) {
            long timeInMillis = exifDateTimeStringToTimestamp(dateTimeString);
            Date dateTimeOriginal = new Date(timeInMillis < 0 ? 0 : timeInMillis);
            exif.setDateTimeOriginal(dateTimeOriginal);
            exif.setDateTimeOriginalTimestamp(timeInMillis);
        }
    }

    private void setExifEquipment(Exif exif, ExifTag modelTag) {
        ExifFormatterAscii formatter = ExifFormatterAscii.INSTANCE;
        String formattedModelTag = formatter.format(modelTag);
        exif.setRecordingEquipment(formattedModelTag);
    }

    private void setExifFocalLength(Exif exif, ExifTag focalLengthTag) {
        try {
            String exifTagStringValue = focalLengthTag.getStringValue();
            StringTokenizer tokenizer = exifTagStringValue == null
                    ? new StringTokenizer("")
                    : new StringTokenizer(exifTagStringValue.trim(), "/:");
            if (tokenizer.countTokens() >= 1) {
                String denominatorString = tokenizer.nextToken();
                String numeratorString = null;

                if (tokenizer.hasMoreTokens()) {
                    numeratorString = tokenizer.nextToken();
                }
                if (!NumberUtil.isDouble(denominatorString)) {
                    return;
                }
                double denominator = Double.parseDouble(denominatorString);
                double focalLength = denominator;
                if (NumberUtil.isDouble(numeratorString)) {
                    double numerator = Double.parseDouble(numeratorString);
                    if (numerator != 0) {
                        focalLength = denominator / numerator;
                    }
                }
                if (focalLength > 0) {
                    exif.setFocalLength(focalLength);
                }
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        }
    }

    private void setExifIsoSpeedRatings(Exif exif, ExifTag isoSpeedRatingsTag) {
        try {
            String exifTagStringValue = isoSpeedRatingsTag.getStringValue();
            String isoSpeedRatingsString = exifTagStringValue == null
                    ? null
                    : exifTagStringValue.trim();
            if (NumberUtil.isShort(isoSpeedRatingsString)) {
                short isoSpeedRatings = Short.parseShort(isoSpeedRatingsString);
                exif.setIsoSpeedRatings(isoSpeedRatings);
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        }
    }

    private void setExifGps(ExifTags fromExifTags, Exif toExif) {
        ExifGpsMetadata gpsMetadata = ExifGpsUtil.createGpsMetadataFromExifTags(fromExifTags);
        ExifGpsLongitude longitude = gpsMetadata.getLongitude();
        ExifGpsLatitude latitude = gpsMetadata.getLatitude();
        if ((latitude != null) && (longitude != null)) {
            double longitudeDegrees = ExifGpsUtil.convertExifDegreesToDouble(longitude.getExifDegrees());
            double latitudeDegrees = ExifGpsUtil.convertExifDegreesToDouble(latitude.getExifDegrees());
            if (ExifGpsLatitude.Ref.SOUTH.equals(latitude.getRef())) {
                latitudeDegrees *= -1;
            }
            if (ExifGpsLongitude.Ref.WEST.equals(longitude.getRef())) {
                longitudeDegrees *= -1;
            }
            toExif.setGpsLatitude(latitudeDegrees);
            toExif.setGpsLongitude(longitudeDegrees);
        }
    }

    private ExifSupport() {
    }
}
