/*
 * @(#)TextAreaSearchPanel.java    Created on 2008-03-31
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.lib.component;

import java.awt.Color;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.resource.JslBundle;

/**
 * Enhances a {@link JTextArea} with search capabilities.
 *
 * @author Elmar Baumann
 */
public class TextAreaSearchPanel extends javax.swing.JPanel implements DocumentListener {
    private static final long serialVersionUID = -7162796020962896471L;
    private JTextArea textArea;
    private static final Color ERROR_BG = Color.RED;
    private final Color textFieldBg;
    private final transient Highlighter                  hilit   =
            new DefaultHighlighter();
    private final transient Highlighter.HighlightPainter painter =
            new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
    private final List<Integer> foundIndices      = new ArrayList<Integer>();
    private int                 currentFoundIndex = -1;

    public TextAreaSearchPanel() {
        initComponents();
        MnemonicUtil.setMnemonics((Container) this);
        textFieldBg = textField.getBackground();
        textField.getDocument().addDocumentListener(this);
    }

    public void setTextArea(JTextArea textArea) {
        if (textArea == null) {
            throw new NullPointerException("textArea == null");
        }

        this.textArea = textArea;
        textArea.setHighlighter(hilit);
    }

    private int search(int startIndex) {
        String searchText = textField.getText().toLowerCase();
        if (textArea == null || searchText.isEmpty()) {
            return -1;
        }

        hilit.removeAllHighlights();

        String text = textArea.getText().toLowerCase();
        int index = text.indexOf(searchText, startIndex);
        if (index >= startIndex) {
            try {
                int end = index + searchText.length();
                hilit.addHighlight(index, end, painter);
                textArea.setCaretPosition(end);
                textField.setBackground(textFieldBg);
                return index;
            } catch (BadLocationException ex) {
                Logger.getLogger(TextAreaSearchPanel.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        } else {
            textField.setBackground(ERROR_BG);
        }

        return -1;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        searchFromTextStart();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        searchFromTextStart();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        // ignore
    }

    private void searchFromTextStart() {
        int     foundIndex = search(0);
        boolean found      = foundIndex >= 0;
        
        foundIndices.clear();
        
        if (found) {
            currentFoundIndex = foundIndex;

            if (!foundIndices.contains(foundIndex)) {
                foundIndices.add(foundIndex);
            }
        }

        buttonDown.setEnabled(found);
        buttonUp.setEnabled(false);
    }

    private void searchDown() {
        if (textArea == null) {
            return;
        }

        int     foundIndex = search(currentFoundIndex + 1);
        boolean found      = foundIndex > 0;

        if (found) {
            currentFoundIndex = foundIndex;

            if (!foundIndices.contains(foundIndex)) {
                foundIndices.add(foundIndex);
            }
        }
        buttonUp.setEnabled(found);
    }

    private void searchUp() {
        if (textArea == null || foundIndices.isEmpty()) {
            return;
        }

        int indexCurFoundIndex = foundIndices.indexOf(currentFoundIndex);
        int prevFoundIndex     = -1;

        if (indexCurFoundIndex > 0) {
            prevFoundIndex = search(foundIndices.get(indexCurFoundIndex - 1));
            boolean found  = prevFoundIndex >= 0;

            if (found) {
                currentFoundIndex = prevFoundIndex;
            }
            buttonUp.setEnabled(foundIndices.indexOf(prevFoundIndex) > 0);
            buttonDown.setEnabled(found);
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

        label = new javax.swing.JLabel();
        textField = new javax.swing.JTextField();
        buttonDown = new javax.swing.JButton();
        buttonUp = new javax.swing.JButton();

        label.setLabelFor(textField);
        label.setText(JslBundle.INSTANCE.getString("TextAreaSearchPanel.label.text")); // NOI18N

        textField.setColumns(10);

        buttonDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_arrow_down.png"))); // NOI18N
        buttonDown.setText(JslBundle.INSTANCE.getString("TextAreaSearchPanel.buttonDown.text")); // NOI18N
        buttonDown.setEnabled(false);
        buttonDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDownActionPerformed(evt);
            }
        });

        buttonUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_arrow_up.png"))); // NOI18N
        buttonUp.setText(JslBundle.INSTANCE.getString("TextAreaSearchPanel.buttonUp.text")); // NOI18N
        buttonUp.setEnabled(false);
        buttonUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textField, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonDown)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonUp))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonDown, buttonUp});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(label)
                .addComponent(textField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(buttonDown)
                .addComponent(buttonUp))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonDown, buttonUp});

    }// </editor-fold>//GEN-END:initComponents

    private void buttonDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDownActionPerformed
        searchDown();
    }//GEN-LAST:event_buttonDownActionPerformed

    private void buttonUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUpActionPerformed
        searchUp();
    }//GEN-LAST:event_buttonUpActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonDown;
    private javax.swing.JButton buttonUp;
    private javax.swing.JLabel label;
    private javax.swing.JTextField textField;
    // End of variables declaration//GEN-END:variables
}
