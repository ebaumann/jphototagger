package org.jphototagger.program.module.thumbnails.info;

import java.awt.Component;
import javax.swing.Icon;
import org.jphototagger.api.windows.OptionPageProvider;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = OptionPageProvider.class)
public final class SidecarSuffixesInfoOptionPageProviderImpl implements OptionPageProvider {

    @Override
    public Component getComponent() {
        return new SidecarSuffixesInfoSettingsPanel();
    }

    @Override
    public String getTitle() {
        return Bundle.getString(SidecarSuffixesInfoOptionPageProviderImpl.class, "SidecarSuffixesInfoOptionPageProviderImpl.Title");
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public int getPosition() {
        return 8000;
    }

    @Override
    public boolean isMiscOptionPage() {
        return true;
    }
}
