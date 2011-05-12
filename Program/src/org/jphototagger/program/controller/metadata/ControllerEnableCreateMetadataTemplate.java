package org.jphototagger.program.controller.metadata;

import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.resource.GUI;

import org.jphototagger.lib.awt.EventQueueUtil;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerEnableCreateMetadataTemplate implements ThumbnailsPanelListener {
    public ControllerEnableCreateMetadataTemplate() {
        listen();
    }

    private void listen() {
        GUI.getThumbnailsPanel().addThumbnailsPanelListener(this);
    }

    @Override
    public void thumbnailsSelectionChanged() {
        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUI.getAppPanel().getButtonMetadataTemplateCreate().setEnabled(
                    GUI.getThumbnailsPanel().isAFileSelected());
            }
        });
    }

    @Override
    public void thumbnailsChanged() {

        // ignore
    }
}
