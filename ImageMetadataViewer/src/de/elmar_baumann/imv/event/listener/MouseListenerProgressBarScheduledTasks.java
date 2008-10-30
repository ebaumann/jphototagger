package de.elmar_baumann.imv.event.listener;

import de.elmar_baumann.imv.view.dialogs.UserSettingsDialog;
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
        showUserSettingsDialog();
    }

    private void showUserSettingsDialog() {
        UserSettingsDialog dialog = UserSettingsDialog.getInstance();
        dialog.selectTab(UserSettingsDialog.Tab.Tasks);
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
    }
}
