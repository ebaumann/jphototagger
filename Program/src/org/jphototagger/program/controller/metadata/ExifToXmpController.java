package org.jphototagger.program.controller.metadata;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import org.jphototagger.program.helper.SetExifToXmp;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.ThumbnailsPopupMenu;

/**
 *
 * @author Elmar Baumann
 */
public final class ExifToXmpController implements ActionListener {

    public ExifToXmpController() {
        listen();
    }

    private void listen() {
        ThumbnailsPopupMenu.INSTANCE.getItemExifToXmp().addActionListener(this);
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
