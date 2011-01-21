package org.jphototagger.program.helper;

import java.awt.EventQueue;
import org.jphototagger.lib.concurrent.Cancelable;
import org.jphototagger.lib.generics.Pair;
import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase.Insert;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.tasks.UserTasks;
import org.jphototagger.program.view.panels.ProgressBar;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JProgressBar;

/**
 * Writes {@link Xmp} objects to XMP files and inserts or updates them into the
 * database.
 *
 * @author Elmar Baumann
 */
public final class SaveXmp extends Thread implements Cancelable {
    private static final String PROGRESSBAR_STRING =
        JptBundle.INSTANCE.getString("SaveXmp.ProgressBar.String");
    private final Collection<Pair<File, Xmp>> imageFilesXmp;
    private JProgressBar                      progressBar;
    private volatile boolean                  cancel;
    private final Object                      pBarOwner = this;

    private SaveXmp(Collection<Pair<File, Xmp>> imageFilesXmp) {
        super("JPhotoTagger: Saving XMP");
        AppLifeCycle.INSTANCE.addSaveObject(this);
        this.imageFilesXmp = new ArrayList<Pair<File, Xmp>>(imageFilesXmp);
    }

    public synchronized static void save(Collection<Pair<File,
            Xmp>> imageFilesXmp) {
        if (imageFilesXmp == null) {
            throw new NullPointerException("imageFilesXmp == null");
        }

        final int fileCount = imageFilesXmp.size();

        if (fileCount >= 1) {
            UserTasks.INSTANCE.add(new SaveXmp(imageFilesXmp));
        }
    }

    @Override
    public void run() {
        int fileIndex = 0;

        // Ignore isInterrupted() because saving user input has high priority
        for (Pair<File, Xmp> pair : imageFilesXmp) {
            if (cancel) {
                break;
            }

            File imageFile   = pair.getFirst();
            Xmp  xmp         = pair.getSecond();
            File sidecarFile = XmpMetadata.suggestSidecarFile(imageFile);

            if (XmpMetadata.writeXmpToSidecarFile(xmp, sidecarFile)) {
                updateDatabase(imageFile);
            }

            updateProgressBar(++fileIndex);
        }

        releaseProgressBar();
        AppLifeCycle.INSTANCE.removeSaveObject(this);
    }

    private void updateDatabase(File imageFile) {
        InsertImageFilesIntoDatabase updater =
            new InsertImageFilesIntoDatabase(Arrays.asList(imageFile),
                Insert.XMP);

        updater.run();    // run in this thread!
    }

    private void getProgressBar() {
        if (progressBar != null) {
            return;
        }

        progressBar = ProgressBar.INSTANCE.getResource(pBarOwner);
    }

    private void updateProgressBar(final int value) {
        getProgressBar();
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null) {
                    progressBar.setMinimum(0);
                    progressBar.setMaximum(imageFilesXmp.size());
                    progressBar.setValue(value);

                    if (!progressBar.isStringPainted()) {
                        progressBar.setStringPainted(true);
                    }

                    if (!PROGRESSBAR_STRING.equals(progressBar.getString())) {
                        progressBar.setString(PROGRESSBAR_STRING);
                    }
                }
            }
        });
    }

    private void releaseProgressBar() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null) {
                    if (progressBar.isStringPainted()) {
                        progressBar.setString("");
                    }

                    progressBar.setValue(0);
                    ProgressBar.INSTANCE.releaseResource(pBarOwner);
                    progressBar = null;
                }
            }
        });
    }

    @Override
    public void cancel() {
        cancel = true;
    }
}
