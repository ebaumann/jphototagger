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

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.SavedSearch;
import de.elmar_baumann.jpt.data.SavedSearchPanel;
import de.elmar_baumann.jpt.data.SavedSearchParamStatement;
import de.elmar_baumann.jpt.database.Util;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.DatabaseMetadataUtil;
import de.elmar_baumann.jpt.database.metadata.ParamStatement;
import de.elmar_baumann.jpt.database.metadata.Table;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifIdFiles;
import de.elmar_baumann.jpt.database.metadata.exif.TableExif;
import de.elmar_baumann.jpt.database.metadata.file.ColumnFilesFilename;
import de.elmar_baumann.jpt.database.metadata.file.ColumnFilesId;
import de.elmar_baumann.jpt.database.metadata.file.TableFiles;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpId;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIdFiles;
import de.elmar_baumann.jpt.database.metadata.xmp.TableXmp;
import de.elmar_baumann.jpt.event.listener.impl.ListenerProvider;
import de.elmar_baumann.jpt.event.SearchEvent;
import de.elmar_baumann.jpt.event.listener.SearchListener;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.types.Persistence;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 */
public final class AdvancedSearchPanel extends javax.swing.JPanel
        implements SearchListener, Persistence {

    private static final int              MIN_COLUMN_COUNT        = 5;
    private static final String           SQL_IDENTIFIER_KEYWORDS = "xmp_dc_subjects.subject IN";
    private static final String           KEY_SELECTED_TAB_INDEX  = "AdvancedSearchPanel.SelectedTabIndex";
    private final List<SearchColumnPanel> searchColumnPanels      = new LinkedList<SearchColumnPanel>();
    private List<SearchListener>          searchListeners         = new ArrayList<SearchListener>();
    private String                        searchName              = ""; // NOI18N
    private boolean                       isSavedSearch           = false;
    private ListenerProvider              listenerProvider;

    public AdvancedSearchPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        listenerProvider = ListenerProvider.INSTANCE;
        searchListeners  = listenerProvider.getSearchListeners();

        panelColumn1.setOperatorsEnabled(false);
        initSearchColumnPanelArray();
        listenToSearchPanels();
        panelKeywordsInput.setAutocomplete();
    }

    public void willDispose() {
        checkChanged();
        isSavedSearch = false;
        setSearchName(""); // NOI18N
    }

    @Override
    public void readProperties() {
        if (UserSettings.INSTANCE.getProperties().containsKey(KEY_SELECTED_TAB_INDEX)) {
            tabbedPane.setSelectedIndex(
                    UserSettings.INSTANCE.getSettings().getInt(KEY_SELECTED_TAB_INDEX));
        }
    }

    @Override
    public void writeProperties() {
        UserSettings.INSTANCE.getSettings().setInt(tabbedPane.getSelectedIndex(), KEY_SELECTED_TAB_INDEX);
    }

    private boolean checkIsSearchValid() {
        boolean valid = existsKeywords();
        int     count = searchColumnPanels.size();
        int     index = 0;

        while (!valid && index < count) {
            valid = !searchColumnPanels.get(index++).getValue().isEmpty();
        }

        valid = valid && checkBrackets();

        if (!valid) {
            MessageDisplayer.error(this, "AdvancedSearchDialog.Error.InvalidQuery"); // NOI18N
        }

        return valid;
    }

    private boolean checkBrackets() {
        int countOpenBrackets   = 0;
        int countClosedBrackets = 0;

        for (SearchColumnPanel panel : searchColumnPanels) {
            countOpenBrackets   += panel.getCountOpenBrackets();
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
        assert searchColumnPanels.size() == MIN_COLUMN_COUNT : searchColumnPanels.size();
    }

    private void listenToSearchPanels() {
        for (SearchColumnPanel panel : searchColumnPanels) {
            panel.addSearchListener(this);
        }
    }

    private SavedSearchParamStatement getParamStatementData() {
        SavedSearchParamStatement paramStmt = new SavedSearchParamStatement();
        ParamStatement            stmt      = getSql();

        paramStmt.setQuery(stmt.isQuery());
        paramStmt.setSql(stmt.getSql());

        List<String> values = stmt.getValuesAsStringList();
        paramStmt.setValues(values.size() > 0
                            ? values
                            : null);
        return paramStmt;
    }

    private synchronized void notifySearch() {
        SearchEvent event       = new SearchEvent(SearchEvent.Type.START);
        SavedSearch savedSearch = new SavedSearch();

        savedSearch.setParamStatement(getParamStatementData());
        event.setData(savedSearch);
        for (SearchListener listener : searchListeners) {
            listener.actionPerformed(event);
        }
    }

    private synchronized void notifySave(SavedSearch savedSearch) {
        SearchEvent event = new SearchEvent(SearchEvent.Type.SAVE);

        event.setData(savedSearch);
        event.setForceOverwrite(isSavedSearch);

        for (SearchListener listener : searchListeners) {
            listener.actionPerformed(event);
        }
    }

    private synchronized void notifyNameChanged() {
        SearchEvent event = new SearchEvent(SearchEvent.Type.NAME_CHANGED);

        event.setSearchName(searchName);
        for (SearchListener listener : searchListeners) {
            listener.actionPerformed(event);
        }
    }

    /**
     * Setzt eine gespeicherte Suche.
     * 
     * @param search Gespeicherte Suche
     */
    public void setSavedSearch(SavedSearch search) {
        resetColumns();
        isSavedSearch = true;
        setSearchName(search.getName());
        setSavedSearchToPanels(search.getPanels());
        setKeywordsToPanel(search);
    }

    private void setSavedSearchToPanels(List<SavedSearchPanel> savedSearchPanels) {
        if (savedSearchPanels != null) {
            int dataSize = savedSearchPanels.size();
            ensureColumnCount(dataSize);
            int panelSize = searchColumnPanels.size();
            for (int dataIndex = 0; dataIndex < dataSize; dataIndex++) {
                if (dataIndex < panelSize) {
                    searchColumnPanels.get(dataIndex).setSavedSearchData(
                            savedSearchPanels.get(dataIndex));
                }
            }
        }
    }

    private void setKeywordsToPanel(SavedSearch search) {
        String  sql         = search.getParamStatement().getSql();
        boolean hasKeywords = sql.contains(SQL_IDENTIFIER_KEYWORDS);

        if (!hasKeywords) return;

        List<String> values       = search.getParamStatement().getValues();
        List<String> keywords     = new ArrayList<String>();
        int          keywordCount = getKeywordCount(sql);
        int          valueCount   = values.size();

        for (int i = 0; i < keywordCount && valueCount - i - 1 >= 0; i++) {
            keywords.add(values.get(valueCount - i - 1));
        }

        panelKeywordsInput.setText(keywords);
    }

    private int getKeywordCount(String sql) {
        int    index  = sql.indexOf(SQL_IDENTIFIER_KEYWORDS);
        String params = sql.substring(index);
        int    count  = 0;
        int    findIndex = params.indexOf('?', 0);

        while (findIndex > 0) {
            count++;
            findIndex = params.indexOf('?', findIndex + 1);
        }

        return count;
    }
    
    private void ensureColumnCount(int count) {
        int currentCount = searchColumnPanels.size();

        if (currentCount >= count) return;

        for (int i = currentCount; i < count; i++) {
            addColumn();
        }
    }

    private void addColumn() {
        GridBagConstraints gbc   = getColumnGridBagConstraints();
        SearchColumnPanel  panel = new SearchColumnPanel();

        searchColumnPanels.add(panel);
        panelColumns.add(panel, gbc);
        buttonRemoveColumn.setEnabled(true);
        ComponentUtil.forceRepaint(this);
    }

    private GridBagConstraints getColumnGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx   = 0;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        return gbc;
    }

    private void removeColumn() {
        int colCount = searchColumnPanels.size();
        assert colCount > MIN_COLUMN_COUNT;
        if (colCount > MIN_COLUMN_COUNT) {
            SearchColumnPanel panel = searchColumnPanels.remove(colCount - 1);
            panelColumns.remove(panel);
            buttonRemoveColumn.setEnabled(colCount - 1 > MIN_COLUMN_COUNT);
            ComponentUtil.forceRepaint(this);
        }
    }

    private void resetColumns() {
        checkChanged();
        for (SearchColumnPanel panel : searchColumnPanels) {
            panel.reset();
        }
    }

    private void checkChanged() {
        if (isSavedSearch && isChanged()) {
            if (MessageDisplayer.confirm(
                    this,
                    "AdvancedSearchDialog.Confirm.SaveChanges", // NOI18N
                    MessageDisplayer.CancelButton.HIDE).equals(
                    MessageDisplayer.ConfirmAction.YES)) {
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
            return JOptionPane.showInputDialog(this,
                    Bundle.getString("AdvancedSearchDialog.Input.SearchName"), // NOI18N
                    searchName);
        }
    }

    private void setPanelsUnchanged() {
        for (SearchColumnPanel panel : searchColumnPanels) {
            panel.setChanged(false);
        }
    }

    private SavedSearch getSavedSearch(String name) {
        SavedSearch               savedSearch        = new SavedSearch();
        SavedSearchParamStatement paramStatementData = getParamStatementData();

        paramStatementData.setName(name);
        savedSearch.setParamStatement(paramStatementData);
        savedSearch.setPanels(getSavedSearchPanels());

        return savedSearch;
    }

    private List<SavedSearchPanel> getSavedSearchPanels() {
        List<SavedSearchPanel> savedSearchPanels = new ArrayList<SavedSearchPanel>();
        int                    size              = searchColumnPanels.size();

        for (int index = 0; index < size; index++) {
            SearchColumnPanel panel            = searchColumnPanels.get(index);
            SavedSearchPanel  savedSearchPanel = panel.getSavedSearchData();

            savedSearchPanel.setPanelIndex(index);
            savedSearchPanels.add(savedSearchPanel);
        }

        return savedSearchPanels.size() > 0
               ? savedSearchPanels
               : null;
    }

    private void search() {
        if (checkIsSearchValid()) {
            notifySearch();
        }
    }

    private void setSearchName(String name) {
        searchName = name;
        notifyNameChanged();
    }

    private ParamStatement getSql() {
        StringBuffer  statement = getStartSelectFrom();
        List<String>  values    = new ArrayList<String>();
        ParamStatement stmt     = new ParamStatement();

        appendFrom(statement);
        appendWhere(statement, values);

        stmt.setSql(statement.toString());
        stmt.setValues(values.toArray());
        stmt.setIsQuery(true);

        return stmt;
    }

    private StringBuffer getStartSelectFrom() {
        Column columnFilename     = ColumnFilesFilename.INSTANCE;
        String columnNameFilename = columnFilename.getName();
        String tableNameFiles     = columnFilename.getTable().getName();

        return new StringBuffer(
                "SELECT DISTINCT " +
                tableNameFiles + "." + columnNameFilename +
                " FROM"); // NOI18N
    }

    private List<Column> getColumns() {
        List<Column> columns = new ArrayList<Column>();
        for (SearchColumnPanel panel : searchColumnPanels) {
            if (panel.hasSql()) {
                columns.add(panel.getSelectedColumn());
            }
        }
        if (existsKeywords()) {
            columns.add(ColumnXmpDcSubjectsSubject.INSTANCE);
        }
        return columns;
    }

    private boolean existsKeywords() {
        return !getKeywords().isEmpty();
    }

    private List<String> getKeywords() {
        String             textFieldText = panelKeywordsInput.getText();
        Collection<String> listText      = panelKeywordsInput.getRepeatableText();
        List<String>       keywords      = new ArrayList<String>(listText);

        if (!textFieldText.isEmpty()) {
            keywords.add(textFieldText);
        }

        return keywords;
    }

    private void appendWhere(StringBuffer statement, List<String> values) {
        statement.append(" WHERE"); // NOI18N
        int index = 0;
        for (SearchColumnPanel panel : searchColumnPanels) {
            if (panel.hasSql()) {
                panel.setIsFirst(index++ == 0);
                statement.append(panel.getSqlString());
                values.add(panel.getValue());
            }
        }
        appendKeywords(statement, values, index > 0);
    }

    private void appendKeywords(StringBuffer statement, List<String> values, boolean and) {
        List<String> keywords = getKeywords();
        int          count    = keywords.size();

        if (count == 0) return;

        statement.append((and ? " AND" : "") + // NOI18N
                " xmp_dc_subjects.subject IN " + // NOI18N
                Util.getParamsInParentheses(count) +
                " GROUP BY files.filename" + // NOI18N
                " HAVING COUNT(*) = " + Integer.toString(count)); // NOI18N
        values.addAll(keywords);
    }

    private void appendFrom(StringBuffer statement) {
        List<Table>               allTables     = DatabaseMetadataUtil.getUniqueTablesOfColumnArray(getColumns());
        Column.ReferenceDirection back          = Column.ReferenceDirection.BACKWARDS;
        List<Table>               refsXmpTables = DatabaseMetadataUtil.getTablesWithReferenceTo(allTables, TableXmp.INSTANCE, back);

        statement.append(" " + TableFiles.INSTANCE.getName()); // NOI18N

        if (allTables.contains(TableExif.INSTANCE)) {
            statement.append(getJoinFiles(TableExif.INSTANCE, ColumnExifIdFiles.INSTANCE));
        }

        if (allTables.contains(TableXmp.INSTANCE) || !refsXmpTables.isEmpty()) {
            statement.append(getJoinFiles(TableXmp.INSTANCE, ColumnXmpIdFiles.INSTANCE));
        }

        String xmpJoinCol = TableXmp.INSTANCE.getName() + "." + ColumnXmpId.INSTANCE.getName(); // NOI18N

        appendInnerJoin(statement, refsXmpTables, TableXmp.INSTANCE, xmpJoinCol);
    }

    private String getJoinFiles(Table joinTable, Column joinColumn) {
        return " INNER JOIN " + 
                joinTable.getName() +
                " ON " + // NOI18N
                joinTable.getName() + "." + joinColumn.getName() + // NOI18N
                " = " +
                TableFiles.INSTANCE.getName() + "." + ColumnFilesId.INSTANCE.getName(); // NOI18N
    }

    private void appendInnerJoin(
            StringBuffer statement,
            List<Table>  refsTables,
            Table        referredTable,
            String       joinCol) {

        for (Table refsTable : refsTables) {
            Column refColumn = refsTable.getJoinColumnsFor(referredTable).get(0);

            statement.append(" INNER JOIN " + refsTable.getName() + " ON " + // NOI18N
                    refsTable.getName() + "." + refColumn.getName() + " = " + // NOI18N
                    joinCol);
        }
    }

    @Override
    public void actionPerformed(SearchEvent evt) {
        if (evt.getType().equals(SearchEvent.Type.START)) {
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
        java.awt.GridBagConstraints gridBagConstraints;

        tabbedPane = new javax.swing.JTabbedPane();
        panelSimpleSql = new javax.swing.JPanel();
        scrollPaneColumns = new javax.swing.JScrollPane();
        panelColumns = new javax.swing.JPanel();
        panelColumn1 = new de.elmar_baumann.jpt.view.panels.SearchColumnPanel();
        panelColumn2 = new de.elmar_baumann.jpt.view.panels.SearchColumnPanel();
        panelColumn3 = new de.elmar_baumann.jpt.view.panels.SearchColumnPanel();
        panelColumn4 = new de.elmar_baumann.jpt.view.panels.SearchColumnPanel();
        panelColumn5 = new de.elmar_baumann.jpt.view.panels.SearchColumnPanel();
        panelKeywords = new javax.swing.JPanel();
        labelInfoKeywords = new javax.swing.JLabel();
        panelKeywordsInput = new de.elmar_baumann.jpt.view.panels.EditRepeatableTextEntryPanel();
        panelKeywordsInput.setPrompt("");
        labelInfoDelete = new javax.swing.JLabel();
        panelButtons = new javax.swing.JPanel();
        buttonSaveSearch = new javax.swing.JButton();
        buttonSaveAs = new javax.swing.JButton();
        buttonRemoveColumn = new javax.swing.JButton();
        buttonAddColumn = new javax.swing.JButton();
        buttonResetColumns = new javax.swing.JButton();
        buttonSearch = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        scrollPaneColumns.setBorder(null);

        panelColumns.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelColumns.add(panelColumn1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelColumns.add(panelColumn2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelColumns.add(panelColumn3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelColumns.add(panelColumn4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelColumns.add(panelColumn5, gridBagConstraints);

        scrollPaneColumns.setViewportView(panelColumns);

        javax.swing.GroupLayout panelSimpleSqlLayout = new javax.swing.GroupLayout(panelSimpleSql);
        panelSimpleSql.setLayout(panelSimpleSqlLayout);
        panelSimpleSqlLayout.setHorizontalGroup(
            panelSimpleSqlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSimpleSqlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneColumns, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelSimpleSqlLayout.setVerticalGroup(
            panelSimpleSqlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSimpleSqlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneColumns, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                .addContainerGap())
        );

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/jpt/resource/properties/Bundle"); // NOI18N
        tabbedPane.addTab(bundle.getString("AdvancedSearchPanel.panelSimpleSql.TabConstraints.tabTitle"), panelSimpleSql); // NOI18N

        labelInfoKeywords.setText(bundle.getString("AdvancedSearchPanel.labelInfoKeywords.text")); // NOI18N

        javax.swing.GroupLayout panelKeywordsLayout = new javax.swing.GroupLayout(panelKeywords);
        panelKeywords.setLayout(panelKeywordsLayout);
        panelKeywordsLayout.setHorizontalGroup(
            panelKeywordsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKeywordsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelKeywordsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelKeywordsInput, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
                    .addComponent(labelInfoKeywords, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        panelKeywordsLayout.setVerticalGroup(
            panelKeywordsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKeywordsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInfoKeywords, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelKeywordsInput, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab(bundle.getString("AdvancedSearchPanel.panelKeywords.TabConstraints.tabTitle"), panelKeywords); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(tabbedPane, gridBagConstraints);

        labelInfoDelete.setForeground(new java.awt.Color(0, 0, 255));
        labelInfoDelete.setText(bundle.getString("AdvancedSearchPanel.labelInfoDelete.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 0, 0);
        add(labelInfoDelete, gridBagConstraints);

        buttonSaveSearch.setMnemonic('p');
        buttonSaveSearch.setText(Bundle.getString("AdvancedSearchPanel.buttonSaveSearch.text")); // NOI18N
        buttonSaveSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveSearchActionPerformed(evt);
            }
        });

        buttonSaveAs.setText(Bundle.getString("AdvancedSearchPanel.buttonSaveAs.text")); // NOI18N
        buttonSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveAsActionPerformed(evt);
            }
        });

        buttonRemoveColumn.setText(bundle.getString("AdvancedSearchPanel.buttonRemoveColumn.text")); // NOI18N
        buttonRemoveColumn.setEnabled(false);
        buttonRemoveColumn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveColumnActionPerformed(evt);
            }
        });

        buttonAddColumn.setText(bundle.getString("AdvancedSearchPanel.buttonAddColumn.text")); // NOI18N
        buttonAddColumn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddColumnActionPerformed(evt);
            }
        });

        buttonResetColumns.setMnemonic('e');
        buttonResetColumns.setText(Bundle.getString("AdvancedSearchPanel.buttonResetColumns.text")); // NOI18N
        buttonResetColumns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonResetColumnsActionPerformed(evt);
            }
        });

        buttonSearch.setMnemonic('s');
        buttonSearch.setText(Bundle.getString("AdvancedSearchPanel.buttonSearch.text")); // NOI18N
        buttonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelButtonsLayout = new javax.swing.GroupLayout(panelButtons);
        panelButtons.setLayout(panelButtonsLayout);
        panelButtonsLayout.setHorizontalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonsLayout.createSequentialGroup()
                .addGroup(panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonSaveSearch)
                    .addComponent(buttonSaveAs))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonRemoveColumn)
                    .addComponent(buttonAddColumn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonSearch)
                    .addComponent(buttonResetColumns)))
        );

        panelButtonsLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonAddColumn, buttonRemoveColumn, buttonResetColumns, buttonSaveAs, buttonSaveSearch, buttonSearch});

        panelButtonsLayout.setVerticalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelButtonsLayout.createSequentialGroup()
                    .addComponent(buttonSaveSearch)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttonSaveAs)
                        .addComponent(buttonRemoveColumn)))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelButtonsLayout.createSequentialGroup()
                    .addGroup(panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttonResetColumns)
                        .addComponent(buttonAddColumn))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(buttonSearch)))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        add(panelButtons, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void buttonSaveSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveSearchActionPerformed
    saveSearch();
}//GEN-LAST:event_buttonSaveSearchActionPerformed

private void buttonSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveAsActionPerformed
    saveAs();
}//GEN-LAST:event_buttonSaveAsActionPerformed

private void buttonResetColumnsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonResetColumnsActionPerformed
    resetColumns();
}//GEN-LAST:event_buttonResetColumnsActionPerformed

private void buttonSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSearchActionPerformed
    search();
}//GEN-LAST:event_buttonSearchActionPerformed

private void buttonAddColumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddColumnActionPerformed
    addColumn();
}//GEN-LAST:event_buttonAddColumnActionPerformed

private void buttonRemoveColumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveColumnActionPerformed
    removeColumn();
}//GEN-LAST:event_buttonRemoveColumnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddColumn;
    private javax.swing.JButton buttonRemoveColumn;
    private javax.swing.JButton buttonResetColumns;
    private javax.swing.JButton buttonSaveAs;
    private javax.swing.JButton buttonSaveSearch;
    private javax.swing.JButton buttonSearch;
    private javax.swing.JLabel labelInfoDelete;
    private javax.swing.JLabel labelInfoKeywords;
    private javax.swing.JPanel panelButtons;
    private de.elmar_baumann.jpt.view.panels.SearchColumnPanel panelColumn1;
    private de.elmar_baumann.jpt.view.panels.SearchColumnPanel panelColumn2;
    private de.elmar_baumann.jpt.view.panels.SearchColumnPanel panelColumn3;
    private de.elmar_baumann.jpt.view.panels.SearchColumnPanel panelColumn4;
    private de.elmar_baumann.jpt.view.panels.SearchColumnPanel panelColumn5;
    private javax.swing.JPanel panelColumns;
    private javax.swing.JPanel panelKeywords;
    private de.elmar_baumann.jpt.view.panels.EditRepeatableTextEntryPanel panelKeywordsInput;
    private javax.swing.JPanel panelSimpleSql;
    private javax.swing.JScrollPane scrollPaneColumns;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
