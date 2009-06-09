package de.elmar_baumann.imv.image.metadata.exif;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.resource.Translation;
import de.elmar_baumann.lib.lang.Util;
import de.elmar_baumann.lib.template.Pair;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Formatiert EXIF-Werte.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/31
 */
public final class ExifFieldValueFormatter {

    private static final Translation translation = new Translation(
            "ExifFieldValueTranslations"); // NOI18N
    private static final Map<Integer, String> exifKeyOfExposureProgram =
            new HashMap<Integer, String>();
    private static final Map<Integer, String> exifKeyOfContrast =
            new HashMap<Integer, String>();
    private static final Map<Integer, String> exifKeyOfMeteringMode =
            new HashMap<Integer, String>();
    private static final Map<Integer, String> exifKeyOfSaturation =
            new HashMap<Integer, String>();
    private static final Map<Integer, String> exifKeyOfSharpness =
            new HashMap<Integer, String>();
    private static final Map<Integer, String> exifKeyOfWhiteBalance =
            new HashMap<Integer, String>();
    private static final List<Integer> asciiTags = new ArrayList<Integer>();


    static {
        // The translation of the keys are in the file
        // ExifFieldValueTranslations.properties

        exifKeyOfExposureProgram.put(0, "ExposureProgramUnkonwn"); // NOI18N
        exifKeyOfExposureProgram.put(1, "ExposureProgramManual"); // NOI18N
        exifKeyOfExposureProgram.put(2, "ExposureProgramNormalProgram"); // NOI18N
        exifKeyOfExposureProgram.put(3, "ExposureProgramAperturePriority"); // NOI18N
        exifKeyOfExposureProgram.put(4, "ExposureProgramTimePriority"); // NOI18N
        exifKeyOfExposureProgram.put(5, "ExposureProgramCreativ"); // NOI18N
        exifKeyOfExposureProgram.put(6, "ExposureProgramAction"); // NOI18N
        exifKeyOfExposureProgram.put(7, "ExposureProgramPortrait"); // NOI18N
        exifKeyOfExposureProgram.put(8, "ExposureProgramLandscape"); // NOI18N

        exifKeyOfContrast.put(0, "ContrastNormal"); // NOI18N
        exifKeyOfContrast.put(1, "ContrastLow"); // NOI18N
        exifKeyOfContrast.put(2, "ContrastHigh"); // NOI18N

        exifKeyOfMeteringMode.put(0, "MeteringModeUnknown"); // NOI18N
        exifKeyOfMeteringMode.put(1, "MeteringModeIntegral"); // NOI18N
        exifKeyOfMeteringMode.put(2, "MeteringModeIntegralCenter"); // NOI18N
        exifKeyOfMeteringMode.put(3, "MeteringModeSpot"); // NOI18N
        exifKeyOfMeteringMode.put(4, "MeteringModeMultiSpot"); // NOI18N
        exifKeyOfMeteringMode.put(5, "MeteringModeMatrix"); // NOI18N
        exifKeyOfMeteringMode.put(6, "MeteringModeSelective"); // NOI18N

        exifKeyOfSaturation.put(0, "SaturationNormal"); // NOI18N
        exifKeyOfSaturation.put(1, "SaturationLow"); // NOI18N
        exifKeyOfSaturation.put(2, "SaturationHigh"); // NOI18N

        exifKeyOfSharpness.put(0, "SharpnessNormal"); // NOI18N
        exifKeyOfSharpness.put(1, "SharpnessSoft"); // NOI18N
        exifKeyOfSharpness.put(2, "SharpnessHard"); // NOI18N

        exifKeyOfWhiteBalance.put(0, "WhiteBalanceAutomatic"); // NOI18N
        exifKeyOfWhiteBalance.put(1, "WhiteBalanceManual"); // NOI18N

        asciiTags.add(ExifTag.MAKE.getId());
        asciiTags.add(ExifTag.MODEL.getId());
        asciiTags.add(ExifTag.SOFTWARE.getId());
    }

    /**
     * Formatis an exif entry.
     * 
     * @param  entry  entry
     * @return entry formatted
     */
    public static String format(IdfEntryProxy entry) {
        int tag = entry.getTag();
        if (tag == ExifTag.SHARPNESS.getId()) {
            return getSharpness(entry);
        } else if (tag == ExifTag.SATURATION.getId()) {
            return getSaturation(entry);
        } else if (tag == ExifTag.WHITE_BALANCE.getId()) {
            return getWhiteBalance(entry);
        } else if (tag == ExifTag.FOCAL_LENGTH.getId()) {
            return getFocalLength(entry);
        } else if (tag == ExifTag.FOCAL_LENGTH_IN_35_MM_FILM.getId()) {
            return getFocalLengthIn35mm(entry);
        } else if (tag == ExifTag.EXPOSURE_TIME.getId()) {
            return getExposureTime(entry);
        } else if (tag == ExifTag.F_NUMBER.getId()) {
            return getFNumber(entry);
        } else if (tag == ExifTag.EXPOSURE_PROGRAM.getId()) {
            return getExposureProgram(entry);
        } else if (tag == ExifTag.METERING_MODE.getId()) {
            return getMeteringMode(entry);
        } else if (tag == ExifTag.FILE_SOURCE.getId()) {
            return getFileSource(entry.getRawValue());
        } else if (tag == ExifTag.USER_COMMENT.getId()) {
            return ExifUserComment.decode(entry.getRawValue());
        } else if (tag == ExifTag.CONTRAST.getId()) {
            return getContrast(entry);
        } else if (tag == ExifTag.FLASH.getId()) {
            return getFlash(entry.getRawValue());
        } else if (tag == ExifTag.DATE_TIME_ORIGINAL.getId()) {
            return getDateTimeOriginal(entry.getRawValue());
        } else if (tag == ExifTag.GPS_DATE_STAMP.getId()) {
            return getGpsDate(entry.getRawValue());
        } else if (tag == ExifTag.GPS_TIME_STAMP.getId()) {
            return getGpsTime(entry);
        } else if (tag == ExifTag.GPS_VERSION_ID.getId()) {
            return getGpsVersion(entry.getRawValue());
        } else if (tag == ExifTag.GPS_SATELLITES.getId()) {
            return getGpsSatellites(entry.getRawValue());
        } else if (tag == ExifTag.ISO_SPEED_RATINGS.getId()) {
            return getShortString(entry, " ISO");
        } else if (tag == ExifTag.COPYRIGHT.getId()) {
            ExifCopyright.getPhotographerCopyright(entry.getRawValue());
        } else if (tag == ExifTag.ARTIST.getId()) {
            return ExifAscii.decode(entry.getRawValue());
        } else if (tag == ExifTag.IMAGE_DESCRIPTION.getId()) {
            return ExifAscii.decode(entry.getRawValue());
        } else if (isAscii(tag)) {
            return ExifAscii.decode(entry.getRawValue());
        }
        return entry.toString().trim();
    }

    private static String getDateTimeOriginal(byte[] rawValue) {
        String value = ExifAscii.decode(rawValue);
        if (value.length() >= 18) {
            try {
                int year = Integer.parseInt(value.substring(0, 4));
                int month = Integer.parseInt(value.substring(5, 7));
                int day = Integer.parseInt(value.substring(8, 10));
                int hour = Integer.parseInt(value.substring(11, 13));
                int minute = Integer.parseInt(value.substring(14, 16));
                int second = Integer.parseInt(value.substring(17, 19));
                GregorianCalendar cal = new GregorianCalendar(
                        year, month - 1, day, hour, minute, second);
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL,
                        DateFormat.FULL);
                return df.format(cal.getTime());
            } catch (Exception ex) {
                AppLog.logWarning(ExifFieldValueFormatter.class, ex);
            }
        }
        return value;
    }

    private static String getFlash(byte[] rawValue) {
        if (rawValue != null && rawValue.length >= 1) {
            boolean[] bitsByte1 = Util.getBits(rawValue[0]);
            boolean fired = bitsByte1[0];
            boolean hasFlash = !bitsByte1[5];
            if (!hasFlash) {
                return translation.translate("FlashNone"); // NOI18N
            }
            return fired
                   ? translation.translate("FlashFired") // NOI18N
                   : translation.translate("FlashNotFired"); // NOI18N
        }
        return ExifAscii.decode(rawValue);
    }

    private static String getFocalLength(IdfEntryProxy entry) {
        if (ExifRational.isRawValueByteCountOk(entry.getRawValue())) {
            ExifRational er = new ExifRational(entry.getRawValue(), entry.
                    getByteOrder());
            DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance();
            df.applyPattern("#.# mm");
            return df.format(ExifUtil.toDouble(er));
        }
        return "?";
    }

    private static String getFocalLengthIn35mm(IdfEntryProxy entry) {
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance();
            df.applyPattern("#.# mm");
            return df.format(es.getValue());
        }
        return "?";
    }

    private static String getContrast(IdfEntryProxy entry) {
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            int value = es.getValue();
            if (exifKeyOfContrast.containsKey(value)) {
                return translation.translate(exifKeyOfContrast.get(value));
            }
        }
        return "?";
    }

    private static String getExposureProgram(IdfEntryProxy entry) {
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            int value = es.getValue();
            if (exifKeyOfExposureProgram.containsKey(value)) {
                return translation.translate(exifKeyOfExposureProgram.get(value));
            }
        }
        return "?";
    }

    private static String getExposureTime(IdfEntryProxy entry) {
        if (ExifRational.getRawValueByteCount() == entry.getRawValue().length) {
            ExifRational time = new ExifRational(entry.getRawValue(), entry.
                    getByteOrder());
            Pair<Integer, Integer> pair = getAsExposureTime(time);
            int numerator = pair.getFirst();
            int denominator = pair.getSecond();
            if (denominator > 1) {
                return Integer.toString(numerator) + " / " + Integer.toString(
                        denominator) + " s";
            } else if (numerator > 1) {
                return Integer.toString(numerator) + " s";
            }
        }
        return "?";
    }

    private static Pair<Integer, Integer> getAsExposureTime(ExifRational er) {
        int numerator = er.getNumerator();
        int denominator = er.getDenominator();
        double result = (double) numerator / (double) denominator;
        if (result < 1) {
            return new Pair<Integer, Integer>(1, (int) ((double) denominator /
                    (double) numerator + 0.5));
        } else if (result >= 1) {
            return new Pair<Integer, Integer>((int) ((double) numerator /
                    (double) denominator + 0.5), 1);
        } else {
            return new Pair<Integer, Integer>(0, 0);
        }
    }

    private static String getFNumber(IdfEntryProxy entry) {
        if (ExifRational.getRawValueByteCount() == entry.getRawValue().length) {
            ExifRational fNumer = new ExifRational(entry.getRawValue(), entry.
                    getByteOrder());
            DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance();
            df.applyPattern("#.#"); // NOI18N
            return df.format(ExifUtil.toDouble(fNumer));
        }
        return "?";
    }

    private static String getFileSource(byte[] rawValue) {
        if (rawValue.length >= 1) {
            int value = rawValue[0];
            if (value == 3) {
                return translation.translate("FileSourceDigitalCamera"); // NOI18N
            }
        }
        return "?";
    }

    private static String getGpsDate(byte[] rawValue) {
        String rawString = new String(rawValue);
        if (rawString.length() != 11)
            return rawString;
        try {
            DateFormat df = new SimpleDateFormat("yyyy:MM:dd");
            Date date = df.parse(rawString.substring(0, 10));
            return DateFormat.getDateInstance(DateFormat.FULL).format(date);
        } catch (ParseException ex) {
            AppLog.logWarning(ExifFieldValueFormatter.class, ex);
        }
        return rawString;
    }

    private static String getGpsSatellites(byte[] rawValue) {
        return ExifAscii.decode(rawValue);
    }

    private static String getGpsTime(IdfEntryProxy entry) {
        ExifMetadata.ByteOrder byteOrder = entry.getByteOrder();
        byte[] rawValue = entry.getRawValue();
        if (rawValue.length != 24)
            return new String(rawValue);
        ExifRational hours = new ExifRational(
                Arrays.copyOfRange(rawValue, 0, 8),
                byteOrder);
        ExifRational minutes = new ExifRational(
                Arrays.copyOfRange(rawValue, 8, 16),
                byteOrder);
        ExifRational seconds = new ExifRational(
                Arrays.copyOfRange(rawValue, 16, 24),
                byteOrder);
        int h = (int) ExifUtil.toLong(hours);
        int m = (int) ExifUtil.toLong(minutes);
        int s = (int) ExifUtil.toLong(seconds);
        Calendar cal = Calendar.getInstance();
        cal.set(2009, 4, 3, h, m, s);
        DateFormat df = DateFormat.getTimeInstance(DateFormat.LONG);
        return df.format(cal.getTime());
    }

    private static String getGpsVersion(byte[] rawValue) {
        assert rawValue.length == 4 : rawValue.length;
        if (rawValue.length != 4)
            return new String(rawValue);
        ExifByte first = new ExifByte(Arrays.copyOfRange(rawValue, 0, 1));
        ExifByte second = new ExifByte(Arrays.copyOfRange(rawValue, 1, 2));
        ExifByte third = new ExifByte(Arrays.copyOfRange(rawValue, 2, 3));
        ExifByte fourth = new ExifByte(Arrays.copyOfRange(rawValue, 3, 4));

        return first.getValue() +
                "." + second.getValue() +
                "." + third.getValue() +
                "." + fourth.getValue();
    }

    private static String getMeteringMode(IdfEntryProxy entry) {
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            int value = es.getValue();
            if (exifKeyOfMeteringMode.containsKey(value)) {
                return translation.translate(exifKeyOfMeteringMode.get(value));
            }
        }
        return "?";
    }

    private static String getSaturation(IdfEntryProxy entry) {
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            int value = es.getValue();
            if (exifKeyOfSaturation.containsKey(value)) {
                return translation.translate(exifKeyOfSaturation.get(value));
            }
        }
        return "?";
    }

    private static String getSharpness(IdfEntryProxy entry) {
        if (ExifShort.getRawValueByteCount() == entry.getRawValue().length) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            int value = es.getValue();
            if (exifKeyOfSharpness.containsKey(value)) {
                return translation.translate(exifKeyOfSharpness.get(value));
            }
        }
        return "?";
    }

    private static String getShortString(IdfEntryProxy entry, String postfix) {
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            return Integer.toString(es.getValue()) + postfix;
        }
        return "?" + postfix;
    }

    private static String getWhiteBalance(IdfEntryProxy entry) {
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            int value = es.getValue();
            if (exifKeyOfWhiteBalance.containsKey(value)) {
                return translation.translate(exifKeyOfWhiteBalance.get(value));
            }
        }
        return "?";
    }

    private static boolean isAscii(int tag) {
        return asciiTags.contains(tag);
    }

    private ExifFieldValueFormatter() {
    }
}
