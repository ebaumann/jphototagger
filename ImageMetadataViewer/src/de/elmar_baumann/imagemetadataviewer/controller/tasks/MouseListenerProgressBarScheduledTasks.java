package de.elmar_baumann.imagemetadataviewer.controller.tasks;

import de.elmar_baumann.imagemetadataviewer.view.dialogs.UserSettingsDialog;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/25
 */
public class MouseListenerProgressBarScheduledTasks extends MouseAdapter {

    @Override
    public void mouseClicked(MouseEvent e) {
        UserSettingsDialog dialog = UserSettingsDialog.getInstance();
        dialog.selectTab(UserSettingsDialog.Tab.Tasks);
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
    }
}
