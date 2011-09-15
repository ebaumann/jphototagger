package org.jphototagger.program.controller.metadata;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppFileFilters;
import org.jphototagger.program.helper.ExtractEmbeddedXmp;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.FileEditorDialog;
import org.jphototagger.program.view.panels.FileEditorPanel;

/**
 * Starts a {@link org.jphototagger.program.view.dialogs.FileEditorDialog} with
 * an {@link org.jphototagger.program.helper.ExtractEmbeddedXmp} editor.
 *
 * @author Elmar Baumann
 */
public final class ExtractEmbeddedXmpController implements ActionListener {

    public ExtractEmbeddedXmpController() {
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
        panel.setTitle(Bundle.getString(ExtractEmbeddedXmpController.class, "ExtractEmbeddedXmpController.Panel.Title"));
        panel.setDescription(Bundle.getString(ExtractEmbeddedXmpController.class, "ExtractEmbeddedXmpController.Panel.Description"));
        panel.setDirChooserFileFilter(AppFileFilters.INSTANCE.getAllAcceptedImageFilesFilter());
        panel.setSelectDirs(true);
        setHelpPage(dlg);
        dlg.setVisible(true);
    }

    private void setHelpPage(FileEditorDialog dlg) {
        // Has to be localized!
        dlg.setHelpPageUrl("import_xmp.html");
    }
}
