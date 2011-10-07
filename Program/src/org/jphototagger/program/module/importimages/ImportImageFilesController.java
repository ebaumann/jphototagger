package org.jphototagger.program.module.importimages;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jphototagger.program.resource.GUI;

/**
 *
 * @author Elmar Baumann
 */
public final class ImportImageFilesController implements ActionListener {

    public ImportImageFilesController() {
        listen();
    }

    private void listen() {
        GUI.getAppFrame().getMenuItemImportImageFiles().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        ImportImageFiles.importFrom(null);
    }
}
