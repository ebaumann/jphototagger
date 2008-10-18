package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.event.ThumbnailsPanelAction;
import de.elmar_baumann.imv.event.ThumbnailsPanelListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import java.text.MessageFormat;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Zeigt die Anzahl der Thumbnails an. 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/25
 */
public class ControllerThumbnailCountDisplay extends Controller
        implements ThumbnailsPanelListener, ChangeListener {

    private static final MessageFormat msg = new MessageFormat(Bundle.getString("ControllerThumbnailCount.InformationMessage"));
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JSlider sliderThumbnailSize = appPanel.getSliderThumbnailSize();
    private JLabel label = appPanel.getLabelStatusbar();
    private ImageFileThumbnailsPanel panelThumbnails = appPanel.getPanelThumbnails();
    private int thumbnailCount = 0;
    private int thumbnailZoom = sliderThumbnailSize.getValue();

    public ControllerThumbnailCountDisplay() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        panelThumbnails.addThumbnailsPanelListener(this);
        sliderThumbnailSize.addChangeListener(this);
    }

    @Override
    public void selectionChanged(ThumbnailsPanelAction action) {
        if (isStarted()) {
            setCount();
        }
    }

    @Override
    public void thumbnailsChanged() {
    }

    private void setCount() {
        thumbnailCount = panelThumbnails.getCount();
        setLabel();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (isStarted()) {
            thumbnailZoom = sliderThumbnailSize.getValue();
            setLabel();
        }
    }

    private void setLabel() {
        label.setText(msg.format(new Object[]{thumbnailCount, thumbnailZoom}));
    }
}
