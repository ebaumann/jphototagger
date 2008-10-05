package de.elmar_baumann.imagemetadataviewer.view.dialogs;

import de.elmar_baumann.imagemetadataviewer.AppSettings;
import de.elmar_baumann.imagemetadataviewer.data.SavedSearch;
import de.elmar_baumann.imagemetadataviewer.data.SavedSearchPanel;
import de.elmar_baumann.imagemetadataviewer.data.SavedSearchParamStatement;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.DatabaseMetadataUtil;
import de.elmar_baumann.imagemetadataviewer.database.metadata.ParamStatement;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Table;
import de.elmar_baumann.imagemetadataviewer.database.metadata.exif.ColumnExifIdFiles;
import de.elmar_baumann.imagemetadataviewer.database.metadata.exif.TableExif;
import de.elmar_baumann.imagemetadataviewer.database.metadata.file.ColumnFilesFilename;
import de.elmar_baumann.imagemetadataviewer.database.metadata.file.ColumnFilesId;
import de.elmar_baumann.imagemetadataviewer.database.metadata.file.TableFiles;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpId;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpIdFiles;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.TableXmp;
import de.elmar_baumann.imagemetadataviewer.event.SearchEvent;
import de.elmar_baumann.imagemetadataviewer.event.SearchListener;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import de.elmar_baumann.imagemetadataviewer.view.panels.SearchColumnPanel;
import de.elmar_baumann.lib.persistence.PersistentAppSizes;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Nicht modaler Dialog für eine erweiterte Suche.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 */
public class AdvancedSearchDialog extends javax.swing.JDialog implements
    SearchListener {

    private ArrayList<SearchListener> searchListener = new ArrayList<SearchListener>();
    private ArrayList<SearchColumnPanel> searchColumnPanels = new ArrayList<SearchColumnPanel>();
    private String searchName = ""; // NOI18N
    private boolean isSavedSearch = false;
    private static AdvancedSearchDialog instance = new AdvancedSearchDialog(null, false);

    private AdvancedSearchDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        panelColumn1.setOperatorsEnabled(false);
        initSearchColumnPanelArray();
        listenToSearchPanels();
        setIconImages(AppSettings.getAppIcons());
    }

    private void beforeWindowClosing() {
        checkChanged();
        isSavedSearch = false;
        setSearchName(""); // NOI18N
        PersistentAppSizes.setSizeAndLocation(this);
        isSavedSearch = false;
    }

    /**
     * Liefert die einige Klasseninstanz.
     * 
     * @return Klasseninstanz
     */
    public static AdvancedSearchDialog getInstance() {
        return instance;
    }

    /**
     * Fügt einen Beobachter hinzu.
     * 
     * @param listener Beobachter
     */
    public void addSearchListener(SearchListener listener) {
        searchListener.add(listener);
    }

    /**
     * Entfernt einen Beobachter.
     * 
     * @param listener Beobachter
     */
    public void removeSearchListener(SearchListener listener) {
        searchListener.remove(listener);
    }

    private boolean checkIsSearchValid() {
        boolean canSearch = false;
        int count = searchColumnPanels.size();
        int index = 0;
        while (!canSearch && index < count) {
            canSearch = !searchColumnPanels.get(index++).getValue().isEmpty();
        }
        canSearch = canSearch && checkBrackets();
        if (!canSearch) {
            JOptionPane.showMessageDialog(this,
                Bundle.getString("AdvancedSearchDialog.ErrorMessage.InvalidQuery"),
                Bundle.getString("AdvancedSearchDialog.ErrorMessage.InvalidQuery.Title"),
                JOptionPane.ERROR_MESSAGE,
                AppSettings.getSmallAppIcon());
        }
        return canSearch;
    }

    private boolean checkBrackets() {
        int countOpenBrackets = 0;
        int countClosedBrackets = 0;
        for (SearchColumnPanel panel : searchColumnPanels) {
            countOpenBrackets += panel.getCountOpenBrackets();
            countClosedBrackets += panel.getCountClosedBrackets();
        }
        return countOpenBrackets == countClosedBrackets;
    }

    private void initSearchColumnPanelArray() {
        searchColumnPanels.add(panelColumn1);
        searchColumnPanels.add(panelColumn2);
        searchColumnPanels.add(panelColumn3);
        searchColumnPanels.add(panelColumn4);
        searchColumnPanels.add(panelColumn5);
    }

    private void listenToSearchPanels() {
        for (SearchColumnPanel panel : searchColumnPanels) {
            panel.addSearchListener(this);
        }
    }

    private SavedSearchParamStatement getParamStatementData() {
        SavedSearchParamStatement data = new SavedSearchParamStatement();
        ParamStatement stmt = getSql();
        data.setQuery(stmt.isQuery());
        data.setSql(stmt.getSql());
        ArrayList<String> values = stmt.getValuesAsStringArray();
        data.setValues(values.size() > 0 ? values : null);
        return data;
    }

    private void notifySearch() {
        SearchEvent event = new SearchEvent(SearchEvent.Type.Start);
        SavedSearch data = new SavedSearch();
        data.setParamStatements(getParamStatementData());
        event.setData(data);
        for (SearchListener listener : searchListener) {
            listener.actionPerformed(event);
        }
    }

    private void notifySave(SavedSearch search) {
        SearchEvent event = new SearchEvent(SearchEvent.Type.Save);
        event.setData(search);
        event.setForceOverwrite(isSavedSearch);
        for (SearchListener listener : searchListener) {
            listener.actionPerformed(event);
        }
    }

    /**
     * Setzt eine gespeicherte Suche.
     * 
     * @param search Gespeicherte Suche
     */
    public void setSavedSearch(SavedSearch search) {
        emptyPanels();
        isSavedSearch = true;
        setSearchName(search.getName());
        setSavedSearchToPanels(search.getPanels());
    }

    private void setSavedSearchToPanels(ArrayList<SavedSearchPanel> data) {
        if (data != null) {
            int panelSize = searchColumnPanels.size();
            int dataSize = data.size();
            for (int dataIndex = 0; dataIndex < dataSize; dataIndex++) {
                if (dataIndex < panelSize) {
                    searchColumnPanels.get(dataIndex).setSavedSearchData(
                        data.get(dataIndex));
                }
            }
        }
    }

    private void emptyPanels() {
        checkChanged();
        for (SearchColumnPanel panel : searchColumnPanels) {
            panel.reset();
        }
    }

    private void checkChanged() {
        if (isSavedSearch && isChanged()) {
            if (JOptionPane.showConfirmDialog(
                this,
                Bundle.getString("AdvancedSearchDialog.ConfirmMessage.SaveChanges"),
                Bundle.getString("AdvancedSearchDialog.ConfirmMessage.SaveChanges.Title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                AppSettings.getSmallAppIcon()) ==
                JOptionPane.YES_OPTION) {
                saveSearch();
            }
        }
    }

    private boolean isChanged() {
        for (SearchColumnPanel panel : searchColumnPanels) {
            if (panel.isChanged()) {
                return true;
            }
        }
        return false;
    }

    private void saveAs() {
        if (isSavedSearch) {
            isSavedSearch = false;
            saveSearch();
            isSavedSearch = true;
        } else {
            saveSearch();
        }
    }

    private boolean saveSearch() {
        if (checkIsSearchValid()) {
            String name = askSearchName();
            if (name != null && !name.isEmpty()) {
                setSearchName(name);
                setPanelsUnchanged();
                notifySave(getSavedSearch(name));
                return true;
            }
        }
        return false;
    }

    private String askSearchName() {
        if (isSavedSearch) {
            return searchName;
        } else {
            return JOptionPane.showInputDialog(
                Bundle.getString("AdvancedSearchDialog.Input.SearchName"),
                searchName);
        }
    }

    private void setPanelsUnchanged() {
        for (SearchColumnPanel panel : searchColumnPanels) {
            panel.setChanged(false);
        }
    }

    private SavedSearch getSavedSearch(String name) {
        SavedSearch search = new SavedSearch();
        SavedSearchParamStatement paramStatementData = getParamStatementData();
        paramStatementData.setName(name);
        search.setParamStatements(paramStatementData);
        search.setPanels(getPanelData());
        return search;
    }

    private ArrayList<SavedSearchPanel> getPanelData() {
        ArrayList<SavedSearchPanel> panelData = new ArrayList<SavedSearchPanel>();
        int size = searchColumnPanels.size();
        for (int index = 0; index < size; index++) {
            SearchColumnPanel panel = searchColumnPanels.get(index);
            SavedSearchPanel data = panel.getSavedSearchData();
            data.setPanelIndex(index);
            panelData.add(data);
        }
        return panelData.size() > 0 ? panelData : null;
    }

    private void search() {
        if (checkIsSearchValid()) {
            notifySearch();
        }
    }

    private void setSearchName(String name) {
        searchName = name;
        String separator = name.isEmpty() ? "" : ": "; // NOI18N
        setTitle(Bundle.getString("AdvancedSearchDialog.TitlePrefix") + separator + name);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            PersistentAppSizes.getSizeAndLocation(this);
        }
        super.setVisible(visible);
    }

    private ParamStatement getSql() {
        StringBuffer statement = getStartSelectFrom();
        ArrayList<String> values = new ArrayList<String>();
        ParamStatement stmt = new ParamStatement();

        appendFrom(statement);
        appendWhere(statement, values);
        stmt.setSql(statement.toString());
        stmt.setValues(values.toArray());
        stmt.setIsQuery(true);

        return stmt;
    }

    private StringBuffer getStartSelectFrom() {
        Column columnFilename = ColumnFilesFilename.getInstance();
        String columnNameFilename = columnFilename.getName();
        String tableNameFiles = columnFilename.getTable().getName();

        return new StringBuffer("SELECT DISTINCT " + tableNameFiles + "." + // NOI18N
            columnNameFilename + " FROM"); // NOI18N
    }

    private ArrayList<Column> getColumns() {
        ArrayList<Column> columns = new ArrayList<Column>();
        for (SearchColumnPanel panel : searchColumnPanels) {
            if (panel.hasSql()) {
                columns.add(panel.getSelectedColumn());
            }
        }
        return columns;
    }

    private void appendWhere(StringBuffer statement, ArrayList<String> values) {
        statement.append(" WHERE"); // NOI18N
        int index = 0;
        for (SearchColumnPanel panel : searchColumnPanels) {
            if (panel.hasSql()) {
                panel.setIsFirst(index++ == 0);
                statement.append(panel.getSqlString());
                values.add(panel.getValue());
            }
        }
    }

    private void appendFrom(StringBuffer statement) {
        ArrayList<Table> allTables =
            DatabaseMetadataUtil.getUniqueTablesOfColumnArray(getColumns());
        Column.ReferenceDirection back = Column.ReferenceDirection.backwards;
        ArrayList<Table> refsXmpTables = DatabaseMetadataUtil.getTablesWithReferenceTo(allTables, TableXmp.getInstance(), back);

        statement.append(" " + TableFiles.getInstance().getName()); // NOI18N

        if (allTables.contains(TableExif.getInstance())) {
            statement.append(getJoinFiles(TableExif.getInstance(),
                ColumnExifIdFiles.getInstance()));
        }

        if (allTables.contains(TableXmp.getInstance()) ||
            !refsXmpTables.isEmpty()) {
            statement.append(getJoinFiles(TableXmp.getInstance(),
                ColumnXmpIdFiles.getInstance()));
        }

        String xmpJoinCol =
            TableXmp.getInstance().getName() + "." + // NOI18N
            ColumnXmpId.getInstance().getName();
        appendInnerJoin(statement, refsXmpTables, TableXmp.getInstance(),
            xmpJoinCol);
    }

    private String getJoinFiles(Table joinTable, Column joinColumn) {
        return " INNER JOIN " + joinTable.getName() + " ON " + // NOI18N
            joinTable.getName() + "." + joinColumn.getName() + // NOI18N
            " = " + TableFiles.getInstance().getName() + // NOI18N
            "." + ColumnFilesId.getInstance().getName(); // NOI18N
    }

    private void appendInnerJoin(StringBuffer statement,
        ArrayList<Table> refsTables, Table referredTable, String joinCol) {
        for (Table refsTable : refsTables) {
            Column refColumn = refsTable.getJoinColumnsFor(referredTable).get(0);
            statement.append(" INNER JOIN " + refsTable.getName() + " ON " + // NOI18N
                refsTable.getName() + "." + refColumn.getName() + " = " + // NOI18N
                joinCol);
        }
    }

    @Override
    public void actionPerformed(SearchEvent evt) {
        if (evt.getType().equals(SearchEvent.Type.Start)) {
            search();
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

        panelScrollPane = new javax.swing.JPanel();
        scrollPaneColumns = new javax.swing.JScrollPane();
        panelColumns = new javax.swing.JPanel();
        panelColumn1 = new de.elmar_baumann.imagemetadataviewer.view.panels.SearchColumnPanel();
        panelColumn2 = new de.elmar_baumann.imagemetadataviewer.view.panels.SearchColumnPanel();
        panelColumn3 = new de.elmar_baumann.imagemetadataviewer.view.panels.SearchColumnPanel();
        panelColumn4 = new de.elmar_baumann.imagemetadataviewer.view.panels.SearchColumnPanel();
        panelColumn5 = new de.elmar_baumann.imagemetadataviewer.view.panels.SearchColumnPanel();
        panelButtons = new javax.swing.JPanel();
        buttonSaveSearch = new javax.swing.JButton();
        buttonSaveAs = new javax.swing.JButton();
        buttonReset = new javax.swing.JButton();
        buttonSearch = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString("AdvancedSearchDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        javax.swing.GroupLayout panelColumnsLayout = new javax.swing.GroupLayout(panelColumns);
        panelColumns.setLayout(panelColumnsLayout);
        panelColumnsLayout.setHorizontalGroup(
            panelColumnsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelColumn5, javax.swing.GroupLayout.DEFAULT_SIZE, 905, Short.MAX_VALUE)
            .addComponent(panelColumn4, javax.swing.GroupLayout.DEFAULT_SIZE, 905, Short.MAX_VALUE)
            .addComponent(panelColumn3, javax.swing.GroupLayout.DEFAULT_SIZE, 905, Short.MAX_VALUE)
            .addComponent(panelColumn2, javax.swing.GroupLayout.DEFAULT_SIZE, 905, Short.MAX_VALUE)
            .addComponent(panelColumn1, javax.swing.GroupLayout.DEFAULT_SIZE, 905, Short.MAX_VALUE)
        );
        panelColumnsLayout.setVerticalGroup(
            panelColumnsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelColumnsLayout.createSequentialGroup()
                .addComponent(panelColumn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelColumn2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelColumn3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelColumn4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelColumn5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        scrollPaneColumns.setViewportView(panelColumns);

        javax.swing.GroupLayout panelScrollPaneLayout = new javax.swing.GroupLayout(panelScrollPane);
        panelScrollPane.setLayout(panelScrollPaneLayout);
        panelScrollPaneLayout.setHorizontalGroup(
            panelScrollPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 932, Short.MAX_VALUE)
            .addGroup(panelScrollPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelScrollPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPaneColumns, javax.swing.GroupLayout.DEFAULT_SIZE, 908, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        panelScrollPaneLayout.setVerticalGroup(
            panelScrollPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 185, Short.MAX_VALUE)
            .addGroup(panelScrollPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelScrollPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPaneColumns, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout panelButtonsLayout = new javax.swing.GroupLayout(panelButtons);
        panelButtons.setLayout(panelButtonsLayout);
        panelButtonsLayout.setHorizontalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 420, Short.MAX_VALUE)
        );
        panelButtonsLayout.setVerticalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        buttonSaveSearch.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonSaveSearch.setMnemonic('p');
        buttonSaveSearch.setText(Bundle.getString("AdvancedSearchDialog.buttonSaveSearch.text")); // NOI18N
        buttonSaveSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveSearchActionPerformed(evt);
            }
        });

        buttonSaveAs.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonSaveAs.setText(Bundle.getString("AdvancedSearchDialog.buttonSaveAs.text")); // NOI18N
        buttonSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveAsActionPerformed(evt);
            }
        });

        buttonReset.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonReset.setMnemonic('e');
        buttonReset.setText(Bundle.getString("AdvancedSearchDialog.buttonReset.text")); // NOI18N
        buttonReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonResetActionPerformed(evt);
            }
        });

        buttonSearch.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonSearch.setMnemonic('s');
        buttonSearch.setText(Bundle.getString("AdvancedSearchDialog.buttonSearch.text")); // NOI18N
        buttonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addComponent(panelButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonSaveSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonSaveAs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonReset)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonSearch)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttonReset)
                        .addComponent(buttonSaveAs)
                        .addComponent(buttonSaveSearch)
                        .addComponent(buttonSearch)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void buttonSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSearchActionPerformed
    search();
}//GEN-LAST:event_buttonSearchActionPerformed

private void buttonSaveSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveSearchActionPerformed
    saveSearch();
}//GEN-LAST:event_buttonSaveSearchActionPerformed

private void buttonResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonResetActionPerformed
    emptyPanels();
}//GEN-LAST:event_buttonResetActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    beforeWindowClosing();
}//GEN-LAST:event_formWindowClosing

private void buttonSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveAsActionPerformed
    saveAs();
}//GEN-LAST:event_buttonSaveAsActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                AdvancedSearchDialog dialog = new AdvancedSearchDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonReset;
    private javax.swing.JButton buttonSaveAs;
    private javax.swing.JButton buttonSaveSearch;
    private javax.swing.JButton buttonSearch;
    private javax.swing.JPanel panelButtons;
    private de.elmar_baumann.imagemetadataviewer.view.panels.SearchColumnPanel panelColumn1;
    private de.elmar_baumann.imagemetadataviewer.view.panels.SearchColumnPanel panelColumn2;
    private de.elmar_baumann.imagemetadataviewer.view.panels.SearchColumnPanel panelColumn3;
    private de.elmar_baumann.imagemetadataviewer.view.panels.SearchColumnPanel panelColumn4;
    private de.elmar_baumann.imagemetadataviewer.view.panels.SearchColumnPanel panelColumn5;
    private javax.swing.JPanel panelColumns;
    private javax.swing.JPanel panelScrollPane;
    private javax.swing.JScrollPane scrollPaneColumns;
    // End of variables declaration//GEN-END:variables

}
