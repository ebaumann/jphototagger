package org.jphototagger.exifmodule;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractAction;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public final class SetExifToXmpAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final Collection<File> selectedFiles = new ArrayList<File>();

    public SetExifToXmpAction() {
        super(Bundle.getString(SetExifToXmpAction.class, "SetExifToXmpAction.Name"));
        putValue(SMALL_ICON, IconUtil.getImageIcon(SetExifToXmpAction.class, "xmp.png"));
        setEnabled(false);
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(ThumbnailsSelectionChangedEvent evt) {
        selectedFiles.clear();
        selectedFiles.addAll(evt.getSelectedFiles());
        setEnabled(!selectedFiles.isEmpty());
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        processSelectedFiles();
    }

    private void processSelectedFiles() {
        if (!selectedFiles.isEmpty()) {
            boolean replaceExistingXmpData = true;
            SetExifToXmp setExifToXmp = new SetExifToXmp(selectedFiles, replaceExistingXmpData);
            setExifToXmp.start();
        }
    }
}
