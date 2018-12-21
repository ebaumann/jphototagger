package org.jphototagger.program.app.ui;

import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.util.Set;
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressHandle;
import org.jphototagger.domain.event.listener.ListenerSupport;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public class ProgressBarPanel extends PanelExt implements ProgressHandle {

    private static final long serialVersionUID = 1L;
    private final ListenerSupport<ProgressBarPanelListener> ls = new ListenerSupport<>();
    private final Cancelable cancelable;

    public ProgressBarPanel() {
        this(null);
    }

    public ProgressBarPanel(Cancelable cancelable) {
        this.cancelable = cancelable;
        initComponents();
    }

    @Override
    public void progressStarted(final ProgressEvent evt) {
        setName("Progress bar for " + evt.getSource());
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                notifyProgressStarted();
                setEventToProgressBar(evt);
            }
        });
    }

    @Override
    public void progressPerformed(final ProgressEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                setEventToProgressBar(evt);
            }
        });
    }

    @Override
    public void progressEnded() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                progressBar.setValue(0);
                progressBar.setString("");
                progressBar.setStringPainted(false);
                progressBar.setIndeterminate(false);
                buttonCancelProgress.setEnabled(false);
                notifyProgressEnded();
            }
        });
    }

    private void setEventToProgressBar(final ProgressEvent evt) {
        if (evt.isIndeterminate()) {
            progressBar.setIndeterminate(true);
        } else {
            progressBar.setMinimum(evt.getMinimum());
            progressBar.setMaximum(evt.getMaximum());
            progressBar.setValue(evt.getValue());
        }
        progressBar.setStringPainted(evt.isStringPainted());
        progressBar.setString(evt.getStringToPaint());
    }

    private void cancel() {
        if (cancelable != null) {
            cancelable.cancel();
            buttonCancelProgress.setEnabled(false);
        }
    }

    void addProgressBarPanelListener(ProgressBarPanelListener listener) {
        ls.add(listener);
    }

    void removeProgressBarPanelListener(ProgressBarPanelListener listener) {
        ls.remove(listener);
    }

    private void notifyProgressStarted() {
        Set<ProgressBarPanelListener> listeners = ls.get();
        for (ProgressBarPanelListener listener : listeners) {
            listener.progressStarted(this);
        }
    }

    private void notifyProgressEnded() {
        Set<ProgressBarPanelListener> listeners = ls.get();
        for (ProgressBarPanelListener listener : listeners) {
            listener.progressEnded(this);
        }
    }

    /**
     * Adds the mouse listener to the progress bar too.
     * @param l
     */
    @Override
    public void addMouseListener(MouseListener l) {
        super.addMouseListener(l);
        progressBar.addMouseListener(l);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        progressBar = UiFactory.progressBar();
        buttonCancelProgress = UiFactory.button();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        progressBar.setMaximumSize(UiFactory.dimension(300, 20));
        progressBar.setName("progressBar"); // NOI18N
        progressBar.setPreferredSize(new Dimension(250, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        add(progressBar, gridBagConstraints);

        buttonCancelProgress.setIcon(org.jphototagger.resources.Icons.getIcon("icon_cancel.png"));
        buttonCancelProgress.setToolTipText(Bundle.getString(getClass(), "ProgressBarPanel.buttonCancelProgress.toolTipText")); // NOI18N
        buttonCancelProgress.setBorder(null);
        buttonCancelProgress.setContentAreaFilled(false);
        buttonCancelProgress.setEnabled(cancelable != null);
        buttonCancelProgress.setName("buttonCancelProgress"); // NOI18N
        buttonCancelProgress.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelProgressActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        add(buttonCancelProgress, gridBagConstraints);
    }

    private void buttonCancelProgressActionPerformed(java.awt.event.ActionEvent evt) {
        cancel();
    }

    private javax.swing.JButton buttonCancelProgress;
    private javax.swing.JProgressBar progressBar;
}
