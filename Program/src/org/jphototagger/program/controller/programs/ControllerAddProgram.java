package org.jphototagger.program.controller.programs;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.helper.ProgramsHelper;
import org.jphototagger.program.resource.JptBundle;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerAddProgram extends AbstractAction {
    private static final long serialVersionUID = -8236351732517551399L;

    public ControllerAddProgram() {
        super(JptBundle.INSTANCE.getString("ControllerAddProgram.Name"));
        putValue(Action.SMALL_ICON, AppLookAndFeel.getIcon("icon_add.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ProgramsHelper.openSelectedFilesWidth(ProgramsHelper.addProgram(), false);
    }

}
