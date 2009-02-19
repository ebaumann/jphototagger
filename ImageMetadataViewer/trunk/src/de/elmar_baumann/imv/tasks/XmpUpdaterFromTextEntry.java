package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.data.TextEntry;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.tasks.InsertImageFilesIntoDatabase.Insert;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * Aktualisiert XMP-Daten in den Filialdateien und in der Datenbank.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class XmpUpdaterFromTextEntry implements Runnable {

    private final List<TextEntry> textEntries;
    private final List<String> filenames;
    private final EnumSet<XmpMetadata.UpdateOption> writeOptions;
    private final List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();
    private boolean stop = false;

    /**
     * Konstruktor.
     * 
     * @param filenames     Zu aktualisierende Dateien
     * @param textEntries   In alle Dateien zu schreibende Einträge
     * @param writeOptions  Optionen
     */
    public XmpUpdaterFromTextEntry(List<String> filenames, List<TextEntry> textEntries,
        EnumSet<XmpMetadata.UpdateOption> writeOptions) {
        this.filenames = filenames;
        this.textEntries = textEntries;
        this.writeOptions = writeOptions;
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
     * Beendet die Arbeit.
     */
    public void stop() {
        stop = true;
    }

    @Override
    public void run() {
        notifyProgressStarted();
        int count = filenames.size();
        for (int i = 0; !stop && i < count; i++) {
            String filename = filenames.get(i);
            String sidecarFilename = XmpMetadata.suggestSidecarFilename(filename);
            if (XmpMetadata.writeMetadataToSidecarFile(
                    sidecarFilename, textEntries, writeOptions)) {
                updateDatabase(filename);
            }
            notifyProgressPerformed(i + 1, filename);
        }
        notifyProgressEnded();
    }

    private void updateDatabase(String filename) {
        InsertImageFilesIntoDatabase updater = new InsertImageFilesIntoDatabase(
                Arrays.asList(filename), EnumSet.of(Insert.XMP));
        updater.run();
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
