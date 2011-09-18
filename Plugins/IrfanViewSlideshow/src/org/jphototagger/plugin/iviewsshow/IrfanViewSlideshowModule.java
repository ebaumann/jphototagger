package org.jphototagger.plugin.iviewsshow;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.modules.Module;
import org.jphototagger.lib.system.SystemUtil;

/**
 *
 *
 * @author  Elmar Baumann
 */
@ServiceProvider(service = Module.class)
public final class IrfanViewSlideshowModule implements Module {

    @Override
    public void init() {
        // Do nothing
    }

    @Override
    public void remove() {
        if (SystemUtil.isWindows()) {
            TemporaryStorage.INSTANE.cleanup();
        }
    }
}
