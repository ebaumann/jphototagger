package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.event.ThumbnailsPanelAction;
import de.elmar_baumann.imv.event.ThumbnailsPanelListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
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
    public void thumbnailsChanged() {
        // Nichts tun
    }

    private void setCount() {
        labelCount.setText(new Integer(
            panelThumbnails.getCount()).toString() +
            Bundle.getString("ControllerThumbnailCount.InformationMessage.ImageFileCount"));
    }
}
