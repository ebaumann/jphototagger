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

import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.lib.thirdparty.DateChooserDialog;
import de.elmar_baumann.jpt.data.SavedSearchPanel;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.selections.ColumnIds;
import de.elmar_baumann.jpt.database.metadata.Comparator;
import de.elmar_baumann.jpt.database.metadata.FormatterFactory;
import de.elmar_baumann.jpt.database.metadata.Operator;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifDateTimeOriginal;
import de.elmar_baumann.jpt.database.metadata.selections.AdvancedSearchColumns;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.datatransfer.TransferHandlerDropEdit;
import de.elmar_baumann.jpt.event.SearchEvent;
import de.elmar_baumann.jpt.event.listener.SearchListener;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.renderer.ListCellRendererTableColumns;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

/**
 * Panel mit einer Suchspalte und deren möglichen Verknüpfungen, Operatoren
 * und Suchtext.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class SearchColumnPanel extends javax.swing.JPanel {

    private final String                       SEL_LEFT_BRACKET      = "<html><font size=\"+1\" color=\"#000000\"><b>(</b></font></html>";
    private final String                       NOT_SEL_LEFT_BRACKET  = "<html><font size=\"+1\" color=\"#dddddd\"><b>(</b></font></html>";
    private final String                       SEL_RIGHT_BRACKET     = "<html><font size=\"+1\" color=\"#000000\"><b>)</b></font></html>";
    private final String                       NOT_SEL_RIGHT_BRACKET = "<html><font size=\"+1\" color=\"#dddddd\"><b>)</b></font></html>";
    private final List<SearchListener>         searchListener        = new ArrayList<SearchListener>();
    private final ListCellRendererTableColumns columnRenderer        = new ListCellRendererTableColumns();
    private       boolean                      isOperatorsEnabled    = true;
    private       boolean                      listenToActions       = true;
    private       boolean                      isFirst;
    private       boolean                      changed;

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

    /**
     * Fügt einen Suchen-Beobachter hinzu.
     *
     * @param listener Beobachter
     */
    public synchronized void addSearchListener(SearchListener listener) {
        searchListener.add(listener);
    }

    /**
     * Setzt alle Werte auf den Ursprungszustand.
     */
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

    public void removeFirst() {

        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();

        setSize(panel1, toggleButtonBracketLeft1.getPreferredSize());
        setSize(panel2, comboBoxOperators.getPreferredSize());

        remove(comboBoxOperators);
        remove(toggleButtonBracketLeft1);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill  = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        add(panel1, gbc);

        gbc.gridx  = 1;
        gbc.insets = new Insets(0, 6, 0, 0);
        add(panel2, gbc);

        ComponentUtil.forceRepaint(this);
    }

    private void setSize(Component c, Dimension d) {
        c.setSize(d);
        c.setPreferredSize(d);
        c.setMinimumSize(d);
    }

    private void setFormatter() {
        textFieldValue.setFormatterFactory(
                FormatterFactory.getFormatterFactory(getColumn()));
    }

    private void setInputVerifier() {
        textFieldValue.setInputVerifier(getColumn().getInputVerifier());
    }

    private Column getColumn() {
        return (Column) comboBoxColumns.getSelectedItem();
    }

    private void checkKey(KeyEvent evt) {
        if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
            notifySearchListener(new SearchEvent(SearchEvent.Type.START));
        }
    }

    private void checkToggleButtons() {
        if (toggleButtonBracketLeft2.isSelected()) {
            toggleButtonBracketLeft1.setSelected(false);
        }
        if (toggleButtonBracketLeft1.isSelected()) {
            toggleButtonBracketLeft2.setSelected(false);
        }
        toggleButtonBracketLeft1.setEnabled(
                isOperatorsEnabled && !toggleButtonBracketLeft2.isSelected());
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

        tb.setText(
                left && sel
                ? SEL_LEFT_BRACKET
                : left && !sel
                ? NOT_SEL_LEFT_BRACKET
                : right && sel
                ? SEL_RIGHT_BRACKET
                : NOT_SEL_RIGHT_BRACKET
                );
    }

    private synchronized void notifySearchListener(SearchEvent evt) {
        for (SearchListener listener : searchListener) {
            listener.actionPerformed(evt);
        }
    }

    /**
     * Setzt, ob dieses Panel das erste ist. In diesem Fall wird die erste
     * Verknüpfung (AND, OR) nicht mit dem Statement geliefert.
     *
     * @param isFirst true, wenn das Panel das erste von mehreren ist.
     *                Default: false.
     */
    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    /**
     * Aktiviert die Combobox mit den Operatoren (AND, OR).
     *
     * @param enable true, wenn aktiv.
     *               Default: true.
     */
    public void setOperatorsEnabled(boolean enable) {
        isOperatorsEnabled = enable;
        toggleButtonBracketLeft1.setEnabled(enable);
        comboBoxOperators.setEnabled(enable);
    }

    private void setComboboxSelIndices() {
        listenToActions  = false;
        comboBoxOperators.setSelectedIndex(0);
        comboBoxColumns.setSelectedIndex(0);
        comboBoxComparators.setSelectedIndex(0);
        listenToActions = true;
    }

    public JTextField getTextFieldValue() {
        return textFieldValue;
    }

    /**
     * Liefert die Anzahl geöffneter Klammern.
     *
     * @return Anzahl
     */
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

    /**
     * Liefert die Anzahl gschlossener Klammern.
     *
     * @return Anzahl
     */
    public int getCountClosedBrackets() {
        int count = 0;
        if (toggleButtonBracketRight.isSelected()) {
            count++;
        }
        return count;
    }

    /**
     * Liefert, ob ein gültiger SQL-String geliefert werden kann.
     *
     * @return true, wenn ein gültiger SQL-String geliefert werden kann
     */
    public boolean hasSql() {
        return comboBoxOperators.getModel().getSelectedItem() != null &&
                comboBoxColumns.getModel().getSelectedItem() != null &&
                comboBoxComparators.getModel().getSelectedItem() != null &&
                !textFieldValue.getText().trim().isEmpty();
    }

    /**
     * Liefert einen SQL-String mit Parameter.
     *
     * @return SQL-String oder null, wenn keiner geliefert werden kann, da
     *         Auswahlen und Eingaben unvollständig sind
     * @see    #hasSql()
     */
    public String getSqlString() {
        if (hasSql()) {
            Operator   relation = (Operator) comboBoxOperators.getModel().getSelectedItem();
            Column     column   = (Column) comboBoxColumns.getModel().getSelectedItem();
            Comparator operator = (Comparator) comboBoxComparators.getModel().getSelectedItem();

            StringBuffer buffer = new StringBuffer();
            buffer.append(toggleButtonBracketLeft1.isSelected() ? " (" : "");
            if (!isFirst) {
                buffer.append(" " + relation.toSqlString());
            }
            buffer.append(toggleButtonBracketLeft2.isSelected() ? " (" : "");
            buffer.append(" " + column.getTable().getName() + "." + column.getName());
            buffer.append(" " + operator.toSqlString());
            buffer.append(" ?");
            buffer.append(toggleButtonBracketRight.isSelected() ? ")" : "");

            return buffer.toString();
        }
        return null;
    }

    /**
     * Liefert den Wert der Abfrage (zum Einsetzen in den SQL-String).
     *
     * @return Wert
     */
    public String getValue() {
        return textFieldValue.getText();
    }

    /**
     * Liefert die ausgewählte Spalte.
     *
     * @return Spalte oder null, wenn keine ausgewählt ist
     * @see    #isColumnSelected()
     */
    public Column getSelectedColumn() {
        Object item = comboBoxColumns.getModel().getSelectedItem();
        if (item != null && item instanceof Column) {
            return (Column) item;
        }
        return null;
    }

    /**
     * Liefert ob eine Spalte selektiert ist.
     *
     * @return true, wenn eine Spalte selektiert ist
     */
    public boolean isColumnSelected() {
        Object item = comboBoxColumns.getModel().getSelectedItem();
        return item != null && item instanceof Column;
    }

    /**
     * Liefert Daten für eine gespeicherte Suche.
     *
     * @return Daten für eine gespeicherte Suche
     */
    public SavedSearchPanel getSavedSearchData() {
        listenToActions = false;
        SavedSearchPanel savedSearchPanel = new SavedSearchPanel();
        savedSearchPanel.setBracketRightSelected(toggleButtonBracketRight.isSelected());
        savedSearchPanel.setColumnId(ColumnIds.getId((Column) comboBoxColumns.getModel().getSelectedItem()));
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

    /**
     * Setzt die Daten einer gespeicherten Suche.
     *
     * @param savedSearchPanel Paneldaten
     */
    public void setSavedSearchPanel(SavedSearchPanel savedSearchPanel) {
        listenToActions = false;
        toggleButtonBracketRight.setSelected(savedSearchPanel.isBracketRightSelected());
        comboBoxColumns         .getModel().setSelectedItem(ColumnIds.getColumn(savedSearchPanel.getColumnId()));
        comboBoxComparators     .getModel().setSelectedItem(Comparator.get(savedSearchPanel.getComparatorId()));
        toggleButtonBracketLeft1           .setSelected    (savedSearchPanel.isBracketLeft1Selected());
        toggleButtonBracketLeft2           .setSelected    (savedSearchPanel.isBracketLeft2Selected());
        comboBoxOperators       .getModel().setSelectedItem(Operator.get(savedSearchPanel.getOperatorId()));
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
        GregorianCalendar cal = DateChooserDialog.showDialog(this, AppLookAndFeel.getAppIcons());
        if (cal != null) {
            String year  = Integer.toString(cal.get(Calendar.YEAR));
            String month = getDateFormatted(Integer.toString(cal.get(Calendar.MONTH)));
            String day   = getDateFormatted(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));

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
            checkKey(evt);
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
        buttonCalendar.setEnabled(
                comboBoxColumns.getModel().getSelectedItem().equals(ColumnExifDateTimeOriginal.INSTANCE));
    }

    private void showInputHelpers() {
        if (listenToActions && getColumn().equals(ColumnXmpDcSubjectsSubject.INSTANCE)) {
            GUI.INSTANCE.getAppFrame().getMenuItemInputHelper().doClick();
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
        java.awt.GridBagConstraints gridBagConstraints;

        toggleButtonBracketLeft1 = new javax.swing.JToggleButton();
        comboBoxOperators = new javax.swing.JComboBox();
        toggleButtonBracketLeft2 = new javax.swing.JToggleButton();
        comboBoxColumns = new javax.swing.JComboBox();
        comboBoxComparators = new javax.swing.JComboBox();
        textFieldValue = new javax.swing.JFormattedTextField();
        textFieldValue.setTransferHandler(new TransferHandlerDropEdit());
        toggleButtonBracketRight = new javax.swing.JToggleButton();
        buttonCalendar = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        toggleButtonBracketLeft1.setForeground(new java.awt.Color(255, 0, 0));
        toggleButtonBracketLeft1.setText("(");
        toggleButtonBracketLeft1.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        toggleButtonBracketLeft1.setContentAreaFilled(false);
        toggleButtonBracketLeft1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        toggleButtonBracketLeft1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonBracketLeft1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        add(toggleButtonBracketLeft1, gridBagConstraints);

        comboBoxOperators.setModel(new DefaultComboBoxModel(Operator.values()));
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
        toggleButtonBracketLeft2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonBracketLeft2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(toggleButtonBracketLeft2, gridBagConstraints);

        comboBoxColumns.setModel(new DefaultComboBoxModel(AdvancedSearchColumns.get().toArray()));
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

        textFieldValue.setFocusLostBehavior(javax.swing.JFormattedTextField.PERSIST);
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
        toggleButtonBracketRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonBracketRightActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(toggleButtonBracketRight, gridBagConstraints);

        buttonCalendar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_calendar.png"))); // NOI18N
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
    private javax.swing.JComboBox comboBoxColumns;
    private javax.swing.JComboBox comboBoxComparators;
    private javax.swing.JComboBox comboBoxOperators;
    private javax.swing.JFormattedTextField textFieldValue;
    private javax.swing.JToggleButton toggleButtonBracketLeft1;
    private javax.swing.JToggleButton toggleButtonBracketLeft2;
    private javax.swing.JToggleButton toggleButtonBracketRight;
    // End of variables declaration//GEN-END:variables
}
