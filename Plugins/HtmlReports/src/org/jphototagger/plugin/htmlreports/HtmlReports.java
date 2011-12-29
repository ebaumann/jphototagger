package org.jphototagger.plugin.htmlreports;

import java.awt.Component;
import java.io.File;
import java.util.Collection;

import javax.swing.Icon;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.plugin.fileprocessor.FileProcessorPlugin;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileProcessorPlugin.class)
public final class HtmlReports implements FileProcessorPlugin {

    @Override
    public void processFiles(Collection<? extends File> files) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getDescription() {
        return Bundle.getString(HtmlReports.class, "HtmlReports.Description");
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(HtmlReports.class, "HtmlReports.Name");
    }

    @Override
    public Icon getSmallIcon() {
        return IconUtil.getImageIcon(HtmlReports.class, "html.png");
    }

    @Override
    public Icon getLargeIcon() {
        return IconUtil.getImageIcon(HtmlReports.class, "html32.png");
    }

    @Override
    public Component getSettingsComponent() {
        return new HtmlReportsSettingsPanel();
    }
}
