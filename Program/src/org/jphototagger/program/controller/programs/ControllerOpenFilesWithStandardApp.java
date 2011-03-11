package org.jphototagger.program.controller.programs;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.helper.ProgramsHelper;
import org.jphototagger.program.helper.StartPrograms;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerOpenFilesWithStandardApp implements ActionListener {
    public ControllerOpenFilesWithStandardApp() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemOpenFilesWithStandardApp().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        openSelectedImages();
    }

    public static void openSelectedImages() {
        Program program = DatabasePrograms.INSTANCE.getDefaultImageOpenProgram();

        if (program == null) {
            MessageDisplayer.information(null, "ControllerOpenFilesWithStandardApp.Info.DefineOpenApp");
            ProgramsHelper.openSelectedFilesWidth(ProgramsHelper.addProgram());
        } else {
            new StartPrograms(null).startProgram(program, GUI.getSelectedImageFiles());
        }
    }
}
