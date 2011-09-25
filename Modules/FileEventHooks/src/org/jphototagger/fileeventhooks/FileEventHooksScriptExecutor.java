package org.jphototagger.fileeventhooks;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.file.event.FileCopiedEvent;
import org.jphototagger.api.file.event.FileDeletedEvent;
import org.jphototagger.api.file.event.FileMovedEvent;
import org.jphototagger.api.file.event.FileRenamedEvent;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.lib.util.StringUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class FileEventHooksScriptExecutor {

    private static final Logger LOGGER = Logger.getLogger(FileEventHooksScriptExecutor.class.getName());
    private String fileCopiedScript;
    private String fileDeletedScript;
    private String fileMovedScript;
    private String fileRenamedScript;

    public FileEventHooksScriptExecutor() {
        initScriptFiles();
        AnnotationProcessor.process(this);
    }

    private void initScriptFiles() {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);

        fileCopiedScript = preferences.getString(FileEventHooksPreferencesKeys.FILE_COPIED_KEY);
        fileDeletedScript = preferences.getString(FileEventHooksPreferencesKeys.FILE_DELETED_KEY);
        fileMovedScript = preferences.getString(FileEventHooksPreferencesKeys.FILE_MOVED_KEY);
        fileRenamedScript = preferences.getString(FileEventHooksPreferencesKeys.FILE_RENAMED_KEY);
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void userPreferenceChanged(PreferencesChangedEvent evt) {
        String key = evt.getKey();
        String stringValue = evt.getNewValue() == null ? "" : evt.getNewValue().toString();

        if (FileEventHooksPreferencesKeys.FILE_COPIED_KEY.equals(key)) {
            fileCopiedScript = stringValue;
        } else if (FileEventHooksPreferencesKeys.FILE_DELETED_KEY.equals(key)) {
            fileDeletedScript = stringValue;
        } else if (FileEventHooksPreferencesKeys.FILE_MOVED_KEY.equals(key)) {
            fileMovedScript = stringValue;
        } else if (FileEventHooksPreferencesKeys.FILE_RENAMED_KEY.equals(key)) {
            fileRenamedScript = stringValue;
        }
    }

    @EventSubscriber(eventClass = FileCopiedEvent.class)
    public void fileCopied(FileCopiedEvent evt) {
        File sourceFile = evt.getSourceFile();
        File targetFile = evt.getTargetFile();

        executeScript(fileCopiedScript, sourceFile, targetFile);
    }

    @EventSubscriber(eventClass = FileDeletedEvent.class)
    public void fileDeleted(FileDeletedEvent evt) {
        File file = evt.getFile();

        executeScript(fileDeletedScript, file, null);
    }

    @EventSubscriber(eventClass = FileMovedEvent.class)
    public void fileMoved(FileMovedEvent evt) {
        File sourceFile = evt.getSourceFile();
        File targetFile = evt.getTargetFile();

        executeScript(fileMovedScript, sourceFile, targetFile);
    }

    @EventSubscriber(eventClass = FileRenamedEvent.class)
    public void fileRenamed(FileRenamedEvent evt) {
        File sourceFile = evt.getSourceFile();
        File targetFile = evt.getTargetFile();

        executeScript(fileRenamedScript, sourceFile, targetFile);
    }

    private void executeScript(String script, File fromFile, File toFile) {
        if (!StringUtil.hasContent(script) || isSidecarFile(fromFile)) {
            return;
        }

        if (!checkScriptExists(script)) {
            return;
        }

        String[] commandArray = createCommandArray(script, fromFile, toFile);
        Runtime runtime = Runtime.getRuntime();

        logCommand(commandArray);
        try {
            runtime.exec(commandArray);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private String[] createCommandArray(String script, File fromFile, File toFile) {
        String[] command = new String[toFile == null ? 2 : 3];
        String fromFilePath = fromFile.getAbsolutePath();

        command[0] = script;
        command[1] = fromFilePath;

        if (toFile != null) {
            String toFilePath = toFile.getAbsolutePath();

            command[2] = toFilePath;
        }

        return command;
    }

    private boolean isSidecarFile(File fromFile) {
        String filenameLowercase = fromFile.getName().toLowerCase();

        return filenameLowercase.endsWith(".xmp");
    }

    private void logCommand(String[] commandArray) {
        StringBuilder command = new StringBuilder();

        for (String token : commandArray) {
            command.append('"');
            command.append(token);
            command.append('"');
            command.append(" ");
        }

        LOGGER.log(Level.INFO, "Executing file hook command {0}", command.toString());
    }

    private boolean checkScriptExists(String script) {
        if (!new File(script).isFile()) {
            LOGGER.log(Level.WARNING, "File Hook script ''{0}'' does not exist", script);
            return false;
        }

        return true;
    }
}
