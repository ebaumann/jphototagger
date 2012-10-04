package org.jphototagger.program.settings;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public final class ShowUserSettingsDialogAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    public ShowUserSettingsDialogAction() {
        super(Bundle.getString(ShowUserSettingsDialogAction.class, "ShowUserSettingsDialogAction.Name"));
        putValue(SMALL_ICON, IconUtil.getImageIcon(ShowUserSettingsDialogAction.class, "settings.png"));
        putValue(ACCELERATOR_KEY, KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_S));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        ComponentUtil.show(SettingsDialog.INSTANCE);
    }
}
