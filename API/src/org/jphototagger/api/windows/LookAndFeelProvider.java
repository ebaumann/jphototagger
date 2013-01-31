package org.jphototagger.api.windows;

import org.jphototagger.api.collections.PositionProvider;

/**
 * @author Elmar Baumann
 */
public interface LookAndFeelProvider extends PositionProvider {

    String getDisplayname();

    String getDescription();

    String getPreferencesKey();

    void setLookAndFeel();
}
