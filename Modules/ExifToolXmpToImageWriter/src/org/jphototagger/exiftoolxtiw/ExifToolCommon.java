package org.jphototagger.exiftoolxtiw;

import java.awt.Frame;
import java.util.Objects;
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

    private ExifToolCommon() {
    }
}
