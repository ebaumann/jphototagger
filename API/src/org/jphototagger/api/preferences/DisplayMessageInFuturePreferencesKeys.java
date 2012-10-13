package org.jphototagger.api.preferences;

import java.util.Collection;

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

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof KeyInfo)) {
                return false;
            }

            KeyInfo other = (KeyInfo) obj;

            return key.equals(other.key);
        }

        @Override
        public int hashCode() {
            int hash = 7;

            hash = 29 * hash + (this.key != null ? this.key.hashCode() : 0);

            return hash;
        }
    }

    Collection<? extends KeyInfo> getKeyInfos();
}
