package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.UserSettingsDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;

/**
 * Focuses the fast search text field.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/10
 */
public class ControllerGotoFastSearch extends Controller implements ActionListener {

    private JTextField textFieldSearch = Panels.getInstance().getAppPanel().getTextFieldSearch();

    public ControllerGotoFastSearch() {
        Panels.getInstance().getAppFrame().getMenuItemGotoFastSearch().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            selectTextField();
        }
    }

    private void selectTextField() {
        if (textFieldSearch.isEnabled()) {
            textFieldSearch.requestFocus();
        } else {
            UserSettingsDialog settingsDialog = UserSettingsDialog.getInstance();
            settingsDialog.selectTab(UserSettingsDialog.Tab.FastSearch);
            if (settingsDialog.isVisible()) {
                settingsDialog.toFront();
            } else {
                settingsDialog.setVisible(true);
            }
        }
    }
}
