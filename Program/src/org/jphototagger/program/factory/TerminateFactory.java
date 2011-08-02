package org.jphototagger.program.factory;

import org.jphototagger.program.app.AppCommandLineOptions;
import org.jphototagger.program.app.AppInit;
import org.jphototagger.program.controller.search.ControllerFastSearch;
import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.program.helper.ImportImageFiles;
import org.jphototagger.program.model.ComboBoxModelFileFilters;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.tasks.ScheduledTasks;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;
import java.io.File;
import java.io.FileFilter;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;

/**
 *
 * @author Elmar Baumann
 */
public final class TerminateFactory {
    static final TerminateFactory INSTANCE = new TerminateFactory();
    private volatile boolean init = false;

    void init() {
        synchronized (this) {
            if (!Support.checkInit(getClass(), init)) {
                return;
            }

            init = true;
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                AppPanel appPanel = GUI.getAppPanel();
                String message = Bundle.getString(TerminateFactory.class, "MiscFactory.Init.Start");
                Support.setStatusbarInfo(message);
                appPanel.getEditMetadataPanels().setAutocomplete();
                PopupMenuThumbnails.INSTANCE.setOtherPrograms();
                ScheduledTasks.INSTANCE.run();
                checkImportImageFiles();
                setAutocomplete();
                setTnPanelFileFilter();
                message = Bundle.getString(TerminateFactory.class, "MiscFactory.Init.Finished");
                Support.setStatusbarInfo(message);
            }
        });
    }

    private void setAutocomplete() {
        if (UserSettings.INSTANCE.isAutocomplete()) {
            ControllerFastSearch controller = ControllerFactory.INSTANCE.getController(ControllerFastSearch.class);

            if (controller != null) {
                controller.setAutocomplete(true);
            }
        }
    }

    private void checkImportImageFiles() {
        AppCommandLineOptions cmdLineOptions = AppInit.INSTANCE.getCommandLineOptions();

        if (cmdLineOptions.isImportImageFiles()) {
            String dirName = cmdLineOptions.getFileImportDir();
            File dir = null;

            if ((dirName != null) && new File(dirName).isDirectory()) {
                dir = new File(dirName);
            }

            ImportImageFiles.importFrom(dir);
        }
    }

    private void setTnPanelFileFilter() {
        ComboBoxModelFileFilters model = ModelFactory.INSTANCE.getModel(ComboBoxModelFileFilters.class);
        Object selItem = model.getSelectedItem();
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

        if (selItem instanceof FileFilter) {
            tnPanel.setFileFilter((FileFilter) selItem);
        } else if (selItem instanceof UserDefinedFileFilter) {
            tnPanel.setFileFilter(((UserDefinedFileFilter) selItem).getFileFilter());
        }
    }
}
