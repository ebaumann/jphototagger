package org.jphototagger.fileeventhooks;

import java.awt.Component;
import javax.swing.Icon;
import org.jphototagger.api.windows.OptionPageProvider;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = OptionPageProvider.class)
public final class OptionPageProviderImpl implements OptionPageProvider {

    @Override
    public Component getComponent() {
        return new SettingsPanel();
    }

    @Override
    public String getTitle() {
        return Bundle.getString(ModuleInstaller.class, "SettingsPanel.Title");
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public int getPosition() {
        return 10;
    }

    @Override
    public boolean isMiscOptionPage() {
        return true;
    }
}
