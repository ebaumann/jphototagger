package de.elmar_baumann.imv.event.listener.impl;

import de.elmar_baumann.imv.view.dialogs.UserSettingsDialog;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/25
 */
public final class MouseListenerProgressBarScheduledTasks extends MouseAdapter {

    @Override
    public void mouseClicked(MouseEvent e) {
        showUserSettingsDialog();
    }

    private void showUserSettingsDialog() {
        UserSettingsDialog dialog = UserSettingsDialog.INSTANCE;
        dialog.selectTab(UserSettingsDialog.Tab.TASKS);
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
    }
}
