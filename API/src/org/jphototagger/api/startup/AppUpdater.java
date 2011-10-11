package org.jphototagger.api.startup;

/**
 * @author Elmar Baumann
 */
public interface AppUpdater {

    void updateToVersion(int major, int minor1, int minor2);
}
