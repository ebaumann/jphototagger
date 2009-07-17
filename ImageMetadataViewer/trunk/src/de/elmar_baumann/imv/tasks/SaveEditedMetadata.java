package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.AppTexts;
import de.elmar_baumann.imv.data.TextEntry;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.tasks.InsertImageFilesIntoDatabase.Insert;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.imv.view.panels.ProgressBarUserTasks;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import javax.swing.JProgressBar;

/**
 * Saves edited metadata.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class SaveEditedMetadata extends Thread {

    private final List<TextEntry> textEntries;
    private final List<String> filenames;
    private final EnumSet<XmpMetadata.UpdateOption> writeOptions;
    private final ProgressBarUserTasks progressBarProvider =
            ProgressBarUserTasks.INSTANCE;
    private JProgressBar progressBar;

    public synchronized static void saveMetadata(
            EditMetadataPanelsArray editPanels) {

        final List<TextEntry> entries = editPanels.getTextEntries(true);
        final List<String> filenames = editPanels.getFilenames();
        final int filenameCount = filenames.size();

        if (filenameCount >= 1) {
            SaveEditedMetadata updater = new SaveEditedMetadata(
                    filenames, entries, getWriteOptions(filenameCount));
            UserTasksQueue.INSTANCE.add(updater);
        } else {
            AppLog.logWarning(SaveEditedMetadata.class,
                    Bundle.getString(
                    "SaveEditedMetadata.Error.NoImageFilesSelected")); // NOI18N
        }
        editPanels.setDirty(false);
        editPanels.setFocusToLastFocussedComponent();
    }

    private static EnumSet<XmpMetadata.UpdateOption> getWriteOptions(
            int filecount) {
        if (filecount < 1)
            throw new IllegalArgumentException("Filecount < 1: " + filecount);
        return filecount == 1
               ? EnumSet.of(
                XmpMetadata.UpdateOption.DELETE_IF_SOURCE_VALUE_IS_EMPTY)
               : EnumSet.of(
                XmpMetadata.UpdateOption.APPEND_TO_REPEATABLE_VALUES);
    }

    private SaveEditedMetadata(
            List<String> filenames,
            List<TextEntry> textEntries,
            EnumSet<XmpMetadata.UpdateOption> writeOptions) {

        GUI.INSTANCE.getAppFrame().addSaveObject(this);
        this.filenames = filenames;
        this.textEntries = textEntries;
        this.writeOptions = writeOptions;
        setName("Saving edited metadata @ " + getClass().getName());
    }

    @Override
    public void run() {
        int count = filenames.size();
        progressStarted(count);
        // Ignore isInterrupted() because saving user input has high priority
        for (int i = 0; i < count; i++) {
            String filename = filenames.get(i);
            String sidecarFilename =
                    XmpMetadata.suggestSidecarFilenameForImageFile(filename);
            if (XmpMetadata.writeMetadataToSidecarFile(
                    sidecarFilename, textEntries, writeOptions)) {
                updateDatabase(filename);
            }
            progressPerformed(i + 1, filename);
        }
        progressEnded(count);
        GUI.INSTANCE.getAppFrame().removeSaveObject(this);
    }

    private void updateDatabase(String filename) {
        InsertImageFilesIntoDatabase updater = new InsertImageFilesIntoDatabase(
                Arrays.asList(filename), EnumSet.of(Insert.XMP));
        updater.run();
    }

    private void progressStarted(int maximum) {
        progressBar = (JProgressBar) progressBarProvider.getResource(this);
        if (progressBar != null) {
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
            progressBarProvider.releaseResource(this);
        }
    }
}
