package org.jphototagger.program.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jphototagger.api.preferences.DisplayMessageInFuturePreferencesKeys;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = DisplayMessageInFuturePreferencesKeys.class)
public final class DisplayMessageInFuturePreferencesKeysImpl implements DisplayMessageInFuturePreferencesKeys {

    private static final List<KeyInfo> KEY_INFOS = new ArrayList<KeyInfo>();

    static {
        KEY_INFOS.add(new KeyInfo(AppPreferencesKeys.KEY_DISPLAY_IN_FUTURE_WARN_ON_EQUAL_BASENAMES,
                Bundle.getString(DisplayMessageInFuturePreferencesKeysImpl.class, "DisplayMessageInFuturePreferencesKeysImpl.WarnOnEqualBasenames.LocalizedDisplayName")));
    }

    @Override
    public Collection<? extends KeyInfo> getKeyInfos() {
        return Collections.unmodifiableList(KEY_INFOS);
    }
}
