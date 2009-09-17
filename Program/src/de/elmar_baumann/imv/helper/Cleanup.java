/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.imv.helper;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.tasks.AutomaticTask;
import de.elmar_baumann.imv.tasks.ScheduledTasks;
import de.elmar_baumann.imv.tasks.UserTasks;

/**
 * Shuts down all Tasks. Should be called when the application exits.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-16
 */
public final class Cleanup {

    /**
     * Sleep time in milliseconds before giving control to the caller, so that
     * the threads can complete their current action before they check for
     * interruption.
     */
    private static long MILLISECONDS_SLEEP = 2000;

    /**
     * Shuts down all Tasks.
     */
    public static void shutdown() {
        ScheduledTasks.INSTANCE.shutdown();
        AutomaticTask.INSTANCE.shutdown();
        UserTasks.INSTANCE.shutdown();
        boolean sleep = ScheduledTasks.INSTANCE.getCount() > 0 ||
                UserTasks.INSTANCE.getCount() > 0;
        if (sleep) {
            sleep();
        }
    }

    private static void sleep() {
        try {
            // Let the tasks a little bit time to complete until they can interrupt
            Thread.sleep(MILLISECONDS_SLEEP);
        } catch (InterruptedException ex) {
            AppLog.logSevere(Cleanup.class, ex);
        }
    }

    private Cleanup() {
    }
}
