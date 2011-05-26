package org.jphototagger.program.factory;

import org.jphototagger.services.plugin.FileProcessorPlugin;

/**
 *
 * @author Elmar Baumann
 */
public final class FileProcessorPluginManager extends PluginManager<FileProcessorPlugin> {

    public static final FileProcessorPluginManager INSTANCE = new FileProcessorPluginManager();

    private FileProcessorPluginManager() {
        super(FileProcessorPlugin.class);
    }
}
