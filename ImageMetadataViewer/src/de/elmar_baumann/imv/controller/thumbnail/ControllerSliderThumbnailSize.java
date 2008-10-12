package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.event.ThumbnailsPanelAction;
import de.elmar_baumann.imv.event.ThumbnailsPanelListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Controls the slider which changes the size of the thumbnails
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/12
 */
public class ControllerSliderThumbnailSize extends Controller
    implements ChangeListener, ThumbnailsPanelListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private ThumbnailsPanel thumbnailsPanel = appPanel.getPanelImageFileThumbnails();
    private JSlider slider = appPanel.getSliderThumbnailSize();
    private int currentValue = 100;
    private int originalThumbnailWidth;

    public ControllerSliderThumbnailSize() {
        originalThumbnailWidth = thumbnailsPanel.getThumbnailWidth();
        thumbnailsPanel.addThumbnailsPanelListener(this);
        initSlider();
        slider.addChangeListener(this);
    }

    private void initSlider() {
        slider.setMinimum(25);
        slider.setMaximum(175);
        slider.setMajorTickSpacing(25);
        slider.setValue(currentValue);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (isStarted()) {
            int value = slider.getValue();
            synchronized (this) {
                if (value % 25 == 0 && value != currentValue) {
                    currentValue = value;
                    int width = (int) ((double) originalThumbnailWidth * ((double) value / 100.0));
                    thumbnailsPanel.setThumbnailWidth(width);
                }
            }
        }
    }

    @Override
    public void thumbnailSelected(ThumbnailsPanelAction action) {
    }

    @Override
    public void allThumbnailsDeselected(ThumbnailsPanelAction action) {
    }

    @Override
    public void thumbnailCountChanged() {
        originalThumbnailWidth = thumbnailsPanel.getThumbnailWidth();
    }
}
