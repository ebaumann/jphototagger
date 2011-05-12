package org.jphototagger.program.controller.misc;

import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;


import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JLabel;
import javax.swing.JSlider;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Zeigt die Anzahl der Thumbnails an.
 *
 * @author Elmar Baumann
 */
public final class ControllerThumbnailCountDisplay implements ThumbnailsPanelListener, ChangeListener {
    private int thumbnailZoom;
    private int thumbnailCount;
    private int selectionCount;

    public ControllerThumbnailCountDisplay() {
        thumbnailZoom = getSlider().getValue();
        listen();
    }

    private void listen() {
        GUI.getThumbnailsPanel().addThumbnailsPanelListener(this);
        getSlider().addChangeListener(this);
    }

    private JSlider getSlider() {
        return GUI.getAppPanel().getSliderThumbnailSize();
    }

    @Override
    public void thumbnailsSelectionChanged() {
        selectionCount = GUI.getThumbnailsPanel().getSelectionCount();
        setCount();
    }

    @Override
    public void thumbnailsChanged() {
        setCount();
    }

    @Override
    public void stateChanged(ChangeEvent evt) {
        setZoom();
    }

    private void setCount() {
        thumbnailCount = GUI.getThumbnailsPanel().getFileCount();
        setLabel();
    }

    private void setZoom() {
        thumbnailZoom = getSlider().getValue();
        setLabel();
    }

    private void setLabel() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                JLabel label = GUI.getAppPanel().getLabelThumbnailInfo();
                String info = JptBundle.INSTANCE.getString("ControllerThumbnailCountDisplay.Info", thumbnailCount,
                                  selectionCount, thumbnailZoom);

                label.setText(info);
                label.setToolTipText(info);
            }
        });
    }
}
