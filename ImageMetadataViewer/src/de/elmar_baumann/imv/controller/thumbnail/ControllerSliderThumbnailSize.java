package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.event.ThumbnailsPanelAction;
import de.elmar_baumann.imv.event.ThumbnailsPanelListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.persistence.PersistentSettings;
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
    private static final int stepWidth = 10;
    private static final int maxMaginficationPercent = 150;
    private static final String keySliderValue = ControllerSliderThumbnailSize.class.getName() + "." + "SliderValue";
    private int currentValue = 100;
    private int originalThumbnailWidth;

    public ControllerSliderThumbnailSize() {
        originalThumbnailWidth = thumbnailsPanel.getThumbnailWidth();
        thumbnailsPanel.addThumbnailsPanelListener(this);
        initSlider();
        slider.addChangeListener(this);
    }

    private void initSlider() {
        readPersistent();
        slider.setMinimum(stepWidth);
        slider.setMaximum(maxMaginficationPercent);
        slider.setMajorTickSpacing(stepWidth);
        slider.setMinorTickSpacing(stepWidth);
        slider.setValue(currentValue);
        if (currentValue != 100 && originalThumbnailWidth > 0) {
            setThumbnailWidth();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (isStarted()) {
            int value = slider.getValue();
            synchronized (this) {
                if (value % stepWidth == 0 && value != currentValue) {
                    currentValue = value;
                    writePersistent();
                    setThumbnailWidth();
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

    private void readPersistent() {
        Integer value = PersistentSettings.getInstance().getInt(keySliderValue);
        if (!value.equals(Integer.MIN_VALUE)) {
            currentValue = value;
        }
    }

    private void setThumbnailWidth() {
        int width = (int) ((double) originalThumbnailWidth * ((double) currentValue / 100.0));
        thumbnailsPanel.setThumbnailWidth(width);
    }
    
    private void writePersistent() {
        PersistentSettings.getInstance().setInt(currentValue, keySliderValue);
    }
}
