/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.lib.dialog;

import de.elmar_baumann.lib.image.util.IconUtil;
import de.elmar_baumann.lib.resource.Bundle;
import de.elmar_baumann.lib.resource.Resources;
import de.elmar_baumann.lib.util.Settings;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Nichtmodaler Dialog mit Fortschrittsbalken.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-03
 */
public final class ProgressDialog extends javax.swing.JDialog {

    private final List<ActionListener> actionListeners = new ArrayList<ActionListener>();
    private boolean closeEnabled = true;

    /**
     * Konstruktor.
     *
     * @param parent      Elternframe
     */
    public ProgressDialog(java.awt.Frame parent) {
        super(parent, false);
        initComponents();
        setIcons();
    }

    private void setIcons() {
        if (Resources.INSTANCE.hasFrameIconImages()) {
            setIconImages(IconUtil.getIconImages(
                    Resources.INSTANCE.getFramesIconImagesPaths()));
        }
    }

    /**
     * Adds an action listener. It is called, when a user clicks the stop button.
     *
     * @param listener  action listener
     */
    public synchronized void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    /**
     * Removes an action listener.
     *
     * @param listener  action listener
     */
    public synchronized void removeActionListener(ActionListener listener) {
        actionListeners.remove(listener);
    }

    /**
     * Setzt den aktuellen Zustand des Fortschrittsbalkens.
     *
     * @param value Wert
     */
    public void setValue(int value) {
        progressBar.setValue(value);
    }

    /**
     * Setzt das Minimum des Fortschrittsbalkens.
     *
     * @param minimum Minimum
     */
    public void setMinimum(int minimum) {
        progressBar.setMinimum(minimum);
    }

    /**
     * Setzt das Maximum des Fortschrittsbalkens.
     *
     * @param maximum Maximum
     */
    public void setMaximum(int maximum) {
        progressBar.setMaximum(maximum);
    }

    /**
     * Anzuzeigender Text über Fortschrittsbalken.
     *
     * @param text  Text
     */
    public void setInfoText(String text) {
        labelInfo.setText(text);
    }

    /**
     * Sets the text below the progress bar to give detailled info about the
     * current progress.
     *
     * @param text  text
     */
    public void setCurrentProgressInfoText(String text) {
        labelProgressInfo.setText(text);
    }

    /**
     * Sets the progress bar in the indeterminate state.
     *
     * @param indeterminate  true, if indeterminate
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

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            readProperties();
        } else {
            writeProperties();
        }
        super.setVisible(visible);
    }

    private void readProperties() {
        Properties properties = Resources.INSTANCE.getProperties();
        if (properties != null) {
            Settings settings = new Settings(properties);
            settings.getSizeAndLocation(this);
        }
    }

    private void writeProperties() {
        Properties properties = Resources.INSTANCE.getProperties();
        if (properties != null) {
            Settings settings = new Settings(properties);
            settings.setSizeAndLocation(this);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelInfo = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        buttonStop = new javax.swing.JButton();
        labelProgressInfo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/lib/resource/properties/Bundle"); // NOI18N
        labelInfo.setText(bundle.getString("ProgressDialogLabelMessageText")); // NOI18N

        buttonStop.setText(Bundle.getString("ProgressDialog.buttonStop.text")); // NOI18N
        buttonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStopActionPerformed(evt);
            }
        });

        labelProgressInfo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelProgressInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStop))
                    .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonStop)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelProgressInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonStop, progressBar});

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void buttonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStopActionPerformed
    stop();
}//GEN-LAST:event_buttonStopActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    stop();
}//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                ProgressDialog dialog = new ProgressDialog(new javax.swing.JFrame());
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonStop;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JLabel labelProgressInfo;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables
}
