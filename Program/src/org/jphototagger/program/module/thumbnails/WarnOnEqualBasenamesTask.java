package org.jphototagger.program.module.thumbnails;

import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.lib.comparator.FilepathIgnoreCaseAscendingComparator;
import org.jphototagger.lib.io.FileUtil;

/**
 * @author Elmar Baumann
 */
public final class WarnOnEqualBasenamesTask extends Thread {

    private final Collection<File> files;
    private final String excludeSuffix = ".xmp";

    public WarnOnEqualBasenamesTask(Collection<? extends File> files) {
        super("JPhotoTagger: Warning on equal Basenames");

        if (files == null) {
            throw new NullPointerException("files == null");
        }

        if (excludeSuffix == null) {
            throw new NullPointerException("excludeSuffix == null");
        }

        this.files = new ArrayList<File>(files);
    }

    @Override
    public void run() {
        final List<File> filesWithEqualBasenames = FileUtil.getFilesWithEqualBasenames(files, excludeSuffix);

        if (!filesWithEqualBasenames.isEmpty()) {
            Logger.getLogger(WarnOnEqualBasenamesTask.class.getName()).log(Level.WARNING,
                    "Files with equal basenames will have the same XMP sidecar file: {0}", filesWithEqualBasenames);

            Collections.sort(filesWithEqualBasenames, new FilepathIgnoreCaseAscendingComparator());

           EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    WarnOnEqualBasenamesTaskDialog dialog = new WarnOnEqualBasenamesTaskDialog(filesWithEqualBasenames);

                    dialog.setVisible(true);
                }
            });
        }
    }
}
