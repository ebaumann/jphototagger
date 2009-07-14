package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Zeigt die Anzahl der Thumbnails an. 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/25
 */
public final class ControllerThumbnailCountDisplay
        implements ThumbnailsPanelListener, ChangeListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JSlider sliderThumbnailSize =
            appPanel.getSliderThumbnailSize();
    private final JLabel label = appPanel.getLabelStatusbar();
    private final ImageFileThumbnailsPanel panelThumbnails =
            appPanel.getPanelThumbnails();
    private int thumbnailCount = 0;
    private int thumbnailZoom = sliderThumbnailSize.getValue();

    public ControllerThumbnailCountDisplay() {
        listen();
    }

    private void listen() {
        panelThumbnails.addThumbnailsPanelListener(this);
        sliderThumbnailSize.addChangeListener(this);
    }

    @Override
    public void thumbnailsSelectionChanged() {
        // ignore
    }

    @Override
    public void thumbnailsChanged() {
        setCount();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        setZoom();
    }

    private void setCount() {
        thumbnailCount = panelThumbnails.getFileCount();
        setLabel();
    }

    private void setZoom() {
        thumbnailZoom = sliderThumbnailSize.getValue();
        setLabel();
    }

    private void setLabel() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                label.setText(Bundle.getString(
                        "ControllerThumbnailCountDisplay.Info", // NOI18N
                        thumbnailCount, thumbnailZoom));
            }
        });
    }
}
