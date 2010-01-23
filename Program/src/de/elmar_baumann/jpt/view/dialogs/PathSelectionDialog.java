/*
 * JPhotoTagger tags and finds images fast
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
package de.elmar_baumann.jpt.view.dialogs;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.lib.dialog.Dialog;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Modal dialog to select a path.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-12
 */
public class PathSelectionDialog extends Dialog implements ListSelectionListener {

    private static final long                           serialVersionUID = -5988292432590183296L;
    private              boolean                        accepted;
    private final        Collection<Collection<String>> paths;
    private              Collection<Collection<String>> selPaths;
    private final        Mode                           mode;

    public enum Mode {

        PATHS,
        DISTINCT_ELEMENTS,
    }

    public PathSelectionDialog(Collection<Collection<String>> paths, Mode mode) {
        super(GUI.INSTANCE.getAppFrame(), true);
        this.paths = paths;
        this.mode = mode;
        assert paths != null : "paths == null!";
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpContentsUrl(Bundle.getString("Help.Url.Contents"));
        list.addListSelectionListener(this);
        if (mode.equals(Mode.DISTINCT_ELEMENTS)) {
            list.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
            list.setVisibleRowCount(-1);
        }
    }

    private void readProperties() {
        UserSettings.INSTANCE.getSettings().applySizeAndLocation(this);
    }

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().setSizeAndLocation(this);
        UserSettings.INSTANCE.writeToFile();
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setInfoMessage(String message) {
        labelInfo.setText(message);
    }

    public Collection<Collection<String>> getSelPaths() {
        return selPaths;
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

    private void handleButtonSelectNothingActionPerformed() {
        selPaths = new ArrayList<Collection<String>>();
        accepted = false;
        setVisible(false);
    }

    private void handleButtonSelectAllActionPerformed() {
        accepted = true;
        selPaths = new ArrayList<Collection<String>>(paths);
        setVisible(false);
    }

    @SuppressWarnings("unchecked")
    private void handleButtonSelectSelectedActionPerformed() {
        accepted = true;
        List<Collection<String>> sel = new ArrayList<Collection<String>>();
        Object[] selValues = list.getSelectedValues();
        for (Object selValue : selValues) {
            if (selValue instanceof Collection<?>) {
                Collection<String> collection = (Collection<String>) selValue;
                sel.add(collection);
            } else if (selValue instanceof String) {
                Collection<String> collection = Collections.singletonList((String) selValue);
                sel.add(collection);
            }
        }
        selPaths = sel;
        setVisible(false);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            buttonSelectSelected.setEnabled(list.getSelectedIndex() >= 0);
        }
    }

    private class Model extends DefaultListModel {

        private static final long serialVersionUID = 7783311389163592108L;

        public Model() {
            if (mode.equals(Mode.DISTINCT_ELEMENTS)) {
                addDistinctElements();
            } else {
                addPaths();
            }
        }

        private void addPaths() {
            for (Collection<?> path : paths) {
                addElement(path);
            }
        }

        private void addDistinctElements() {
            for (Collection<String> path : paths) {
                for (String element : path) {
                    addElement(element);
                }
            }
        }
    }

    private class Renderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = -3753515545397949621L;

        private final Icon ICON = AppLookAndFeel.getIcon("icon_keyword.png");

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            if (value instanceof Collection<?>) {
                renderCollection(value, label);
            } else if (value instanceof String) {
                String padding = "  ";
                label.setText((String) value + padding);
            }
            label.setIcon(ICON);
            return label;
        }

        private void renderCollection(Object value, JLabel label) {
            Collection<? extends Object> collection = (Collection<? extends Object>) value;
            StringBuilder sb = new StringBuilder();
            String pathDelim = " > ";
            int i = 0;
            for (Object element : collection) {
                sb.append((i++ == 0
                           ? ""
                           : pathDelim) + element.toString());
            }
            label.setText(sb.toString());
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
        scrollPane = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        buttonSelectNothing = new javax.swing.JButton();
        buttonSelectAll = new javax.swing.JButton();
        buttonSelectSelected = new javax.swing.JButton();

        setTitle(Bundle.getString("PathSelectionDialog.title"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        list.setModel(new Model());
        list.setCellRenderer(new Renderer());
        scrollPane.setViewportView(list);

        buttonSelectNothing.setMnemonic('n');
        buttonSelectNothing.setText(Bundle.getString("PathSelectionDialog.buttonSelectNothing.text"));
        buttonSelectNothing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectNothingActionPerformed(evt);
            }
        });

        buttonSelectAll.setMnemonic('a');
        buttonSelectAll.setText(Bundle.getString("PathSelectionDialog.buttonSelectAll.text"));
        buttonSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectAllActionPerformed(evt);
            }
        });

        buttonSelectSelected.setMnemonic('s');
        buttonSelectSelected.setText(Bundle.getString("PathSelectionDialog.buttonSelectSelected.text"));
        buttonSelectSelected.setEnabled(false);
        buttonSelectSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectSelectedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
                    .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonSelectNothing)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSelectAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSelectSelected)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonSelectSelected)
                    .addComponent(buttonSelectAll)
                    .addComponent(buttonSelectNothing))
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonSelectNothingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectNothingActionPerformed
        handleButtonSelectNothingActionPerformed();
    }//GEN-LAST:event_buttonSelectNothingActionPerformed

    private void buttonSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectAllActionPerformed
        handleButtonSelectAllActionPerformed();
    }//GEN-LAST:event_buttonSelectAllActionPerformed

    private void buttonSelectSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectSelectedActionPerformed
        handleButtonSelectSelectedActionPerformed();
    }//GEN-LAST:event_buttonSelectSelectedActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        setVisible(false); // writes properties
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                @SuppressWarnings("unchecked")
                PathSelectionDialog dialog = new PathSelectionDialog(
                        new ArrayList(new ArrayList<String>()), Mode.PATHS);
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
    private javax.swing.JButton buttonSelectAll;
    private javax.swing.JButton buttonSelectNothing;
    private javax.swing.JButton buttonSelectSelected;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JList list;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
