package de.elmar_baumann.imagemetadataviewer.controller.misc;

import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.event.ThumbnailsPanelAction;
import de.elmar_baumann.imagemetadataviewer.event.ThumbnailsPanelListener;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import de.elmar_baumann.imagemetadataviewer.view.panels.ImageFileThumbnailsPanel;
import javax.swing.JLabel;

/**
 * Zeigt die Anzahl der Thumbnails an. 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/25
 */
public class ControllerThumbnailCountDisplay extends Controller
    implements ThumbnailsPanelListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JLabel labelCount = appPanel.getLabelStatusbar();
    private ImageFileThumbnailsPanel panelThumbnails = appPanel.getPanelImageFileThumbnails();

    public ControllerThumbnailCountDisplay() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        panelThumbnails.addThumbnailsPanelListener(this);
    }

    @Override
    public void thumbnailSelected(ThumbnailsPanelAction action) {
        if (isStarted()) {
            setCount();
        }
    }

    @Override
    public void allThumbnailsDeselected(ThumbnailsPanelAction action) {
        if (isStarted()) {
            setCount();
        }
    }

    @Override
    public void thumbnailCountChanged() {
        // Nichts tun
    }

    private void setCount() {
        labelCount.setText(new Integer(
            panelThumbnails.getThumbnailCount()).toString() +
            Bundle.getString("ControllerThumbnailCount.InformationMessage.ImageFileCount"));
    }
}
