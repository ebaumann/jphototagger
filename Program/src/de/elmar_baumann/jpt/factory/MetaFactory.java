/*
 * JPhotoTagger tags and finds images fast
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

import de.elmar_baumann.jpt.app.AppWindowPersistence;
import de.elmar_baumann.jpt.app.update.UpdateDownload;

/**
 * Initalizes all other factories in the right order and sets the persistent
 * settings to the application's frame and panel.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-29
 */
public final class MetaFactory implements Runnable {

    public static final MetaFactory INSTANCE = new MetaFactory();
    private             boolean     init     = false;

    @Override
    public void run() {
        init();
    }

    private void init() {
        synchronized (this) {
            if(!Support.checkInit(getClass(), init)) return;
            init = true;
        }
        AppWindowPersistence appPersistence = new AppWindowPersistence();

        appPersistence.readAppFrameFromProperties();

        ModelFactory            .INSTANCE.init();
        RendererFactory         .INSTANCE.init();
        ControllerFactory       .INSTANCE.init();
        ActionKeyListenerFactory.INSTANCE.init();
        MouseListenerFactory    .INSTANCE.init();
        MiscFactory             .INSTANCE.init();

        appPersistence.readAppPanelFromProperties();
        UpdateDownload.checkForNewerVersion(60 * 1000);
    }
}
