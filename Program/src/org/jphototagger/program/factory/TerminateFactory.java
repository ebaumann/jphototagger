/*
 * @(#)TerminateFactory.java    Created on 2008-10-16
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

package org.jphototagger.program.factory;

import org.jphototagger.lib.componentutil.MessageLabel;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.program.app.AppCommandLineOptions;
import org.jphototagger.program.app.AppInit;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.controller.search.ControllerFastSearch;
import org.jphototagger.program.data.UserDefinedFileFilter;
import org.jphototagger.program.helper.ImportImageFiles;
import org.jphototagger.program.model.ComboBoxModelFileFilters;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.tasks.ScheduledTasks;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author  Elmar Baumann
 */
public final class TerminateFactory {
    static final TerminateFactory INSTANCE = new TerminateFactory();
    private volatile boolean      init     = false;

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
        setTnPanelFileFilter();
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

    private void setTnPanelFileFilter() {
        ComboBoxModelFileFilters model =
            ModelFactory.INSTANCE.getModel(ComboBoxModelFileFilters.class);
        Object          selItem = model.getSelectedItem();
        ThumbnailsPanel tnPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();

        if (selItem instanceof FileFilter) {
            tnPanel.setFileFilter((FileFilter) selItem);
        } else if (selItem instanceof UserDefinedFileFilter) {
            tnPanel.setFileFilter(
                ((UserDefinedFileFilter) selItem).getFileFilter());
        }
    }
}
