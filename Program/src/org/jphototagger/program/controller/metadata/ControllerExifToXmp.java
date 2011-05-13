package org.jphototagger.program.controller.metadata;

import org.jphototagger.program.helper.SetExifToXmp;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerExifToXmp implements ActionListener {
    public ControllerExifToXmp() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemExifToXmp().addActionListener(this);
        GUI.getAppPanel().getButtonExifToXmp().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        processSelectedFiles();
    }

    private void processSelectedFiles() {
        final List<File> selFiles = GUI.getSelectedImageFiles();

        if (selFiles.size() > 0) {
            new SetExifToXmp(selFiles, true).start();
        }
    }
}
