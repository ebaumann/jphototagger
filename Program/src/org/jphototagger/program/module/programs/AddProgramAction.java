package org.jphototagger.program.module.programs;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.openide.util.Lookup;

import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.AppLookAndFeel;

/**
 * @author Elmar Baumann
 */
public final class AddProgramAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final ProgramsRepository repo = Lookup.getDefault().lookup(ProgramsRepository.class);

    public AddProgramAction() {
        super(Bundle.getString(AddProgramAction.class, "AddProgramAction.Name"));
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

            repo.saveProgram(program);
        }
    }
}
