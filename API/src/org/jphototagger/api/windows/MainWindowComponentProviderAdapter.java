package org.jphototagger.api.windows;

import java.util.Collection;
import java.util.Collections;

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
