package org.jphototagger.program.controller.metadata;

import org.jphototagger.program.app.AppFileFilters;
import org.jphototagger.program.helper.ExtractEmbeddedXmp;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.dialogs.FileEditorDialog;
import org.jphototagger.program.view.panels.FileEditorPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Starts a {@link org.jphototagger.program.view.dialogs.FileEditorDialog} with
 * an {@link org.jphototagger.program.helper.ExtractEmbeddedXmp} editor.
 *
 * @author Elmar Baumann
 */
public final class ControllerExtractEmbeddedXmp implements ActionListener {
    public ControllerExtractEmbeddedXmp() {
        listen();
    }

    private void listen() {
        GUI.getAppFrame().getMenuItemExtractEmbeddedXmp().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        showDialog();
    }

    private void showDialog() {
        FileEditorDialog dlg = new FileEditorDialog();
        FileEditorPanel panel = dlg.getFileEditorPanel();

        panel.setEditor(new ExtractEmbeddedXmp());
        panel.setTitle(JptBundle.INSTANCE.getString("ControllerExtractEmbeddedXmp.Panel.Title"));
        panel.setDescription(JptBundle.INSTANCE.getString("ControllerExtractEmbeddedXmp.Panel.Description"));
        panel.setDirChooserFileFilter(AppFileFilters.INSTANCE.getAllAcceptedImageFilesFilter());
        panel.setSelectDirs(true);
        dlg.setHelpPageUrl(JptBundle.INSTANCE.getString("Help.Url.ExtractEmbeddedXmp"));
        dlg.setVisible(true);
    }
}
