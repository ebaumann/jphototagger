package org.jphototagger.program.module.imagecollections;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.imagecollections.ImageCollection;
import org.jphototagger.domain.imagecollections.ImageCollectionService;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.app.ui.AppPanel;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ImageCollectionService.class)
public final class ImageCollectionServiceImpl implements ImageCollectionService {

    @Override
    public void selectPreviousImportedFiles() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                AppPanel appPanel = GUI.getAppPanel();

                appPanel.getTabbedPaneSelection().setSelectedComponent(appPanel.getTabSelectionImageCollections());
                GUI.getAppPanel().getListImageCollections().setSelectedValue(
                        ImageCollection.PREVIOUS_IMPORT_NAME, true);
            }
        });
    }
}
