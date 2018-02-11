package org.jphototagger.exiftoolxtiw;

import java.awt.Frame;
import java.io.File;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.lib.runtime.ProcessResult;
import org.jphototagger.lib.swing.InputDialog2;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
public final class ExifToolCommon {

    public static InputDialog2 createSettingsDialog() {
        Frame frame = ComponentUtil.findFrameWithIcon();
        InputDialog2 dlg = new InputDialog2(frame, true);

        dlg.setTitle(Bundle.getString(ExifTooolXmpToImageWriterController.class, "ExifToolCommon.SettingsDlg.Title"));
        dlg.setComponent(new SettingsPanel());
        dlg.getButtonCancel().setVisible(false);
        dlg.pack();

        return dlg;
    }

    /**
     * Checks whether ExifTool can be executed. If not, a configure dialog will
     * be shown.
     *
     * @param settings settngs
     *
     * @return true, if ExifTool can be executed
     */
    public static boolean checkExecute(Settings settings) {
        Objects.requireNonNull(settings, "settings == null");

        if (!checkCanExecute(settings, false)) {
            if (MessageDisplayer.confirmYesNo(ComponentUtil.findFrameWithIcon(), Bundle.getString(ExifToolCommon.class, "ExifToolCommon.ConfirmConfigure"))) {
                InputDialog2 dlg = ExifToolCommon.createSettingsDialog();

                dlg.setLocationRelativeTo(ComponentUtil.findFrameWithIcon());
                dlg.setVisible(true);

                return dlg.isAccepted()
                        ? checkCanExecute(settings, true)
                        : false;
            }
        }

        return true;
    }

    public static boolean checkCanExecute(Settings settings, boolean log) {
        Objects.requireNonNull(log, "log == null");

        if (!settings.isSelfResponsible()) {
            if (log) {
                Logger.getLogger(ExifToolCommon.class.getName()).log(Level.WARNING, "User does not take self-responsibility of modifying image files. Cancelling.");
            }
            return false;
        }

        if (!settings.isExifToolEnabled()) {
            if (log) {
                Logger.getLogger(ExifToolCommon.class.getName()).log(Level.WARNING, "ExifTool is not enabled. Cancelling.");
            }
            return false;
        }

        if (ExifToolCommon.getExifToolFilePath(settings) == null) {
            if (log) {
                Logger.getLogger(ExifToolCommon.class.getName()).log(Level.WARNING, "Exif tool excecutable not accessible: {0}. Cancelling.", settings.getExifToolFilePath());
            }
            return false;
        }

        return true;
    }

    private static String getExifToolFilePath(Settings settings) {
        String exifToolFilePath = settings.getExifToolFilePath();

        if (!StringUtil.hasContent(exifToolFilePath)) {
            return null;
        }

        File et = new File(exifToolFilePath);

        return et.isFile()
                ? et.getAbsolutePath()
                : null;
    }

    /**
     * @param command command
     *
     * @return Loggable command as string
     */
    public static String toString(String[] command) {
        Objects.requireNonNull(command, "command == null");

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < command.length; i++) {
            String cmdToken = command[i];
            if (i == 0) {
                sb.append(cmdToken);
            } else {
                sb.append(" \"").append(cmdToken).append("\"");
            }
        }

        return sb.toString();
    }

    public static void logError(String[] command, ProcessResult processResult) {
        Objects.requireNonNull(command, "command == null");

        Logger.getLogger(ExifToolCommon.class.getName()).log(Level.WARNING, "Error executing command  ''{0}'': {1}!", new Object[]{
            toString(command), (processResult == null)
            ? "?"
            : new String(processResult.getStdErrBytes())});
    }

    public static void logSuccess(String[] command) {
        Objects.requireNonNull(command, "command == null");

        Logger.getLogger(ExifToolCommon.class.getName()).log(Level.INFO, "Successfully executed command ''{0}''", new Object[]{toString(command)});
    }

    private ExifToolCommon() {
    }
}
