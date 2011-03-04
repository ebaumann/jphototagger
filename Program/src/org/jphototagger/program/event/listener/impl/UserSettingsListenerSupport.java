package org.jphototagger.program.event.listener.impl;

import org.jphototagger.program.event.listener.UserSettingsListener;
import org.jphototagger.program.event.UserSettingsEvent;

/**
 *
 * @author Elmar Baumann
 */
public final class UserSettingsListenerSupport extends ListenerSupport<UserSettingsListener> {
    public void notifyUserListeners(UserSettingsEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        for (UserSettingsListener l : listeners) {
            l.applySettings(evt);
        }
    }
}
