package org.jphototagger.program.serviceprovider.core;

import org.jphototagger.program.UserSettings;
import org.jphototagger.services.core.UserProperties;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class UserPropertiesImpl implements UserProperties {

    @Override
    public String getIptcCharset() {
        return UserSettings.INSTANCE.getIptcCharset();
    }

    @Override
    public boolean isDisplayIptc() {
        return UserSettings.INSTANCE.isDisplayIptc();
    }

}
