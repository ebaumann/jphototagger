package org.jphototagger.program.app.ui;

import java.awt.Component;

import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.progress.ProgressHandle;
import org.jphototagger.api.progress.ProgressHandleFactory;
import org.jphototagger.api.windows.StatusLineElementProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProviders({
    @ServiceProvider(service = ProgressHandleFactory.class),
    @ServiceProvider(service = StatusLineElementProvider.class)
})
public final class ProgressBarProvider implements ProgressHandleFactory, StatusLineElementProvider {

    private final ProgressBarPanelArray progressBarPanelArray = new ProgressBarPanelArray();

    @Override
    public Component getStatusLineElement() {
        return progressBarPanelArray;
    }

    @Override
    public int getPosition() {
        return 400;
    }

    @Override
    public ProgressHandle createProgressHandle() {
        return progressBarPanelArray.createHandle();
    }

    @Override
    public ProgressHandle createProgressHandle(Cancelable cancelable) {
        return progressBarPanelArray.createHandle(cancelable);
    }
}
