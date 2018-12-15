package org.jphototagger.program.module.search;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValueIds;
import org.jphototagger.domain.metadata.exif.ExifDateTimeOriginalMetaDataValue;
import org.jphototagger.domain.metadata.search.Comparator;
import org.jphototagger.domain.metadata.search.Operator;
import org.jphototagger.domain.metadata.search.SavedSearchPanel;
import org.jphototagger.domain.metadata.xmp.XmpIptc4XmpCoreDateCreatedMetaDataValue;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.thirdparty.DateChooserDialog;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.Icons;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann, Tobias Stening
 */
public final class SearchMetaDataValuePanel extends PanelExt {

    private static final long   serialVersionUID = 1L;
    private static final String SEL_LEFT_BRACKET = "<html><font color=\"#000000\"><b>(</b></font></html>";
    private static final String NOT_SEL_LEFT_BRACKET = "<html><font color=\"#777777\"><b>(</b></font></html>";
    private static final String SEL_RIGHT_BRACKET = "<html><font color=\"#000000\"><b>)</b></font></html>";
    private static final String NOT_SEL_RIGHT_BRACKET = "<html><font color=\"#777777\"><b>)</b></font></html>";
    private MetaDataValue.ValueType  prevColumnDataType;
    private boolean isOperatorsEnabled = true;
    private boolean listenToActions = true;
    private boolean changed;

    public SearchMetaDataValuePanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setComboboxSelIndices();
        setFormatter();
        setToggleButtonsTexts(toggleButtonBracketLeft1, true);
        setToggleButtonsTexts(toggleButtonBracketLeft2, true);
        setToggleButtonsTexts(toggleButtonBracketRight, false);
    }

    public void reset() {
        listenToActions = false;
        toggleButtonBracketLeft1.setSelected(false);
        toggleButtonBracketLeft2.setSelected(false);
        toggleButtonBracketRight.setSelected(false);
        comboBoxColumns.setSelectedIndex(0);
        comboBoxComparators.setSelectedIndex(0);
        comboBoxOperators.setSelectedIndex(0);
        textFieldValue.setText("");
        setChanged(false);
        setFormatter();
        setInputVerifier();
        listenToActions = true;
    }

    // TextFiel#setFormatterFactory() removes text; reset text if the column
    // data type is equal to the previous column data type
    private void setFormatter() {
        String value = textFieldValue.getText();
        MetaDataValue column = getColumn();
        MetaDataValue.ValueType columnDataType = column.getValueType();

        textFieldValue.setFormatterFactory(column.getFormatterFactory());

        if (columnDataType.equals(prevColumnDataType) && !value.isEmpty()) {
            textFieldValue.setText(value);
        }

        prevColumnDataType = column.getValueType();
    }

    private void setInputVerifier() {
        textFieldValue.setInputVerifier(getColumn().getInputVerifier());
    }

    private MetaDataValue getColumn() {
        return (MetaDataValue) comboBoxColumns.getSelectedItem();
    }

    private void checkToggleButtons() {
        if (toggleButtonBracketLeft2.isSelected()) {
            toggleButtonBracketLeft1.setSelected(false);
        }

        if (toggleButtonBracketLeft1.isSelected()) {
            toggleButtonBracketLeft2.setSelected(false);
        }

        toggleButtonBracketLeft1.setEnabled(isOperatorsEnabled &&!toggleButtonBracketLeft2.isSelected());
        toggleButtonBracketLeft2.setEnabled(!toggleButtonBracketLeft1.isSelected());
        setToggleButtonsText();
    }

    private void setToggleButtonsText() {
        setToggleButtonsTexts(toggleButtonBracketLeft1, true);
        setToggleButtonsTexts(toggleButtonBracketLeft2, true);
        setToggleButtonsTexts(toggleButtonBracketRight, false);
    }

    private void setToggleButtonsTexts(JToggleButton tb, boolean left) {
        boolean sel   = tb.isSelected();
        boolean right = !left;

        tb.setText((left && sel)
                   ? SEL_LEFT_BRACKET
                   : (left &&!sel)
                     ? NOT_SEL_LEFT_BRACKET
                     : (right && sel)
                       ? SEL_RIGHT_BRACKET
                       : NOT_SEL_RIGHT_BRACKET);
    }

    private void setComboboxSelIndices() {
        listenToActions = false;
        comboBoxOperators.setSelectedIndex(0);
        comboBoxColumns.setSelectedIndex(0);
        comboBoxComparators.setSelectedIndex(0);
        listenToActions = true;
    }

    public JTextField getTextFieldValue() {
        return textFieldValue;
    }

    public int getCountOpenBrackets() {
        int count = 0;

        if (toggleButtonBracketLeft1.isSelected()) {
            count++;
        }

        if (toggleButtonBracketLeft2.isSelected()) {
            count++;
        }

        return count;
    }

    public int getCountClosedBrackets() {
        int count = 0;

        if (toggleButtonBracketRight.isSelected()) {
            count++;
        }

        return count;
    }

    public boolean canCreateSql() {
        return (comboBoxOperators.getModel().getSelectedItem() != null)
               && (comboBoxColumns.getModel().getSelectedItem() != null)
               && (comboBoxComparators.getModel().getSelectedItem() != null)
               &&!textFieldValue.getText().trim().isEmpty();
    }

    public String getValue() {
        return textFieldValue.getText();
    }

    public SavedSearchPanel getSavedSearchPanel() {
        listenToActions = false;

        SavedSearchPanel savedSearchPanel = new SavedSearchPanel();

        savedSearchPanel.setBracketRightSelected(toggleButtonBracketRight.isSelected());
        savedSearchPanel.setColumnId(MetaDataValueIds.getId((MetaDataValue) comboBoxColumns.getModel().getSelectedItem()));
        savedSearchPanel.setComparatorId(((Comparator) comboBoxComparators.getModel().getSelectedItem()).getId());
        savedSearchPanel.setBracketLeft1Selected(toggleButtonBracketLeft1.isSelected());
        savedSearchPanel.setBracketLeft2Selected(toggleButtonBracketLeft2.isSelected());
        savedSearchPanel.setOperatorId(((Operator) comboBoxOperators.getModel().getSelectedItem()).getId());

        String value = textFieldValue.getText();

        savedSearchPanel.setValue(value.isEmpty()
                                  ? null
                                  : value);
        listenToActions = true;

        return savedSearchPanel;
    }

    public void setSavedSearchPanel(SavedSearchPanel savedSearchPanel) {
        if (savedSearchPanel == null) {
            throw new NullPointerException("savedSearchPanel == null");
        }

        listenToActions = false;
        toggleButtonBracketRight.setSelected(savedSearchPanel.isBracketRightSelected());
        comboBoxColumns.getModel().setSelectedItem(MetaDataValueIds.getMetaDataValue(savedSearchPanel.getColumnId()));
        comboBoxComparators.getModel().setSelectedItem(Comparator.get(savedSearchPanel.getComparatorId()));
        toggleButtonBracketLeft1.setSelected(savedSearchPanel.isBracketLeft1Selected());
        toggleButtonBracketLeft2.setSelected(savedSearchPanel.isBracketLeft2Selected());
        comboBoxOperators.getModel().setSelectedItem(Operator.get(savedSearchPanel.getOperatorId()));

        if (savedSearchPanel.hasValue()) {
            textFieldValue.setText(savedSearchPanel.getValue());
        }

        setToggleButtonsText();
        listenToActions = true;
    }

    /**
     * Setzt den Status auf verändert (modifiziert). Das ist nur bei
     * Initialisierungen notwendig. Modifiziert der Benutzer ein Element
     * dieses Panels, wird der Status automatisch auf true gesetzt.
     *
     * @param changed true, wenn modifiziert. Default: false.
     */
    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    /**
     * Liefert, ob ein Element des Panels modifiziert wurde.
     *
     * @return true, wenn modifiziert
     */
    public boolean isChanged() {
        return changed;
    }

    private void setDate() {
        GregorianCalendar cal = DateChooserDialog.showDialog(this, Icons.getAppIcons());

        if (cal != null) {
            String year  = Integer.toString(cal.get(Calendar.YEAR));
            String month = getDateFormatted(Integer.toString(cal.get(Calendar.MONTH)));
            String day = getDateFormatted(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));

            textFieldValue.setText(year + "-" + month + "-" + day);
            setChanged(true);
        }
    }

    private String getDateFormatted(String dayOrMonth) {
        if (dayOrMonth.length() < 2) {
            return "0" + dayOrMonth;
        }

        return dayOrMonth;
    }

    private void setChanged() {
        if (listenToActions) {
            setChanged(true);
        }
    }

    private void handleButtonCalendarActionPerformed() {
        if (listenToActions) {
            setDate();
        }
    }

    private void handleTaggleButtonBracketLeft2ActionPerformed() {
        setToggleButtonsTexts(toggleButtonBracketLeft2, true);

        if (listenToActions) {
            checkToggleButtons();
            setChanged(true);
        }
    }

    private void handleTaggleButtonBracketLeftActionPerformed() {
        setToggleButtonsTexts(toggleButtonBracketLeft1, true);

        if (listenToActions) {
            checkToggleButtons();
            setChanged(true);
        }
    }

    private void handleTextFieldValueKeyTyped(KeyEvent evt) {
        if (listenToActions) {
            setChanged(true);
        }
    }

    private void handleColumnChanged() {
        setChanged();
        setFormatter();
        setInputVerifier();
        setEnabledCalendarButton();
    }

    private void setEnabledCalendarButton() {
        Object selItem = comboBoxColumns.getModel().getSelectedItem();
        buttonCalendar.setEnabled(
            selItem.equals(ExifDateTimeOriginalMetaDataValue.INSTANCE)
            || selItem.equals(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE));
    }

    private final ListCellRenderer<Object> columnRenderer = new DefaultListCellRenderer() {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            MetaDataValue metaDataValue = (MetaDataValue) value;
            label.setIcon(metaDataValue.getCategoryIcon());
            label.setText(metaDataValue.getDescription());

            return label;
        }
    };

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        toggleButtonBracketLeft1 = UiFactory.toggleButton();
        comboBoxOperators = UiFactory.comboBox();
        toggleButtonBracketLeft2 = UiFactory.toggleButton();
        comboBoxColumns = UiFactory.comboBox();
        comboBoxComparators = UiFactory.comboBox();
        textFieldValue = UiFactory.formattedTextField();
        textFieldValue.setTransferHandler(new org.jphototagger.program.datatransfer.DropTextComponentTransferHandler());
        toggleButtonBracketRight = UiFactory.toggleButton();
        buttonCalendar = UiFactory.button();
        buttonRemoveColumn = UiFactory.button();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        toggleButtonBracketLeft1.setText("("); // NOI18N
        toggleButtonBracketLeft1.setMargin(UiFactory.insets(2, 2, 2, 2));
        toggleButtonBracketLeft1.setName("toggleButtonBracketLeft1"); // NOI18N
        toggleButtonBracketLeft1.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonBracketLeft1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(toggleButtonBracketLeft1, gridBagConstraints);

        comboBoxOperators.setModel(new DefaultComboBoxModel<Object>(Operator.values()));
        comboBoxOperators.setName("comboBoxOperators"); // NOI18N
        comboBoxOperators.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxOperatorsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = UiFactory.insets(0, 6, 0, 0);
        add(comboBoxOperators, gridBagConstraints);

        toggleButtonBracketLeft2.setText("("); // NOI18N
        toggleButtonBracketLeft2.setMargin(UiFactory.insets(2, 2, 2, 2));
        toggleButtonBracketLeft2.setName("toggleButtonBracketLeft2"); // NOI18N
        toggleButtonBracketLeft2.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonBracketLeft2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = UiFactory.insets(0, 6, 0, 0);
        add(toggleButtonBracketLeft2, gridBagConstraints);

        comboBoxColumns.setModel(new DefaultComboBoxModel<>(org.jphototagger.domain.metadata.search.AdvancedSearchMetaDataValues.get().toArray()));
        comboBoxColumns.setName("comboBoxColumns"); // NOI18N
        comboBoxColumns.setRenderer(columnRenderer);
        comboBoxColumns.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxColumnsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = UiFactory.insets(0, 6, 0, 0);
        add(comboBoxColumns, gridBagConstraints);

        comboBoxComparators.setModel(new DefaultComboBoxModel<Object>(Comparator.values()));
        comboBoxComparators.setName("comboBoxComparators"); // NOI18N
        comboBoxComparators.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxComparatorsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = UiFactory.insets(0, 6, 0, 0);
        add(comboBoxComparators, gridBagConstraints);

        textFieldValue.setColumns(10);
        textFieldValue.setFocusLostBehavior(javax.swing.JFormattedTextField.PERSIST);
        textFieldValue.setName("textFieldValue"); // NOI18N
        textFieldValue.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textFieldValueKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 6, 0, 0);
        add(textFieldValue, gridBagConstraints);

        toggleButtonBracketRight.setText(")"); // NOI18N
        toggleButtonBracketRight.setMargin(UiFactory.insets(2, 2, 2, 2));
        toggleButtonBracketRight.setName("toggleButtonBracketRight"); // NOI18N
        toggleButtonBracketRight.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonBracketRightActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = UiFactory.insets(0, 6, 0, 0);
        add(toggleButtonBracketRight, gridBagConstraints);

        buttonCalendar.setIcon(org.jphototagger.resources.Icons.getIcon("icon_calendar.png"));
        buttonCalendar.setMnemonic('1');
        buttonCalendar.setMargin(UiFactory.insets(0, 0, 0, 0));
        buttonCalendar.setName("buttonCalendar"); // NOI18N
        buttonCalendar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCalendarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 6, 0, 0);
        add(buttonCalendar, gridBagConstraints);

        buttonRemoveColumn.setIcon(org.jphototagger.resources.Icons.getIcon("icon_delete12.png"));
        buttonRemoveColumn.setToolTipText(Bundle.getString(getClass(), "SearchMetaDataValuePanel.buttonRemoveColumn.toolTipText")); // NOI18N
        buttonRemoveColumn.setMargin(UiFactory.insets(0, 0, 0, 0));
        buttonRemoveColumn.setName("buttonRemoveColumn"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 6, 0, 0);
        add(buttonRemoveColumn, gridBagConstraints);
    }

    private void toggleButtonBracketLeft1ActionPerformed(java.awt.event.ActionEvent evt) {
        handleTaggleButtonBracketLeftActionPerformed();
    }

    private void toggleButtonBracketLeft2ActionPerformed(java.awt.event.ActionEvent evt) {
        handleTaggleButtonBracketLeft2ActionPerformed();
    }

    private void buttonCalendarActionPerformed(java.awt.event.ActionEvent evt) {
        handleButtonCalendarActionPerformed();
    }

    private void comboBoxOperatorsActionPerformed(java.awt.event.ActionEvent evt) {
        setChanged();
    }

    private void comboBoxColumnsActionPerformed(java.awt.event.ActionEvent evt) {
        handleColumnChanged();
    }

    private void comboBoxComparatorsActionPerformed(java.awt.event.ActionEvent evt) {
        setChanged();
    }

    private void toggleButtonBracketRightActionPerformed(java.awt.event.ActionEvent evt) {
        setToggleButtonsTexts(toggleButtonBracketRight, false);
        setChanged();
    }

    private void textFieldValueKeyTyped(java.awt.event.KeyEvent evt) {
        handleTextFieldValueKeyTyped(evt);
    }

    private javax.swing.JButton buttonCalendar;
    public javax.swing.JButton buttonRemoveColumn;
    private javax.swing.JComboBox<Object> comboBoxColumns;
    private javax.swing.JComboBox<Object> comboBoxComparators;
    private javax.swing.JComboBox<Object> comboBoxOperators;
    private javax.swing.JFormattedTextField textFieldValue;
    private javax.swing.JToggleButton toggleButtonBracketLeft1;
    private javax.swing.JToggleButton toggleButtonBracketLeft2;
    private javax.swing.JToggleButton toggleButtonBracketRight;
}
