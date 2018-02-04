package org.jphototagger.program.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.concurrent.SerialTaskExecutor;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressHandle;
import org.jphototagger.api.progress.ProgressHandleFactory;
import org.jphototagger.domain.metadata.xmp.FileXmp;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.domain.repository.SaveOrUpdate;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.ThreadUtil;
import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.xmp.XmpMetadata;
import org.openide.util.Lookup;

/**
 * Writes {@code Xmp} objects to XMP files and inserts or updates them into the
 * repository.
 *
 * @author Elmar Baumann
 */
public final class SaveXmp extends Thread implements Cancelable {

    private static final String PROGRESSBAR_STRING = Bundle.getString(SaveXmp.class, "SaveXmp.ProgressBar.String");
    private final Collection<FileXmp> imageFilesXmp;
    private volatile boolean cancel;
    private final Object source = this;
    private final XmpSidecarFileResolver xmpSidecarFileResolver = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);
    private ProgressHandle progressHandle;

    private SaveXmp(Collection<FileXmp> imageFilesXmp) {
        super("JPhotoTagger: Saving XMP");
        this.imageFilesXmp = new ArrayList<>(imageFilesXmp);
        addThisToSaveObjects();
    }

    private void addThisToSaveObjects() {
        AppLifeCycle.INSTANCE.addSaveObject(this);
    }

    public synchronized static void save(Collection<FileXmp> imageFilesXmp) {
        if (imageFilesXmp == null) {
            throw new NullPointerException("imageFilesXmp == null");
        }
        final int fileCount = imageFilesXmp.size();
        if (fileCount >= 1) {
            SerialTaskExecutor executor = Lookup.getDefault().lookup(SerialTaskExecutor.class);
            SaveXmp saveXmp = new SaveXmp(imageFilesXmp);
            executor.addTask(saveXmp);
        }
    }

    @Override
    public void run() {
        int fileIndex = 0;
        progressHandle = Lookup.getDefault().lookup(ProgressHandleFactory.class).createProgressHandle(this);
        progressHandle.progressStarted(createProgressEvent(0));
        // Ignore isInterrupted() because saving user input has high priority
        for (FileXmp fileXmp : imageFilesXmp) {
            if (cancel) {
                break;
            }
            File imageFile = fileXmp.getFile();
            Xmp xmp = fileXmp.getXmp();
            File sidecarFile = xmpSidecarFileResolver.suggestXmpSidecarFile(imageFile);
            if (XmpMetadata.writeXmpToSidecarFile(xmp, sidecarFile)) {
                updateRepository(imageFile);
            }
            fileIndex++;
            progressHandle.progressPerformed(createProgressEvent(fileIndex));
        }
        progressHandle.progressEnded();
        AppLifeCycle.INSTANCE.removeSaveObject(this);
    }

    private void updateRepository(File imageFile) {
        SaveToOrUpdateFilesInRepositoryImpl updater = new SaveToOrUpdateFilesInRepositoryImpl(
                Arrays.asList(imageFile), SaveOrUpdate.XMP);
        ThreadUtil.runInThisThread(updater);
    }

    private ProgressEvent createProgressEvent(int value) {
        return new ProgressEvent.Builder()
                .source(source)
                .stringPainted(true)
                .stringToPaint(PROGRESSBAR_STRING)
                .minimum(0)
                .maximum(imageFilesXmp.size())
                .value(value)
                .build();
    }

    @Override
    public void cancel() {
        cancel = true;
    }
}
