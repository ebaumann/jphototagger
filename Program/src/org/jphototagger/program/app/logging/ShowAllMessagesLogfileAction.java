package org.jphototagger.program.app.logging;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import javax.swing.AbstractAction;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.logging.LogfileDialog;

/**
 * @author Elmar Baumann
 */
public final class ShowAllMessagesLogfileAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public ShowAllMessagesLogfileAction() {
        super(Bundle.getString(ShowAllMessagesLogfileAction.class, "ShowAllMessagesLogfileAction.Name"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showAllMessagesLogfile();
    }

    static void showAllMessagesLogfile() {
        Frame parentFrame = ComponentUtil.findFrameWithIcon();
        String logfilePath = AppLoggingSystem.getAllMessagesLogfilePath();
        LogfileDialog dialog = new LogfileDialog(parentFrame, logfilePath, SimpleFormatter.class);

        dialog.setFilterableMinIntValue(Level.WARNING.intValue());
        dialog.setVisible(true);
    }
}
