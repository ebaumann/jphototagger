package org.jphototagger.program.module.metadatatemplates;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.text.JTextComponent;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.xmp.Xmp;
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
import org.jphototagger.program.module.editmetadata.EditRepeatableTextEntryPanel;
import org.jphototagger.program.module.editmetadata.EditTextEntryPanel;
import org.openide.util.Lookup;

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
    private final List<TextEntry> textEntries = new ArrayList<>(18);
    private Component firstInputComponent;
    private Component lastInputComponent;
    private Component lastFocussedComponent;
    private transient Xmp xmp = new Xmp();

    public EditXmpPanel() {
        init();
    }

    private void init() {
        initComponents();
        addTextEntries();
        setAutocomplete();
        addAsFocusListener();
        firstInputComponent = panelDcSubjects.textAreaInput;
        lastInputComponent = panelPhotoshopCaptionwriter.getInputComponents().get(0);
        lastFocussedComponent = firstInputComponent;
        firstInputComponent.requestFocusInWindow();
        panelDcSubjects.setBundleKeyPosRenameDialog("EditXmpPanel.Keywords.RenameDialog.Pos");
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
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs == null
                ? false
                : prefs.containsKey(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
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
    public boolean requestFocusInWindow() {
        boolean requested = super.requestFocusInWindow();
        focusLastFocuessedComponent();
        return requested;
    }

    @Override
    public void focusGained(FocusEvent evt) {
        lastFocussedComponent = evt.getComponent();
        if (lastFocussedComponent instanceof JTextComponent) {
            ((JTextComponent) lastFocussedComponent).selectAll();
        }
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
        java.awt.GridBagConstraints gridBagConstraints;

        panelDcSubjects = new org.jphototagger.program.module.editmetadata.EditRepeatableTextEntryPanel();
        panelDcTitle = new EditTextEntryPanel(XmpDcTitleMetaDataValue.INSTANCE);
        panelDcDescription = new EditTextEntryPanel(XmpDcDescriptionMetaDataValue.INSTANCE);
        panelPhotoshopHeadline = new EditTextEntryPanel(XmpPhotoshopHeadlineMetaDataValue.INSTANCE);
        panelIptc4xmpcoreLocation = new EditTextEntryPanel(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE);
        panelIptc4XmpCoreDateCreated = new EditTextEntryPanel(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE);
        panelPhotoshopAuthorsposition = new EditTextEntryPanel(XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE);
        panelDcCreator = new EditTextEntryPanel(XmpDcCreatorMetaDataValue.INSTANCE);
        panelPhotoshopCity = new EditTextEntryPanel(XmpPhotoshopCityMetaDataValue.INSTANCE);
        panelPhotoshopState = new EditTextEntryPanel(XmpPhotoshopStateMetaDataValue.INSTANCE);
        panelPhotoshopCountry = new EditTextEntryPanel(XmpPhotoshopCountryMetaDataValue.INSTANCE);
        panelDcRights = new EditTextEntryPanel(XmpDcRightsMetaDataValue.INSTANCE);
        panelPhotoshopCredit = new EditTextEntryPanel(XmpPhotoshopCreditMetaDataValue.INSTANCE);
        panelPhotoshopSource = new EditTextEntryPanel(XmpPhotoshopSourceMetaDataValue.INSTANCE);
        panelPhotoshopTransmissionReference = new EditTextEntryPanel(XmpPhotoshopTransmissionReferenceMetaDataValue.INSTANCE);
        panelPhotoshopInstructions = new EditTextEntryPanel(XmpPhotoshopInstructionsMetaDataValue.INSTANCE);
        panelPhotoshopCaptionwriter = new EditTextEntryPanel(XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE);
        panelFill = new javax.swing.JPanel();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        panelDcSubjects.setName("panelDcSubjects"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelDcSubjects, gridBagConstraints);

        panelDcTitle.setName("panelDcTitle"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelDcTitle, gridBagConstraints);

        panelDcDescription.setName("panelDcDescription"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelDcDescription, gridBagConstraints);

        panelPhotoshopHeadline.setName("panelPhotoshopHeadline"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelPhotoshopHeadline, gridBagConstraints);

        panelIptc4xmpcoreLocation.setName("panelIptc4xmpcoreLocation"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelIptc4xmpcoreLocation, gridBagConstraints);

        panelIptc4XmpCoreDateCreated.setName("panelIptc4XmpCoreDateCreated"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelIptc4XmpCoreDateCreated, gridBagConstraints);

        panelPhotoshopAuthorsposition.setName("panelPhotoshopAuthorsposition"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelPhotoshopAuthorsposition, gridBagConstraints);

        panelDcCreator.setName("panelDcCreator"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelDcCreator, gridBagConstraints);

        panelPhotoshopCity.setName("panelPhotoshopCity"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelPhotoshopCity, gridBagConstraints);

        panelPhotoshopState.setName("panelPhotoshopState"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelPhotoshopState, gridBagConstraints);

        panelPhotoshopCountry.setName("panelPhotoshopCountry"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelPhotoshopCountry, gridBagConstraints);

        panelDcRights.setName("panelDcRights"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelDcRights, gridBagConstraints);

        panelPhotoshopCredit.setName("panelPhotoshopCredit"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelPhotoshopCredit, gridBagConstraints);

        panelPhotoshopSource.setName("panelPhotoshopSource"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelPhotoshopSource, gridBagConstraints);

        panelPhotoshopTransmissionReference.setName("panelPhotoshopTransmissionReference"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelPhotoshopTransmissionReference, gridBagConstraints);

        panelPhotoshopInstructions.setName("panelPhotoshopInstructions"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelPhotoshopInstructions, gridBagConstraints);

        panelPhotoshopCaptionwriter.setName("panelPhotoshopCaptionwriter"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelPhotoshopCaptionwriter, gridBagConstraints);

        javax.swing.GroupLayout panelFillLayout = new javax.swing.GroupLayout(panelFill);
        panelFill.setLayout(panelFillLayout);
        panelFillLayout.setHorizontalGroup(
            panelFillLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelFillLayout.setVerticalGroup(
            panelFillLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        add(panelFill, gridBagConstraints);
    }//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jphototagger.program.module.editmetadata.EditTextEntryPanel panelDcCreator;
    private org.jphototagger.program.module.editmetadata.EditTextEntryPanel panelDcDescription;
    private org.jphototagger.program.module.editmetadata.EditTextEntryPanel panelDcRights;
    private org.jphototagger.program.module.editmetadata.EditRepeatableTextEntryPanel panelDcSubjects;
    private org.jphototagger.program.module.editmetadata.EditTextEntryPanel panelDcTitle;
    private javax.swing.JPanel panelFill;
    private org.jphototagger.program.module.editmetadata.EditTextEntryPanel panelIptc4XmpCoreDateCreated;
    private org.jphototagger.program.module.editmetadata.EditTextEntryPanel panelIptc4xmpcoreLocation;
    private org.jphototagger.program.module.editmetadata.EditTextEntryPanel panelPhotoshopAuthorsposition;
    private org.jphototagger.program.module.editmetadata.EditTextEntryPanel panelPhotoshopCaptionwriter;
    private org.jphototagger.program.module.editmetadata.EditTextEntryPanel panelPhotoshopCity;
    private org.jphototagger.program.module.editmetadata.EditTextEntryPanel panelPhotoshopCountry;
    private org.jphototagger.program.module.editmetadata.EditTextEntryPanel panelPhotoshopCredit;
    private org.jphototagger.program.module.editmetadata.EditTextEntryPanel panelPhotoshopHeadline;
    private org.jphototagger.program.module.editmetadata.EditTextEntryPanel panelPhotoshopInstructions;
    private org.jphototagger.program.module.editmetadata.EditTextEntryPanel panelPhotoshopSource;
    private org.jphototagger.program.module.editmetadata.EditTextEntryPanel panelPhotoshopState;
    private org.jphototagger.program.module.editmetadata.EditTextEntryPanel panelPhotoshopTransmissionReference;
    // End of variables declaration//GEN-END:variables
}
