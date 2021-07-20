package org.jphototagger.api.applifecycle;

/**
 * @author Elmar Baumann
 */
public interface AppUpdater {

    void updateToVersion(int major, int minor1, int minor2);
}
