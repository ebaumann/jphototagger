package org.jphototagger.program.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.openide.util.Lookup;

import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.concurrent.SerialTaskExecutor;
import org.jphototagger.api.progress.MainWindowProgressBarProvider;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.domain.metadata.xmp.FileXmp;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.repository.InsertIntoRepository;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.xmp.XmpMetadata;

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
    private final Object pBarOwner = this;
    private final MainWindowProgressBarProvider progressBarProvider = Lookup.getDefault().lookup(MainWindowProgressBarProvider.class);

    private SaveXmp(Collection<FileXmp> imageFilesXmp) {
        super("JPhotoTagger: Saving XMP");
        AppLifeCycle.INSTANCE.addSaveObject(this);
        this.imageFilesXmp = new ArrayList<FileXmp>(imageFilesXmp);
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

        progressBarProvider.progressStarted(createProgressEvent(0));
        // Ignore isInterrupted() because saving user input has high priority
        for (FileXmp fileXmp : imageFilesXmp) {
            if (cancel) {
                break;
            }

            File imageFile = fileXmp.getFile();
            Xmp xmp = fileXmp.getXmp();
            File sidecarFile = XmpMetadata.suggestSidecarFile(imageFile);

            if (XmpMetadata.writeXmpToSidecarFile(xmp, sidecarFile)) {
                updateRepository(imageFile);
            }

            fileIndex++;
            progressBarProvider.progressPerformed(createProgressEvent(fileIndex));
        }

        progressBarProvider.progressEnded(pBarOwner);
        AppLifeCycle.INSTANCE.removeSaveObject(this);
    }

    private void updateRepository(File imageFile) {
        InsertImageFilesIntoRepository updater = new InsertImageFilesIntoRepository(Arrays.asList(imageFile), InsertIntoRepository.XMP);

        updater.run();    // run in this thread!
    }

    private ProgressEvent createProgressEvent(int value) {
        return new ProgressEvent.Builder()
               .source(pBarOwner)
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
