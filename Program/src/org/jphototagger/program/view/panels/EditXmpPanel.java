package org.jphototagger.program.view.panels;

import org.jphototagger.program.UserSettings;
import org.jphototagger.program.data.TextEntry;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcCreator;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcDescription;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcRights;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcTitle;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpPhotoshopAuthorsposition;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpPhotoshopCaptionwriter;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCity;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpPhotoshopInstructions;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopSource;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopState;
import org.jphototagger.program.database.metadata.xmp
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
 * {@link #setXmp(org.jphototagger.program.data.Xmp)} and call
 * {@link #setXmpToInputComponents()} and {@link #setInputToXmp()} to write
 * the input values into the referenced XMP object.
 *
 * @author Elmar Baumann
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
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

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
    public void focusGained(FocusEvent evt) {
        lastFocussedComponent = evt.getComponent();

        if (lastFocussedComponent instanceof JTextComponent) {
            ((JTextComponent) lastFocussedComponent).selectAll();
        }

        scrollToVisible(lastFocussedComponent);
    }

    @Override
    public void focusLost(FocusEvent evt) {
        Component c = evt.getComponent();

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

        panelDcSubjects = new org.jphototagger.program.view.panels.EditRepeatableTextEntryPanel();
        panelDcTitle = new org.jphototagger.program.view.panels.EditTextEntryPanel();
        panelDcDescription = new org.jphototagger.program.view.panels.EditTextEntryPanel();
        panelPhotoshopHeadline = new org.jphototagger.program.view.panels.EditTextEntryPanel();
        panelIptc4xmpcoreLocation = new org.jphototagger.program.view.panels.EditTextEntryPanel();
        panelIptc4XmpCoreDateCreated = new org.jphototagger.program.view.panels.EditTextEntryPanel();
        panelPhotoshopAuthorsposition = new org.jphototagger.program.view.panels.EditTextEntryPanel();
        panelDcCreator = new org.jphototagger.program.view.panels.EditTextEntryPanel();
        panelPhotoshopCity = new org.jphototagger.program.view.panels.EditTextEntryPanel();
        panelPhotoshopState = new org.jphototagger.program.view.panels.EditTextEntryPanel();
        panelPhotoshopCountry = new org.jphototagger.program.view.panels.EditTextEntryPanel();
        panelDcRights = new org.jphototagger.program.view.panels.EditTextEntryPanel();
        panelPhotoshopCredit = new org.jphototagger.program.view.panels.EditTextEntryPanel();
        panelPhotoshopSource = new org.jphototagger.program.view.panels.EditTextEntryPanel();
        panelPhotoshopTransmissionReference = new org.jphototagger.program.view.panels.EditTextEntryPanel();
        panelPhotoshopInstructions = new org.jphototagger.program.view.panels.EditTextEntryPanel();
        panelPhotoshopCaptionwriter = new org.jphototagger.program.view.panels.EditTextEntryPanel();

        setName("Form"); // NOI18N

        panelDcSubjects.setName("panelDcSubjects"); // NOI18N

        panelDcTitle.setName("panelDcTitle"); // NOI18N

        panelDcDescription.setName("panelDcDescription"); // NOI18N

        panelPhotoshopHeadline.setName("panelPhotoshopHeadline"); // NOI18N

        panelIptc4xmpcoreLocation.setName("panelIptc4xmpcoreLocation"); // NOI18N

        panelIptc4XmpCoreDateCreated.setName("panelIptc4XmpCoreDateCreated"); // NOI18N

        panelPhotoshopAuthorsposition.setName("panelPhotoshopAuthorsposition"); // NOI18N

        panelDcCreator.setName("panelDcCreator"); // NOI18N

        panelPhotoshopCity.setName("panelPhotoshopCity"); // NOI18N

        panelPhotoshopState.setName("panelPhotoshopState"); // NOI18N

        panelPhotoshopCountry.setName("panelPhotoshopCountry"); // NOI18N

        panelDcRights.setName("panelDcRights"); // NOI18N

        panelPhotoshopCredit.setName("panelPhotoshopCredit"); // NOI18N

        panelPhotoshopSource.setName("panelPhotoshopSource"); // NOI18N

        panelPhotoshopTransmissionReference.setName("panelPhotoshopTransmissionReference"); // NOI18N

        panelPhotoshopInstructions.setName("panelPhotoshopInstructions"); // NOI18N

        panelPhotoshopCaptionwriter.setName("panelPhotoshopCaptionwriter"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelDcSubjects, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
            .addComponent(panelDcTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
            .addComponent(panelDcDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
            .addComponent(panelPhotoshopHeadline, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
            .addComponent(panelIptc4xmpcoreLocation, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
            .addComponent(panelIptc4XmpCoreDateCreated, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
            .addComponent(panelPhotoshopAuthorsposition, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
            .addComponent(panelDcCreator, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
            .addComponent(panelPhotoshopCity, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
            .addComponent(panelPhotoshopState, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
            .addComponent(panelPhotoshopCountry, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
            .addComponent(panelDcRights, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
            .addComponent(panelPhotoshopCredit, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
            .addComponent(panelPhotoshopSource, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
            .addComponent(panelPhotoshopTransmissionReference, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
            .addComponent(panelPhotoshopInstructions, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
            .addComponent(panelPhotoshopCaptionwriter, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelDcSubjects, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDcTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDcDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPhotoshopHeadline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelIptc4xmpcoreLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelIptc4XmpCoreDateCreated, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPhotoshopAuthorsposition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDcCreator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPhotoshopCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPhotoshopState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPhotoshopCountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDcRights, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPhotoshopCredit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPhotoshopSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPhotoshopTransmissionReference, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPhotoshopInstructions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPhotoshopCaptionwriter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jphototagger.program.view.panels.EditTextEntryPanel panelDcCreator;
    private org.jphototagger.program.view.panels.EditTextEntryPanel panelDcDescription;
    private org.jphototagger.program.view.panels.EditTextEntryPanel panelDcRights;
    private org.jphototagger.program.view.panels.EditRepeatableTextEntryPanel panelDcSubjects;
    private org.jphototagger.program.view.panels.EditTextEntryPanel panelDcTitle;
    private org.jphototagger.program.view.panels.EditTextEntryPanel panelIptc4XmpCoreDateCreated;
    private org.jphototagger.program.view.panels.EditTextEntryPanel panelIptc4xmpcoreLocation;
    private org.jphototagger.program.view.panels.EditTextEntryPanel panelPhotoshopAuthorsposition;
    private org.jphototagger.program.view.panels.EditTextEntryPanel panelPhotoshopCaptionwriter;
    private org.jphototagger.program.view.panels.EditTextEntryPanel panelPhotoshopCity;
    private org.jphototagger.program.view.panels.EditTextEntryPanel panelPhotoshopCountry;
    private org.jphototagger.program.view.panels.EditTextEntryPanel panelPhotoshopCredit;
    private org.jphototagger.program.view.panels.EditTextEntryPanel panelPhotoshopHeadline;
    private org.jphototagger.program.view.panels.EditTextEntryPanel panelPhotoshopInstructions;
    private org.jphototagger.program.view.panels.EditTextEntryPanel panelPhotoshopSource;
    private org.jphototagger.program.view.panels.EditTextEntryPanel panelPhotoshopState;
    private org.jphototagger.program.view.panels.EditTextEntryPanel panelPhotoshopTransmissionReference;
    // End of variables declaration//GEN-END:variables
}
