package org.jphototagger.iptcmodule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;

import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.metadata.iptc.Iptc;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.domain.repository.SaveOrUpdate;
import org.jphototagger.domain.repository.SaveToOrUpdateFilesInRepository;
import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;
import org.jphototagger.iptc.IptcMetadata;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.xmp.XmpMetadata;

/**
 * @author Elmar Baumann
 */
public final class ConvertIptcToXmp implements Runnable, Cancelable {

    private final List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();
    private final List<File> files;
    private boolean cancel;
    private static final Logger LOGGER = Logger.getLogger(ConvertIptcToXmp.class.getName());
    private final XmpSidecarFileResolver xmpSidecarFileResolver = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);

    public ConvertIptcToXmp(Collection<? extends File> imageFiles) {
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        this.files = new ArrayList<File>(imageFiles);
    }

    public synchronized void addProgressListener(ProgressListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        progressListeners.add(listener);
    }

    @Override
    public void cancel() {
        cancel = true;
    }

    @Override
    public void run() {
        notifyStart();
        int size = files.size();
        int index;
        for (index = 0; !cancel && (index < size); index++) {
            File file = files.get(index);
            File xmpFile = xmpSidecarFileResolver.suggestXmpSidecarFile(file);
            Iptc iptc = null;
            if (!isUserDefinedFileType(file)) {
                iptc = IptcMetadata.getIptc(file);
            }
            if (iptc != null) {
                Xmp xmp = null;
                try {
                    xmp = XmpMetadata.getXmpFromSidecarFileOf(file);
                } catch (IOException ex) {
                    Logger.getLogger(ConvertIptcToXmp.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (xmp == null) {
                    xmp = new Xmp();
                }
                xmp.setIptc(iptc, Xmp.SetIptc.DONT_CHANGE_EXISTING_VALUES);
                logWriteXmpFile(file);
                if (XmpMetadata.writeXmpToSidecarFile(xmp, xmpFile)) {
                    updateRepository(file);
                }
            }
            notifyPerformed(index);
        }
        notifyEnd(index);
    }

    private boolean isUserDefinedFileType(File file) {
        String suffix = FileUtil.getSuffix(file);
        UserDefinedFileTypesRepository repo = Lookup.getDefault().lookup(UserDefinedFileTypesRepository.class);
        return repo.existsUserDefinedFileTypeWithSuffix(suffix);
    }

    private void updateRepository(File imageFile) {
        SaveToOrUpdateFilesInRepository updater = Lookup.getDefault().lookup(SaveToOrUpdateFilesInRepository.class)
                .createInstance(Arrays.asList(imageFile), SaveOrUpdate.XMP);
        updater.saveOrUpdateWaitForTermination();
    }

    private void checkCancel(ProgressEvent event) {
        if (event.isCancel()) {
            cancel();
        }
    }

    private void logWriteXmpFile(File imageFile) {
        LOGGER.log(Level.INFO, "Write XMP sidecar file from IPTC in file ''{0}''", imageFile);
    }

    private synchronized void notifyStart() {
        int count = files.size();
        Object info = files.size() > 0
                ? files.get(0)
                : "";
        ProgressEvent event = new ProgressEvent.Builder()
                .source(this)
                .minimum(0)
                .maximum(count)
                .value(0)
                .info(info)
                .build();
        for (ProgressListener progressListener : progressListeners) {
            progressListener.progressStarted(event);
            checkCancel(event);
        }
    }

    private synchronized void notifyPerformed(int index) {
        ProgressEvent event = new ProgressEvent.Builder()
                .source(this)
                .minimum(0)
                .maximum(files.size())
                .value(index + 1)
                .info(files.get(index))
                .build();
        for (ProgressListener progressListener : progressListeners) {
            progressListener.progressPerformed(event);
            checkCancel(event);
        }
    }

    private synchronized void notifyEnd(int index) {
        ProgressEvent event = new ProgressEvent.Builder()
                .source(this)
                .minimum(0)
                .maximum(files.size())
                .value(index + 1)
                .info("")
                .build();
        for (ProgressListener progressListener : progressListeners) {
            progressListener.progressEnded(event);
        }
    }
}
