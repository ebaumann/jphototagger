package org.jphototagger.program.image.metadata.exif.gps;

import org.jphototagger.lib.dialog.FileChooserExt;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.PropertiesUtil;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.helper.HelperThread;
import org.jphototagger.program.image.metadata.exif.ExifMetadata;
import org.jphototagger.program.image.metadata.exif.ExifTags;
import org.jphototagger.program.image.metadata.exif.GPSImageInfo;
import org.jphototagger.program.image.metadata.exif.tag.ExifGpsMetadata;
import org.jphototagger.program.image.metadata.exif.tag.ExifGpsUtil;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.tasks.UserTasks;
import org.jphototagger.program.UserSettings;

import java.io.File;
import java.io.FileOutputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.swing.JFileChooser;

/**
 * Utils for exporting GPS metadata.
 *
 * @author Elmar Baumann
 */
public final class GPSLocationExportUtil {
    private static final String KEY_CURRENT_DIR = "GPSLocationExportUtil.CurrentDir";

    /**
     * Returns the filename depending on
     * {@link UserSettings#isAddFilenameToGpsLocationExport()}.
     *
     * @param  file file
     * @return      filename in the format <code>" [name]"</code> or empty
     *              string if no filename is to export
     */
    static String getFilename(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (UserSettings.INSTANCE.isAddFilenameToGpsLocationExport()) {
            return " [" + file.getName() + "]";
        }

        return "";
    }

    private static class Exporter extends HelperThread {
        private volatile boolean cancel;
        private final GPSLocationExporter exporter;
        private final List<? extends File> imageFiles;

        Exporter(GPSLocationExporter exporter, Collection<? extends File> imageFiles) {
            super("JPhotoTagger: Exporting GPS locations");
            this.exporter = exporter;
            this.imageFiles = new ArrayList<File>(imageFiles);
            setInfo(JptBundle.INSTANCE.getString("GPSLocationExportUtil.Exporter.Info"));
        }

        @Override
        public void cancel() {
            cancel = true;
        }

        @Override
        public void run() {
            int fileCount = imageFiles.size();

            progressStarted(0, 0, fileCount, (fileCount > 0)
                                             ? imageFiles.get(0)
                                             : null);

            List<GPSImageInfo> imageInfos = new ArrayList<GPSImageInfo>(fileCount);

            for (int i = 0; !cancel &&!isInterrupted() && (i < fileCount); i++) {
                File imageFile = imageFiles.get(i);
                ExifTags et = ExifMetadata.getExifTags(imageFile);

                if (et != null) {
                    ExifGpsMetadata gpsMetadata = ExifGpsUtil.gpsMetadata(et);

                    imageInfos.add(new GPSImageInfo(imageFile, gpsMetadata));
                }

                progressPerformed(i + 1, imageFile);
            }

            export(exporter, imageInfos);
            progressEnded(null);
        }
    }


    /**
     * Exports GPS metadata in image files into a file.
     *
     * @param exporter   exporter for a specific file format
     * @param imageFiles image files with EXIF metadata containing GPS
     *                   information
     */
    public static void export(GPSLocationExporter exporter, Collection<? extends File> imageFiles) {
        if (exporter == null) {
            throw new NullPointerException("exporter == null");
        }

        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        UserTasks.INSTANCE.add(new Exporter(exporter, imageFiles));
    }

    private static void export(GPSLocationExporter exporter, List<GPSImageInfo> gpsImageInfos) {
        File exportFile = getFile(exporter);

        if (exportFile != null) {
            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(exportFile);
                exporter.export(gpsImageInfos, fos);
                fos.flush();
            } catch (Exception ex) {
                AppLogger.logSevere(GPSLocationExportUtil.class, ex);
                MessageDisplayer.error(null, "GPSLocationExportUtil.Error.Export", exportFile);
            } finally {
                FileUtil.close(fos);
            }
        }
    }

    private static File getFile(GPSLocationExporter exporter) {
        FileChooserExt fileChooser = new FileChooserExt(getCurrentDir());

        fileChooser.setFileFilter(exporter.getFileFilter());
        fileChooser.setConfirmOverwrite(true);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setSaveFilenameExtension(exporter.getFilenameExtension());

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            setCurrentDir(file);

            return file;
        }

        return null;
    }

    private static synchronized File getCurrentDir() {
        Properties properties = UserSettings.INSTANCE.getProperties();

        return new File(properties.getProperty(KEY_CURRENT_DIR, ""));
    }

    private static synchronized void setCurrentDir(File dir) {
        Properties properties = UserSettings.INSTANCE.getProperties();

        if (PropertiesUtil.setDirectory(properties, KEY_CURRENT_DIR, dir)) {
            UserSettings.INSTANCE.writeToFile();
        }
    }

    private GPSLocationExportUtil() {}
}
