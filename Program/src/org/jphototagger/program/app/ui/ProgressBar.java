package org.jphototagger.program.app.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JProgressBar;

import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.MutualExcludedResource;

/**
 * @author Elmar Baumann
 */
final class ProgressBar extends MutualExcludedResource<JProgressBar> implements ActionListener {

    static final ProgressBar INSTANCE = new ProgressBar();
    private final JButton buttonCancel;
    private volatile boolean cancelEnabled = true;
    private final ProgressBarPanel progressBarPanel = new ProgressBarPanel();

    private ProgressBar() {
        setResource(progressBarPanel.getProgressBar());
        buttonCancel = progressBarPanel.getButtonCancelProgress();
        listen();
    }

    private void listen() {
        buttonCancel.addActionListener(this);
        buttonCancel.setEnabled(false);
    }

    /**
     * Enables or disables a cancel button if the owner implements
     * {@code Cancelable}.
     *
     * @param owner   owner
     * @param enabled true if enable a cancel button. Default: true.
     */
    synchronized void setEnabledCancel(Object owner, boolean enabled) {
        if (owner == getOwner()) {
            cancelEnabled = enabled;
        }
    }

    /**
     * Returns the progress bar and offers a cancel button if the owner
     * implements {@code Cancelable}.
     *
     * @param  owner
     * @return       progress bar or null if locked through another owner
     */
    @Override
    public synchronized JProgressBar getResource(Object owner) {
        JProgressBar pb = super.getResource(owner);
        boolean canCancel = (pb != null) && (owner instanceof Cancelable);

        if (cancelEnabled && canCancel) {
            setEnabledCancelButton(true);
        }

        return pb;
    }

    @Override
    public synchronized boolean releaseResource(Object owner) {
        boolean released = super.releaseResource(owner);

        // Only the owner can deactivate the button (released is only true if
        // the owner did call it)
        if (released) {
            setEnabledCancelButton(false);
            cancelEnabled = true;
        }

        return released;
    }

    private void setEnabledCancelButton(boolean enabled) {
        buttonCancel.setEnabled(enabled);

        if (enabled) {
            buttonCancel.setToolTipText(Bundle.getString(ProgressBar.class, "ProgressBar.TooltipText.Cancel"));
            buttonCancel.setIcon(AppLookAndFeel.ICON_CANCEL);
        } else {
            buttonCancel.setToolTipText("");
            buttonCancel.setIcon(null);
        }
    }

    private synchronized void cancel() {
        Object owner = getOwner();

        if (owner instanceof Cancelable) {
            ((Cancelable) owner).cancel();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        cancel();
    }

    ProgressBarPanel getProgressBarPanel() {
        return progressBarPanel;
    }
}
