package org.jphototagger.program.controller.programs;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.helper.StartPrograms;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.SettingsDialog;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
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
        if (isOpenAppDefined(true)) {
            openSelectedFiles();
        }
    }

    public static void openSelectedFiles() {
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

        if (!tnPanel.isFileSelected()) {
            return;
        }

        Program program = DatabasePrograms.INSTANCE.getDefaultImageOpenProgram();

        if (program != null) {
            new StartPrograms(null).startProgram(program, tnPanel.getSelectedFiles());
        }
    }

    public static boolean isOpenAppDefined(boolean displayDefineDialog) {
        boolean appIsDefined = DatabasePrograms.INSTANCE.getDefaultImageOpenProgram() != null;

        if (!appIsDefined && displayDefineDialog) {
            MessageDisplayer.information(null, "ControllerOpenFilesWithStandardApp.Info.DefineOpenApp");

            SettingsDialog dlg = SettingsDialog.INSTANCE;

            dlg.selectTab(SettingsDialog.Tab.PROGRAMS);
            ComponentUtil.show(dlg);
        }

        return appIsDefined;
    }
}
