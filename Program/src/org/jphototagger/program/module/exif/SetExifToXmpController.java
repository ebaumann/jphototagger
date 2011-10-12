package org.jphototagger.program.module.exif;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.module.thumbnails.ThumbnailsPopupMenu;

/**
 * @author Elmar Baumann
 */
public final class SetExifToXmpController implements ActionListener {

    public SetExifToXmpController() {
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
            SetExifToXmp setExifToXmp = new SetExifToXmp(selFiles, true);
            setExifToXmp.start();
        }
    }
}
