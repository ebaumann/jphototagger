package org.jphototagger.program.serviceprovider;

import javax.swing.JProgressBar;
import org.jphototagger.program.view.panels.ProgressBar;
import org.jphototagger.services.ProgressBarProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class JptProgressBarProvider implements ProgressBarProvider {

    @Override
    public JProgressBar getProgressBar(Object owner) {
        return ProgressBar.INSTANCE.getResource(owner);
    }

    @Override
    public boolean releaseProgressBar(JProgressBar progressBar, Object owner) {
        return ProgressBar.INSTANCE.releaseResource(owner);
    }
}
