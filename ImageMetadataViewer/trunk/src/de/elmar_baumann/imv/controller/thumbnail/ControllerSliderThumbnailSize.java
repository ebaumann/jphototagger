package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.event.listener.impl.ListenerProvider;
import de.elmar_baumann.imv.event.ThumbnailsPanelEvent;
import de.elmar_baumann.imv.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.listener.UserSettingsChangeListener;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Controls the slider which changes the size of the thumbnails
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/12
 */
public final class ControllerSliderThumbnailSize
        implements ChangeListener, ThumbnailsPanelListener,
                   UserSettingsChangeListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final ThumbnailsPanel thumbnailsPanel =
            appPanel.getPanelThumbnails();
    private final JSlider slider = appPanel.getSliderThumbnailSize();
    private static final int stepWidth = 10;
    private static final int maxMaginficationPercent = 100;
    private static final String keySliderValue =
            ControllerSliderThumbnailSize.class.getName() + "." + "SliderValue";
    private int currentValue = 100;
    private int maxThumbnailWidth =
            UserSettings.INSTANCE.getMaxThumbnailLength();

    public ControllerSliderThumbnailSize() {
        initSlider();
        listen();
    }

    private void listen() {
        thumbnailsPanel.addThumbnailsPanelListener(this);
        slider.addChangeListener(this);
        ListenerProvider.INSTANCE.addUserSettingsChangeListener(this);
    }

    private void initSlider() {
        readProperties();
        slider.setMinimum(stepWidth);
        slider.setMaximum(maxMaginficationPercent);
        slider.setMajorTickSpacing(stepWidth);
        slider.setMinorTickSpacing(stepWidth);
        slider.setValue(currentValue);
        setThumbnailWidth();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        handleSliderMoved();
    }

    @Override
    public void selectionChanged(ThumbnailsPanelEvent action) {
    }

    @Override
    public void thumbnailsChanged() {
        setThumbnailWidth();
    }

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        if (evt.getType().equals(
                UserSettingsChangeEvent.Type.MAX_THUMBNAIL_WIDTH)) {
            maxThumbnailWidth = UserSettings.INSTANCE.getMaxThumbnailLength();
            setThumbnailWidth();
        }
    }

    private void handleSliderMoved() {
        int value = slider.getValue();
        synchronized (this) {
            if (value % stepWidth == 0 && value != currentValue) {
                currentValue = value;
                writeProperties();
                setThumbnailWidth();
            }
        }
    }

    private void readProperties() {
        Integer value = UserSettings.INSTANCE.getSettings().getInt(
                keySliderValue);
        if (!value.equals(Integer.MIN_VALUE)) {
            currentValue = value;
        }
    }

    private void setThumbnailWidth() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                int width = (int) ((double) maxThumbnailWidth *
                        ((double) currentValue / 100.0));
                thumbnailsPanel.setThumbnailWidth(width);
            }
        });
    }

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().setInt(currentValue, keySliderValue);
        UserSettings.INSTANCE.writeToFile();
    }
}
