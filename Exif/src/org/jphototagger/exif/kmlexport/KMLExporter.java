package org.jphototagger.exif.kmlexport;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.UnsupportedCharsetException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jphototagger.api.branding.AppProperties;
import org.jphototagger.exif.datatype.ExifDatatypeUtil;
import org.jphototagger.exif.tag.ExifGpsAltitude;
import org.jphototagger.exif.tag.ExifGpsDateStamp;
import org.jphototagger.exif.tag.ExifGpsLatitude;
import org.jphototagger.exif.tag.ExifGpsLongitude;
import org.jphototagger.exif.tag.ExifGpsMetadata;
import org.jphototagger.exif.tag.ExifGpsTimeStamp;
import org.jphototagger.exif.tag.ExifGpsUtil;
import org.jphototagger.kml.KMLDocument;
import org.jphototagger.kml.KMLPlacemark;
import org.jphototagger.kml.KMLPoint;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * Exports GPS metadata into an {@code KMLDocument}.
 *
 * @author Elmar Baumann
 */
public final class KMLExporter implements GPSLocationExporter {

    @Override
    public void export(Collection<? extends GPSImageInfo> gpsImageInfos, OutputStream os)
            throws IOException, UnsupportedCharsetException {
        if (gpsImageInfos == null) {
            throw new NullPointerException("gpsMetadata == null");
        }

        if (os == null) {
            throw new NullPointerException("os == null");
        }

        KMLDocument doc = new KMLDocument();

        doc.setGenerator(getGenerator());

        for (GPSImageInfo gpsImageInfo : gpsImageInfos) {
            KMLPlacemark placemark = getPlacemark(gpsImageInfo);

            if (placemark != null) {
                doc.addElement(placemark);
            }
        }

        doc.write(os);
    }

    private static String getGenerator() {
        AppProperties appProperties = Lookup.getDefault().lookup(AppProperties.class);
        return "JPhotoTagger " + appProperties.getAppVersionString() + " (http://www.jphototagger.org)";
    }

    private static KMLPlacemark getPlacemark(GPSImageInfo gpsImageInfo) {
        ExifGpsMetadata gpsMd = gpsImageInfo.getGPSMetaData();
        ExifGpsLongitude longitude = gpsMd.getLongitude();
        ExifGpsLatitude latitude = gpsMd.getLatitude();
        ExifGpsAltitude altitude = gpsMd.getAltitude();

        if ((latitude != null) && (longitude != null)) {
            double longDeg = ExifGpsUtil.convertExifDegreesToDouble(longitude.getExifDegrees());
            double latDeg = ExifGpsUtil.convertExifDegreesToDouble(latitude.getExifDegrees());
            double alt = (altitude == null)
                    ? Double.MIN_VALUE
                    : ExifDatatypeUtil.convertExifRationalToDouble(altitude.getValue());

            if (latitude.getRef().equals(ExifGpsLatitude.Ref.SOUTH)) {
                latDeg *= -1;
            }

            if (longitude.getRef().equals(ExifGpsLongitude.Ref.WEST)) {
                longDeg *= -1;
            }

            KMLPoint point = (alt >= 0)
                    ? new KMLPoint(longDeg, latDeg, alt)
                    : new KMLPoint(longDeg, latDeg);
            KMLPlacemark placemark = new KMLPlacemark(point);

            addName(placemark, gpsImageInfo);

            return placemark;
        }

        return null;
    }

    // Sets the GPS date and time as name
    private static void addName(KMLPlacemark placemark, GPSImageInfo gpsImageInfo) {
        ExifGpsMetadata gpsMd = gpsImageInfo.getGPSMetaData();
        ExifGpsDateStamp dateStamp = gpsMd.getGpsDateStamp();
        ExifGpsTimeStamp timeStamp = gpsMd.getTimeStamp();

        if ((dateStamp != null) && (timeStamp != null)) {
            try {
                Calendar cal = ExifGpsUtil.getGpsTimeFromExifGpsMetadata(gpsMd);
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG);
                String filename = GPSLocationExportUtil.getFilename(gpsImageInfo.getImageFile());
                String name = df.format(cal.getTime()) + filename;

                placemark.setName(name);
            } catch (Throwable t) {
                Logger.getLogger(KMLExporter.class.getName()).log(Level.SEVERE, null, t);
            }
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return new FileNameExtensionFilter(getDisplayName(), "kml");
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(KMLExporter.class, "KMLExporter.DisplayName");
    }

    @Override
    public String getFilenameExtension() {
        return ".kml";
    }
}
