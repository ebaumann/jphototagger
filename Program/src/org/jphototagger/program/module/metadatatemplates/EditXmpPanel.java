package org.jphototagger.program.module.metadatatemplates;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.text.JTextComponent;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcCreatorMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcDescriptionMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcRightsMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcTitleMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpIptc4XmpCoreDateCreatedMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpIptc4xmpcoreLocationMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopAuthorspositionMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCaptionwriterMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCityMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCountryMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCreditMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopHeadlineMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopInstructionsMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopSourceMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopStateMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopTransmissionReferenceMetaDataValue;
import org.jphototagger.domain.text.TextEntry;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.program.app.ui.EditRepeatableTextEntryPanel;

/**
 * Edits a {@code Xmp} object.
 * <p>
 * To get a new XMP object, call {@code #getXmp()}.
 * <p>
 * To modify an existing XMP object set it through
 * {@code #setXmp(org.jphototagger.program.data.Xmp)} and call
 * {@code #setXmpToInputComponents()} and {@code #setInputToXmp()} to write
 * the input values into the referenced XMP object.
 *
 * @author Elmar Baumann
 */
public class EditXmpPanel extends javax.swing.JPanel implements FocusListener {
    private static final long serialVersionUID = 1L;
    private final List<TextEntry> textEntries = new ArrayList<TextEntry>(18);
    private Component firstInputComponent;
    private Component lastInputComponent;
    private Component lastFocussedComponent;
    private transient Xmp xmp = new Xmp();

    public EditXmpPanel() {
        init();
    }

    private void init() {
        initComponents();
        setMetaDataValues();
        addTextEntries();
        setAutocomplete();
        addAsFocusListener();
        firstInputComponent = panelDcSubjects.textAreaInput;
        lastInputComponent = panelPhotoshopCaptionwriter.getInputComponents().get(0);
        firstInputComponent.requestFocusInWindow();
        panelDcSubjects.setBundleKeyPosRenameDialog("EditXmpPanel.Keywords.RenameDialog.Pos");
    }

    private void setMetaDataValues() {
        panelDcTitle.setMetaDataValue(XmpDcTitleMetaDataValue.INSTANCE);
        panelDcDescription.setMetaDataValue(XmpDcDescriptionMetaDataValue.INSTANCE);
        panelPhotoshopHeadline.setMetaDataValue(XmpPhotoshopHeadlineMetaDataValue.INSTANCE);
        panelIptc4xmpcoreLocation.setMetaDataValue(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE);
        panelIptc4XmpCoreDateCreated.setMetaDataValue(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE);
        panelPhotoshopAuthorsposition.setMetaDataValue(XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE);
        panelDcCreator.setMetaDataValue(XmpDcCreatorMetaDataValue.INSTANCE);
        panelPhotoshopCity.setMetaDataValue(XmpPhotoshopCityMetaDataValue.INSTANCE);
        panelPhotoshopState.setMetaDataValue(XmpPhotoshopStateMetaDataValue.INSTANCE);
        panelPhotoshopCountry.setMetaDataValue(XmpPhotoshopCountryMetaDataValue.INSTANCE);
        panelDcRights.setMetaDataValue(XmpDcRightsMetaDataValue.INSTANCE);
        panelPhotoshopCredit.setMetaDataValue(XmpPhotoshopCreditMetaDataValue.INSTANCE);
        panelPhotoshopSource.setMetaDataValue(XmpPhotoshopSourceMetaDataValue.INSTANCE);
        panelPhotoshopTransmissionReference.setMetaDataValue(XmpPhotoshopTransmissionReferenceMetaDataValue.INSTANCE);
        panelPhotoshopInstructions.setMetaDataValue(XmpPhotoshopInstructionsMetaDataValue.INSTANCE);
        panelPhotoshopCaptionwriter.setMetaDataValue(XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE);
    }

    private void addTextEntries() {
        for (Component c : getComponents()) {
            if (c instanceof TextEntry) {
                textEntries.add((TextEntry) c);
            }
        }
    }

    private void setAutocomplete() {
        if (isAutocomplete()) {
            for (TextEntry textEntry : textEntries) {
                textEntry.enableAutocomplete();
            }
        }
    }

    private boolean isAutocomplete() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage == null
                ? false
                : storage.containsKey(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                ? storage.getBoolean(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                : true;
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
            MetaDataValue value = textEntry.getMetaDataValue();

            if (textEntry instanceof EditRepeatableTextEntryPanel) {
                for (String text : ((EditRepeatableTextEntryPanel) textEntry).getRepeatableText()) {
                    xmp.setValue(value, text);
                }

                xmp.setValue(value, textEntry.getText());
            } else {
                xmp.setValue(value, textEntry.getText());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void setXmpToInputComponents() {
        for (TextEntry textEntry : textEntries) {
            Object value = xmp.getValue(textEntry.getMetaDataValue());

            if (value != null) {
                if (textEntry instanceof EditRepeatableTextEntryPanel) {
                    ((EditRepeatableTextEntryPanel) textEntry).setTexts((Collection<String>) value);
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
        Component parent = c.getParent();

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

    private void initComponents() {//GEN-BEGIN:initComponents

        panelDcSubjects = new org.jphototagger.program.app.ui.EditRepeatableTextEntryPanel();
        panelDcTitle = new org.jphototagger.program.app.ui.EditTextEntryPanel();
        panelDcDescription = new org.jphototagger.program.app.ui.EditTextEntryPanel();
        panelPhotoshopHeadline = new org.jphototagger.program.app.ui.EditTextEntryPanel();
        panelIptc4xmpcoreLocation = new org.jphototagger.program.app.ui.EditTextEntryPanel();
        panelIptc4XmpCoreDateCreated = new org.jphototagger.program.app.ui.EditTextEntryPanel();
        panelPhotoshopAuthorsposition = new org.jphototagger.program.app.ui.EditTextEntryPanel();
        panelDcCreator = new org.jphototagger.program.app.ui.EditTextEntryPanel();
        panelPhotoshopCity = new org.jphototagger.program.app.ui.EditTextEntryPanel();
        panelPhotoshopState = new org.jphototagger.program.app.ui.EditTextEntryPanel();
        panelPhotoshopCountry = new org.jphototagger.program.app.ui.EditTextEntryPanel();
        panelDcRights = new org.jphototagger.program.app.ui.EditTextEntryPanel();
        panelPhotoshopCredit = new org.jphototagger.program.app.ui.EditTextEntryPanel();
        panelPhotoshopSource = new org.jphototagger.program.app.ui.EditTextEntryPanel();
        panelPhotoshopTransmissionReference = new org.jphototagger.program.app.ui.EditTextEntryPanel();
        panelPhotoshopInstructions = new org.jphototagger.program.app.ui.EditTextEntryPanel();
        panelPhotoshopCaptionwriter = new org.jphototagger.program.app.ui.EditTextEntryPanel();

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
    }//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jphototagger.program.app.ui.EditTextEntryPanel panelDcCreator;
    private org.jphototagger.program.app.ui.EditTextEntryPanel panelDcDescription;
    private org.jphototagger.program.app.ui.EditTextEntryPanel panelDcRights;
    private org.jphototagger.program.app.ui.EditRepeatableTextEntryPanel panelDcSubjects;
    private org.jphototagger.program.app.ui.EditTextEntryPanel panelDcTitle;
    private org.jphototagger.program.app.ui.EditTextEntryPanel panelIptc4XmpCoreDateCreated;
    private org.jphototagger.program.app.ui.EditTextEntryPanel panelIptc4xmpcoreLocation;
    private org.jphototagger.program.app.ui.EditTextEntryPanel panelPhotoshopAuthorsposition;
    private org.jphototagger.program.app.ui.EditTextEntryPanel panelPhotoshopCaptionwriter;
    private org.jphototagger.program.app.ui.EditTextEntryPanel panelPhotoshopCity;
    private org.jphototagger.program.app.ui.EditTextEntryPanel panelPhotoshopCountry;
    private org.jphototagger.program.app.ui.EditTextEntryPanel panelPhotoshopCredit;
    private org.jphototagger.program.app.ui.EditTextEntryPanel panelPhotoshopHeadline;
    private org.jphototagger.program.app.ui.EditTextEntryPanel panelPhotoshopInstructions;
    private org.jphototagger.program.app.ui.EditTextEntryPanel panelPhotoshopSource;
    private org.jphototagger.program.app.ui.EditTextEntryPanel panelPhotoshopState;
    private org.jphototagger.program.app.ui.EditTextEntryPanel panelPhotoshopTransmissionReference;
    // End of variables declaration//GEN-END:variables
}
