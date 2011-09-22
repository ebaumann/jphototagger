package org.jphototagger.program.view.panels;

import javax.swing.JProgressBar;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.progress.MainWindowProgressBarProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = MainWindowProgressBarProvider.class)
public final class ProgressBarProviderImpl implements MainWindowProgressBarProvider {

    @Override
    public JProgressBar getProgressBar(Object owner) {
        return ProgressBar.INSTANCE.getResource(owner);
    }

    @Override
    public boolean releaseProgressBar(JProgressBar progressBar, Object owner) {
        return ProgressBar.INSTANCE.releaseResource(owner);
    }
}
