/*
 * @(#)ActionMap.java    Created on 2010-04-04
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

package org.jphototagger.lib.componentutil;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

import javax.swing.Action;

/**
 * Contains one action per action class.
 *
 * @author Elmar Baumann
 */
public class ActionMap {
    private final Map<Class<? extends Action>, Action> ACTION_OF =
        new HashMap<Class<? extends Action>, Action>();

    /**
     * Returns an action of a specific class.
     * <p>
     * If the action does not exist, one with the default contructor will
     * be created.
     *
     * @param  actionClass class
     * @return             action
     * @throws             IllegalArgumentException if the map does not contain
     *                     that action and it couldn't be created with its
     *                     default controctur
     */
    public synchronized Action get(Class<? extends Action> actionClass) {
        if (actionClass == null) {
            throw new NullPointerException("actionClass == null");
        }

        Action action = ACTION_OF.get(actionClass);

        if (action == null) {
            action = createAction(actionClass);
        }

        if (action == null) {
            throw new IllegalArgumentException("No action available: " + actionClass);
        } else {
            ACTION_OF.put(actionClass, action);
        }

        return action;
    }

    /**
     * Puts an action.
     * <p>
     * Usage, if the action does not contain a default constructor.
     *
     * @param action action
     */
    public synchronized void put(Action action) {
        if (action == null) {
            throw new NullPointerException("action == null");
        }

        ACTION_OF.put(action.getClass(), action);
    }

    private synchronized Action createAction(
            Class<? extends Action> actionClass) {
        try {
            return actionClass.newInstance();
        } catch (Exception ex) {
            Logger.getLogger(ActionMap.class.getName()).log(Level.SEVERE, null,
                             ex);
        }

        return null;
    }
}
