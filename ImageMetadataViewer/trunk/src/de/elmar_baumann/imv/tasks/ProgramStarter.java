package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.io.IoUtil;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.dialogs.ProgramInputParametersDialog;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.runtime.External;
import de.elmar_baumann.lib.generics.Pair;
import java.io.File;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JProgressBar;

/**
 * Executes in a thread programs which processes image files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/06
 */
public final class ProgramStarter {

    private final JProgressBar progressBar;
    private final Queue<Execute> queue = new ConcurrentLinkedQueue<Execute>();

    /**
     * Constructor.
     * 
     * @param progressBar  progressbar or null, if the progress shouldn't be
     *                     displayed
     */
    public ProgramStarter(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    /**
     * Executes a program.
     * 
     * @param  program     program
     * @param  imageFiles  files to process
     */
    public void startProgram(Program program, List<File> imageFiles) {
        if (checkFilecount(imageFiles)) {
            Execute execute = new Execute(program, imageFiles);
            execute.setPriority(UserSettings.INSTANCE.getThreadPriority());
            synchronized (this) {
                if (queue.isEmpty()) {
                    execute.start();
                } else {
                    queue.add(execute);
                }
            }
        }
    }

    private boolean checkFilecount(List<File> imageFiles) {
        if (imageFiles.size() <= 0) {
            MessageDisplayer.error("ProgramStarter.Error.Selection"); // NOI18N
            return false;
        }
        return true;
    }

    private class Execute extends Thread {

        private Program program;
        private List<File> imageFiles;
        ProgramInputParametersDialog dialog = new ProgramInputParametersDialog();

        public Execute(Program program, List<File> imageFiles) {
            this.imageFiles = imageFiles;
            this.program = program;
            setName("Executing program " + program.getAlias() + " @ " + // NOI18N
                    getClass().getName());
        }

        @Override
        public void run() {
            initProgressBar();
            if (program.isSingleFileProcessing()) {
                processSingle();
            } else {
                processAll();
            }
            updateDatabase();
            nextExecutor();
        }

        private void logCommand(String command) {
            AppLog.logInfo(ProgramStarter.class, Bundle.getString(
                    "ProgramStarter.Info.ExecuteCommand", command)); // NOI18N
        }

        private void processAll() {
            String command = getProcessAllCommand();
            logCommand(command);
            Pair<byte[], byte[]> output = External.executeGetOutput(command,
                    UserSettings.INSTANCE.
                    getMaxSecondsToTerminateExternalPrograms() * 1000);
            if (output != null) {
                checkLogErrors(output);
                setValueToProgressBar(imageFiles.size());
            }
        }

        private String getProcessAllCommand() {
            return program.getFile().getAbsolutePath() + " " + // NOI18N
                    program.getCommandlineParameters(
                    IoUtil.getQuotedForCommandline(imageFiles, ""), // NOI18N
                    getAdditionalParameters(
                    Bundle.getString("ProgramStarter.GetInput.Title"), 2), // NOI18N
                    dialog.isParametersBeforeFilename());
        }

        private void processSingle() {
            int count = 0;
            for (File file : imageFiles) {
                String command = getProcessSingleCommand(file, count);
                logCommand(command);
                Pair<byte[], byte[]> output = External.executeGetOutput(command,
                        UserSettings.INSTANCE.
                        getMaxSecondsToTerminateExternalPrograms() * 1000);
                if (output != null) {
                    checkLogErrors(output);
                    setValueToProgressBar(++count);
                }
            }
        }

        private String getProcessSingleCommand(File file, int count) {
            return program.getFile().getAbsolutePath() + " " + // NOI18N
                    program.getCommandlineParameters(
                    IoUtil.getQuotedForCommandline(
                    Collections.singletonList(file), ""), // NOI18N
                    getAdditionalParameters(file.getAbsolutePath(), count + 1),
                    dialog.isParametersBeforeFilename());
        }

        private String getAdditionalParameters(String filename, int count) {
            if (!program.isInputBeforeExecute()) {
                return ""; // NOI18N
            }
            if ((!program.isInputBeforeExecutePerFile() && count > 1)) {
                return dialog.getParameters();
            }
            dialog.setProgram(program.getAlias());
            dialog.setFilename(filename);
            dialog.setVisible(true);
            if (dialog.accepted()) {
                return dialog.getParameters();
            }
            return ""; // NOI18N
        }

        private synchronized void nextExecutor() {
            if (!queue.isEmpty()) {
                queue.poll().start();
            }
        }

        private void checkLogErrors(Pair<byte[], byte[]> output) {
            byte[] stderr = output.getSecond();
            String message = (stderr == null
                              ? "" // NOI18N
                              : new String(stderr).trim()); // NOI18N
            if (!message.isEmpty()) {
                message =
                        Bundle.getString("ProgramStarter.Error.Program") + // NOI18N
                        message;
                AppLog.logWarning(Execute.class, message);
            }
        }

        private void initProgressBar() {
            if (progressBar != null) {
                progressBar.setMinimum(0);
                progressBar.setMaximum(imageFiles.size());
                progressBar.setValue(0);
            }
        }

        private void setValueToProgressBar(final int value) {
            if (progressBar != null) {
                progressBar.setValue(value);
            }
        }

        private void updateDatabase() {
            if (program.isChangeFile()) {
                InsertImageFilesIntoDatabase updater = new InsertImageFilesIntoDatabase(
                        FileUtil.getAbsolutePathnames(imageFiles),
                        EnumSet.of(
                        InsertImageFilesIntoDatabase.Insert.OUT_OF_DATE));
                updater.run(); // no subsequent thread
            }
        }
    }
}
