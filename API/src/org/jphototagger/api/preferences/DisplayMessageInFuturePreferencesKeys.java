package org.jphototagger.api.preferences;

import java.util.Set;

/**
 * Keys whith a boolean value of the meaning: Display that message
 * in future or not.
 *
 * @author Elmar Baumann
 */
public interface DisplayMessageInFuturePreferencesKeys {

    public static final class KeyInfo {

        private final String key;
        private final String localizedDisplayName;

        public KeyInfo(String key, String localizedDisplayName) {
            if (key == null) {
                throw new NullPointerException("key == null");
            }

            if (localizedDisplayName == null) {
                throw new NullPointerException("localizedDisplayName == null");
            }

            this.key = key;
            this.localizedDisplayName = localizedDisplayName;
        }

        public String getKey() {
            return key;
        }

        public String getLocalizedDisplayName() {
            return localizedDisplayName;
        }
    }

    Set<KeyInfo> getKeyInfos();
}
