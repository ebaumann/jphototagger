package org.jphototagger.program.misc;

import javax.swing.JLabel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.thumbnails.event.ThumbnailZoomChangedEvent;
import org.jphototagger.domain.thumbnails.event.ThumbnailsChangedEvent;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.settings.AppPreferencesKeys;

/**
 * @author Elmar Baumann
 */
public final class ThumbnailCountDisplayController {

    private int thumbnailZoom;
    private int thumbnailCount;
    private int selectionCount;

    public ThumbnailCountDisplayController() {
        initThumbnailZoom();
        listen();
    }

    private void initThumbnailZoom() {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        Integer value = preferences.getInt(AppPreferencesKeys.KEY_THUMBNAILS_ZOOM);

        thumbnailZoom = value.equals(Integer.MIN_VALUE) ? 100 : value;
    }

    private void listen() {
        AnnotationProcessor.process(this);
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

    @EventSubscriber(eventClass = ThumbnailZoomChangedEvent.class)
    public void thumbnailZoomChanged(ThumbnailZoomChangedEvent evt) {
        thumbnailZoom = evt.getZoomValue();
        setLabel();
    }

    private void setCount() {
        thumbnailCount = GUI.getThumbnailsPanel().getFileCount();
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
