package org.jphototagger.api.lifecycle;

/**
 * @author Elmar Baumann
 */
public interface AppUpdater {

    void updateToVersion(int major, int minor1, int minor2);
}
