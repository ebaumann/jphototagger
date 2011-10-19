package org.jphototagger.program.app.logging;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.XMLFormatter;

import javax.swing.AbstractAction;

import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.logging.LogfileDialog;

/**
 * @author Elmar Baumann
 */
final class ShowErrorLogfileAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    ShowErrorLogfileAction() {
        super(Bundle.getString(ShowErrorLogfileAction.class, "ShowErrorLogfileAction.Name"));
        putValue(SMALL_ICON, IconUtil.getImageIcon(ShowErrorLogfileAction.class, "error.png"));
        putValue(ACCELERATOR_KEY, KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_L));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showErrorLogfile();
    }

    static void showErrorLogfile() {
        Frame parentFrame = ComponentUtil.findFrameWithIcon();
        String logfilePath = AppLoggingSystem.getErrorMessagesLogfilePath();
        LogfileDialog dialog = new LogfileDialog(parentFrame, logfilePath, XMLFormatter.class);

        dialog.setFilterableMinIntValue(Level.WARNING.intValue());
        dialog.setVisible(true);
    }
}
