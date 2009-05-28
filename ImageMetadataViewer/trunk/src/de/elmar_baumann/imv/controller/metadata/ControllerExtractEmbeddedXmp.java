package de.elmar_baumann.imv.controller.metadata;

import de.elmar_baumann.imv.app.AppFileFilter;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.tasks.ExtractEmbeddedXmpEditor;
import de.elmar_baumann.imv.view.dialogs.FileEditorDialog;
import de.elmar_baumann.imv.view.panels.FileEditorPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Starts a {@link de.elmar_baumann.imv.view.dialogs.FileEditorDialog} with
 * an {@link de.elmar_baumann.imv.tasks.ExtractEmbeddedXmpEditor} editor.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/05/22
 */
public final class ControllerExtractEmbeddedXmp implements ActionListener {

    public ControllerExtractEmbeddedXmp() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemExtractEmbeddedXmp().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showDialog();
    }

    private void showDialog() {
        FileEditorDialog dialog = new FileEditorDialog(GUI.INSTANCE.getAppFrame());
        FileEditorPanel panel = dialog.getFileEditorPanel();
        panel.setEditor(new ExtractEmbeddedXmpEditor());
        panel.setTitle(Bundle.getString("ControllerExtractEmbeddedXmp.Panel.Title"));
        panel.setDescription(Bundle.getString("ControllerExtractEmbeddedXmp.Panel.Description"));
        panel.setDirChooserFileFilter(AppFileFilter.acceptedImageFileFormats);
        panel.setSelectDirs(true);
        dialog.setHelpPageUrl(Bundle.getString("Help.Url.ExtractEmbeddedXmp"));
        dialog.setVisible(true);
    }

}
