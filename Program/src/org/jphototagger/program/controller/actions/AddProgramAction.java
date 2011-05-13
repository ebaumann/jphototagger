package org.jphototagger.program.controller.actions;

import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.dialogs.ProgramPropertiesDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class AddProgramAction extends AbstractAction {
    private static final long serialVersionUID = -8236351732517551399L;

    public AddProgramAction() {
        super(JptBundle.INSTANCE.getString("ControllerAddAction.Name"));
        putValue(Action.SMALL_ICON, AppLookAndFeel.getIcon("icon_add.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        addProgram();
    }

    private void addProgram() {
        ProgramPropertiesDialog dlg = new ProgramPropertiesDialog(true);

        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            Program program = dlg.getProgram();

            DatabasePrograms.INSTANCE.insert(program);
        }
    }
}
