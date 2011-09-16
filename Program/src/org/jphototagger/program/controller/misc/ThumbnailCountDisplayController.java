package org.jphototagger.program.controller.misc;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.jphototagger.domain.thumbnails.event.ThumbnailsChangedEvent;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;

/**
 * Zeigt die Anzahl der Thumbnails an.
 *
 * @author Elmar Baumann
 */
public final class ThumbnailCountDisplayController implements ChangeListener {

    private int thumbnailZoom;
    private int thumbnailCount;
    private int selectionCount;

    public ThumbnailCountDisplayController() {
        thumbnailZoom = getSlider().getValue();
        listen();
    }

    private void listen() {
        getSlider().addChangeListener(this);
        AnnotationProcessor.process(this);
    }

    private JSlider getSlider() {
        return GUI.getAppPanel().getSliderThumbnailSize();
    }

    @EventSubscriber(eventClass = ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(final ThumbnailsSelectionChangedEvent evt) {
        selectionCount = evt.getSelectionCount();
        setCount();
    }

    @EventSubscriber(eventClass = ThumbnailsChangedEvent.class)
    public void thumbnailsChanged(final ThumbnailsChangedEvent evt) {
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
                String info = Bundle.getString(ThumbnailCountDisplayController.class,
                        "ThumbnailCountDisplayController.Info", thumbnailCount, selectionCount, thumbnailZoom);

                label.setText(info);
                label.setToolTipText(info);
            }
        });
    }
}
