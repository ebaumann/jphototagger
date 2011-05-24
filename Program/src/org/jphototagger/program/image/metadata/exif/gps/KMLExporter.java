package org.jphototagger.program.image.metadata.exif.gps;

import org.jphototagger.kml.KMLDocument;
import org.jphototagger.kml.KMLPlacemark;
import org.jphototagger.kml.KMLPoint;
import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.image.metadata.exif.datatype.ExifDatatypeUtil;
import org.jphototagger.program.image.metadata.exif.GPSImageInfo;
import org.jphototagger.program.image.metadata.exif.tag.ExifGpsAltitude;
import org.jphototagger.program.image.metadata.exif.tag.ExifGpsDateStamp;
import org.jphototagger.program.image.metadata.exif.tag.ExifGpsLatitude;
import org.jphototagger.program.image.metadata.exif.tag.ExifGpsLongitude;
import org.jphototagger.program.image.metadata.exif.tag.ExifGpsMetadata;
import org.jphototagger.program.image.metadata.exif.tag.ExifGpsTimeStamp;
import org.jphototagger.program.image.metadata.exif.tag.ExifGpsUtil;
import org.jphototagger.program.resource.JptBundle;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.UnsupportedCharsetException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Exports GPS metadata into an {@link KMLDocument}.
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
        return AppInfo.APP_NAME + " " + AppInfo.APP_VERSION + " (http://www.jphototagger.org)";
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
            } catch (Exception ex) {
                AppLogger.logSevere(ExifGpsUtil.class, ex);
            }
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return new FileNameExtensionFilter(getDisplayName(), "kml");
    }

    @Override
    public String getDisplayName() {
        return JptBundle.INSTANCE.getString("KMLExporter.DisplayName");
    }

    @Override
    public String getFilenameExtension() {
        return ".kml";
    }
}
