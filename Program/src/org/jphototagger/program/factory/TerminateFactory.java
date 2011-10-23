package org.jphototagger.program.factory;

import java.io.File;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.FileImportService;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppCommandLineOptions;
import org.jphototagger.program.app.AppInit;
import org.jphototagger.program.module.thumbnails.ThumbnailsPopupMenu;
import org.jphototagger.program.tasks.ScheduledTasks;

/**
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
                String message = Bundle.getString(TerminateFactory.class, "MiscFactory.Init.Start");
                Support.setStatusbarInfo(message);
                ThumbnailsPopupMenu.INSTANCE.setOtherPrograms();
                ScheduledTasks.INSTANCE.run();
                checkImportImageFiles();
                message = Bundle.getString(TerminateFactory.class, "MiscFactory.Init.Finished");
                Support.setStatusbarInfo(message);
            }
        });
    }

    private boolean isAutocomplete() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                ? storage.getBoolean(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                : true;
    }

    private void checkImportImageFiles() {
        AppCommandLineOptions cmdLineOptions = AppInit.INSTANCE.getCommandLineOptions();

        if (cmdLineOptions.isImportImageFiles()) {
            String dirName = cmdLineOptions.getFileImportDir();
            File dir = null;

            if ((dirName != null) && new File(dirName).isDirectory()) {
                dir = new File(dirName);
            }

            FileImportService service = Lookup.getDefault().lookup(FileImportService.class);

            service.importFilesFromDirectory(dir);
        }
    }
}
