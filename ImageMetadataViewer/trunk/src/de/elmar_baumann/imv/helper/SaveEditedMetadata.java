package de.elmar_baumann.imv.helper;

import de.elmar_baumann.imv.app.AppLifeCycle;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.AppTexts;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.helper.InsertImageFilesIntoDatabase.Insert;
import de.elmar_baumann.imv.tasks.UserTasks;
import de.elmar_baumann.imv.view.panels.ProgressBarUserTasks;
import de.elmar_baumann.lib.generics.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import javax.swing.JProgressBar;

/**
 * Saves edited metadata.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class SaveEditedMetadata extends Thread {

    private final Collection<Pair<String, Xmp>> filenamesXmp;
    private final ProgressBarUserTasks progressBarRessource =
            ProgressBarUserTasks.INSTANCE;
    private JProgressBar progressBar;

    public synchronized static void saveMetadata(
            Collection<Pair<String, Xmp>> filenamesXmp) {

        final int fileCount = filenamesXmp.size();

        if (fileCount >= 1) {
            SaveEditedMetadata updater = new SaveEditedMetadata(filenamesXmp);
            UserTasks.INSTANCE.add(updater);
        } else {
            AppLog.logWarning(SaveEditedMetadata.class,
                    "SaveEditedMetadata.Error.NoImageFilesSelected"); // NOI18N
        }
    }

    private SaveEditedMetadata(Collection<Pair<String, Xmp>> filenamesXmp) {

        AppLifeCycle.INSTANCE.addSaveObject(this);
        this.filenamesXmp = new ArrayList<Pair<String, Xmp>>(filenamesXmp);
        setName("Saving edited metadata @ " + getClass().getName()); // NOI18N
    }

    @Override
    public void run() {
        int count = filenamesXmp.size();
        progressStarted(count);
        // Ignore isInterrupted() because saving user input has high priority
        int index = 0;
        for (Pair<String, Xmp> pair : filenamesXmp) {
            String filename = pair.getFirst();
            Xmp xmp = pair.getSecond();
            String sidecarFilename =
                    XmpMetadata.suggestSidecarFilenameForImageFile(filename);
            if (XmpMetadata.writeMetadataToSidecarFile(sidecarFilename, xmp)) {
                updateDatabase(filename);
            }
            progressPerformed(++index, filename);
        }
        progressEnded(count);
        AppLifeCycle.INSTANCE.removeSaveObject(this);
    }

    private void updateDatabase(String filename) {
        InsertImageFilesIntoDatabase updater = new InsertImageFilesIntoDatabase(
                Arrays.asList(filename),
                EnumSet.of(Insert.XMP),
                null);
        updater.run();
    }

    private void progressStarted(int maximum) {
        progressBar = progressBarRessource.getResource(this);
        if (progressBar == null) {
            AppLog.logInfo(getClass(), "ProgressBar.Locked", getClass(), // NOI18N
                    progressBarRessource.getOwner());
        } else {
            progressBar.setMinimum(0);
            progressBar.setMaximum(maximum);
            progressBar.setValue(0);
        }
    }

    private void progressPerformed(int value, String filename) {
        if (progressBar != null) {
            progressBar.setValue(value);
            progressBar.setToolTipText(filename);
        }
    }

    public void progressEnded(int value) {
        if (progressBar != null) {
            progressBar.setValue(value);
            progressBar.setToolTipText(
                    AppTexts.TOOLTIP_TEXT_PROGRESSBAR_CURRENT_TASKS);
            progressBar = null;
            progressBarRessource.releaseResource(this);
        }
    }
}
