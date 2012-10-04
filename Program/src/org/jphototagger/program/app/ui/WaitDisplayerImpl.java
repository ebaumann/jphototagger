package org.jphototagger.program.app.ui;

import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = WaitDisplayer.class)
public final class WaitDisplayerImpl implements WaitDisplayer {

    @Override
    public void show() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                WaitDisplay.INSTANCE.show();
            }
        });

    }

    @Override
    public void hide() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                WaitDisplay.INSTANCE.hide();
            }
        });
    }

    @Override
    public boolean isShow() {
        return WaitDisplay.INSTANCE.isShow();
    }
}
