package org.jphototagger.program.controller.programs;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.helper.StartPrograms;
import org.jphototagger.program.view.dialogs.SettingsDialog;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jphototagger.program.resource.GUI;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerOpenFilesWithStandardApp
        implements ActionListener {
    public ControllerOpenFilesWithStandardApp() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemOpenFilesWithStandardApp()
            .addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (checkOpenAppIsDefined()) {
            openFiles();
        }
    }

    private void openFiles() {
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

        if (!tnPanel.isFileSelected()) {
            return;
        }

        Program program =
            DatabasePrograms.INSTANCE.getDefaultImageOpenProgram();

        if (program != null) {
            new StartPrograms(null).startProgram(program,
                              tnPanel.getSelectedFiles());
        }
    }

    private boolean checkOpenAppIsDefined() {
        if (DatabasePrograms.INSTANCE.getDefaultImageOpenProgram() == null) {
            SettingsDialog dlg = SettingsDialog.INSTANCE;

            dlg.selectTab(SettingsDialog.Tab.PROGRAMS);
            ComponentUtil.show(dlg);

            return false;
        }

        return true;
    }
}
