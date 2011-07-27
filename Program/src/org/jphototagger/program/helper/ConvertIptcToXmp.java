package org.jphototagger.program.helper;

import org.jphototagger.lib.concurrent.Cancelable;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.data.Iptc;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase.Insert;
import org.jphototagger.program.image.metadata.iptc.IptcMetadata;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.program.app.AppFileFilters;

/**
 * Erzeugt XMP-Daten anhand bestehender IPTC-Daten.
 *
 * @author Elmar Baumann
 */
public final class ConvertIptcToXmp implements Runnable, Cancelable {
    private final List<ProgressListener> prLs = new ArrayList<ProgressListener>();
    private final List<File> imageFiles;
    private boolean cancel;

    public ConvertIptcToXmp(List<File> imageFiles) {
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        this.imageFiles = new ArrayList<File>(imageFiles);
    }

    public synchronized void addProgressListener(ProgressListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        prLs.add(listener);
    }

    @Override
    public void cancel() {
        cancel = true;
    }

    @Override
    public void run() {
        notifyStart();

        int size = imageFiles.size();
        int index = 0;

        for (index = 0; !cancel && (index < size); index++) {
            File imageFile = imageFiles.get(index);
            File xmpFile = XmpMetadata.suggestSidecarFile(imageFile);
            Iptc iptc = null;

            if (!AppFileFilters.INSTANCE.isUserDefinedFileType(imageFile)) {
                iptc = IptcMetadata.getIptc(imageFile);
            }

            if (iptc != null) {
                Xmp xmp = null;

                try {
                    xmp = XmpMetadata.getXmpFromSidecarFileOf(imageFile);
                } catch (IOException ex) {
                    Logger.getLogger(ConvertIptcToXmp.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (xmp == null) {
                    xmp = new Xmp();
                }

                xmp.setIptc(iptc, Xmp.SetIptc.DONT_CHANGE_EXISTING_VALUES);
                logWriteXmpFile(imageFile);

                if (XmpMetadata.writeXmpToSidecarFile(xmp, xmpFile)) {
                    updateDatabase(imageFile);
                }
            }

            notifyPerformed(index);
        }

        notifyEnd(index);
    }

    private void updateDatabase(File imageFile) {
        InsertImageFilesIntoDatabase insert = new InsertImageFilesIntoDatabase(Arrays.asList(imageFile), Insert.XMP);

        insert.run();    // run in this thread!
    }

    private void checkCancel(ProgressEvent event) {
        if (event.isCancel()) {
            cancel();
        }
    }

    private void logWriteXmpFile(File imageFile) {
        AppLogger.logInfo(ConvertIptcToXmp.class, "ConvertIptcToXmp.Info.StartWriteXmpFile", imageFile);
    }

    private synchronized void notifyStart() {
        int count = imageFiles.size();
        ProgressEvent event = new ProgressEvent(this, 0, count, 0, (imageFiles.size() > 0)
                ? imageFiles.get(0)
                : "");

        for (ProgressListener progressListener : prLs) {
            progressListener.progressStarted(event);
            checkCancel(event);
        }
    }

    private synchronized void notifyPerformed(int index) {
        ProgressEvent event = new ProgressEvent(this, 0, imageFiles.size(), index + 1, imageFiles.get(index));

        for (ProgressListener progressListener : prLs) {
            progressListener.progressPerformed(event);
            checkCancel(event);
        }
    }

    private synchronized void notifyEnd(int index) {
        ProgressEvent event = new ProgressEvent(this, 0, imageFiles.size(), index + 1, "");

        for (ProgressListener progressListener : prLs) {
            progressListener.progressEnded(event);
        }
    }
}
