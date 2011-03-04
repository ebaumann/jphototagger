package org.jphototagger.program.controller.filesystem;

import org.jphototagger.program.helper.ImportImageFiles;
import org.jphototagger.program.resource.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerImportImageFiles implements ActionListener {
    public ControllerImportImageFiles() {
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
