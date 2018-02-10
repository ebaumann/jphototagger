package org.jphototagger.iptcmodule;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public final class ShowIptcToXmpDialogAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    public ShowIptcToXmpDialogAction() {
        super(Bundle.getString(ShowIptcToXmpDialogAction.class, "ShowIptcToXmpDialogAction.Name"));
        putValue(SMALL_ICON, org.jphototagger.resources.Icons.getIcon("icon_file.png"));
        putValue(ACCELERATOR_KEY, KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_I));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        IptcToXmpDialog dlg = new IptcToXmpDialog();
        dlg.setVisible(true);
    }
}
