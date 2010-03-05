/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.Program;
import de.elmar_baumann.jpt.helper.InsertImageFilesIntoDatabase.Insert;
import de.elmar_baumann.jpt.io.IoUtil;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.view.dialogs.ProgramInputParametersDialog;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.runtime.External;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JProgressBar;

/**
 * Executes in a thread programs which processes image files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-06
 */
public final class StartPrograms {

    private final JProgressBar progressBar;
    private final Queue<Execute> queue = new ConcurrentLinkedQueue<Execute>();

    /**
     * Constructor.
     *
     * @param progressBar  progressbar or null, if the progress shouldn't be
     *                     displayed
     */
    public StartPrograms(JProgressBar progressBar) {
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
            MessageDisplayer.error(null, "StartPrograms.Error.Selection");
            return false;
        }
        return true;
    }

    private class Execute extends Thread {

        private Program program;
        private List<File> imageFiles;
        ProgramInputParametersDialog dialog = new ProgramInputParametersDialog();

        public Execute(Program program, List<File> imageFiles) {
            this.imageFiles = new ArrayList<File>(imageFiles);
            this.program = program;
            setName("Executing program " + program.getAlias() + " @ " + getClass().getSimpleName());
        }

        @Override
        public void run() {
            initProgressBar();
            if (program.isUsePattern()) {
                processPattern();
            }
            else if (program.isSingleFileProcessing()) {
                processSingle();
            } else {
                processAll();
            }
            updateDatabase();
            nextExecutor();
        }

        private void logCommand(String command) {
            AppLogger.logInfo(StartPrograms.class, "StartPrograms.Info.ExecuteCommand", command);
        }

        private void processPattern() {
            int count = 0;
            for (File file : imageFiles) {
                String command = getProcessPatternCommand(file);
                logCommand(command);
                External.ProcessResult result = External.execute(command, true);
                if (result == null || result.getExitValue() != 0) {
                    AppLogger.logWarning(Execute.class, "Execute.ExternalExcecute.Error", command, result == null ? "?" : result.getErrorStream());
                }
                setValueToProgressBar(++count);
            }
        }

        private String getProcessPatternCommand(File file) {
            return IoUtil.quoteForCommandLine(program.getFile()) +
                    IoUtil.getDefaultCommandLineSeparator() +
                    IoUtil.substitudePattern(file, program.getPattern());
        }

        private void processAll() {
            String command = getProcessAllCommand();
            logCommand(command);
            External.ProcessResult result = External.execute(command, true);
            if (result == null || result.getExitValue() != 0) {
                AppLogger.logWarning(Execute.class, "Execute.ExternalExcecute.Error", command, result == null ? "?" : result.getErrorStream());
            }
            setValueToProgressBar(imageFiles.size());
        }

        private String getProcessAllCommand() {
            return IoUtil.quoteForCommandLine(program.getFile()) +
                    IoUtil.getDefaultCommandLineSeparator() +
                    program.getCommandlineParameters(imageFiles,
                        getAdditionalParameters(JptBundle.INSTANCE.getString("StartPrograms.GetInput.Title"), 2),
                        dialog.isParametersBeforeFilename());
        }

        private void processSingle() {
            int count = 0;
            for (File file : imageFiles) {
                String command = getProcessSingleCommand(file, count);
                logCommand(command);
                External.ProcessResult result = External.execute(command, true);
                if (result == null || result.getExitValue() != 0) {
                    AppLogger.logWarning(Execute.class, "Execute.ExternalExcecute.Error", command, result == null ? "?" : result.getErrorStream());
                }
                setValueToProgressBar(++count);
            }
        }

        private String getProcessSingleCommand(File file, int count) {
            return IoUtil.quoteForCommandLine(program.getFile()) +
                    IoUtil.getDefaultCommandLineSeparator() +
                    program.getCommandlineParameters(
                        Arrays.asList(file),
                        getAdditionalParameters(file.getAbsolutePath(), count + 1),
                        dialog.isParametersBeforeFilename());
        }

        private String getAdditionalParameters(String filename, int count) {
            if (program.isUsePattern()) return "";
            if (!program.isInputBeforeExecute()) return "";
            if ((!program.isInputBeforeExecutePerFile() && count > 1)) return dialog.getParameters();

            dialog.setProgram(program.getAlias());
            dialog.setFilename(filename);
            dialog.setVisible(true);
            if (dialog.accepted()) {
                return dialog.getParameters();
            }
            return "";
        }

        private synchronized void nextExecutor() {
            if (!queue.isEmpty()) {
                queue.poll().start();
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
                        FileUtil.getAbsolutePathnames(imageFiles), Insert.OUT_OF_DATE);
                updater.run(); // no subsequent thread
            }
        }
    }
}
