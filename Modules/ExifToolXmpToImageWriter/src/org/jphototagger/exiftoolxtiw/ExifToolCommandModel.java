package org.jphototagger.exiftoolxtiw;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.lib.runtime.External;
import org.jphototagger.lib.runtime.ProcessResult;

/**
 * Executes a command with ExifTool where an image file is the last and only
 * variable parameter.
 *
 * @author Elmar Baumann
 */
public final class ExifToolCommandModel {

    private final Settings settings = new Settings();
    private final List<String> commandTokens = new ArrayList<>();
    private final Collection<File> files = new ArrayList<>();
    private long maxMillisecondsUntilInterrupt = 60000;

    public void setMaxMillisecondsUntilInterrupt(long milliseconds) {
        this.maxMillisecondsUntilInterrupt = milliseconds;
    }

    public void addToCommandTokens(String token) {
        Objects.requireNonNull(token, "token == null");

        commandTokens.add(token);
    }

    public void setFiles(Collection<? extends File> files) {
        Objects.requireNonNull(files, "files == null");

        this.files.clear();
        this.files.addAll(files);
    }

    public void execute() {
        for (File file : files) {
            processFile(file);
        }
    }

    private void processFile(File file) {
        String[] command = getCommand(file);

        Logger.getLogger(ExifToolCommandModel.class.getName()).log(Level.INFO, "Executing command {0}", ExifToolCommon.toString(command));

        ProcessResult processResult = External.executeWaitForTermination(command, maxMillisecondsUntilInterrupt);

        boolean terminatedWithErrors = processResult == null || processResult.getExitValue() != 0;

        if (terminatedWithErrors) {
            ExifToolCommon.logError(command, processResult);
        } else {
            ExifToolCommon.logSuccess(command);
        }
    }

    private String[] getCommand(File file) {
        List<String> cmdList = new ArrayList<>(commandTokens.size() + 2);

        cmdList.add(settings.getExifToolFilePath());
        cmdList.addAll(commandTokens);
        cmdList.add(file.getAbsolutePath());

        return cmdList.toArray(new String[cmdList.size()]);
    }
}
