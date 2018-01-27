package org.jphototagger.program.app.update;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.AppLookAndFeel;

/**
 * @author Elmar Baumann
 */
final class UpdateCheckAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    UpdateCheckAction() {
        super(Bundle.getString(UpdateCheckAction.class, "UpdateCheckAction.Name"));
        putValue(SMALL_ICON, AppLookAndFeel.getIcon("icon_refresh.png"));
    }

    @Override
    public void actionPerformed(ActionEvent ignored) {
        if (UpdateDownload.isCheckPending()) {
            String message = Bundle.getString(UpdateCheckAction.class, "UpdateCheckAction.Error.CheckDownload");
            MessageDisplayer.error(null, message);
        } else {
            UpdateDownload.checkForNewerVersion();
        }
    }
}
