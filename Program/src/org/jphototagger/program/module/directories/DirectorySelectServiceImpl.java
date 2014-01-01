package org.jphototagger.program.module.directories;

import java.awt.Component;
import java.io.File;
import javax.swing.JTabbedPane;
import org.jphototagger.domain.DirectorySelectService;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.AllSystemDirectoriesTreeModel;
import org.jphototagger.program.app.ui.AppPanel;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.resource.GUI;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = DirectorySelectService.class)
public final class DirectorySelectServiceImpl implements DirectorySelectService {

    @Override
    public void selectDirectory(final File dir) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                AppPanel appPanel = GUI.getAppPanel();
                JTabbedPane tabbedPaneSelection = appPanel.getTabbedPaneSelection();
                Component tabTreeDirectories = appPanel.getTabSelectionDirectories();
                tabbedPaneSelection.setSelectedComponent(tabTreeDirectories);
                AllSystemDirectoriesTreeModel model = ModelFactory.INSTANCE.getModel(AllSystemDirectoriesTreeModel.class);
                if (model != null) {
                    model.expandToFile(dir, true);
                }
            }
        });
    }
}
