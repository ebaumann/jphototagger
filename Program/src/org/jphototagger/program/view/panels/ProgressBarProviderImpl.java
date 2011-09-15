package org.jphototagger.program.view.panels;

import javax.swing.JProgressBar;

import org.jphototagger.api.progress.ProgressBarProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ProgressBarProvider.class)
public final class ProgressBarProviderImpl implements ProgressBarProvider {

    @Override
    public JProgressBar getProgressBar(Object owner) {
        return ProgressBar.INSTANCE.getResource(owner);
    }

    @Override
    public boolean releaseProgressBar(JProgressBar progressBar, Object owner) {
        return ProgressBar.INSTANCE.releaseResource(owner);
    }
}
