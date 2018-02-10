package org.jphototagger.exiftoolxtiw;

import java.awt.Frame;
import org.jphototagger.lib.swing.InputDialog2;
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

    private ExifToolCommon() {
    }
}
