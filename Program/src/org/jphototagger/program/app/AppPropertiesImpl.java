package org.jphototagger.program.app;

import org.jphototagger.api.branding.AppProperties;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = AppProperties.class)
public final class AppPropertiesImpl implements AppProperties {

    @Override
    public String getAppVersionString() {
        return AppInfo.APP_VERSION;
    }
}
