package org.jphototagger.lib.api;

import java.util.Collection;
import java.util.Collections;

import org.jphototagger.api.windows.MainWindowComponent;
import org.jphototagger.api.windows.MainWindowComponentProvider;

/**
 * @author Elmar Baumann
 */
public class MainWindowComponentProviderAdapter implements MainWindowComponentProvider {

    @Override
    public Collection<? extends MainWindowComponent> getMainWindowSelectionComponents() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends MainWindowComponent> getMainWindowEditComponents() {
        return Collections.emptyList();
    }
}
