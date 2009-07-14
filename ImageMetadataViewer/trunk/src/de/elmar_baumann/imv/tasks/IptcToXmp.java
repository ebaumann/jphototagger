package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.Iptc;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.listener.ProgressListener;
import de.elmar_baumann.imv.image.metadata.iptc.IptcMetadata;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * Erzeugt XMP-Daten anhand bestehender IPTC-Daten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class IptcToXmp implements Runnable {

    private final List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();
    private final List<String> filenames;
    private boolean stop = false;

    public IptcToXmp(List<String> filenames) {
        this.filenames = filenames;
    }

    public synchronized void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    public void stop() {
        stop = true;
    }

    @Override
    public void run() {
        notifyStart();
        int size = filenames.size();
        int index = 0;
        for (index = 0; !stop && index < size; index++) {
            String imageFilename = filenames.get(index);
            String xmpFilename = XmpMetadata.suggestSidecarFilenameForImageFile(imageFilename);
            Iptc iptc = IptcMetadata.getIptc(new File(imageFilename));
            Xmp xmp = XmpMetadata.getXmpOfImageFile(imageFilename);
            if (xmp == null) {
                xmp = new Xmp();
            }
            xmp.setIptc(iptc, Xmp.SetIptc.DONT_CHANGE_EXISTING_VALUES);
            logWriteXmpFile(imageFilename);
            if (XmpMetadata.writeMetadataToSidecarFile(xmpFilename, xmp)) {
                updateDatabase(imageFilename);
            }
            notifyPerformed(index);
        }
        notifyEnd(index);
    }

    private void updateDatabase(String imageFilename) {
        InsertImageFilesIntoDatabase insert = new InsertImageFilesIntoDatabase(
                Arrays.asList(imageFilename),
                EnumSet.of(InsertImageFilesIntoDatabase.Insert.XMP));
        insert.run(); // Shall run in this thread!
    }

    private void checkStopEvent(ProgressEvent event) {
        if (event.isStop()) {
            stop();
        }
    }

    private void logWriteXmpFile(String imageFilename) {
        AppLog.logInfo(IptcToXmp.class, Bundle.getString(
                "IptcToXmp.Info.StartWriteXmpFile", imageFilename)); // NOI18N
    }

    private synchronized void notifyStart() {
        int count = filenames.size();
        ProgressEvent event = new ProgressEvent(this, 0, count, 0,
                filenames.size() > 0 ? filenames.get(0) : ""); // NOI18N
        for (ProgressListener progressListener : progressListeners) {
            progressListener.progressStarted(event);
            checkStopEvent(event);
        }
    }

    private synchronized void notifyPerformed(int index) {
        ProgressEvent event = new ProgressEvent(this, 0, filenames.size(), index + 1, filenames.get(index));
        for (ProgressListener progressListener : progressListeners) {
            progressListener.progressPerformed(event);
            checkStopEvent(event);
        }
    }

    private synchronized void notifyEnd(int index) {
        ProgressEvent event = new ProgressEvent(this, 0, filenames.size(), index + 1, ""); // NOI18N
        for (ProgressListener progressListener : progressListeners) {
            progressListener.progressEnded(event);
        }
    }
}
