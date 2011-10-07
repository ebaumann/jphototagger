package org.jphototagger.program.module.programs;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.AppLookAndFeel;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class AddProgramController extends AbstractAction {

    private static final long serialVersionUID = -8236351732517551399L;

    public AddProgramController() {
        super(Bundle.getString(AddProgramController.class, "AddProgramController.Name"));
        putValue(Action.SMALL_ICON, AppLookAndFeel.getIcon("icon_add.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ProgramsHelper.openSelectedFilesWidth(ProgramsHelper.addProgram(), false);
    }
}
