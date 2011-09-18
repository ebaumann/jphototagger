package org.jphototagger.program.controller.metadata;

import javax.swing.JButton;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.AppPanel;

/**
 *
 * @author Elmar Baumann
 */
public final class EnableCreateMetadataTemplateController {

    public EnableCreateMetadataTemplateController() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(final ThumbnailsSelectionChangedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                AppPanel appPanel = GUI.getAppPanel();
                JButton buttonMetadataTemplateCreate = appPanel.getButtonMetadataTemplateCreate();
                boolean aFileIsSelected = evt.isAFileSelected();

                buttonMetadataTemplateCreate.setEnabled(aFileIsSelected);
            }
        });
    }
}