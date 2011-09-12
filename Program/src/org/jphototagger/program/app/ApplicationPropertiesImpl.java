package org.jphototagger.program.app;

import org.jphototagger.api.core.ApplicationProperties;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ApplicationProperties.class)
public final class ApplicationPropertiesImpl implements ApplicationProperties {

    @Override
    public String getApplicationVersionString() {
        return AppInfo.APP_VERSION;
    }
}
