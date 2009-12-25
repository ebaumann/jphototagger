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
package de.elmar_baumann.jpt.view.panels;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.database.DatabaseMaintainance;
import de.elmar_baumann.jpt.database.metadata.file.ColumnFilesFilename;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.types.SubstringPosition;
import de.elmar_baumann.lib.util.SettingsHints;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JRadioButton;

/**
 * Uses
 * {@link de.elmar_baumann.jpt.database.DatabaseMaintainance#replaceString(de.elmar_baumann.jpt.database.metadata.Column, java.lang.String, java.lang.String, de.elmar_baumann.jpt.types.SubstringPosition)}
 * to replace substrings in filenames.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-16
 */
public class RenameFilenamesInDbPanel extends javax.swing.JPanel {

    private final Map<JRadioButton, SubstringPosition> stringPositionOfRadioButton =
            new HashMap<JRadioButton, SubstringPosition>();
    private final List<JRadioButton> radioButtonsPositions =
            new ArrayList<JRadioButton>(5);
    private volatile boolean runs;

    public RenameFilenamesInDbPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        initStringPositionsOfRadioButtons();
        initRadioButtonList();
        UserSettings.INSTANCE.getSettings().getComponent(this,
                new SettingsHints(EnumSet.of(SettingsHints.Option.NONE)));
        setButtonReplaceEnabled();
    }

    private void initStringPositionsOfRadioButtons() {
        stringPositionOfRadioButton.put(radioButtonExactMatch,
                SubstringPosition.EXACT_MATCH);
        stringPositionOfRadioButton.put(radioButtonPositionAnywhere,
                SubstringPosition.ANYWHERE);
        stringPositionOfRadioButton.put(radioButtonPositionBegin,
                SubstringPosition.BEGIN);
        stringPositionOfRadioButton.put(radioButtonPositionEnd,
                SubstringPosition.END);
        stringPositionOfRadioButton.put(radioButtonPositionMiddle,
                SubstringPosition.MIDDLE);
    }

    private void initRadioButtonList() {
        radioButtonsPositions.add(radioButtonExactMatch);
        radioButtonsPositions.add(radioButtonPositionAnywhere);
        radioButtonsPositions.add(radioButtonPositionBegin);
        radioButtonsPositions.add(radioButtonPositionEnd);
        radioButtonsPositions.add(radioButtonPositionMiddle);
    }

    public boolean runs() {
        return runs;
    }

    private boolean radioButtonPositionSelected() {
        for (JRadioButton radioButton : radioButtonsPositions) {
            if (radioButton.isSelected()) {
                return true;
            }
        }
        return false;
    }

    private synchronized JRadioButton getSelectedRadioButton() {
        for (JRadioButton radioButton : radioButtonsPositions) {
            if (radioButton.isSelected()) {
                return radioButton;
            }
        }
        return null;
    }

    private void replace() {
        if (confirmReplace()) {
            runs = true;
            buttonReplace.setEnabled(false);
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    JRadioButton selectedRadioButton = getSelectedRadioButton();
                    if (selectedRadioButton != null) {
                        progressBar.setIndeterminate(true);
                        int count = DatabaseMaintainance.INSTANCE.replaceString(
                                ColumnFilesFilename.INSTANCE,
                                textFieldSearch.getText(),
                                textFieldReplacement.getText(),
                                stringPositionOfRadioButton.get(
                                selectedRadioButton));
                        progressBar.setIndeterminate(false);
                        informationReplaced(count);
                    }
                    runs = false;
                    buttonReplace.setEnabled(true);
                }

                private void informationReplaced(int count) {
                    MessageDisplayer.information(null,
                            "RenameFilenamesInDbPanel.Info.CountReplaced", count);
                }
            });
            thread.setName("Replacing filename substrings in the database @ " +
                    getClass().getName());
            thread.start();
        }
    }

    private void setButtonReplaceEnabled() {
        String searchText = textFieldSearch.getText().trim();
        String replacementText = textFieldReplacement.getText().trim();
        boolean equals = searchText.equals(replacementText);
        buttonReplace.setEnabled(!searchText.isEmpty() && !equals &&
                radioButtonPositionSelected());
    }

    private boolean confirmReplace() {
        return MessageDisplayer.confirm(this,
                "RenameFilenamesInDbPanel.Confirm.Replace",
                MessageDisplayer.CancelButton.HIDE, textFieldSearch.getText(),
                textFieldReplacement.getText()).equals(
                MessageDisplayer.ConfirmAction.YES);
    }

    public void writeProperties() {
        UserSettings.INSTANCE.getSettings().setComponent(
                this, new SettingsHints(EnumSet.of(SettingsHints.Option.NONE)));
        UserSettings.INSTANCE.writeToFile();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupPosition = new javax.swing.ButtonGroup();
        labelTitle = new javax.swing.JLabel();
        labelSearch = new javax.swing.JLabel();
        textFieldSearch = new javax.swing.JTextField();
        labelReplacement = new javax.swing.JLabel();
        textFieldReplacement = new javax.swing.JTextField();
        panelPosition = new javax.swing.JPanel();
        radioButtonPositionBegin = new javax.swing.JRadioButton();
        radioButtonPositionMiddle = new javax.swing.JRadioButton();
        radioButtonPositionEnd = new javax.swing.JRadioButton();
        radioButtonPositionAnywhere = new javax.swing.JRadioButton();
        radioButtonExactMatch = new javax.swing.JRadioButton();
        progressBar = new javax.swing.JProgressBar();
        buttonReplace = new javax.swing.JButton();

        labelTitle.setForeground(new java.awt.Color(0, 0, 255));
        labelTitle.setText(Bundle.getString("RenameFilenamesInDbPanel.labelTitle.text"));
        labelTitle.setPreferredSize(new java.awt.Dimension(605, 38));

        labelSearch.setForeground(new java.awt.Color(0, 196, 0));
        labelSearch.setText(Bundle.getString("RenameFilenamesInDbPanel.labelSearch.text"));

        textFieldSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldSearchKeyReleased(evt);
            }
        });

        labelReplacement.setForeground(new java.awt.Color(0, 196, 0));
        labelReplacement.setText(Bundle.getString("RenameFilenamesInDbPanel.labelReplacement.text"));

        panelPosition.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString("RenameFilenamesInDbPanel.panelPosition.border.title")));

        buttonGroupPosition.add(radioButtonPositionBegin);
        radioButtonPositionBegin.setText(Bundle.getString("RenameFilenamesInDbPanel.radioButtonPositionBegin.text"));

        buttonGroupPosition.add(radioButtonPositionMiddle);
        radioButtonPositionMiddle.setText(Bundle.getString("RenameFilenamesInDbPanel.radioButtonPositionMiddle.text"));

        buttonGroupPosition.add(radioButtonPositionEnd);
        radioButtonPositionEnd.setText(Bundle.getString("RenameFilenamesInDbPanel.radioButtonPositionEnd.text"));

        buttonGroupPosition.add(radioButtonPositionAnywhere);
        radioButtonPositionAnywhere.setText(Bundle.getString("RenameFilenamesInDbPanel.radioButtonPositionAnywhere.text"));

        buttonGroupPosition.add(radioButtonExactMatch);
        radioButtonExactMatch.setText(Bundle.getString("RenameFilenamesInDbPanel.radioButtonExactMatch.text"));

        javax.swing.GroupLayout panelPositionLayout = new javax.swing.GroupLayout(panelPosition);
        panelPosition.setLayout(panelPositionLayout);
        panelPositionLayout.setHorizontalGroup(
            panelPositionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPositionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPositionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radioButtonExactMatch)
                    .addComponent(radioButtonPositionAnywhere, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                    .addComponent(radioButtonPositionEnd, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                    .addComponent(radioButtonPositionMiddle, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                    .addComponent(radioButtonPositionBegin, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelPositionLayout.setVerticalGroup(
            panelPositionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPositionLayout.createSequentialGroup()
                .addComponent(radioButtonPositionBegin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonPositionMiddle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonPositionEnd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonPositionAnywhere)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonExactMatch))
        );

        buttonReplace.setMnemonic('n');
        buttonReplace.setText(Bundle.getString("RenameFilenamesInDbPanel.buttonReplace.text"));
        buttonReplace.setEnabled(false);
        buttonReplace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonReplaceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelSearch, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelReplacement, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textFieldSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                            .addComponent(textFieldReplacement, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)))
                    .addComponent(panelPosition, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelTitle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
                    .addComponent(buttonReplace, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelSearch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelReplacement)
                    .addComponent(textFieldReplacement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPosition, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonReplace)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {textFieldReplacement, textFieldSearch});

    }// </editor-fold>//GEN-END:initComponents

    private void buttonReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonReplaceActionPerformed
        replace();
    }//GEN-LAST:event_buttonReplaceActionPerformed

    private void textFieldSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldSearchKeyReleased
        setButtonReplaceEnabled();
    }//GEN-LAST:event_textFieldSearchKeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupPosition;
    private javax.swing.JButton buttonReplace;
    private javax.swing.JLabel labelReplacement;
    private javax.swing.JLabel labelSearch;
    private javax.swing.JLabel labelTitle;
    private javax.swing.JPanel panelPosition;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton radioButtonExactMatch;
    private javax.swing.JRadioButton radioButtonPositionAnywhere;
    private javax.swing.JRadioButton radioButtonPositionBegin;
    private javax.swing.JRadioButton radioButtonPositionEnd;
    private javax.swing.JRadioButton radioButtonPositionMiddle;
    private javax.swing.JTextField textFieldReplacement;
    private javax.swing.JTextField textFieldSearch;
    // End of variables declaration//GEN-END:variables
}
