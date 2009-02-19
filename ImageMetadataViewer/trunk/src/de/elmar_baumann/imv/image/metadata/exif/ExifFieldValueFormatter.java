package de.elmar_baumann.imv.image.metadata.exif;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Translation;
import de.elmar_baumann.lib.lang.Util;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

/**
 * Formatiert EXIF-Werte.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/31
 */
public final class ExifFieldValueFormatter {

    private static final Translation translation = new Translation("ExifFieldValueTranslations"); // NOI18N

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
        } else if (tag == ExifTag.FOCAL_LENGTH.getId()
            || tag == ExifTag.FOCAL_LENGTH_IN_35_MM_FILM.getId()) {
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
                GregorianCalendar calendar = new GregorianCalendar(
                    year, month - 1, day, hour, minute, second);
                SimpleDateFormat dateFormat = new SimpleDateFormat(Bundle.getString("ExifFieldValueFormatter.DateTimeDigitized.Format"));
                StringBuffer buffer = new StringBuffer();
                dateFormat.format(calendar.getTime(), buffer, new FieldPosition(0));
                return buffer.toString();
            } catch (NumberFormatException ex) {
                de.elmar_baumann.imv.app.AppLog.logWarning(ExifFieldValueFormatter.class, ex);
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
                de.elmar_baumann.imv.app.AppLog.logWarning(ExifFieldValueFormatter.class, ex);
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
        if (value.equals("0")) { // NOI18N
            return translation.translate("ContrastNormal"); // NOI18N
        } else if (value.equals("1")) { // NOI18N
            return translation.translate("ContrastLow"); // NOI18N
        } else if (value.equals("2")) { // NOI18N
            return translation.translate("ContrastHigh"); // NOI18N
        }
        return value;
    }

    private static String getExposureProgram(String value) {
        if (value.equals("0")) { // NOI18N
            return translation.translate("ExposureProgramUnkonwn"); // NOI18N
        } else if (value.equals("1")) { // NOI18N
            return translation.translate("ExposureProgramManual"); // NOI18N
        } else if (value.equals("2")) { // NOI18N
            return translation.translate("ExposureProgramNormalProgram"); // NOI18N
        } else if (value.equals("3")) { // NOI18N
            return translation.translate("ExposureProgramAperturePriority"); // NOI18N
        } else if (value.equals("4")) { // NOI18N
            return translation.translate("ExposureProgramTimePriority"); // NOI18N
        } else if (value.equals("5")) { // NOI18N
            return translation.translate("ExposureProgramCreativ"); // NOI18N
        } else if (value.equals("6")) { // NOI18N
            return translation.translate("ExposureProgramAction"); // NOI18N
        } else if (value.equals("7")) { // NOI18N
            return translation.translate("ExposureProgramPortrait"); // NOI18N
        } else if (value.equals("8")) { // NOI18N
            return translation.translate("ExposureProgramLandscape"); // NOI18N
        }
        return value;
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
                de.elmar_baumann.imv.app.AppLog.logWarning(ExifFieldValueFormatter.class, ex);
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
                de.elmar_baumann.imv.app.AppLog.logWarning(ExifFieldValueFormatter.class, ex);
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

    private static String getMeteringMode(String value) {
        if (value.equals("0")) { // NOI18N
            return translation.translate("MeteringModeUnknown"); // NOI18N
        } else if (value.equals("1")) { // NOI18N
            return translation.translate("MeteringModeIntegral"); // NOI18N
        } else if (value.equals("2")) { // NOI18N
            return translation.translate("MeteringModeIntegralCenter"); // NOI18N
        } else if (value.equals("3")) { // NOI18N
            return translation.translate("MeteringModeSpot"); // NOI18N
        } else if (value.equals("4")) { // NOI18N
            return translation.translate("MeteringModeMultiSpot"); // NOI18N
        } else if (value.equals("5")) { // NOI18N
            return translation.translate("MeteringModeMatrix"); // NOI18N
        } else if (value.equals("6")) { // NOI18N
            return translation.translate("MeteringModeSelective"); // NOI18N
        }
        return value;
    }

    private static String getSaturation(String value) {
        if (value.equals("0")) { // NOI18N
            return translation.translate("SaturationNormal"); // NOI18N
        } else if (value.equals("1")) { // NOI18N
            return translation.translate("SaturationLow"); // NOI18N
        } else if (value.equals("2")) { // NOI18N
            return translation.translate("SaturationHigh"); // NOI18N
        }
        return value;
    }

    private static String getSharpness(String value) {
        if (value.equals("0")) { // NOI18N
            return translation.translate("SaturationNormal"); // NOI18N
        } else if (value.equals("1")) { // NOI18N
            return translation.translate("SaturationLow"); // NOI18N
        } else if (value.equals("2")) { // NOI18N
            return translation.translate("SaturationHigh"); // NOI18N
        }
        return value;
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
        if (value.equals("0")) { // NOI18N
            return translation.translate("WhiteBalanceAutomatic"); // NOI18N
        } else if (value.equals("1")) { // NOI18N
            return translation.translate("WhiteBalanceManual"); // NOI18N
        }
        return value;
    }

    private ExifFieldValueFormatter() {}
}
