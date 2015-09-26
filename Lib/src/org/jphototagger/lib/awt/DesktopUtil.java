package org.jphototagger.lib.awt;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.swing.FileChooserHelper;
import org.jphototagger.lib.swing.FileChooserProperties;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class DesktopUtil {

    public static final Logger LOGGER = Logger.getLogger(DesktopUtil.class.getName());

    /**
     * Tries to open a file with the desktop. Opens a runtime process with a program, if not possible.
     * @param file
     * @param prefrencesKeyForAlternateProgram null if choosen browser shall not persisted
     */
    public static void open(File file, String prefrencesKeyForAlternateProgram) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        boolean failed = false;
        boolean chooseProgramDenied = false;
        try {
            Desktop.getDesktop().open(file);
        } catch (Throwable t) {
            String confirmMessage = Bundle.getString(DesktopUtil.class, "DesktopUtil.Open.ConfirmChooseProgram");
            String programPath = resolveProgramPath(prefrencesKeyForAlternateProgram + ".Open", confirmMessage);
            chooseProgramDenied = programPath == null;
            if (programPath != null) {
                try {
                    String[] commandArray = new String[]{programPath, file.getAbsolutePath()};
                    runProgram(commandArray);
                } catch (Throwable t2) {
                    failed = true;
                    LOGGER.log(Level.SEVERE, null, t2);
                }
            } else {
                failed = true;
                LOGGER.log(Level.SEVERE, null, t);
            }
        }

        String confirmMessage = Bundle.getString(DesktopUtil.class, "DesktopUtil.Open.ConfirmRetry");
        if (failed && !chooseProgramDenied && MessageDisplayer.confirmYesNo(ComponentUtil.findFrameWithIcon(), confirmMessage)) {
            open(file, prefrencesKeyForAlternateProgram); // Recursive
        }
    }

    /**
     * Tries to browse with the desktop. Opens a runtime process with a browser, if not possible.
     * @param uriString
     * @param prefrencesKeyForAlternateBrowser null if choosen browser shall not persisted
     */
    public static void browse(String uriString, String prefrencesKeyForAlternateBrowser) {
        if (uriString == null) {
            throw new NullPointerException("uriString == null");
        }

        boolean failed = false;

        try {
            URI uri = new URI(uriString);
            Desktop.getDesktop().browse(uri);
        } catch (Throwable t) {
            String confirmMessage = Bundle.getString(DesktopUtil.class, "DesktopUtil.Browse.ConfirmChooseProgram");
            String browserPath = resolveProgramPath(prefrencesKeyForAlternateBrowser + ".Browser", confirmMessage);
            if (browserPath != null) {
                try {
                    String[] commandArray = new String[]{browserPath, uriString};
                    runProgram(commandArray);
                } catch (Throwable t2) {
                    failed = true;
                    LOGGER.log(Level.SEVERE, null, t2);
                }
            } else {
                failed = true;
                LOGGER.log(Level.SEVERE, null, t);
            }
        }

        if (failed && MessageDisplayer.confirmYesNo(ComponentUtil.findFrameWithIcon(), "DesktopUtil.Browse.ConfirmRetry")) {
            browse(uriString, prefrencesKeyForAlternateBrowser); // Recursive
        }
    }

    private static String resolveProgramPath(String key, String confirmMessage) {
        String prefString = readStringFromPreferences(key);
        if (StringUtil.hasContent(prefString)) {
            return prefString;
        }

        if (MessageDisplayer.confirmYesNo(ComponentUtil.findFrameWithIcon(), confirmMessage)) {
            File programFile = chooseProgramFile();
            String programPath = programFile == null ? null : programFile.getAbsolutePath();

            if (programPath != null) {
                writeStringToPreferences(key, programPath);
            }

            return programPath == null ? null : programPath;

        } else {
            return null;
        }
    }

    private static String readStringFromPreferences(String key) {
        if (key == null) {
            return null;
        }

        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs != null) {
            return prefs.getString(key);
        }
        return null;
    }

    private static void writeStringToPreferences(String key, String value) {
        if (key == null) {
            return;
        }

        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs != null) {
            prefs.setString(key, value);
        }
    }

    private static File chooseProgramFile() {
        FileChooserProperties fcProps = new FileChooserProperties();

        fcProps.dialogTitle(Bundle.getString(DesktopUtil.class, "DesktopUtil.ChooseProgramFileChooserTitle"));
        fcProps.multiSelectionEnabled(false);
        fcProps.fileSelectionMode(JFileChooser.FILES_ONLY);

        return FileChooserHelper.chooseFile(fcProps);
    }

    private static void runProgram(String[] commandArray) throws IOException {
        logCommand(commandArray);
        Runtime runtime = Runtime.getRuntime();
        runtime.exec(commandArray);
    }

    private static void logCommand(String[] commandArray) {
        StringBuilder command = new StringBuilder();

        for (String token : commandArray) {
            command.append('"');
            command.append(token);
            command.append('"');
            command.append(" ");
        }

        LOGGER.log(Level.INFO, "Executing desktop command {0}", command.toString());
    }

    private DesktopUtil() {
    }
}
