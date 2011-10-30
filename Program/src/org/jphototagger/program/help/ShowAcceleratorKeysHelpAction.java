package org.jphototagger.program.help;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
final class ShowAcceleratorKeysHelpAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    ShowAcceleratorKeysHelpAction() {
        super(Bundle.getString(ShowAcceleratorKeysHelpAction.class, "ShowAcceleratorKeysHelpAction.Name"));
        putValue(SMALL_ICON, IconUtil.getImageIcon(ShowAcceleratorKeysHelpAction.class, "keyboard.png"));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        HelpBrowserDisplayer.browseHelp(Bundle.getString(ShowAcceleratorKeysHelpAction.class, "ShowAcceleratorKeysHelpAction.HelpPage"));
    }
}
