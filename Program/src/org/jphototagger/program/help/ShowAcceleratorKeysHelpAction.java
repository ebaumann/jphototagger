package org.jphototagger.program.help;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.AppLookAndFeel;

/**
 * @author Elmar Baumann
 */
final class ShowAcceleratorKeysHelpAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    ShowAcceleratorKeysHelpAction() {
        super(Bundle.getString(ShowAcceleratorKeysHelpAction.class, "ShowAcceleratorKeysHelpAction.Name"));
        putValue(SMALL_ICON, AppLookAndFeel.getIcon("icon_keyboard.png"));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        HelpBrowserDisplayer.browseHelp(Bundle.getString(ShowAcceleratorKeysHelpAction.class, "ShowAcceleratorKeysHelpAction.HelpPage"));
    }
}
