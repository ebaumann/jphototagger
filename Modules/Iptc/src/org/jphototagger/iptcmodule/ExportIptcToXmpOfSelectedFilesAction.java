package org.jphototagger.iptcmodule;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.lib.api.AppIconProvider;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ExportIptcToXmpOfSelectedFilesAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final List<File> selectedFiles = new ArrayList<>();

    public ExportIptcToXmpOfSelectedFilesAction() {
        super(Bundle.getString(ExportIptcToXmpOfSelectedFilesAction.class, "ExportIptcToXmpOfSelectedFilesAction.Name"));
        putValue(SMALL_ICON, Lookup.getDefault().lookup(AppIconProvider.class).getIcon("icon_file.png"));
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        processSelectedFiles();
    }

    private void processSelectedFiles() {
        if (selectedFiles.size() > 0) {
            IptcToXmpDialog dlg = new IptcToXmpDialog();

            dlg.setFiles(selectedFiles);
            dlg.setVisible(true);
        }
    }

    @EventSubscriber(eventClass = ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(ThumbnailsSelectionChangedEvent evt) {
        selectedFiles.clear();
        selectedFiles.addAll(evt.getSelectedFiles());
        setEnabled(!selectedFiles.isEmpty());
    }
}
