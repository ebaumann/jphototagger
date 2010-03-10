/*
 * JPhotoTagger tags and finds images fast.
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

package de.elmar_baumann.jpt.view.panels;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.data.TextEntry;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpIptc4XmpCoreDateCreated;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopCaptionwriter;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCity;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopInstructions;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopState;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopTransmissionReference;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.text.JTextComponent;

/**
 * Edits a {@link Xmp} object.
 * <p>
 * To get a new XMP object, call {@link #getXmp()}.
 * <p>
 * To modify an existing XMP object set it through
 * {@link #setXmp(de.elmar_baumann.jpt.data.Xmp)} and call
 * {@link #setXmpToInputComponents()} and {@link #setInputToXmp()} to write
 * the input values into the referenced XMP object.
 *
 * @author  Elmar Baumann
 * @version 2010-01-08
 */
public class EditXmpPanel extends javax.swing.JPanel implements FocusListener {
    private static final long     serialVersionUID = 7898855480121337499L;
    private final List<TextEntry> textEntries      =
        new ArrayList<TextEntry>(18);
    private Component             firstInputComponent;
    private Component             lastInputComponent;
    private Component             lastFocussedComponent;
    private transient Xmp         xmp = new Xmp();

    public EditXmpPanel() {
        init();
    }

    private void init() {
        initComponents();
        setColumns();
        addTextEntries();
        setAutocomplete();
        addAsFocusListener();
        firstInputComponent = panelDcSubjects.textAreaInput;
        lastInputComponent  =
            panelPhotoshopCaptionwriter.getInputComponents().get(0);
        firstInputComponent.requestFocusInWindow();
        panelDcSubjects.setBundleKeyPosRenameDialog(
            "EditXmpPanel.Keywords.RenameDialog.Pos");
    }

    private void setColumns() {
        panelDcTitle.setColumn(ColumnXmpDcTitle.INSTANCE);
        panelDcDescription.setColumn(ColumnXmpDcDescription.INSTANCE);
        panelPhotoshopHeadline.setColumn(ColumnXmpPhotoshopHeadline.INSTANCE);
        panelIptc4xmpcoreLocation.setColumn(
            ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        panelIptc4XmpCoreDateCreated.setColumn(
            ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
        panelPhotoshopAuthorsposition.setColumn(
            ColumnXmpPhotoshopAuthorsposition.INSTANCE);
        panelDcCreator.setColumn(ColumnXmpDcCreator.INSTANCE);
        panelPhotoshopCity.setColumn(ColumnXmpPhotoshopCity.INSTANCE);
        panelPhotoshopState.setColumn(ColumnXmpPhotoshopState.INSTANCE);
        panelPhotoshopCountry.setColumn(ColumnXmpPhotoshopCountry.INSTANCE);
        panelDcRights.setColumn(ColumnXmpDcRights.INSTANCE);
        panelPhotoshopCredit.setColumn(ColumnXmpPhotoshopCredit.INSTANCE);
        panelPhotoshopSource.setColumn(ColumnXmpPhotoshopSource.INSTANCE);
        panelPhotoshopTransmissionReference.setColumn(
            ColumnXmpPhotoshopTransmissionReference.INSTANCE);
        panelPhotoshopInstructions.setColumn(
            ColumnXmpPhotoshopInstructions.INSTANCE);
        panelPhotoshopCaptionwriter.setColumn(
            ColumnXmpPhotoshopCaptionwriter.INSTANCE);
    }

    private void addTextEntries() {
        for (Component c : getComponents()) {
            if (c instanceof TextEntry) {
                textEntries.add((TextEntry) c);
            }
        }
    }

    private void setAutocomplete() {
        if (UserSettings.INSTANCE.isAutocomplete()) {
            for (TextEntry textEntry : textEntries) {
                textEntry.setAutocomplete();
            }
        }
    }

    public void focusLastFocuessedComponent() {
        if (lastFocussedComponent != null) {
            lastFocussedComponent.requestFocusInWindow();
        }
    }

    public void setXmp(Xmp xmp) {
        assert xmp != null;
        this.xmp = xmp;
        setXmpToInputComponents();
    }

    public void setInputToXmp() {
        xmp.clear();

        for (TextEntry textEntry : textEntries) {
            Column column = textEntry.getColumn();

            if (textEntry instanceof EditRepeatableTextEntryPanel) {
                for (String text :
                        ((EditRepeatableTextEntryPanel) textEntry)
                            .getRepeatableText()) {
                    xmp.setValue(column, text);
                }

                xmp.setValue(column, textEntry.getText());
            } else {
                xmp.setValue(column, textEntry.getText());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void setXmpToInputComponents() {
        for (TextEntry textEntry : textEntries) {
            Object value = xmp.getValue(textEntry.getColumn());

            if (value != null) {
                if (textEntry instanceof EditRepeatableTextEntryPanel) {
                    ((EditRepeatableTextEntryPanel) textEntry).setText(
                        (Collection<String>) value);
                } else {
                    textEntry.setText(value.toString());
                }
            }
        }
    }

    public boolean isDirty() {
        for (TextEntry textEntry : textEntries) {
            if (textEntry.isDirty()) {
                return true;
            }
        }

        return false;
    }

    public void setDirty(boolean dirty) {
        for (TextEntry textEntry : textEntries) {
            textEntry.setDirty(dirty);
        }
    }

    public Xmp getXmp() {
        return xmp;
    }

    private void addAsFocusListener() {
        for (TextEntry textEntry : textEntries) {
            for (Component c : textEntry.getInputComponents()) {
                c.addFocusListener(this);
            }
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        lastFocussedComponent = e.getComponent();

        if (lastFocussedComponent instanceof JTextComponent) {
            ((JTextComponent) lastFocussedComponent).selectAll();
        }

        scrollToVisible(lastFocussedComponent);
    }

    @Override
    public void focusLost(FocusEvent e) {
        Component c = e.getComponent();

        if (c == lastInputComponent) {
            firstInputComponent.requestFocusInWindow();
        }

        if (c instanceof JTextComponent) {
            ((JTextComponent) c).select(0, 0);
        }
    }

    private void scrollToVisible(Component c) {
        if (c == null) {
            return;
        }

        TextEntry textEntry = (c instanceof TextEntry)
                              ? (TextEntry) c
                              : null;
        Component parent    = c.getParent();

        while ((textEntry == null) && (parent != null)) {
            if (parent instanceof TextEntry) {
                textEntry = (TextEntry) parent;
            }

            parent = parent.getParent();
        }

        if (textEntry != null) {
            scrollRectToVisible(((Component) textEntry).getBounds());
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        panelDcSubjects =
            new de.elmar_baumann.jpt.view.panels.EditRepeatableTextEntryPanel();
        panelDcTitle =
            new de.elmar_baumann.jpt.view.panels.EditTextEntryPanel();
        panelDcDescription =
            new de.elmar_baumann.jpt.view.panels.EditTextEntryPanel();
        panelPhotoshopHeadline =
            new de.elmar_baumann.jpt.view.panels.EditTextEntryPanel();
        panelIptc4xmpcoreLocation =
            new de.elmar_baumann.jpt.view.panels.EditTextEntryPanel();
        panelIptc4XmpCoreDateCreated =
            new de.elmar_baumann.jpt.view.panels.EditTextEntryPanel();
        panelPhotoshopAuthorsposition =
            new de.elmar_baumann.jpt.view.panels.EditTextEntryPanel();
        panelDcCreator =
            new de.elmar_baumann.jpt.view.panels.EditTextEntryPanel();
        panelPhotoshopCity =
            new de.elmar_baumann.jpt.view.panels.EditTextEntryPanel();
        panelPhotoshopState =
            new de.elmar_baumann.jpt.view.panels.EditTextEntryPanel();
        panelPhotoshopCountry =
            new de.elmar_baumann.jpt.view.panels.EditTextEntryPanel();
        panelDcRights =
            new de.elmar_baumann.jpt.view.panels.EditTextEntryPanel();
        panelPhotoshopCredit =
            new de.elmar_baumann.jpt.view.panels.EditTextEntryPanel();
        panelPhotoshopSource =
            new de.elmar_baumann.jpt.view.panels.EditTextEntryPanel();
        panelPhotoshopTransmissionReference =
            new de.elmar_baumann.jpt.view.panels.EditTextEntryPanel();
        panelPhotoshopInstructions =
            new de.elmar_baumann.jpt.view.panels.EditTextEntryPanel();
        panelPhotoshopCaptionwriter =
            new de.elmar_baumann.jpt.view.panels.EditTextEntryPanel();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);

        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                panelDcSubjects, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE).addComponent(
                panelDcTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE).addComponent(
                panelDcDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE).addComponent(
                panelPhotoshopHeadline, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE).addComponent(
                panelIptc4xmpcoreLocation, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE).addComponent(
                panelIptc4XmpCoreDateCreated, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE).addComponent(
                panelPhotoshopAuthorsposition, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE).addComponent(
                panelDcCreator, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE).addComponent(
                panelPhotoshopCity, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE).addComponent(
                panelPhotoshopState, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE).addComponent(
                panelPhotoshopCountry, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE).addComponent(
                panelDcRights, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE).addComponent(
                panelPhotoshopCredit, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE).addComponent(
                panelPhotoshopSource, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE).addComponent(
                panelPhotoshopTransmissionReference, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE).addComponent(
                panelPhotoshopInstructions, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE).addComponent(
                panelPhotoshopCaptionwriter, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE));
        layout.setVerticalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addComponent(
                    panelDcSubjects, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panelDcTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panelDcDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panelPhotoshopHeadline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panelIptc4xmpcoreLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panelIptc4XmpCoreDateCreated, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panelPhotoshopAuthorsposition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panelDcCreator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panelPhotoshopCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panelPhotoshopState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panelPhotoshopCountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panelDcRights, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panelPhotoshopCredit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panelPhotoshopSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panelPhotoshopTransmissionReference, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panelPhotoshopInstructions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panelPhotoshopCaptionwriter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));
    }    // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.elmar_baumann.jpt.view.panels.EditTextEntryPanel panelDcCreator;
    private de.elmar_baumann.jpt.view.panels.EditTextEntryPanel panelDcDescription;
    private de.elmar_baumann.jpt.view.panels.EditTextEntryPanel           panelDcRights;
    private de.elmar_baumann.jpt.view.panels.EditRepeatableTextEntryPanel panelDcSubjects;
    private de.elmar_baumann.jpt.view.panels.EditTextEntryPanel panelDcTitle;
    private de.elmar_baumann.jpt.view.panels.EditTextEntryPanel panelIptc4XmpCoreDateCreated;
    private de.elmar_baumann.jpt.view.panels.EditTextEntryPanel panelIptc4xmpcoreLocation;
    private de.elmar_baumann.jpt.view.panels.EditTextEntryPanel panelPhotoshopAuthorsposition;
    private de.elmar_baumann.jpt.view.panels.EditTextEntryPanel panelPhotoshopCaptionwriter;
    private de.elmar_baumann.jpt.view.panels.EditTextEntryPanel panelPhotoshopCity;
    private de.elmar_baumann.jpt.view.panels.EditTextEntryPanel panelPhotoshopCountry;
    private de.elmar_baumann.jpt.view.panels.EditTextEntryPanel panelPhotoshopCredit;
    private de.elmar_baumann.jpt.view.panels.EditTextEntryPanel panelPhotoshopHeadline;
    private de.elmar_baumann.jpt.view.panels.EditTextEntryPanel panelPhotoshopInstructions;
    private de.elmar_baumann.jpt.view.panels.EditTextEntryPanel panelPhotoshopSource;
    private de.elmar_baumann.jpt.view.panels.EditTextEntryPanel panelPhotoshopState;
    private de.elmar_baumann.jpt.view.panels.EditTextEntryPanel panelPhotoshopTransmissionReference;

    // End of variables declaration//GEN-END:variables
}
