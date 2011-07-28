package org.jphototagger.domain.event.listener.impl;

import org.jphototagger.domain.event.listener.UserSettingsListener;
import org.jphototagger.domain.event.UserSettingsEvent;

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
