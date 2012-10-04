package org.jphototagger.program.app.update;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
final class UpdateCheckAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    UpdateCheckAction() {
        super(Bundle.getString(UpdateCheckAction.class, "UpdateCheckAction.Name"));
        putValue(SMALL_ICON, IconUtil.getImageIcon(UpdateCheckAction.class, "check_update.png"));
    }

    @Override
    public void actionPerformed(ActionEvent ignored) {
        if (UpdateDownload.isCheckPending()) {
            String message = Bundle.getString(UpdateCheckAction.class, "UpdateCheckAction.Error.CheckDownload");
            MessageDisplayer.error(null, message);
        } else {
            String message = Bundle.getString(UpdateCheckAction.class, "UpdateCheckAction.Info");
            MessageDisplayer.information(null, message);
            UpdateDownload.checkForNewerVersion();
        }
    }
}
