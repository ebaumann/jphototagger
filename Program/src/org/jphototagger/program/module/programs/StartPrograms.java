package org.jphototagger.program.module.programs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressHandle;
import org.jphototagger.api.progress.ProgressHandleFactory;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.repository.SaveOrUpdate;
import org.jphototagger.domain.repository.SaveToOrUpdateFilesInRepository;
import org.jphototagger.lib.runtime.External;
import org.jphototagger.lib.runtime.ProcessResult;
import org.jphototagger.lib.runtime.RuntimeUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class StartPrograms {

    private static final long MAX_MILLISECONDS_UNTIL_TERMINATE = 300 * 1000;
    private static final Logger LOGGER = Logger.getLogger(StartPrograms.class.getName());
    private final Queue<Execute> queue = new ConcurrentLinkedQueue<Execute>();
    private ProgressHandle progressHandle;

    /**
     * Executes a program.
     *
     * @param program program
     * @param imageFiles files to process
     * @param waitForTermination
     */
    public void startProgram(Program program, List<File> imageFiles, boolean waitForTermination) {
        if (checkFilecount(imageFiles)) {
            Execute execute = new Execute(program, imageFiles, waitForTermination);
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
            String message = Bundle.getString(StartPrograms.class, "StartPrograms.Error.Selection");
            MessageDisplayer.error(null, message);
            return false;
        }
        return true;
    }

    private class Execute extends Thread {

        private final ProgramInputParametersDialog dlg = new ProgramInputParametersDialog();
        private final List<File> imageFiles;
        private final Program program;
        private final boolean waitForTermination;

        Execute(Program program, List<File> imageFiles, boolean waitForTermination) {
            super("JPhotoTagger: Executing program " + program.getAlias());
            if (program == null) {
                throw new NullPointerException("program == null");
            }
            if (imageFiles == null) {
                throw new NullPointerException("imageFiles == null");
            }
            this.imageFiles = new ArrayList<File>(imageFiles);
            this.program = program;
            this.waitForTermination = waitForTermination;
        }

        @Override
        public void run() {
            progressStarted();
            if (program.isUsePattern()) {
                processPattern();
            } else if (program.isSingleFileProcessing()) {
                processSingle();
            } else {
                processAll();
            }
            progressHandle.progressEnded();
            updateRepository();
            nextExecutor();
        }

        private void logCommand(String command) {
            LOGGER.log(Level.INFO, "Execute command: ''{0}''", command);
        }

        private void processPattern() {
            int count = 0;
            for (File file : imageFiles) {
                String command = getProcessPatternCommand(file);
                logCommand(command);
                if (waitForTermination) {
                    ProcessResult processResult = External.executeWaitForTermination(command, MAX_MILLISECONDS_UNTIL_TERMINATE);
                    boolean terminatedWithErrors = processResult == null || processResult.getExitValue() != 0;
                    if (terminatedWithErrors) {
                        logError(command, processResult);
                    }
                } else {
                    External.execute(command);
                }
                count++;
                progressPerformed(count);
            }
        }

        private void logError(String command, ProcessResult processResult) {
            LOGGER.log(Level.WARNING, "Error executing command  ''{0}'': {1}!", new Object[]{
                        command, (processResult == null)
                        ? "?"
                        : new String(processResult.getStdErrBytes())});
        }

        private String getProcessPatternCommand(File file) {
            return getCommandPrefix() + getProgramPath() + RuntimeUtil.getDefaultCommandLineSeparator()
                    + RuntimeUtil.substitudePattern(file, getPattern());
        }

        private String getPattern() {
            String pattern = program.getPattern();
            return (program.isUsePattern() && (pattern != null) && !pattern.isEmpty())
                    ? program.getPattern()
                    : RuntimeUtil.PATTERN_FS_PATH;
        }

        private void processAll() {
            String command = getProcessAllCommand();
            logCommand(command);
            if (waitForTermination) {
                ProcessResult processResult = External.executeWaitForTermination(command, MAX_MILLISECONDS_UNTIL_TERMINATE);
                boolean terminatedWithErrors = processResult == null || processResult.getExitValue() != 0;
                if (terminatedWithErrors) {
                    logError(command, processResult);
                }
            } else {
                External.execute(command);
            }
            progressPerformed(imageFiles.size());
        }

        private String getProcessAllCommand() {
            String commandPrefix = getCommandPrefix();
            String programPath = getProgramPath();
            String separator = RuntimeUtil.getDefaultCommandLineSeparator();
            String additionalParameters = getAdditionalParameters(Bundle.getString(Execute.class, "StartPrograms.GetInput.Title"), 2);
            String commandlineParameters = program.getCommandlineParameters(imageFiles, additionalParameters, dlg.isParametersBeforeFilename());
            return commandPrefix + programPath + separator + commandlineParameters;
        }

        private void processSingle() {
            int count = 0;
            for (File file : imageFiles) {
                String command = getProcessSingleCommand(file, count);
                logCommand(command);
                if (waitForTermination) {
                    ProcessResult processResult = External.executeWaitForTermination(command, MAX_MILLISECONDS_UNTIL_TERMINATE);
                    boolean terminatedWithErrors = processResult == null || processResult.getExitValue() != 0;
                    if (terminatedWithErrors) {
                        logError(command, processResult);
                    }
                } else {
                    External.execute(command);
                }
                count++;
                progressPerformed(count);
            }
        }

        private String getProcessSingleCommand(File file, int count) {
            String commandPrefix = getCommandPrefix();
            String programPath = getProgramPath();
            String separator = RuntimeUtil.getDefaultCommandLineSeparator();
            List<File> files = Arrays.asList(file);
            String additionalParameters = getAdditionalParameters(file.getAbsolutePath(), count + 1);
            String commandlineParameters = program.getCommandlineParameters(files, additionalParameters, dlg.isParametersBeforeFilename());
            return commandPrefix + programPath + separator + commandlineParameters;
        }

        private String getProgramPath() {
            return RuntimeUtil.quoteForCommandLine(program.getFile());
        }

        private String getCommandPrefix() {
            return isMacApplication()
                    ? "open -a "
                    : "";
        }

        private boolean isMacApplication() {
            String filename = program.getFile().getName();
            String filenameLowerCase = filename.toLowerCase();
            return filenameLowerCase.endsWith(".app");
        }

        private String getAdditionalParameters(String filename, int count) {
            if (program.isUsePattern()) {
                return "";
            }
            if (!program.isInputBeforeExecute()) {
                return "";
            }
            if ((!program.isInputBeforeExecutePerFile() && (count > 1))) {
                return dlg.getParameters();
            }
            dlg.setProgram(program.getAlias());
            dlg.setFilename(filename);
            dlg.setVisible(true);
            if (dlg.isAccepted()) {
                return dlg.getParameters();
            }
            return "";
        }

        private synchronized void nextExecutor() {
            if (!queue.isEmpty()) {
                queue.poll().start();
            }
        }

        private void progressStarted() {
            ProgressEvent evt = new ProgressEvent.Builder()
                    .source(this)
                    .minimum(0)
                    .maximum(imageFiles.size())
                    .value(0)
                    .build();
            progressHandle = Lookup.getDefault().lookup(ProgressHandleFactory.class).createProgressHandle();
            progressHandle.progressStarted(evt);
        }

        private void progressPerformed(int value) {
            ProgressEvent evt = new ProgressEvent.Builder()
                    .source(this)
                    .minimum(0)
                    .maximum(imageFiles.size())
                    .value(value)
                    .build();
            progressHandle.progressPerformed(evt);
        }

        private void updateRepository() {
            if (program.isChangeFile()) {
                SaveToOrUpdateFilesInRepository updater = Lookup.getDefault().lookup(SaveToOrUpdateFilesInRepository.class).createInstance(imageFiles, SaveOrUpdate.OUT_OF_DATE);
                updater.saveOrUpdateWaitForTermination();
            }
        }
    }
}
