package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.Log;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.io.IoUtil;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.DatabaseUpdate;
import de.elmar_baumann.imv.view.dialogs.ProgramInputParametersDialog;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.runtime.External;
import de.elmar_baumann.lib.template.Pair;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

/**
 * Executes in a thread programs which processes image files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/06
 */
public final class ProgramExecutor {

    private JProgressBar progressBar;
    private final Queue<Execute> queue = new ConcurrentLinkedQueue<Execute>();

    /**
     * Constructor.
     * 
     * @param progressBar  progressbar or null, if the progress shouldn't be
     *                     displayed
     */
    public ProgramExecutor(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    /**
     * Executes a program.
     * 
     * @param  program     program
     * @param  imageFiles  files to process
     */
    public void execute(Program program, List<File> imageFiles) {
        if (checkFilecount(imageFiles)) {
            Execute execute = new Execute(program, imageFiles);
            execute.setPriority(UserSettings.getInstance().getThreadPriority());
            synchronized (queue) {
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
            errorMessageSelection();
            return false;
        }
        return true;

    }

    private void errorMessageSelection() {
        JOptionPane.showMessageDialog(
            null,
            Bundle.getString("ProgramExecutor.ErrorMessage.Selection"),
            Bundle.getString("ProgramExecutor.ErrorMessage.Selection.Title"),
            JOptionPane.ERROR_MESSAGE,
            AppSettings.getMediumAppIcon());
    }

    private class Execute extends Thread {

        private Program program;
        private List<File> imageFiles;
        ProgramInputParametersDialog dialog = new ProgramInputParametersDialog();

        public Execute(Program program, List<File> imageFiles) {
            this.imageFiles = imageFiles;
            this.program = program;
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
            MessageFormat msg = new MessageFormat(Bundle.getString("ProgramExecutor.InformationMessage.ExecuteCommand"));
            Object[] params = {command};
            Log.logInfo(ProgramExecutor.class, msg.format(params));
        }

        private void processAll() {
            String command = program.getFile().getAbsolutePath() + " " + // NOI18N
                program.getCommandlineAfterProgram(
                IoUtil.getQuotedForCommandline(imageFiles, ""), // NOI18N
                getInput(Bundle.getString("ProgramExecutor.GetInput.Title"), 2),
                dialog.isParametersBeforeFilename());
            logCommand(command);
            Pair<byte[], byte[]> output = External.executeGetOutput(command);
            if (output != null) {
                logErrors(output);
                setValueToProgressBar(imageFiles.size());
            }
        }

        private void processSingle() {
            int count = 0;
            for (File file : imageFiles) {
                String command = program.getFile().getAbsolutePath() + " " + // NOI18N
                    program.getCommandlineAfterProgram(
                    file.getAbsolutePath(),
                    getInput(file.getAbsolutePath(), count + 1),
                    dialog.isParametersBeforeFilename());
                logCommand(command);
                Pair<byte[], byte[]> output = External.executeGetOutput(command);
                if (output != null) {
                    logErrors(output);
                    setValueToProgressBar(++count);
                }
            }
        }

        private String getInput(String filename, int count) {
            if (!program.isInputBeforeExecute()) {
                return ""; // NOI18N
            }
            if ((!program.isInputBeforeExecutePerFile() && count > 1)) {
                return dialog.getParameters();
            }
            dialog.setProgram(program.getAlias());
            dialog.setFilename(filename);
            dialog.setVisible(true);
            if (dialog.isAccepted()) {
                return dialog.getParameters();
            }
            return ""; // NOI18N
        }

        private void nextExecutor() {
            synchronized (queue) {
                if (!queue.isEmpty()) {
                    queue.poll().start();
                }
            }
        }

        private void logErrors(Pair<byte[], byte[]> output) {
            byte[] stderr = output.getSecond();
            String message = (stderr == null ? "" : new String(stderr).trim()); // NOI18N
            if (!message.isEmpty()) {
                message = Bundle.getString("ProgramExecutor.ErrorMessage.Program") + message;
                Log.logWarning(Execute.class, message);
            }
        }

        private void initProgressBar() {
            if (progressBar != null) {
                progressBar.setMinimum(0);
                progressBar.setMaximum(imageFiles.size());
                progressBar.setValue(0);
            }
        }

        private void setValueToProgressBar(int value) {
            if (progressBar != null) {
                progressBar.setValue(value);
            }
        }

        private void updateDatabase() {
            if (program.isChangeFile()) {
                ImageMetadataToDatabase updater = new ImageMetadataToDatabase(
                    FileUtil.getAbsolutePathnames(imageFiles), DatabaseUpdate.COMPLETE);
                updater.run(); // no subsequent thread
            }
        }
    }
}
