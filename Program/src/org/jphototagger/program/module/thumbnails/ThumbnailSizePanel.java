package org.jphototagger.program.module.thumbnails;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.api.windows.StatusLineElementProvider;
import org.jphototagger.domain.thumbnails.event.ThumbnailZoomChangedEvent;
import org.jphototagger.domain.thumbnails.event.ThumbnailsChangedEvent;
import org.jphototagger.image.thumbnail.ThumbnailDefaults;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = StatusLineElementProvider.class)
public class ThumbnailSizePanel extends javax.swing.JPanel implements AWTEventListener, ChangeListener, StatusLineElementProvider {

    private static final long serialVersionUID = 1L;
    private static final int STEP_WIDTH = 1;
    private static final int LARGER_STEP_WIDTH = 10;
    private static final int MIN_MAGINFICATION_PERCENT = 10;
    private static final int MAX_MAGINFICATION_PERCENT = 100;
    private int currentValue = 100;

    public ThumbnailSizePanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        initSlider();
        listen();
    }

    private void listen() {
        thumbnailSizeSlider.addChangeListener(this);
        AnnotationProcessor.process(this);
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
    }

    private int getMaxTnWidth() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        int width = prefs.getInt(Preferences.KEY_MAX_THUMBNAIL_WIDTH);

        return (width != Integer.MIN_VALUE)
                ? width
                : ThumbnailDefaults.DEFAULT_THUMBNAIL_WIDTH;
    }

    private void initSlider() {
        readProperties();

        thumbnailSizeSlider.setMinimum(MIN_MAGINFICATION_PERCENT);
        thumbnailSizeSlider.setMaximum(MAX_MAGINFICATION_PERCENT);
        thumbnailSizeSlider.setMajorTickSpacing(STEP_WIDTH);
        thumbnailSizeSlider.setMinorTickSpacing(STEP_WIDTH);
        thumbnailSizeSlider.setValue(currentValue);
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
        int value = thumbnailSizeSlider.getValue();
        int newValue = Math.min(Math.max(value + increment, MIN_MAGINFICATION_PERCENT), MAX_MAGINFICATION_PERCENT);

        thumbnailSizeSlider.setValue(newValue);
    }

    @EventSubscriber(eventClass = ThumbnailsChangedEvent.class)
    public void thumbnailsChanged(final ThumbnailsChangedEvent evt) {
        setThumbnailWidth();
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void applySettings(PreferencesChangedEvent evt) {
        if (Preferences.KEY_MAX_THUMBNAIL_WIDTH.equals(evt.getKey())) {
            setThumbnailWidth();
        }
    }

    private void handleSliderMoved() {
        int value = thumbnailSizeSlider.getValue();

        // value % STEP_WIDTH == 0 is not necessary as long as STEP_WIDTH == 1
        if ( /* value % STEP_WIDTH == 0 && */value != currentValue) {
            currentValue = value;
            writeProperties();
            setThumbnailWidth();
            EventBus.publish(new ThumbnailZoomChangedEvent(thumbnailSizeSlider, currentValue));
        }
    }

    private void readProperties() {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        Integer value = preferences.getInt(AppPreferencesKeys.KEY_THUMBNAILS_ZOOM);

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
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        preferences.setInt(AppPreferencesKeys.KEY_THUMBNAILS_ZOOM, currentValue);
    }

    private void showThumbnailDimensionsDialog() {
        ThumbnailDimensionsSettingsDialog dialog = new ThumbnailDimensionsSettingsDialog();
        dialog.setVisible(true);
    }

    @Override
    public Component getStatusLineElement() {
        return this;
    }

    @Override
    public int getPosition() {
        return 900;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        thumbnailSizeSlider = new javax.swing.JSlider();
        thumbnailDimensionsButton = new javax.swing.JButton();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        thumbnailSizeSlider.setMajorTickSpacing(5);
        thumbnailSizeSlider.setMinimum(10);
        thumbnailSizeSlider.setSnapToTicks(true);
        thumbnailSizeSlider.setToolTipText(Bundle.getString(getClass(), "ThumbnailSizePanel.thumbnailSizeSlider.toolTipText")); // NOI18N
        thumbnailSizeSlider.setName("thumbnailSizeSlider"); // NOI18N
        thumbnailSizeSlider.setPreferredSize(org.jphototagger.resources.UiFactory.dimension(200, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        add(thumbnailSizeSlider, gridBagConstraints);

        thumbnailDimensionsButton.setIcon(org.jphototagger.resources.Icons.getIcon("icon_thumbnail_size.png"));
        thumbnailDimensionsButton.setToolTipText(Bundle.getString(getClass(), "ThumbnailSizePanel.thumbnailDimensionsButton.toolTipText")); // NOI18N
        thumbnailDimensionsButton.setBorder(null);
        thumbnailDimensionsButton.setContentAreaFilled(false);
        thumbnailDimensionsButton.setName("thumbnailDimensionsButton"); // NOI18N
        thumbnailDimensionsButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thumbnailDimensionsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        add(thumbnailDimensionsButton, gridBagConstraints);
    }//GEN-END:initComponents

    private void thumbnailDimensionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thumbnailDimensionsButtonActionPerformed
        showThumbnailDimensionsDialog();
    }//GEN-LAST:event_thumbnailDimensionsButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton thumbnailDimensionsButton;
    private javax.swing.JSlider thumbnailSizeSlider;
    // End of variables declaration//GEN-END:variables
}
