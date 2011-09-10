package org.jphototagger.program.controller.programs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.domain.database.programs.Program;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.helper.ProgramsHelper;
import org.jphototagger.program.helper.StartPrograms;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

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

    private void openSelectedImages() {
        Program program = DatabasePrograms.INSTANCE.getDefaultImageOpenProgram();

        if (program == null) {
            String message = Bundle.getString(ControllerOpenFilesWithOtherApp.class, "ControllerOpenFilesWithStandardApp.Info.DefineOpenApp");
            MessageDisplayer.information(null, message);
            ProgramsHelper.openSelectedFilesWidth(ProgramsHelper.addProgram(), false);
        } else {
            new StartPrograms(null).startProgram(program, GUI.getSelectedImageFiles(), false);
        }
    }
}
