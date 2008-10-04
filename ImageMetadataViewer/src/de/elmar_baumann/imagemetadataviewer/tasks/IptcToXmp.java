package de.elmar_baumann.imagemetadataviewer.tasks;

import de.elmar_baumann.imagemetadataviewer.data.Iptc;
import de.elmar_baumann.imagemetadataviewer.data.Xmp;
import de.elmar_baumann.imagemetadataviewer.event.ProgressEvent;
import de.elmar_baumann.imagemetadataviewer.event.ProgressListener;
import de.elmar_baumann.imagemetadataviewer.image.metadata.iptc.IptcMetadata;
import de.elmar_baumann.imagemetadataviewer.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.lib.io.FileUtil;
import java.util.Vector;

/**
 * Erzeugt XMP-Daten anhand bestehender IPTC-Daten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/30
 */
public class IptcToXmp implements Runnable {

    private Vector<String> filenames;
    private boolean stop = false;
    private Vector<ProgressListener> progressListeners = new Vector<ProgressListener>();

    public IptcToXmp(Vector<String> filenames) {
        this.filenames = filenames;
    }

    public void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    public void removeProgressListener(ProgressListener listener) {
        progressListeners.remove(listener);
    }

    public void stop() {
        stop = true;
    }

    @Override
    public void run() {
        notifyStart();
        int size = filenames.size();
        XmpMetadata xmpMeta = new XmpMetadata();
        int index = 0;
        for (index = 0; !stop && index < size; index++) {
            String imageFilename = filenames.get(index);
            String sidecarFilename = XmpMetadata.suggestSidecarFilename(imageFilename);
            Iptc iptc = IptcMetadata.getIptc(imageFilename);
            Xmp xmp = XmpMetadata.getXmp(imageFilename);
            if (xmp == null) {
                xmp = new Xmp();
            }
            xmp.setIptc(iptc, false);
            xmpMeta.writeMetaDataToSidecarFile(sidecarFilename, xmp);
            notifyPerformed(index);
        }
        notifyEnd(index);
    }

    private void notifyStart() {
        int count = filenames.size();
        ProgressEvent event = new ProgressEvent(this, 0, count, 0,
            filenames.size() > 0 ? filenames.get(0) : ""); // NOI18N
        for (ProgressListener progressListener : progressListeners) {
            progressListener.progressStarted(event);
            if (event.isStop()) {
                stop();
            }
        }
    }

    private void notifyPerformed(int index) {
        ProgressEvent event = new ProgressEvent(this, 0, filenames.size(), index + 1, filenames.get(index));
        for (ProgressListener progressListener : progressListeners) {
            progressListener.progressPerformed(event);
            if (event.isStop()) {
                stop();
            }
        }
    }

    private void notifyEnd(int index) {
        ProgressEvent event = new ProgressEvent(this, 0, filenames.size(), index + 1, ""); // NOI18N
        for (ProgressListener progressListener : progressListeners) {
            progressListener.progressEnded(event);
        }
    }
}
