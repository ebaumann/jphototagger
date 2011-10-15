package org.jphototagger.fileeventhooks;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.modules.Module;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = Module.class)
public final class ModuleInstaller implements Module {

    private final FileEventHooksScriptExecutor EXECUTOR = new FileEventHooksScriptExecutor();

    @Override
    public void init() {
        // ignore
    }

    @Override
    public void remove() {
        // ignore
    }
}
