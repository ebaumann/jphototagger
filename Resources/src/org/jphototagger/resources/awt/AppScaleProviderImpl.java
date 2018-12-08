package org.jphototagger.resources.awt;

import org.jphototagger.api.preferences.CommonPreferences;

/**
 * @author Elmar Baumann
 */
public final class AppScaleProviderImpl implements AppScaleProvider {

    @Override
    public double getScaleFactor() {
        return (double) CommonPreferences.getFontScale();
    }
}
