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
 * @author Elmar Baumann
 */
public final class ExifToolCommandModel {

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

    public void setFiles(Collection<File> files) {
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
            ExifToolCommon.logError(getClass(), command, processResult);
        } else {
            ExifToolCommon.logSuccess(getClass(), command);
        }
    }

    private String[] getCommand(File file) {
        List<String> cmdList = new ArrayList<>(commandTokens);

        cmdList.add(file.getAbsolutePath());

        return cmdList.toArray(new String[cmdList.size()]);
    }

}
