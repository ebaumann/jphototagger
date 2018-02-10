package org.jphototagger.program.help;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.resources.Icons;

/**
 * @author Elmar Baumann
 */
final class ShowHelpAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    ShowHelpAction() {
        super(Bundle.getString(ShowHelpAction.class, "ShowHelpAction.Name"));
        putValue(SMALL_ICON, Icons.getIcon("icon_help.png"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        HelpBrowserDisplayer.browseHelp("");
    }
}
