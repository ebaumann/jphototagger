package org.jphototagger.program.view.panels;

import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.SavedSearchPanel;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Comparator;
import org.jphototagger.program.database.metadata.exif.ColumnExifDateTimeOriginal;
import org.jphototagger.program.database.metadata.FormatterFactory;
import org.jphototagger.program.database.metadata.Operator;
import org.jphototagger.program.database.metadata.selections.AdvancedSearchColumns;
import org.jphototagger.program.database.metadata.selections.ColumnIds;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.renderer.ListCellRendererTableColumns;
import org.jphototagger.lib.thirdparty.DateChooserDialog;

import java.awt.event.KeyEvent;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.program.resource.JptBundle;

/**
 * Panel mit einer Suchspalte und deren möglichen Verknüpfungen, Operatoren
 * und Suchtext.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class SearchColumnPanel extends javax.swing.JPanel {
    private static final long   serialVersionUID = -2063583386957538525L;
    private static final String SEL_LEFT_BRACKET =
        "<html><font size=\"+1\" color=\"#000000\"><b>(</b></font></html>";
    private static final String NOT_SEL_LEFT_BRACKET =
        "<html><font size=\"+1\" color=\"#dddddd\"><b>(</b></font></html>";
    private static final String SEL_RIGHT_BRACKET =
        "<html><font size=\"+1\" color=\"#000000\"><b>)</b></font></html>";
    private static final String NOT_SEL_RIGHT_BRACKET =
        "<html><font size=\"+1\" color=\"#dddddd\"><b>)</b></font></html>";
    private final ListCellRendererTableColumns columnRenderer =
        new ListCellRendererTableColumns();
    private Column.DataType  prevColumnDataType;
    private boolean          isOperatorsEnabled = true;
    private boolean          listenToActions    = true;
    private boolean          changed;

    public SearchColumnPanel() {
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
        String          value          = textFieldValue.getText();
        Column          column         = getColumn();
        Column.DataType columnDataType = column.getDataType();

        textFieldValue.setFormatterFactory(
                FormatterFactory.getFormatterFactory(column));

        if (columnDataType.equals(prevColumnDataType) && !value.isEmpty()) {
            textFieldValue.setText(value);
        }
        prevColumnDataType = column.getDataType();
    }

    private void setInputVerifier() {
        textFieldValue.setInputVerifier(getColumn().getInputVerifier());
    }

    private Column getColumn() {
        return (Column) comboBoxColumns.getSelectedItem();
    }

    private void checkToggleButtons() {
        if (toggleButtonBracketLeft2.isSelected()) {
            toggleButtonBracketLeft1.setSelected(false);
        }

        if (toggleButtonBracketLeft1.isSelected()) {
            toggleButtonBracketLeft2.setSelected(false);
        }

        toggleButtonBracketLeft1.setEnabled(isOperatorsEnabled
                &&!toggleButtonBracketLeft2.isSelected());
        toggleButtonBracketLeft2.setEnabled(
            !toggleButtonBracketLeft1.isSelected());
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

        savedSearchPanel.setBracketRightSelected(
            toggleButtonBracketRight.isSelected());
        savedSearchPanel.setColumnId(
            ColumnIds.getId(
                (Column) comboBoxColumns.getModel().getSelectedItem()));
        savedSearchPanel
            .setComparatorId(((Comparator) comboBoxComparators.getModel()
                .getSelectedItem()).getId());
        savedSearchPanel.setBracketLeft1Selected(
            toggleButtonBracketLeft1.isSelected());
        savedSearchPanel.setBracketLeft2Selected(
            toggleButtonBracketLeft2.isSelected());
        savedSearchPanel
            .setOperatorId(((Operator) comboBoxOperators.getModel()
                .getSelectedItem()).getId());

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
        toggleButtonBracketRight.setSelected(
            savedSearchPanel.isBracketRightSelected());
        comboBoxColumns.getModel().setSelectedItem(
            ColumnIds.getColumn(savedSearchPanel.getColumnId()));
        comboBoxComparators.getModel().setSelectedItem(
            Comparator.get(savedSearchPanel.getComparatorId()));
        toggleButtonBracketLeft1.setSelected(
            savedSearchPanel.isBracketLeft1Selected());
        toggleButtonBracketLeft2.setSelected(
            savedSearchPanel.isBracketLeft2Selected());
        comboBoxOperators.getModel().setSelectedItem(
            Operator.get(savedSearchPanel.getOperatorId()));

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
        GregorianCalendar cal = DateChooserDialog.showDialog(this,
                                    AppLookAndFeel.getAppIcons());

        if (cal != null) {
            String year  = Integer.toString(cal.get(Calendar.YEAR));
            String month =
                getDateFormatted(Integer.toString(cal.get(Calendar.MONTH)));
            String day = getDateFormatted(
                             Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));

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
        showInputHelpers();
    }

    private void setEnabledCalendarButton() {
        Object selItem = comboBoxColumns.getModel().getSelectedItem();
        buttonCalendar.setEnabled(
            selItem.equals(ColumnExifDateTimeOriginal.INSTANCE)
            || selItem.equals(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE));
    }

    private void showInputHelpers() {
        if (listenToActions
                && getColumn().equals(ColumnXmpDcSubjectsSubject.INSTANCE)) {
            GUI.getAppFrame().getMenuItemInputHelper().doClick();
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
        java.awt.GridBagConstraints gridBagConstraints;

        toggleButtonBracketLeft1 = new javax.swing.JToggleButton();
        comboBoxOperators = new javax.swing.JComboBox();
        toggleButtonBracketLeft2 = new javax.swing.JToggleButton();
        comboBoxColumns = new javax.swing.JComboBox();
        comboBoxComparators = new javax.swing.JComboBox();
        textFieldValue = new javax.swing.JFormattedTextField();
        textFieldValue.setTransferHandler(new org.jphototagger.program.datatransfer.TransferHandlerDropTextComponent());
        toggleButtonBracketRight = new javax.swing.JToggleButton();
        buttonCalendar = new javax.swing.JButton();
        buttonRemoveColumn = new javax.swing.JButton();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        toggleButtonBracketLeft1.setForeground(new java.awt.Color(255, 0, 0));
        toggleButtonBracketLeft1.setText("(");
        toggleButtonBracketLeft1.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        toggleButtonBracketLeft1.setContentAreaFilled(false);
        toggleButtonBracketLeft1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        toggleButtonBracketLeft1.setName("toggleButtonBracketLeft1"); // NOI18N
        toggleButtonBracketLeft1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonBracketLeft1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(toggleButtonBracketLeft1, gridBagConstraints);

        comboBoxOperators.setModel(new DefaultComboBoxModel(Operator.values()));
        comboBoxOperators.setName("comboBoxOperators"); // NOI18N
        comboBoxOperators.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxOperatorsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(comboBoxOperators, gridBagConstraints);

        toggleButtonBracketLeft2.setForeground(new java.awt.Color(255, 0, 0));
        toggleButtonBracketLeft2.setText("(");
        toggleButtonBracketLeft2.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        toggleButtonBracketLeft2.setContentAreaFilled(false);
        toggleButtonBracketLeft2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        toggleButtonBracketLeft2.setName("toggleButtonBracketLeft2"); // NOI18N
        toggleButtonBracketLeft2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonBracketLeft2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(toggleButtonBracketLeft2, gridBagConstraints);

        comboBoxColumns.setModel(new DefaultComboBoxModel(AdvancedSearchColumns.get().toArray()));
        comboBoxColumns.setName("comboBoxColumns"); // NOI18N
        comboBoxColumns.setRenderer(columnRenderer);
        comboBoxColumns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxColumnsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(comboBoxColumns, gridBagConstraints);

        comboBoxComparators.setModel(new DefaultComboBoxModel(Comparator.values()));
        comboBoxComparators.setName("comboBoxComparators"); // NOI18N
        comboBoxComparators.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxComparatorsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(comboBoxComparators, gridBagConstraints);

        textFieldValue.setColumns(10);
        textFieldValue.setFocusLostBehavior(javax.swing.JFormattedTextField.PERSIST);
        textFieldValue.setName("textFieldValue"); // NOI18N
        textFieldValue.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textFieldValueKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(textFieldValue, gridBagConstraints);

        toggleButtonBracketRight.setForeground(new java.awt.Color(255, 0, 0));
        toggleButtonBracketRight.setText(")");
        toggleButtonBracketRight.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        toggleButtonBracketRight.setContentAreaFilled(false);
        toggleButtonBracketRight.setMargin(new java.awt.Insets(2, 2, 2, 2));
        toggleButtonBracketRight.setName("toggleButtonBracketRight"); // NOI18N
        toggleButtonBracketRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonBracketRightActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(toggleButtonBracketRight, gridBagConstraints);

        buttonCalendar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_calendar.png"))); // NOI18N
        buttonCalendar.setMnemonic('1');
        buttonCalendar.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonCalendar.setMaximumSize(new java.awt.Dimension(16, 16));
        buttonCalendar.setMinimumSize(new java.awt.Dimension(16, 16));
        buttonCalendar.setName("buttonCalendar"); // NOI18N
        buttonCalendar.setPreferredSize(new java.awt.Dimension(16, 16));
        buttonCalendar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCalendarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(buttonCalendar, gridBagConstraints);

        buttonRemoveColumn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_delete12.png"))); // NOI18N
        buttonRemoveColumn.setToolTipText(JptBundle.INSTANCE.getString("SearchColumnPanel.buttonRemoveColumn.toolTipText")); // NOI18N
        buttonRemoveColumn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonRemoveColumn.setMaximumSize(new java.awt.Dimension(16, 16));
        buttonRemoveColumn.setMinimumSize(new java.awt.Dimension(16, 16));
        buttonRemoveColumn.setName("buttonRemoveColumn"); // NOI18N
        buttonRemoveColumn.setPreferredSize(new java.awt.Dimension(16, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(buttonRemoveColumn, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void toggleButtonBracketLeft1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleButtonBracketLeft1ActionPerformed
        handleTaggleButtonBracketLeftActionPerformed();
    }//GEN-LAST:event_toggleButtonBracketLeft1ActionPerformed

    private void toggleButtonBracketLeft2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleButtonBracketLeft2ActionPerformed
        handleTaggleButtonBracketLeft2ActionPerformed();
    }//GEN-LAST:event_toggleButtonBracketLeft2ActionPerformed

    private void buttonCalendarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCalendarActionPerformed
        handleButtonCalendarActionPerformed();
    }//GEN-LAST:event_buttonCalendarActionPerformed

    private void comboBoxOperatorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxOperatorsActionPerformed
        setChanged();
    }//GEN-LAST:event_comboBoxOperatorsActionPerformed

    private void comboBoxColumnsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxColumnsActionPerformed
        handleColumnChanged();
    }//GEN-LAST:event_comboBoxColumnsActionPerformed

    private void comboBoxComparatorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxComparatorsActionPerformed
        setChanged();
    }//GEN-LAST:event_comboBoxComparatorsActionPerformed

    private void toggleButtonBracketRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleButtonBracketRightActionPerformed
        setToggleButtonsTexts(toggleButtonBracketRight, false);
        setChanged();
    }//GEN-LAST:event_toggleButtonBracketRightActionPerformed

    private void textFieldValueKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldValueKeyTyped
        handleTextFieldValueKeyTyped(evt);
    }//GEN-LAST:event_textFieldValueKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCalendar;
    public javax.swing.JButton buttonRemoveColumn;
    private javax.swing.JComboBox comboBoxColumns;
    private javax.swing.JComboBox comboBoxComparators;
    private javax.swing.JComboBox comboBoxOperators;
    private javax.swing.JFormattedTextField textFieldValue;
    private javax.swing.JToggleButton toggleButtonBracketLeft1;
    private javax.swing.JToggleButton toggleButtonBracketLeft2;
    private javax.swing.JToggleButton toggleButtonBracketRight;
    // End of variables declaration//GEN-END:variables
}
