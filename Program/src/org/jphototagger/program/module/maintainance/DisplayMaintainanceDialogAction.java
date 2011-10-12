package org.jphototagger.program.module.maintainance;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.AppLookAndFeel;

/**
 * @author Elmar Baumann
 */
public final class DisplayMaintainanceDialogAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    public static final DisplayMaintainanceDialogAction INSTANCE = new DisplayMaintainanceDialogAction();

    private DisplayMaintainanceDialogAction() {
        super(Bundle.getString(DisplayMaintainanceDialogAction.class, "DisplayMaintainanceDialogAction.Name"));

        putValue(Action.SMALL_ICON, AppLookAndFeel.getIcon("icon_settings.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MaintainanceDialog dialog = new MaintainanceDialog();

        dialog.setVisible(true);
    }
}
