package org.jphototagger.program.serviceprovider.core;

import org.jphototagger.api.core.UserProperties;
import org.jphototagger.program.UserSettings;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = UserProperties.class)
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
