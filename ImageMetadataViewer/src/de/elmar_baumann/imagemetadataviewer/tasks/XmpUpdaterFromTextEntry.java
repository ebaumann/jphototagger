package de.elmar_baumann.imagemetadataviewer.tasks;

import de.elmar_baumann.imagemetadataviewer.data.TextEntry;
import de.elmar_baumann.imagemetadataviewer.event.ProgressEvent;
import de.elmar_baumann.imagemetadataviewer.event.ProgressListener;
import de.elmar_baumann.imagemetadataviewer.image.metadata.xmp.XmpMetadata;
import java.util.ArrayList;
import java.util.List;

/**
 * Aktualisiert XMP-Daten in den Filialdateien und in der Datenbank.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class XmpUpdaterFromTextEntry implements Runnable {

    private List<TextEntry> textEntries;
    private List<String> filenames;
    private boolean deleteEmpty;
    private boolean append;
    private boolean stop = false;
    private List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();

    /**
     * Konstruktor.
     * 
     * @param filenames     Zu aktualisierende Dateien
     * @param textEntries   In alle Dateien zu schreibende Einträge
     * @param deleteEmpty   true, wenn in einer existierenden XMP-Datei
     *                      Einträge gelöscht werden sollen, wenn das
     *                      zugehörige Textfeld leer ist
     * @param append        true, wenn existierende Einträge um nicht
     *                      existierende ergänzt werden sollen und nicht
     *                      gelöscht
     */
    public XmpUpdaterFromTextEntry(List<String> filenames, List<TextEntry> textEntries,
        boolean deleteEmpty, boolean append) {
        this.filenames = filenames;
        this.textEntries = textEntries;
        this.deleteEmpty = deleteEmpty;
        this.append = append;
    }

    /**
     * Fügt einen Fortschrittsbeobachter hinzu.
     * 
     * @param listener Beobachter
     */
    public void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    /**
     * Entfernt einen Fortschrittsbeobachter.
     * 
     * @param listener Beobachter
     */
    public void removeProgressListener(ProgressListener listener) {
        progressListeners.remove(listener);
    }

    /**
     * Unterbricht die Arbeit.
     */
    public void stop() {
        stop = true;
    }

    @Override
    public void run() {
        notifyProgressStarted();
        XmpMetadata meta = new XmpMetadata();
        int count = filenames.size();
        for (int i = 0; !stop && i < count; i++) {
            String filename = filenames.get(i);
            String sidecarFilename = XmpMetadata.suggestSidecarFilename(filename);
            if (meta.writeMetaDataToSidecarFile(sidecarFilename, textEntries,
                deleteEmpty, append)) {
                updateDatabase(sidecarFilename);
            }
            notifyProgressPerformed(i + 1, filename);
        }
        notifyProgressEnded();
    }

    private void updateDatabase(String sidecarFilename) {
        List<String> fNames = new ArrayList<String>();
        fNames.add(getArbitraryImageFilename(sidecarFilename));
        ImageMetadataToDatabase updater = new ImageMetadataToDatabase(fNames, 0);
        updater.setCreateThumbnails(false);
        updater.setReadExif(false);
        updater.setForceUpdate(true);
        updater.run();
    }

    private String getArbitraryImageFilename(String sidecarFilename) {
        if (sidecarFilename.toLowerCase().endsWith(".xmp")) { // NOI18N
            return sidecarFilename.substring(0, sidecarFilename.length() - 4) + ".jpg"; // NOI18N
        }
        return sidecarFilename;
    }

    private void notifyProgressStarted() {
        for (ProgressListener listener : progressListeners) {
            listener.progressStarted(getProgressEvent(0, "")); // NOI18N
        }
    }

    private void notifyProgressPerformed(int value, String filename) {
        for (ProgressListener listener : progressListeners) {
            ProgressEvent event = getProgressEvent(value, filename);
            listener.progressPerformed(event);
            if (event.isStop()) {
                stop();
            }
        }
    }

    private void notifyProgressEnded() {
        for (ProgressListener listener : progressListeners) {
            listener.progressEnded(getProgressEvent(filenames.size(), "")); // NOI18N
        }
    }

    private ProgressEvent getProgressEvent(int value, Object info) {
        int taskCount = filenames.size();
        ProgressEvent evt = new ProgressEvent(this, 0, taskCount, value, info);
        return evt;
    }
}
