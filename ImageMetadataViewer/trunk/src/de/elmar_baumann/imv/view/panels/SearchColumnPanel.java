package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.lib.thirdparty.DateChooserDialog;
import de.elmar_baumann.imv.data.SavedSearchPanel;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.selections.ColumnIds;
import de.elmar_baumann.imv.database.metadata.Comparator;
import de.elmar_baumann.imv.database.metadata.MetadataUtil;
import de.elmar_baumann.imv.database.metadata.Operator;
import de.elmar_baumann.imv.database.metadata.selections.AdvancedSearchColumns;
import de.elmar_baumann.imv.event.SearchEvent;
import de.elmar_baumann.imv.event.listener.SearchListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.renderer.ListCellRendererTableColumns;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 * Panel mit einer Suchspalte und deren möglichen Verknüpfungen, Operatoren
 * und Suchtext.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class SearchColumnPanel extends javax.swing.JPanel {

    private final List<SearchListener> searchListener = new ArrayList<SearchListener>();
    private final ListCellRendererTableColumns columnRenderer = new ListCellRendererTableColumns();
    private DefaultComboBoxModel modelOperators;
    private DefaultComboBoxModel modelColumns;
    private DefaultComboBoxModel modelComparators;
    private boolean isFirst = false;
    private boolean isOperatorsEnabled = true;
    private boolean listenToActions = true;
    private boolean changed = false;

    public SearchColumnPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        initModels();
        setValueFormatter();
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
        textFieldValue.setText(""); // NOI18N
        setChanged(false);
        setValueFormatter();
        listenToActions = true;
    }

    private void setValueFormatter() {
        textFieldValue.setFormatterFactory(
            MetadataUtil.getFormatterFactory((Column) comboBoxColumns.getSelectedItem()));
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
        toggleButtonBracketLeft1.setEnabled(isOperatorsEnabled &&
            !toggleButtonBracketLeft2.isSelected());
        toggleButtonBracketLeft2.setEnabled(
            !toggleButtonBracketLeft1.isSelected());
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

    private void initModels() {
        listenToActions = false;
        modelOperators = new DefaultComboBoxModel();
        modelColumns = new DefaultComboBoxModel();
        modelComparators = new DefaultComboBoxModel();

        initOperatorsModel();
        initColumnsModel();
        initComparatorsModel();

        comboBoxOperators.setModel(modelOperators);
        comboBoxColumns.setModel(modelColumns);
        comboBoxComparators.setModel(modelComparators);

        comboBoxOperators.setSelectedIndex(0);
        comboBoxColumns.setSelectedIndex(0);
        comboBoxComparators.setSelectedIndex(0);
        listenToActions = true;
    }

    private void initOperatorsModel() {
        modelOperators.addElement(Operator.AND);
        modelOperators.addElement(Operator.OR);
    }

    private void initComparatorsModel() {
        modelComparators.addElement(Comparator.EQUALS);
        modelComparators.addElement(Comparator.LIKE);
        modelComparators.addElement(Comparator.NOT_EQUALS);
        modelComparators.addElement(Comparator.GREATER);
        modelComparators.addElement(Comparator.GREATER_EQUALS);
        modelComparators.addElement(Comparator.LOWER);
        modelComparators.addElement(Comparator.LOWER_EQUALS);
    }

    private void initColumnsModel() {
        List<Column> columns = AdvancedSearchColumns.get();
        for (Column column : columns) {
            if (!column.isPrimaryKey() && !column.isForeignKey()) {
                modelColumns.addElement(column);
            }
        }
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
        return modelComparators.getSelectedItem() != null &&
            modelColumns.getSelectedItem() != null &&
            modelOperators.getSelectedItem() != null &&
            !textFieldValue.getText().isEmpty();
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
            Operator relation = (Operator) modelOperators.getSelectedItem();
            Column column = (Column) modelColumns.getSelectedItem();
            Comparator operator = (Comparator) modelComparators.getSelectedItem();

            StringBuffer buffer = new StringBuffer();
            buffer.append(toggleButtonBracketLeft1.isSelected() ? " (" : ""); // NOI18N
            if (!isFirst) {
                buffer.append(" " + relation.toSqlString()); // NOI18N
            }
            buffer.append(toggleButtonBracketLeft2.isSelected() ? " (" : ""); // NOI18N
            buffer.append(" " + column.getTable().getName() + "." + column.getName()); // NOI18N
            buffer.append(" " + operator.toSqlString()); // NOI18N
            buffer.append(" ?"); // NOI18N
            buffer.append(toggleButtonBracketRight.isSelected() ? ")" : ""); // NOI18N

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
        Object item = modelColumns.getSelectedItem();
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
        Object item = modelColumns.getSelectedItem();
        return item != null && item instanceof Column;
    }

    /**
     * Liefert Daten für eine gespeicherte Suche.
     * 
     * @return Daten für eine gespeicherte Suche
     */
    public SavedSearchPanel getSavedSearchData() {
        listenToActions = false;
        SavedSearchPanel data = new SavedSearchPanel();
        data.setBracketRightSelected(toggleButtonBracketRight.isSelected());
        data.setColumnId(ColumnIds.getId(
            (Column) comboBoxColumns.getModel().getSelectedItem()));
        data.setComparatorId(
            ((Comparator) comboBoxComparators.getModel().getSelectedItem()).getId());
        data.setBracketLeft1Selected(toggleButtonBracketLeft1.isSelected());
        data.setBracketLeft2Selected(toggleButtonBracketLeft2.isSelected());
        data.setOperatorId(
            ((Operator) comboBoxOperators.getModel().getSelectedItem()).getId());
        String value = textFieldValue.getText();
        data.setValue(value.isEmpty() ? null : value);
        listenToActions = true;
        return data;
    }

    /**
     * Setzt die Daten einer gespeicherten Suche.
     * 
     * @param data Paneldaten
     */
    public void setSavedSearchData(SavedSearchPanel data) {
        listenToActions = false;
        toggleButtonBracketRight.setSelected(data.isBracketRightSelected());
        comboBoxColumns.getModel().setSelectedItem(
            ColumnIds.getColumn(data.getColumnId()));
        comboBoxComparators.getModel().setSelectedItem(
            Comparator.get(data.getComparatorId()));
        toggleButtonBracketLeft1.setSelected(data.isBracketLeft1Selected());
        toggleButtonBracketLeft2.setSelected(data.isBracketLeft2Selected());
        comboBoxOperators.getModel().setSelectedItem(
            Operator.get(data.getOperatorId()));
        if (data.hasValue()) {
            textFieldValue.setText(data.getValue());
        }
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
        GregorianCalendar cal = DateChooserDialog.showDialog(this, AppIcons.getAppIcons());
        if (cal != null) {
            String year = Integer.toString(cal.get(Calendar.YEAR));
            String month = getDateFormatted(Integer.toString(cal.get(Calendar.MONTH)));
            String day = getDateFormatted(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
            textFieldValue.setText(year + "-" + month + "-" + day); // NOI18N
            setChanged(true);
        }
    }

    private String getDateFormatted(String dayOrMonth) {
        if (dayOrMonth.length() < 2) {
            return "0" + dayOrMonth; // NOI18N
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
        if (listenToActions) {
            checkToggleButtons();
            setChanged(true);
        }
    }

    private void handleTaggleButtonBracketLeftActionPerformed() {
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
        setValueFormatter();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toggleButtonBracketLeft1 = new javax.swing.JToggleButton();
        comboBoxOperators = new javax.swing.JComboBox();
        toggleButtonBracketLeft2 = new javax.swing.JToggleButton();
        comboBoxColumns = new javax.swing.JComboBox();
        comboBoxComparators = new javax.swing.JComboBox();
        textFieldValue = new javax.swing.JFormattedTextField();
        toggleButtonBracketRight = new javax.swing.JToggleButton();
        buttonCalendar = new javax.swing.JButton();

        toggleButtonBracketLeft1.setForeground(new java.awt.Color(255, 0, 0));
        toggleButtonBracketLeft1.setText(Bundle.getString("SearchColumnPanel.toggleButtonBracketLeft1.text")); // NOI18N
        toggleButtonBracketLeft1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonBracketLeft1ActionPerformed(evt);
            }
        });

        comboBoxOperators.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboBoxOperators.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxOperatorsActionPerformed(evt);
            }
        });

        toggleButtonBracketLeft2.setForeground(new java.awt.Color(255, 0, 0));
        toggleButtonBracketLeft2.setText(Bundle.getString("SearchColumnPanel.toggleButtonBracketLeft2.text")); // NOI18N
        toggleButtonBracketLeft2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonBracketLeft2ActionPerformed(evt);
            }
        });

        comboBoxColumns.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboBoxColumns.setRenderer(columnRenderer);
        comboBoxColumns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxColumnsActionPerformed(evt);
            }
        });

        comboBoxComparators.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboBoxComparators.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxComparatorsActionPerformed(evt);
            }
        });

        textFieldValue.setFocusLostBehavior(javax.swing.JFormattedTextField.PERSIST);
        textFieldValue.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textFieldValueKeyTyped(evt);
            }
        });

        toggleButtonBracketRight.setForeground(new java.awt.Color(255, 0, 0));
        toggleButtonBracketRight.setText(Bundle.getString("SearchColumnPanel.toggleButtonBracketRight.text")); // NOI18N
        toggleButtonBracketRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonBracketRightActionPerformed(evt);
            }
        });

        buttonCalendar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_calendar.png"))); // NOI18N
        buttonCalendar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCalendarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(toggleButtonBracketLeft1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxOperators, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toggleButtonBracketLeft2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxColumns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxComparators, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldValue, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toggleButtonBracketRight, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(toggleButtonBracketLeft1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(comboBoxOperators, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(toggleButtonBracketLeft2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(comboBoxColumns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(comboBoxComparators, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(toggleButtonBracketRight, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(buttonCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(textFieldValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonCalendar, toggleButtonBracketRight});

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
