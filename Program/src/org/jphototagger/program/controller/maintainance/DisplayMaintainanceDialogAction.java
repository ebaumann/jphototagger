package org.jphototagger.program.controller.maintainance;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.dialogs.MaintainanceDialog;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class DisplayMaintainanceDialogAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
    public static final DisplayMaintainanceDialogAction INSTANCE = new DisplayMaintainanceDialogAction();

    private DisplayMaintainanceDialogAction() {
        super(JptBundle.INSTANCE.getString("DisplayMaintainanceDialogAction.Name"));

        putValue(Action.SMALL_ICON, AppLookAndFeel.getIcon("icon_settings.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MaintainanceDialog dialog = new MaintainanceDialog();

        dialog.setVisible(true);
    }
}
