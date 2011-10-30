package org.jphototagger.lib.plugin;

import java.awt.Component;

import javax.swing.Icon;

import org.jphototagger.api.plugin.fileprocessor.FileProcessorPlugin;

/**
 * @author Elmar Baumann
 */
public abstract class AbstractFileProcessorPlugin implements FileProcessorPlugin {

    /**
     *
     * @return null
     */
    @Override
    public Icon getSmallIcon() {
        return null;
    }

    /**
     *
     * @return null
     */
    @Override
    public Icon getLargeIcon() {
        return null;
    }

    /**
     *
     * @return null
     */
    @Override
    public Component getSettingsComponent() {
        return null;
    }
}
