package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.Log;
import de.elmar_baumann.imv.data.Iptc;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.image.metadata.iptc.IptcMetadata;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
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
    volatile private boolean stop = false;

    public IptcToXmp(List<String> filenames) {
        this.filenames = filenames;
    }

    public void addProgressListener(ProgressListener listener) {
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
            String xmpFilename = XmpMetadata.suggestSidecarFilename(imageFilename);
            Iptc iptc = IptcMetadata.getIptc(new File(imageFilename));
            Xmp xmp = XmpMetadata.getXmp(imageFilename);
            if (xmp == null) {
                xmp = new Xmp();
            }
            xmp.setIptc(iptc, Xmp.SetIptc.DONT_CHANGE_EXISTING_VALUES);
            logWriteXmpFile(imageFilename);
            XmpMetadata.writeMetadataToSidecarFile(xmpFilename, xmp);
            notifyPerformed(index);
        }
        notifyEnd(index);
    }

    private void checkStopEvent(ProgressEvent event) {
        if (event.isStop()) {
            stop();
        }
    }

    private void logWriteXmpFile(String imageFilename) {
        MessageFormat msg = new MessageFormat(Bundle.getString("IptcToXmp.InformationMessage.StartWriteXmpFile"));
        Object[] params = {imageFilename};
        Log.logInfo(IptcToXmp.class, msg.format(params));
    }

    private void notifyStart() {
        int count = filenames.size();
        ProgressEvent event = new ProgressEvent(this, 0, count, 0,
            filenames.size() > 0 ? filenames.get(0) : ""); // NOI18N
        for (ProgressListener progressListener : progressListeners) {
            progressListener.progressStarted(event);
            checkStopEvent(event);
        }
    }

    private void notifyPerformed(int index) {
        ProgressEvent event = new ProgressEvent(this, 0, filenames.size(), index + 1, filenames.get(index));
        for (ProgressListener progressListener : progressListeners) {
            progressListener.progressPerformed(event);
            checkStopEvent(event);
        }
    }

    private void notifyEnd(int index) {
        ProgressEvent event = new ProgressEvent(this, 0, filenames.size(), index + 1, ""); // NOI18N
        for (ProgressListener progressListener : progressListeners) {
            progressListener.progressEnded(event);
        }
    }
}
