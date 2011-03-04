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
    private final Map<Class<? extends Action>, Action> ACTION_OF = new HashMap<Class<? extends Action>, Action>();

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

    private synchronized Action createAction(Class<? extends Action> actionClass) {
        try {
            return actionClass.newInstance();
        } catch (Exception ex) {
            Logger.getLogger(ActionMap.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
