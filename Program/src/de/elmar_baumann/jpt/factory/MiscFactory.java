/*
 * @(#)MiscFactory.java    2008-10-16
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.factory;

import de.elmar_baumann.jpt.app.AppCommandLineOptions;
import de.elmar_baumann.jpt.app.AppInit;
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.controller.search.ControllerFastSearch;
import de.elmar_baumann.jpt.helper.ImportImageFiles;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.tasks.ScheduledTasks;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;
import de.elmar_baumann.lib.componentutil.MessageLabel;
import de.elmar_baumann.lib.io.FileUtil;

import java.io.File;

/**
 *
 * @author  Elmar Baumann
 */
public final class MiscFactory {
    static final MiscFactory INSTANCE = new MiscFactory();
    private volatile boolean init     = false;

    void init() {
        synchronized (this) {
            if (!Support.checkInit(getClass(), init)) {
                return;
            }

            init = true;
        }

        AppPanel appPanel = GUI.INSTANCE.getAppPanel();

        AppLogger.logFine(getClass(), "MiscFactory.Init.Start");
        appPanel.setStatusbarText(
            JptBundle.INSTANCE.getString("MiscFactory.Init.Start"),
            MessageLabel.MessageType.INFO, -1);
        appPanel.getEditMetadataPanels().setAutocomplete();
        PopupMenuThumbnails.INSTANCE.setOtherPrograms();
        ScheduledTasks.INSTANCE.run();
        checkImportImageFiles();
        setAutocomplete();
        AppLogger.logFine(getClass(), "MiscFactory.Init.Finished");
        appPanel.setStatusbarText(
            JptBundle.INSTANCE.getString("MiscFactory.Init.Finished"),
            MessageLabel.MessageType.INFO, 1000);
    }

    private void setAutocomplete() {
        if (UserSettings.INSTANCE.isAutocomplete()) {
            ControllerFactory.INSTANCE.getController(
                ControllerFastSearch.class).setAutocomplete(true);
        }
    }

    private void checkImportImageFiles() {
        AppCommandLineOptions cmdLineOptions =
            AppInit.INSTANCE.getCommandLineOptions();

        if (cmdLineOptions.isImportImageFiles()) {
            String dirName = cmdLineOptions.getFileImportDir();
            File   dir     = null;

            if ((dirName != null) && FileUtil.existsDirectory(dirName)) {
                dir = new File(dirName);
            }

            ImportImageFiles.importFrom(dir);
        }
    }
}
