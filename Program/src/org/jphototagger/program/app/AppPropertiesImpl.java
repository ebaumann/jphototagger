package org.jphototagger.program.app;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.branding.AppProperties;

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
