package de.elmar_baumann.imv.image.metadata.exif;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.resource.Translation;
import de.elmar_baumann.lib.lang.Util;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Formatiert EXIF-Werte.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/31
 */
public final class ExifFieldValueFormatter {

    private static final Translation translation = new Translation("ExifFieldValueTranslations"); // NOI18N
    private static final Map<String, String> exifKeyOfExposureProgram = new HashMap<String, String>();
    private static final Map<String, String> exifKeyOfContrast = new HashMap<String, String>();
    private static final Map<String, String> exifKeyOfMeteringMode = new HashMap<String, String>();
    private static final Map<String, String> exifKeyOfSaturation = new HashMap<String, String>();
    private static final Map<String, String> exifKeyOfSharpness = new HashMap<String, String>();
    private static final Map<String, String> exifKeyOfWhiteBalance = new HashMap<String, String>();


    static {
        exifKeyOfExposureProgram.put("0", "ExposureProgramUnkonwn"); // NOI18N
        exifKeyOfExposureProgram.put("1", "ExposureProgramManual"); // NOI18N
        exifKeyOfExposureProgram.put("2", "ExposureProgramNormalProgram"); // NOI18N
        exifKeyOfExposureProgram.put("3", "ExposureProgramAperturePriority"); // NOI18N
        exifKeyOfExposureProgram.put("4", "ExposureProgramTimePriority"); // NOI18N
        exifKeyOfExposureProgram.put("5", "ExposureProgramCreativ"); // NOI18N
        exifKeyOfExposureProgram.put("6", "ExposureProgramAction"); // NOI18N
        exifKeyOfExposureProgram.put("7", "ExposureProgramPortrait"); // NOI18N
        exifKeyOfExposureProgram.put("8", "ExposureProgramLandscape"); // NOI18N

        exifKeyOfContrast.put("0", "ContrastNormal"); // NOI18N
        exifKeyOfContrast.put("1", "ContrastLow"); // NOI18N
        exifKeyOfContrast.put("2", "ContrastHigh"); // NOI18N

        exifKeyOfMeteringMode.put("0", "MeteringModeUnknown"); // NOI18N
        exifKeyOfMeteringMode.put("1", "MeteringModeIntegral"); // NOI18N
        exifKeyOfMeteringMode.put("2", "MeteringModeIntegralCenter"); // NOI18N
        exifKeyOfMeteringMode.put("3", "MeteringModeSpot"); // NOI18N
        exifKeyOfMeteringMode.put("4", "MeteringModeMultiSpot"); // NOI18N
        exifKeyOfMeteringMode.put("5", "MeteringModeMatrix"); // NOI18N
        exifKeyOfMeteringMode.put("6", "MeteringModeSelective"); // NOI18N

        exifKeyOfSaturation.put("0", "SaturationNormal"); // NOI18N
        exifKeyOfSaturation.put("1", "SaturationLow"); // NOI18N
        exifKeyOfSaturation.put("2", "SaturationHigh"); // NOI18N

        exifKeyOfSharpness.put("0", "SaturationNormal"); // NOI18N
        exifKeyOfSharpness.put("1", "SaturationLow"); // NOI18N
        exifKeyOfSharpness.put("2", "SaturationHigh"); // NOI18N

        exifKeyOfWhiteBalance.put("0", "WhiteBalanceAutomatic"); // NOI18N
        exifKeyOfWhiteBalance.put("1", "WhiteBalanceManual"); // NOI18N
    }

    /**
     * Formatiert einen Entry.
     * 
     * @param  entry Entry
     * @return       Formatierter Wert
     */
    public static String format(IdfEntryProxy entry) {
        String value = entry.toString().trim();
        int tag = entry.getTag();
        if (tag == ExifTag.SHARPNESS.getId()) {
            return getSharpness(value);
        } else if (tag == ExifTag.SATURATION.getId()) {
            return getSaturation(value);
        } else if (tag == ExifTag.WHITE_BALANCE.getId()) {
            return getWhiteBalance(value);
        } else if (tag == ExifTag.FOCAL_LENGTH.getId() || tag == ExifTag.FOCAL_LENGTH_IN_35_MM_FILM.getId()) {
            return getFocalLength(value);
        } else if (tag == ExifTag.EXPOSURE_TIME.getId()) {
            return getExposureTime(value);
        } else if (tag == ExifTag.F_NUMBER.getId()) {
            return getFNumber(value);
        } else if (tag == ExifTag.EXPOSURE_PROGRAM.getId()) {
            return getExposureProgram(value);
        } else if (tag == ExifTag.METERING_MODE.getId()) {
            return getMeteringMode(value);
        } else if (tag == ExifTag.FILE_SOURCE.getId()) {
            return getFileSource(value);
        } else if (tag == ExifTag.USER_COMMENT.getId()) {
            return getUserComment(value);
        } else if (tag == ExifTag.CONTRAST.getId()) {
            return getContrast(value);
        } else if (tag == ExifTag.FLASH.getId()) {
            return getFlash(entry);
        } else if (tag == ExifTag.DATE_TIME_ORIGINAL.getId()) {
            return getDateTimeOriginal(value);
        } else if (tag == ExifTag.GPS_DATE_STAMP.getId()) {
            return getGpsDate(entry.getRawValue());
        } else if (tag == ExifTag.GPS_TIME_STAMP.getId()) {
            return getGpsTime(entry);
        } else if (tag == ExifTag.GPS_VERSION_ID.getId()) {
            return getGpsVersion(entry.getRawValue());
        } else if (tag == ExifTag.GPS_SATELLITES.getId()) {
            return getGpsSatellites(entry.getRawValue());
        }

        return value;
    }

    private static String getDateTimeOriginal(String value) {
        String string = value.trim();
        if (string.length() >= 18) {
            try {
                int year = Integer.parseInt(string.substring(0, 4));
                int month = Integer.parseInt(string.substring(5, 7));
                int day = Integer.parseInt(string.substring(8, 10));
                int hour = Integer.parseInt(string.substring(11, 13));
                int minute = Integer.parseInt(string.substring(14, 16));
                int second = Integer.parseInt(string.substring(17, 19));
                GregorianCalendar cal = new GregorianCalendar(
                    year, month - 1, day, hour, minute, second);
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
                return df.format(cal.getTime());
            } catch (Exception ex) {
                AppLog.logWarning(ExifFieldValueFormatter.class, ex);
            }
        }
        return string;
    }

    private static String getFlash(IdfEntryProxy entry) {
        byte[] rawValue = entry.getRawValue();
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
        return entry.toString().trim();
    }

    private static String getFocalLength(String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, "/"); // NOI18N
        if (tokenizer.countTokens() == 2) {
            String z = tokenizer.nextToken();
            String n = tokenizer.nextToken();
            try {
                Double numerator = new Double(z);
                Double denominator = new Double(n);
                if (denominator != 0) {
                    return getFraction(numerator, denominator);
                }
            } catch (NumberFormatException ex) {
                AppLog.logWarning(ExifFieldValueFormatter.class, ex);
            }
        }
        return value;
    }

    private static String getFraction(Double numerator, Double denominator) {
        StringBuffer buffer = new StringBuffer();
        DecimalFormat format = new DecimalFormat();
        format.format(numerator / denominator, buffer, new FieldPosition(0));
        return buffer.toString();
    }

    private static String getContrast(String value) {
        if (exifKeyOfContrast.containsKey(value)) {
            return translation.translate(exifKeyOfContrast.get(value));
        } else {
            return value;
        }
    }

    private static String getExposureProgram(String value) {
        if (exifKeyOfExposureProgram.containsKey(value)) {
            return translation.translate(exifKeyOfExposureProgram.get(value));
        } else {
            return value;
        }
    }

    private static String getExposureTime(String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, "/"); // NOI18N
        if (tokenizer.countTokens() == 2) {
            String z = tokenizer.nextToken();
            String n = tokenizer.nextToken();
            try {
                Double numerator = new Double(z);
                Double denominator = new Double(n);
                if (denominator != 0 && numerator % 10 == 0) {
                    return Integer.toString((int) (numerator / 10)) + "/" + // NOI18N
                        Integer.toString((int) (denominator / 10));
                }
            } catch (NumberFormatException ex) {
                AppLog.logWarning(ExifFieldValueFormatter.class, ex);
            }
        }
        return value;
    }

    private static String getFNumber(String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, "/"); // NOI18N
        if (tokenizer.countTokens() == 2) {
            String z = tokenizer.nextToken();
            String n = tokenizer.nextToken();
            try {
                Double numerator = new Double(z);
                Double denominator = new Double(n);
                if (denominator != 0) {
                    return getFraction(numerator, denominator);
                }
            } catch (NumberFormatException ex) {
                AppLog.logWarning(ExifFieldValueFormatter.class, ex);
            }
        }
        return value;
    }

    private static String getFileSource(String value) {
        if (value.equals("3") || value.startsWith("DSC")) { // NOI18N
            return translation.translate("FileSourceDigitalCamera"); // NOI18N
        }
        return value;
    }

    private static String getGpsDate(byte[] rawValue) {
        String rawString = new String(rawValue);
        if (rawString.length() != 11)
            return rawString;
        try {
            DateFormat df = new SimpleDateFormat("yyyy:MM:dd");
            Date date = df.parse(rawString.substring(0, 10));
            return DateFormat.getDateInstance(DateFormat.LONG).format(date);
        } catch (ParseException ex) {
            AppLog.logWarning(ExifFieldValueFormatter.class, ex);
        }
        return rawString;
    }

    private static String getGpsSatellites(byte[] rawValue) {
        return new ExifAscii(rawValue).getValue();
    }

    private static String getGpsTime(IdfEntryProxy entry) {
        ExifMetadata.ByteOrder byteOrder = entry.getByteOrder();
        byte[] rawValue = entry.getRawValue();
        if (rawValue.length != 24)
            return new String(rawValue);
        ExifRational hours = new ExifRational(
            Arrays.copyOfRange(rawValue, 0, 4),
            Arrays.copyOfRange(rawValue, 4, 8),
            byteOrder);
        ExifRational minutes = new ExifRational(
            Arrays.copyOfRange(rawValue, 8, 12),
            Arrays.copyOfRange(rawValue, 12, 16),
            byteOrder);
        ExifRational seconds = new ExifRational(
            Arrays.copyOfRange(rawValue, 16, 20),
            Arrays.copyOfRange(rawValue, 20, 24),
            byteOrder);
        int h = (int) ExifGpsUtil.toLong(hours);
        int m = (int) ExifGpsUtil.toLong(minutes);
        int s = (int) ExifGpsUtil.toLong(seconds);
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

        return new String(first.getValue() +
            "." + second.getValue() +
            "." + third.getValue() +
            "." + fourth.getValue());
    }

    private static String getMeteringMode(String value) {
        if (exifKeyOfMeteringMode.containsKey(value)) {
            return translation.translate(exifKeyOfMeteringMode.get(value));
        } else {
            return value;
        }
    }

    private static String getSaturation(String value) {
        if (exifKeyOfSaturation.containsKey(value)) {
            return translation.translate(exifKeyOfSaturation.get(value));
        } else {
            return value;
        }
    }

    private static String getSharpness(String value) {
        if (exifKeyOfSharpness.containsKey(value)) {
            return translation.translate(exifKeyOfSharpness.get(value));
        } else {
            return value;
        }
    }

    private static String getUserComment(String value) {
        if (value.startsWith("ASCII")) { // NOI18N
            return value.substring(5).trim();
        } else if (value.startsWith("Unicode")) { // NOI18N
            return value.substring(7).trim();
        } else if (value.startsWith("JIS")) { // NOI18N
            return value.substring(3).trim();
        } else if (value.startsWith("Undefined")) { // NOI18N
            return value.substring(9).trim();
        }
        return value;
    }

    private static String getWhiteBalance(String value) {
        if (exifKeyOfWhiteBalance.containsKey(value)) {
            return translation.translate(exifKeyOfWhiteBalance.get(value));
        } else {
            return value;
        }
    }

    private ExifFieldValueFormatter() {
    }
}
