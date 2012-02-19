package org.jphototagger.exif.kmlexport;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

import org.bushe.swing.event.EventBus;

import org.openide.util.Lookup;

import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.concurrent.SerialTaskExecutor;
import org.jphototagger.api.plugin.fileprocessor.FileProcessingFinishedEvent;
import org.jphototagger.api.plugin.fileprocessor.FileProcessingStartedEvent;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.progress.MainWindowProgressBarProvider;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.exif.ExifMetadata;
import org.jphototagger.exif.ExifPreferencesKeys;
import org.jphototagger.exif.ExifTags;
import org.jphototagger.exif.tag.ExifGpsMetadata;
import org.jphototagger.exif.tag.ExifGpsUtil;
import org.jphototagger.lib.io.IoUtil;
import org.jphototagger.lib.swing.FileChooserExt;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StorageUtil;

/**
 * @author Elmar Baumann
 */
public final class GPSLocationExportUtil {

    private static final String KEY_CURRENT_DIR = "GPSLocationExportUtil.CurrentDir";

    /**
     * Returns the filename depending on
     * {@code UserSettings#isAddFilenameToGpsLocationExport()}.
     *
     * @param  file file
     * @return      filename in the format <code>" [name]"</code> or empty
     *              string if no filename is to export
     */
    static String getFilename(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (isAddFilenameToGpsLocationExport()) {
            return " [" + file.getName() + "]";
        }

        return "";
    }

    private static boolean isAddFilenameToGpsLocationExport() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(ExifPreferencesKeys.KEY_GPS_ADD_FILENAME_TO_GPS_LOCATION_EXPORT)
                ? prefs.getBoolean(ExifPreferencesKeys.KEY_GPS_ADD_FILENAME_TO_GPS_LOCATION_EXPORT)
                : false;
    }

    private static class Exporter extends Thread implements Cancelable {

        private volatile boolean cancel;
        private final GPSLocationExporter exporter;
        private final List<? extends File> imageFiles;
        private static final String PROGRESS_BAR_STRING = Bundle.getString(GPSLocationExportUtil.class, "GPSLocationExportUtil.Exporter.Info");
        private final MainWindowProgressBarProvider progressBarProvider = Lookup.getDefault().lookup(MainWindowProgressBarProvider.class);

        Exporter(GPSLocationExporter exporter, Collection<? extends File> imageFiles) {
            super("JPhotoTagger: Exporting GPS locations");
            this.exporter = exporter;
            this.imageFiles = new ArrayList<File>(imageFiles);
        }

        @Override
        public void cancel() {
            cancel = true;
        }

        @Override
        public void run() {
            int fileCount = imageFiles.size();

            EventBus.publish(new FileProcessingStartedEvent(this));
            progressBarProvider.progressStarted(createStartProgressEvent(fileCount));

            List<GPSImageInfo> imageInfos = new ArrayList<GPSImageInfo>(fileCount);

            for (int i = 0; !cancel && !isInterrupted() && (i < fileCount); i++) {
                File imageFile = imageFiles.get(i);
                ExifTags et = ExifMetadata.getExifTagsPreferCached(imageFile);

                if (et != null) {
                    ExifGpsMetadata gpsMetadata = ExifGpsUtil.createGpsMetadataFromExifTags(et);

                    imageInfos.add(new GPSImageInfo(imageFile, gpsMetadata));
                }

                progressBarProvider.progressPerformed(createPerformedProgressEvent(fileCount, i + 1));
            }

            export(exporter, imageInfos);
            createFinishedProgressEvent(fileCount, fileCount);
        }

        private ProgressEvent createStartProgressEvent(int maximum) {
            return new ProgressEvent.Builder()
                    .source(this)
                    .minimum(0)
                    .maximum(maximum)
                    .value(0)
                    .stringPainted(true)
                    .stringToPaint(PROGRESS_BAR_STRING)
                    .build();
        }

        private ProgressEvent createPerformedProgressEvent(int maximum, int value) {
            return new ProgressEvent.Builder()
                    .source(this)
                    .minimum(0)
                    .maximum(maximum)
                    .value(value)
                    .stringPainted(true)
                    .stringToPaint(PROGRESS_BAR_STRING)
                    .build();
        }

        private void createFinishedProgressEvent(int countOfImagesToUpload, int countOfUploadedImages) {
            progressBarProvider.progressEnded(this);
            EventBus.publish(new FileProcessingFinishedEvent(this, true));
        }
    }

    /**
     * Exports GPS metadata in image files into a file.
     *
     * @param gpsExporter   exporter for a specific file format
     * @param imageFiles image files with EXIF metadata containing GPS information
     */
    static void export(GPSLocationExporter gpsExporter, Collection<? extends File> imageFiles) {
        SerialTaskExecutor executor = Lookup.getDefault().lookup(SerialTaskExecutor.class);
        Exporter exporter = new Exporter(gpsExporter, imageFiles);

        executor.addTask(exporter);
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
                Logger.getLogger(GPSLocationExportUtil.class.getName()).log(Level.SEVERE, null, ex);
                String message = Bundle.getString(GPSLocationExportUtil.class, "GPSLocationExportUtil.Error.Export", exportFile);
                MessageDisplayer.error(null, message);
            } finally {
                IoUtil.close(fos);
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
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        String path = prefs.getString(KEY_CURRENT_DIR);

        return new File(path == null ? "" : path);
    }

    private static synchronized void setCurrentDir(File dir) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        StorageUtil.setDirectory(prefs, KEY_CURRENT_DIR, dir);
    }

    private GPSLocationExportUtil() {
    }
}
