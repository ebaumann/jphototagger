package org.jphototagger.exiftoolxtiw;

import java.awt.Frame;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.lib.runtime.ProcessResult;
import org.jphototagger.lib.swing.InputDialog2;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;

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
     * @param  model model
     * @return true, if ExifTool can be exected
     */
    public static boolean checkExecute(ExifTooolXmpToImageWriterModel model) {
        Objects.requireNonNull(model, "model == null");

        if (!model.checkCanExecute(false)) {
            if (MessageDisplayer.confirmYesNo(ComponentUtil.findFrameWithIcon(), Bundle.getString(ExifToolProcessFilesPlugin.class, "ExifToolCommon.ConfirmConfigure"))) {
                InputDialog2 dlg = ExifToolCommon.createSettingsDialog();

                dlg.setLocationRelativeTo(ComponentUtil.findFrameWithIcon());
                dlg.setVisible(true);

                return dlg.isAccepted()
                        ? model.checkCanExecute(true)
                        : false;
            }
        }

        return true;
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

    public static void logError(Class<?> srcClass, String[] command, ProcessResult processResult) {
        Objects.requireNonNull(srcClass, "srcClass == null");
        Objects.requireNonNull(command, "command == null");
        Objects.requireNonNull(processResult, "processResult == null");

        Logger.getLogger(srcClass.getName()).log(Level.WARNING, "Error executing command  ''{0}'': {1}!", new Object[]{
                    toString(command), (processResult == null)
                    ? "?"
                    : new String(processResult.getStdErrBytes())});
    }

    public static void logSuccess(Class<?> srcClass, String[] command) {
        Logger.getLogger(srcClass.getName()).log(Level.INFO, "Successfully executed command ''{0}''", new Object[]{toString(command)});
    }

    private ExifToolCommon() {
    }
}
