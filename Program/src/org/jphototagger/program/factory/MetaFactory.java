/*
 * @(#)MetaFactory.java    Created on 2008-09-29
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.factory;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppWindowPersistence;
import org.jphototagger.program.app.update.UpdateDownload;
import org.jphototagger.program.tasks.ScheduledTaskBackupDatabase;
import org.jphototagger.program.UserSettings;

/**
 * Initalizes all other factories in the right order and sets the persistent
 * settings to the application's frame and panel.
 *
 * @author  Elmar Baumann
 */
public final class MetaFactory implements Runnable {
    public static final MetaFactory INSTANCE = new MetaFactory();
    private boolean                 init     = false;

    @Override
    public void run() {
        init();
    }

    private void init() {
        synchronized (this) {
            if (!Support.checkInit(getClass(), init)) {
                return;
            }

            init = true;
        }

        AppWindowPersistence appPersistence = new AppWindowPersistence();

        appPersistence.readAppFrameFromProperties();
        ModelFactory.INSTANCE.init();
        RendererFactory.INSTANCE.init();
        ControllerFactory.INSTANCE.init();
        ActionKeyListenerFactory.INSTANCE.init();
        MouseListenerFactory.INSTANCE.init();

        // No other factory after:
        TerminateFactory.INSTANCE.init();
        appPersistence.readAppPanelFromProperties();
        checkForDownload();
        ScheduledTaskBackupDatabase.INSTANCE.setBackup();
    }

    private void checkForDownload() {
        if (UserSettings.INSTANCE.isAutoDownloadNewerVersions()) {

            // Returning immediately
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(60 * 1000);
                        UpdateDownload.checkForNewerVersion();
                    } catch (Exception ex) {
                        AppLogger.logSevere(getClass(), ex);
                    }
                }
            }, "JPhotoTagger: Checking for a newer version").start();
        }
    }
}
