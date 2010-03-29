/*
 * @(#)AdvancedSearchPanel.java    Created on 
 *
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

package org.jphototagger.program.view.panels;

import java.awt.event.ActionEvent;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.data.SavedSearchPanel;
import org.jphototagger.program.data.SavedSearchParamStatement;
import org.jphototagger.program.event.listener.impl.SearchListenerSupport;
import org.jphototagger.program.event.listener.SearchListener;
import org.jphototagger.program.event.SearchEvent;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Persistence;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.component.TabOrEnterLeavingTextArea;
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.componentutil.MnemonicUtil;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author  Elmar Baumann
 */
public final class AdvancedSearchPanel extends javax.swing.JPanel
        implements SearchListener, Persistence {
    private static final String SQL_IDENTIFIER_KEYWORDS =
        "dc_subjects.subject IN";
    private static final String KEY_SELECTED_TAB_INDEX =
        "AdvancedSearchPanel.SelectedTabIndex";
    private static final long             serialVersionUID   =
        -4036432653670374380L;
    private final List<SearchColumnPanel> searchColumnPanels =
        new LinkedList<SearchColumnPanel>();
    private final Map<Component, Component> defaultInputOfComponent =
        new HashMap<Component, Component>();
    private final Map<JButton, SearchColumnPanel> searchPanelOfRemoveButton =
        new HashMap<JButton, SearchColumnPanel>();
    private String                                searchName      = "";
    private boolean                               isSavedSearch;
    private boolean                               columnRemoved;
    private final transient SearchListenerSupport listenerSupport =
        new SearchListenerSupport();
    private final JPanel                          panelPadding    = new JPanel();

    public AdvancedSearchPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        panelPadding.setSize(10, 2);
        listenToSearchPanels();
        setAutocomplete();
        panelKeywordsInput.setBundleKeyPosRenameDialog(
            "AdvancedSearchPanel.Keywords.RenameDialog.Pos");
        setDefaultInputOfComponent();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setAutocomplete() {
        if (UserSettings.INSTANCE.isAutocomplete()) {
            panelKeywordsInput.setAutocomplete();
        }
    }

    private void setDefaultInputOfComponent() {
        defaultInputOfComponent.put(panelKeywords,
                                    panelKeywordsInput.textAreaInput);
        defaultInputOfComponent.put(panelCustomSql, textAreaCustomSqlQuery);
    }

    private synchronized void addPanelPadding() {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx      = 0;
        gbc.gridy      = GridBagConstraints.RELATIVE;
        gbc.weightx    = 1;
        gbc.weighty    = 1;
        gbc.gridwidth  = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.anchor     = GridBagConstraints.NORTHWEST;
        gbc.fill       = GridBagConstraints.BOTH;

        panelColumns.add(panelPadding, gbc);
    }

    private synchronized void setFirstColumn() {
        if (!searchColumnPanels.isEmpty()) {

            SearchColumnPanel scp = searchColumnPanels.get(0);

            defaultInputOfComponent.put(panelSimpleSql, scp.getTextFieldValue());
        }
    }

    public synchronized void ensureOneColumn() {
        if (searchColumnPanels.size() < 1 && !existsCustomSqlText()
                && !existsKeywords()) {
            addColumn();
        }
    }

    public void willDispose() {
        checkChanged();
        isSavedSearch = false;
        columnRemoved = false;
        setSearchName("");
    }

    @Override
    public void readProperties() {
        if (UserSettings.INSTANCE.getProperties().containsKey(
                KEY_SELECTED_TAB_INDEX)) {
            tabbedPane.setSelectedIndex(
                UserSettings.INSTANCE.getSettings().getInt(
                    KEY_SELECTED_TAB_INDEX));
        }
    }

    @Override
    public void writeProperties() {
        UserSettings.INSTANCE.getSettings().set(tabbedPane.getSelectedIndex(),
                KEY_SELECTED_TAB_INDEX);
        UserSettings.INSTANCE.writeToFile();
    }

    private synchronized boolean checkIsSearchValid() {
        if (!checkConsistent()) {
            return false;
        }
        if (existsCustomSqlText()) {
            return true;
        }

        boolean valid = existsKeywords();
        int     count = searchColumnPanels.size();
        int     index = 0;

        while (!valid && (index < count)) {
            valid = !searchColumnPanels.get(index++).getValue().isEmpty();
        }

        valid = valid && checkBrackets();

        if (!valid) {
            MessageDisplayer.error(this,
                                   "AdvancedSearchPanel.Error.InvalidQuery");
        }

        return valid;
    }

    private boolean checkConsistent() {
        if (existsCustomSqlText() &&
                (existsKeywords() || existsSimpleSqlValue())) {
            MessageDisplayer.error(
                    this, "AdvancedSearchPanel.Error.Inconsistent");
            return false;
        }
        return true;
    }

    private synchronized boolean checkBrackets() {
        int countOpenBrackets   = 0;
        int countClosedBrackets = 0;

        for (SearchColumnPanel panel : searchColumnPanels) {
            countOpenBrackets   += panel.getCountOpenBrackets();
            countClosedBrackets += panel.getCountClosedBrackets();
        }

        return countOpenBrackets == countClosedBrackets;
    }

    private synchronized void listenToSearchPanels() {
        for (SearchColumnPanel panel : searchColumnPanels) {
            panel.addSearchListener(this);
        }
    }

    public void addSearchListener(SearchListener listener) {
        listenerSupport.add(listener);
    }

    public void removeSearchListener(SearchListener listener) {
        listenerSupport.remove(listener);
    }

    private synchronized void notifySearch() {
        SearchEvent event       = new SearchEvent(SearchEvent.Type.START);
        
        event.setData(createSavedSearch());
        listenerSupport.notifyListeners(event);
    }

    private SavedSearch createSavedSearch() {
        SavedSearch savedSearch = new SavedSearch();

        savedSearch.setKeywords(getKeywords());
        savedSearch.setPanels(getSavedSearchPanels());
        if (existsCustomSqlText()) {
            savedSearch.createAndSetParamStatementFromCustomSql(
                    textAreaCustomSqlQuery.getText().trim());
        } else {
            savedSearch.createAndSetParamStatementFromPanels();
        }
        // Has to be called after creating the search has created the param stmt
        savedSearch.setName(searchName);
        return savedSearch;
    }

    private synchronized void notifySave(SavedSearch savedSearch) {
        SearchEvent event = new SearchEvent(SearchEvent.Type.SAVE);

        event.setData(savedSearch);
        event.setForceOverwrite(isSavedSearch);
        listenerSupport.notifyListeners(event);
    }

    public synchronized void notify(SearchEvent event) {
        listenerSupport.notifyListeners(event);
    }

    private synchronized void notifyNameChanged() {
        SearchEvent event = new SearchEvent(SearchEvent.Type.NAME_CHANGED);

        event.setSearchName(searchName);
        listenerSupport.notifyListeners(event);
    }

    public void setSavedSearch(SavedSearch search) {
        clearInput(true);
        isSavedSearch = true;
        setSearchName(search.getName());
        if (search.hasPanels()) {
            setSavedSearchToPanels(getSavedSearchPanelsContainingValues(
                    search.getPanels()));
        }
        if (search.hasKeywords()) {
            setKeywordsToPanel(search);
        }
        setCustomSqlToPanel(search);

        JPanel selPanel = existsCustomSqlText()
                ? panelCustomSql
                : existsSimpleSqlValue()
                ? panelSimpleSql
                : panelKeywords;

        setSelectedComponent(selPanel);
    }

    private synchronized void setSavedSearchToPanels(
            List<SavedSearchPanel> savedSearchPanels) {
        if (savedSearchPanels != null) {
            int dataSize = savedSearchPanels.size();

            removeAllColumns();
            columnRemoved = false;
            ensureColumnCount(dataSize);

            int panelSize = searchColumnPanels.size();

            for (int dataIndex = 0; dataIndex < dataSize; dataIndex++) {
                if (dataIndex < panelSize) {
                    searchColumnPanels.get(dataIndex).setSavedSearchPanel(
                        savedSearchPanels.get(dataIndex));
                }
            }
            addPanelPadding();
        }
    }

    private synchronized void removeAllColumns() {
        searchColumnPanels.clear();
        panelColumns.removeAll();
        searchPanelOfRemoveButton.clear();
    }

    private List<SavedSearchPanel> getSavedSearchPanelsContainingValues(
            List<SavedSearchPanel> savedSearchPanels) {
        List<SavedSearchPanel> panels = new ArrayList<SavedSearchPanel>(savedSearchPanels.size());

        for (SavedSearchPanel panel : savedSearchPanels) {
            if (panel.hasValue()) {
                panels.add(panel);
            }
        }

        return panels;
    }

    private void setKeywordsToPanel(SavedSearch search) {
        String  sql         = search.getSavedSearchParamStatement().getSql();
        boolean hasKeywords = sql.contains(SQL_IDENTIFIER_KEYWORDS);

        if (!hasKeywords) {
            return;
        }

        List<String> values       = search.getSavedSearchParamStatement().getValues();
        List<String> keywords     = new ArrayList<String>();
        int          keywordCount = getKeywordCount(sql);
        int          valueCount   = values.size();

        for (int i = 0; (i < keywordCount) && (valueCount - i - 1 >= 0); i++) {
            keywords.add(values.get(valueCount - i - 1));
        }

        panelKeywordsInput.setText(keywords);

        if (!existsSimpleSqlValue()) {
            setSelectedComponent(panelKeywords);
        }
    }

    private int getKeywordCount(String sql) {
        int    index     = sql.indexOf(SQL_IDENTIFIER_KEYWORDS);
        String params    = sql.substring(index);
        int    count     = 0;
        int    findIndex = params.indexOf('?', 0);

        while (findIndex > 0) {
            count++;
            findIndex = params.indexOf('?', findIndex + 1);
        }

        return count;
    }

    private void setCustomSqlToPanel(SavedSearch search) {
        if (existsSimpleSqlValue() || existsKeywords()) {
            return;
        }

        SavedSearchParamStatement stmt = search.getSavedSearchParamStatement();

        if (stmt.getSql() == null) {
            return;
        }

        textAreaCustomSqlQuery.setText(stmt.getSql());
        setSelectedComponent(panelCustomSql);
    }

    private void setSelectedComponent(Component c) {
        tabbedPane.setSelectedComponent(c);
        writeProperties();
    }

    private synchronized boolean existsSimpleSqlValue() {

        for (SearchColumnPanel panel : searchColumnPanels) {
            if (!panel.getValue().trim().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    private boolean existsCustomSqlText() {
        return !textAreaCustomSqlQuery.getText().trim().isEmpty();
    }

    private synchronized void ensureColumnCount(int count) {
        int currentCount = 0;

        currentCount = searchColumnPanels.size();

        if (currentCount >= count) {
            return;
        }

        for (int i = currentCount; i < count; i++) {
            addColumn();
        }
    }

    private synchronized void addColumn() {
        GridBagConstraints gbc     =  getColumnGridBagConstraints();
        SearchColumnPanel  scPanel = new SearchColumnPanel();

        panelColumns.remove(panelPadding);
        panelColumns.add(scPanel, gbc);
        addPanelPadding();
        searchColumnPanels.add(scPanel);
        searchPanelOfRemoveButton.put(scPanel.buttonRemoveColumn, scPanel);
        scPanel.buttonRemoveColumn.addActionListener(new RemoveButtonListener());
        setFirstColumn();
        scPanel.addSearchListener(this);
        ComponentUtil.forceRepaint(this);
    }

    private synchronized GridBagConstraints getColumnGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx      = 0;
        gbc.gridy      = GridBagConstraints.RELATIVE;
        gbc.gridwidth  = GridBagConstraints.REMAINDER;
        gbc.gridheight = 1;
        gbc.weightx    = 1.0;
        gbc.weighty    = 0;
        gbc.fill       = GridBagConstraints.HORIZONTAL;
        gbc.anchor     = GridBagConstraints.NORTHWEST;

        return gbc;
    }

    private class RemoveButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            assert e.getSource() instanceof JButton : e.getSource();

            SearchColumnPanel panel =
                    searchPanelOfRemoveButton.get((JButton) e.getSource());

            if (panel != null) {
                removeColumn(panel);
            }
        }
    }

    private synchronized void removeColumn(SearchColumnPanel scPanel) {
        panelColumns.remove(panelPadding);
        panelColumns.remove(scPanel);
        addPanelPadding();
        searchColumnPanels.remove(scPanel);
        searchPanelOfRemoveButton.remove(scPanel.buttonRemoveColumn);
        if (scPanel.canCreateSql()) {
            columnRemoved = true;
        }
        setFirstColumn();
        ComponentUtil.forceRepaint(this);
    }

    private synchronized void clearInput(boolean allPanels) {
        checkChanged();

        Component selComponent = tabbedPane.getSelectedComponent();

        if (allPanels || (selComponent == panelSimpleSql)) {
            for (SearchColumnPanel panel : searchColumnPanels) {
                panel.reset();
            }
        }

        if (allPanels || (selComponent == panelKeywords)) {
            panelKeywordsInput.setText(new ArrayList<String>());
        }

        if (allPanels || (selComponent == panelCustomSql)) {
            textAreaCustomSqlQuery.setText("");
        }
    }

    private void checkChanged() {
        if (isSavedSearch && (columnRemoved || columnChanged())) {
            if (MessageDisplayer.confirmYesNo(
                    this, "AdvancedSearchPanel.Confirm.SaveChanges")) {
                save();
            }
        }
    }

    private synchronized boolean columnChanged() {
        for (SearchColumnPanel panel : searchColumnPanels) {
            if (panel.isChanged()) {
                return true;
            }
        }

        return false;
    }

    private void setFocusToInputInTab(Component selectedComponent) {
        if (selectedComponent == null) {
            return;
        }

        Component input = defaultInputOfComponent.get(selectedComponent);

        if (input != null) {
            input.requestFocusInWindow();
        }
    }

    private void saveAs() {
        if (isSavedSearch) {
            isSavedSearch = false;
            save();
            isSavedSearch = true;
        } else {
            save();
        }
    }

    private boolean save() {
        if (checkIsSearchValid()) {
            String name = getSearchName();

            if ((name != null) &&!name.isEmpty()) {
                setSearchName(name);
                setPanelsUnchanged();
                notifySave(createSavedSearch());

                return true;
            }
        }

        return false;
    }

    private String getSearchName() {
        if (isSavedSearch) {
            return searchName;
        } else {
            return MessageDisplayer.input(
                "AdvancedSearchPanel.Input.SearchName", searchName,
                getClass().getName());
        }
    }

    private synchronized void setPanelsUnchanged() {
        for (SearchColumnPanel panel : searchColumnPanels) {
            panel.setChanged(false);
        }
    }

    private synchronized List<SavedSearchPanel> getSavedSearchPanels() {
        List<SavedSearchPanel> panels = new ArrayList<SavedSearchPanel>();
        int                    size   = searchColumnPanels.size();

        for (int index = 0; index < size; index++) {
            SearchColumnPanel panel            = searchColumnPanels.get(index);
            SavedSearchPanel  savedSearchPanel = panel.getSavedSearchPanel();

            savedSearchPanel.setPanelIndex(index);
            panels.add(savedSearchPanel);
        }

        return (panels.size() > 0)
               ? panels
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

    private boolean existsKeywords() {
        return !getKeywords().isEmpty();
    }

    private List<String> getKeywords() {
        String             textFieldText = panelKeywordsInput.getText();
        Collection<String> listText      =
            panelKeywordsInput.getRepeatableText();
        List<String>       keywords      = new ArrayList<String>(listText);

        if (!textFieldText.isEmpty()) {
            keywords.add(textFieldText);
        }

        return keywords;
    }

    @Override
    public void actionPerformed(SearchEvent evt) {
        if (evt.getType().equals(SearchEvent.Type.START)) {
            search();
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

        tabbedPane = new javax.swing.JTabbedPane();
        panelKeywords = new javax.swing.JPanel();
        labelInfoKeywords = new javax.swing.JLabel();
        panelKeywordsInput = new org.jphototagger.program.view.panels.EditRepeatableTextEntryPanel();
        panelKeywordsInput.setPrompt("");
        panelKeywordsInput.getTextArea().setTransferHandler(new org.jphototagger.program.datatransfer.TransferHandlerDropTextComponent());
        panelKeywordsInput.getList().setTransferHandler(new org.jphototagger.program.datatransfer.TransferHandlerDropList());
        panelSimpleSql = new javax.swing.JPanel();
        scrollPaneColumns = new javax.swing.JScrollPane();
        panelColumns = new javax.swing.JPanel();
        labelInfoDelete = new javax.swing.JLabel();
        buttonAddColumn = new javax.swing.JButton();
        panelCustomSql = new javax.swing.JPanel();
        labelCustomSqlInfo = new javax.swing.JLabel();
        scrollPaneCustomSqlQuery = new javax.swing.JScrollPane();
        textAreaCustomSqlQuery = new TabOrEnterLeavingTextArea();
        textAreaCustomSqlQuery.setTransferHandler(new org.jphototagger.program.datatransfer.TransferHandlerDropTextComponent());
        panelButtons = new javax.swing.JPanel();
        buttonSaveSearch = new javax.swing.JButton();
        buttonSaveAs = new javax.swing.JButton();
        buttonResetColumns = new javax.swing.JButton();
        buttonSearch = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        tabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPaneStateChanged(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/resource/properties/Bundle"); // NOI18N
        labelInfoKeywords.setText(bundle.getString("AdvancedSearchPanel.labelInfoKeywords.text")); // NOI18N

        javax.swing.GroupLayout panelKeywordsLayout = new javax.swing.GroupLayout(panelKeywords);
        panelKeywords.setLayout(panelKeywordsLayout);
        panelKeywordsLayout.setHorizontalGroup(
            panelKeywordsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKeywordsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelKeywordsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelKeywordsInput, javax.swing.GroupLayout.DEFAULT_SIZE, 632, Short.MAX_VALUE)
                    .addComponent(labelInfoKeywords, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        panelKeywordsLayout.setVerticalGroup(
            panelKeywordsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKeywordsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInfoKeywords, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelKeywordsInput, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab(bundle.getString("AdvancedSearchPanel.panelKeywords.TabConstraints.tabTitle"), panelKeywords); // NOI18N

        scrollPaneColumns.setBorder(null);

        panelColumns.setLayout(new java.awt.GridBagLayout());
        scrollPaneColumns.setViewportView(panelColumns);

        labelInfoDelete.setForeground(new java.awt.Color(0, 0, 255));
        labelInfoDelete.setText(bundle.getString("AdvancedSearchPanel.labelInfoDelete.text")); // NOI18N

        buttonAddColumn.setText(bundle.getString("AdvancedSearchPanel.buttonAddColumn.text")); // NOI18N
        buttonAddColumn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddColumnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelSimpleSqlLayout = new javax.swing.GroupLayout(panelSimpleSql);
        panelSimpleSql.setLayout(panelSimpleSqlLayout);
        panelSimpleSqlLayout.setHorizontalGroup(
            panelSimpleSqlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSimpleSqlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSimpleSqlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPaneColumns, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 632, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSimpleSqlLayout.createSequentialGroup()
                        .addComponent(labelInfoDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 249, Short.MAX_VALUE)
                        .addComponent(buttonAddColumn)))
                .addContainerGap())
        );
        panelSimpleSqlLayout.setVerticalGroup(
            panelSimpleSqlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSimpleSqlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneColumns, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSimpleSqlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonAddColumn)
                    .addComponent(labelInfoDelete))
                .addContainerGap())
        );

        tabbedPane.addTab(bundle.getString("AdvancedSearchPanel.panelSimpleSql.TabConstraints.tabTitle"), panelSimpleSql); // NOI18N

        panelCustomSql.setLayout(new java.awt.GridBagLayout());

        labelCustomSqlInfo.setDisplayedMnemonic('k');
        labelCustomSqlInfo.setText(bundle.getString("AdvancedSearchPanel.labelCustomSqlInfo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelCustomSql.add(labelCustomSqlInfo, gridBagConstraints);

        textAreaCustomSqlQuery.setColumns(20);
        textAreaCustomSqlQuery.setLineWrap(true);
        textAreaCustomSqlQuery.setRows(2);
        scrollPaneCustomSqlQuery.setViewportView(textAreaCustomSqlQuery);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelCustomSql.add(scrollPaneCustomSqlQuery, gridBagConstraints);

        tabbedPane.addTab(bundle.getString("AdvancedSearchPanel.panelCustomSql.TabConstraints.tabTitle"), panelCustomSql); // NOI18N

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

        buttonSaveSearch.setText(JptBundle.INSTANCE.getString("AdvancedSearchPanel.buttonSaveSearch.text")); // NOI18N
        buttonSaveSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveSearchActionPerformed(evt);
            }
        });

        buttonSaveAs.setText(JptBundle.INSTANCE.getString("AdvancedSearchPanel.buttonSaveAs.text")); // NOI18N
        buttonSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveAsActionPerformed(evt);
            }
        });

        buttonResetColumns.setText(JptBundle.INSTANCE.getString("AdvancedSearchPanel.buttonResetColumns.text")); // NOI18N
        buttonResetColumns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonResetColumnsActionPerformed(evt);
            }
        });

        buttonSearch.setText(JptBundle.INSTANCE.getString("AdvancedSearchPanel.buttonSearch.text")); // NOI18N
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
                .addComponent(buttonSaveSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonSaveAs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonResetColumns)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonSearch))
        );
        panelButtonsLayout.setVerticalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(buttonSaveSearch)
                .addComponent(buttonSaveAs)
                .addComponent(buttonResetColumns)
                .addComponent(buttonSearch))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        add(panelButtons, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void buttonSaveSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveSearchActionPerformed
        save();
    }//GEN-LAST:event_buttonSaveSearchActionPerformed

    private void buttonSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveAsActionPerformed
        saveAs();
    }//GEN-LAST:event_buttonSaveAsActionPerformed

    private void buttonResetColumnsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonResetColumnsActionPerformed
        clearInput(false);
    }//GEN-LAST:event_buttonResetColumnsActionPerformed

    private void buttonSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSearchActionPerformed
        search();
    }//GEN-LAST:event_buttonSearchActionPerformed

    private void buttonAddColumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddColumnActionPerformed
        addColumn();
    }//GEN-LAST:event_buttonAddColumnActionPerformed

    private void tabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPaneStateChanged
        setFocusToInputInTab(tabbedPane.getSelectedComponent());
    }//GEN-LAST:event_tabbedPaneStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddColumn;
    private javax.swing.JButton buttonResetColumns;
    private javax.swing.JButton buttonSaveAs;
    private javax.swing.JButton buttonSaveSearch;
    private javax.swing.JButton buttonSearch;
    private javax.swing.JLabel labelCustomSqlInfo;
    private javax.swing.JLabel labelInfoDelete;
    private javax.swing.JLabel labelInfoKeywords;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelColumns;
    private javax.swing.JPanel panelCustomSql;
    private javax.swing.JPanel panelKeywords;
    private org.jphototagger.program.view.panels.EditRepeatableTextEntryPanel panelKeywordsInput;
    private javax.swing.JPanel panelSimpleSql;
    private javax.swing.JScrollPane scrollPaneColumns;
    private javax.swing.JScrollPane scrollPaneCustomSqlQuery;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextArea textAreaCustomSqlQuery;
    // End of variables declaration//GEN-END:variables
}
