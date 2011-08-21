package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.domain.event.UserPropertyChangedEvent;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.UserSettings;
import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JSlider;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.thumbnails.event.ThumbnailsChangedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Controls the slider which changes the size of the thumbnails
 *
 * @author Elmar Baumann
 */
public final class ControllerSliderThumbnailSize implements AWTEventListener, ChangeListener {

    private static final int STEP_WIDTH = 1;
    private static final int LARGER_STEP_WIDTH = 10;
    private static final int MIN_MAGINFICATION_PERCENT = 10;
    private static final int MAX_MAGINFICATION_PERCENT = 100;
    private static final String KEY_SLIDER_VALUE = "org.jphototagger.program.controller.thumbnail.ControllerSliderThumbnailSize." + "SliderValue";
    private int currentValue = 100;

    public ControllerSliderThumbnailSize() {
        initSlider();
        listen();
    }

    private void listen() {
        getSlider().addChangeListener(this);
        AnnotationProcessor.process(this);
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
    }

    private JSlider getSlider() {
        return GUI.getAppPanel().getSliderThumbnailSize();
    }

    private int getMaxTnWidth() {
        return UserSettings.INSTANCE.getMaxThumbnailWidth();
    }

    private void initSlider() {
        readProperties();

        JSlider slider = getSlider();

        slider.setMinimum(MIN_MAGINFICATION_PERCENT);
        slider.setMaximum(MAX_MAGINFICATION_PERCENT);
        slider.setMajorTickSpacing(STEP_WIDTH);
        slider.setMinorTickSpacing(STEP_WIDTH);
        slider.setValue(currentValue);
        setThumbnailWidth();
    }

    @Override
    public void stateChanged(ChangeEvent evt) {
        handleSliderMoved();
    }

    @Override
    public void eventDispatched(AWTEvent awtEvent) {
        KeyEvent keyEvent = (KeyEvent) awtEvent;

        if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
            keyPressed(keyEvent);
        }
    }

    private void keyPressed(KeyEvent evt) {
        if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_PLUS)) {
            moveSlider(LARGER_STEP_WIDTH, true);
        } else if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_MINUS)) {
            moveSlider(LARGER_STEP_WIDTH, false);
        }
    }

    private void moveSlider(int stepWidth, boolean increase) {
        if (increase) {
            addToSliderValue(stepWidth);
        } else {
            addToSliderValue(-stepWidth);
        }
    }

    private void addToSliderValue(int increment) {
        JSlider slider = getSlider();
        int value = slider.getValue();
        int newValue = Math.min(Math.max(value + increment, MIN_MAGINFICATION_PERCENT), MAX_MAGINFICATION_PERCENT);

        slider.setValue(newValue);
    }

    @EventSubscriber(eventClass=ThumbnailsChangedEvent.class)
    public void thumbnailsChanged(final ThumbnailsChangedEvent evt) {
        setThumbnailWidth();
    }

    @EventSubscriber(eventClass = UserPropertyChangedEvent.class)
    public void applySettings(UserPropertyChangedEvent evt) {
        if (UserPropertyChangedEvent.PROPERTY_MAX_THUMBNAIL_WIDTH.equals(evt.getProperty())) {
            setThumbnailWidth();
        }
    }

    private void handleSliderMoved() {
        int value = getSlider().getValue();

        // value % STEP_WIDTH == 0 is not necessary as long as STEP_WIDTH == 1
        if ( /* value % STEP_WIDTH == 0 && */value != currentValue) {
            currentValue = value;
            writeProperties();
            setThumbnailWidth();
        }
    }

    private void readProperties() {
        Integer value = UserSettings.INSTANCE.getSettings().getInt(KEY_SLIDER_VALUE);

        if (!value.equals(Integer.MIN_VALUE)) {
            currentValue = value;
        }
    }

    private void setThumbnailWidth() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                int width = (int) ((double) getMaxTnWidth() * ((double) currentValue / 100.0));

                GUI.getThumbnailsPanel().setThumbnailWidth(width);
            }
        });
    }

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().set(KEY_SLIDER_VALUE, currentValue);
        UserSettings.INSTANCE.writeToFile();
    }
}
