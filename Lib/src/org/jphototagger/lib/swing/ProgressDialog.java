package org.jphototagger.lib.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * Non modal DialogExt with a progress bar.
 *
 * @author Elmar Baumann
 */
public final class ProgressDialog extends DialogExt {

    private static final long serialVersionUID = 1L;
    private final Set<ActionListener> actionListeners = new CopyOnWriteArraySet<>();
    private boolean closeEnabled = true;

    public ProgressDialog(java.awt.Frame parent) {
        super(parent, false);
        init();
    }

    private void init() {
        setPersistSizeAndLocation(false);
        initComponents();
    }

    /**
     * Adds an action listener. It is called, when a user clicks the stop
     * button.
     *
     * @param listener  action listener
     */
    public void addActionListener(ActionListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        actionListeners.add(listener);
    }

    /**
     * Removes an action listener.
     *
     * @param listener  action listener
     */
    public void removeActionListener(ActionListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        actionListeners.remove(listener);
    }

    /**
     * Sets the current value of the progress bar.
     *
     * @param value value
     */
    public void setValue(int value) {
        progressBar.setValue(value);
    }

    /**
     * Sets the minimum value of the progress bar.
     *
     * @param minimum minimum
     */
    public void setMinimum(int minimum) {
        progressBar.setMinimum(minimum);
    }

    /**
     * Sets the maximum value of the progress bar.
     *
     * @param maximum maximum
     */
    public void setMaximum(int maximum) {
        progressBar.setMaximum(maximum);
    }

    /**
     * Sets the text to be displayed by the progress bar.
     *
     * @param text text
     */
    public void setInfoText(String text) {
        if (text == null) {
            throw new NullPointerException("text == null");
        }

        labelInfo.setText(text);
    }

    /**
     * Sets the text below the progress bar to give detailled info about the
     * current progress.
     *
     * @param text text
     */
    public void setCurrentProgressInfoText(String text) {
        if (text == null) {
            throw new NullPointerException("text == null");
        }

        labelProgressInfo.setText(text);
    }

    /**
     * Sets the progress bar in the indeterminate state.
     *
     * @param indeterminate true, if indeterminate
     */
    public void setIndeterminate(boolean indeterminate) {
        progressBar.setIndeterminate(indeterminate);
    }

    /**
     * Sets the progress bar string painted (progress bar displays a string).
     *
     * @param stringPainted true, if string painted
     */
    public void setStringPainted(boolean stringPainted) {
        progressBar.setStringPainted(stringPainted);
    }

    public void setProgressBarString(String string) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }

        progressBar.setString(string);
    }

    /**
     * Enables closing the dialog.
     *
     * @param enabled  true, if enabled. Default: true.
     */
    public void setEnabledClose(boolean enabled) {
        closeEnabled = enabled;
    }

    /**
     * Sets the stop button enabled.
     *
     * @param stop  true, if enabled. Default: true.
     */
    public void setEnabledStop(boolean stop) {
        buttonStop.setEnabled(stop);
    }

    private void stop() {
        if (closeEnabled) {
            for (ActionListener listener : actionListeners) {
                listener.actionPerformed(new ActionEvent(this, 0, "Stop"));
            }

            setVisible(false);
        }
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = UiFactory.panel();
        labelInfo = UiFactory.label();
        progressBar = UiFactory.progressBar();
        buttonStop = UiFactory.button();
        labelProgressInfo = UiFactory.label();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "ProgressDialog.title")); // NOI18N
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setName("panelContent"); // NOI18N
        panelContent.setLayout(new java.awt.GridBagLayout());

        labelInfo.setText("Info"); // NOI18N
        labelInfo.setName("labelInfo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(labelInfo, gridBagConstraints);

        progressBar.setName("progressBar"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        panelContent.add(progressBar, gridBagConstraints);

        buttonStop.setText(Bundle.getString(getClass(), "ProgressDialog.buttonStop.text")); // NOI18N
        buttonStop.setName("buttonStop"); // NOI18N
        buttonStop.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStopActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = UiFactory.insets(3, 5, 0, 0);
        panelContent.add(buttonStop, gridBagConstraints);

        labelProgressInfo.setText(" "); // NOI18N
        labelProgressInfo.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelProgressInfo.setName("labelProgressInfo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 0, 0, 0);
        panelContent.add(labelProgressInfo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }

    private void buttonStopActionPerformed(java.awt.event.ActionEvent evt) {
        stop();
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        stop();
    }

    private javax.swing.JButton buttonStop;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JLabel labelProgressInfo;
    private javax.swing.JPanel panelContent;
    private javax.swing.JProgressBar progressBar;
}
