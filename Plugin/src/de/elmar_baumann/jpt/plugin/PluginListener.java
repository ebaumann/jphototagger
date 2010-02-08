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
package de.elmar_baumann.jpt.plugin;

import java.util.Set;

/**
 * Listens to {@link Plugin} actions.
 * <p>
 * The listener can be added with
 * {@link Plugin#addPluginListener(de.elmar_baumann.jpt.plugin.PluginListener)}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-27
 */
public interface PluginListener {

    /**
     * Event of a process.
     */
    public enum Event {

        /**
         * The plugin action has been started
         */
        STARTED,
        /**
         * The plugin action has been finished without errors
         */
        FINISHED_NO_ERRORS,
        /**
         * The plugin action has been finished with errors
         */
        FINISHED_ERRORS,
        /**
         * The plugin action has been changed the processed files
         */
        FINISHED_FILES_CHANGED,
    }

    /**
     * Called on plugin actions.
     *
     * @param events events
     */
    public void action(Set<Event> events);
}
