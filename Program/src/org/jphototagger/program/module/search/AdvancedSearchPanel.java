package org.jphototagger.program.module.search;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.storage.Persistence;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.event.listener.ListenerSupport;
import org.jphototagger.domain.metadata.search.ParamStatement;
import org.jphototagger.domain.metadata.search.SavedSearch;
import org.jphototagger.domain.metadata.search.SavedSearchPanel;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.TabOrEnterLeavingTextArea;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.factory.ControllerFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class AdvancedSearchPanel extends javax.swing.JPanel implements Persistence {

    private static final String KEY_SELECTED_TAB_INDEX = "AdvancedSearchPanel.SelectedTabIndex";
    private static final long serialVersionUID = 1L;
    private final List<SearchMetaDataValuePanel> searchColumnPanels = new LinkedList<>();
    private final Map<Component, Component> defaultInputOfComponent = new HashMap<>();
    private final Map<JButton, SearchMetaDataValuePanel> searchPanelOfRemoveButton = new HashMap<>();
    private String searchName;
    private boolean isSavedSearch;
    private boolean columnRemoved;
    private boolean customSqlChanged;
    private final transient ListenerSupport<NameListener> ls = new ListenerSupport<>();
    private final JPanel panelPadding = new JPanel();

    public AdvancedSearchPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setUndefinedSearchName();
        panelPadding.setSize(10, 2);
        listenToSearchPanels();
        setAutocomplete();
        panelKeywordsInput.setBundleKeyPosRenameDialog("AdvancedSearchPanel.Keywords.RenameDialog.Pos");
        setDefaultInputOfComponent();
        setFocusToInputInTab(tabbedPane.getSelectedComponent());
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setUndefinedSearchName() {
        setSearchName(Bundle.getString(AdvancedSearchPanel.class, "AdvancedSearchPanel.UndefinedName"));
    }


    private void setAutocomplete() {
        if (isAutocomplete()) {
            panelKeywordsInput.enableAutocomplete();
        }
    }

    private boolean isAutocomplete() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs == null
                ? true
                : prefs.containsKey(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                : true;
    }

    private void setDefaultInputOfComponent() {
        defaultInputOfComponent.put(panelKeywords, panelKeywordsInput.textAreaInput);
        defaultInputOfComponent.put(panelCustomSql, textAreaCustomSqlQuery);
    }

    private void addPanelPadding() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        panelColumns.add(panelPadding, gbc);
    }

    private void setFirstColumn() {
        if (!searchColumnPanels.isEmpty()) {
            SearchMetaDataValuePanel scp = searchColumnPanels.get(0);
            defaultInputOfComponent.put(panelSimpleSql, scp.getTextFieldValue());
        }
    }

    public void empty() {
        removeAllColumns();
        emptyKeywordsPanel();
        textAreaCustomSqlQuery.setText("");
        setUndirty();
    }

    private void emptyKeywordsPanel() {
        ListModel<?> model = panelKeywordsInput.getList().getModel();
        if (model instanceof DefaultListModel) {
            ((DefaultListModel) model).clear();
        }
        panelKeywordsInput.getTextArea().setText("");
    }

    private class CustomSqlChangeListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent evt) {
            customSqlChanged = true;
        }

        @Override
        public void removeUpdate(DocumentEvent evt) {
            customSqlChanged = true;
        }

        @Override
        public void changedUpdate(DocumentEvent evt) {
            customSqlChanged = true;
        }

    }

    public void willDispose() {
        checkChanged();
        setUndirty();
        setUndefinedSearchName();
    }

    private void setUndirty() {
        isSavedSearch = false;
        columnRemoved = false;
        customSqlChanged = false;
        panelKeywordsInput.setDirty(false);
        setSearchColumnPanelsUnchanged();
    }

    private void setSearchColumnPanelsUnchanged() {
        for (SearchMetaDataValuePanel panel : searchColumnPanels) {
            panel.setChanged(false);
        }
    }

    @Override
    public void restore() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs.containsKey(KEY_SELECTED_TAB_INDEX)) {
            tabbedPane.setSelectedIndex(prefs.getInt(KEY_SELECTED_TAB_INDEX));
        }
    }

    @Override
    public void persist() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setInt(KEY_SELECTED_TAB_INDEX, tabbedPane.getSelectedIndex());
    }

    private boolean checkIsSearchValid() {
        if (!checkSearchTypeIsValid()) {
            return false;
        }
        if (existsCustomSqlText()) {
            return true;
        }
        boolean valid = existsKeywords();
        int columnCount = searchColumnPanels.size();
        int index = 0;
        while (!valid && (index < columnCount)) {
            SearchMetaDataValuePanel searchColumnPanel = searchColumnPanels.get(index);
            String value = searchColumnPanel.getValue();
            valid = !value.isEmpty();
            index++;
        }
        valid = valid && checkBrackets();
        if (!valid) {
            String message = Bundle.getString(AdvancedSearchPanel.class, "AdvancedSearchPanel.Error.InvalidQuery");
            MessageDisplayer.error(this, message);
        }
        return valid;
    }

    private boolean checkSearchTypeIsValid() {
        boolean customSqlTextExists = existsCustomSqlText();
        boolean keywordsExisting = existsKeywords();
        boolean simpleSqlValueExists = existsSimpleSqlValue();
        boolean valid = !customSqlTextExists || customSqlTextExists && !keywordsExisting && !simpleSqlValueExists;
        if (!valid) {
            String message = Bundle.getString(AdvancedSearchPanel.class, "AdvancedSearchPanel.Error.SearchTypeInvalid");
            MessageDisplayer.error(this, message);
            return false;
        }
        return true;
    }

    private boolean checkBrackets() {
        int countOpenBrackets = 0;
        int countClosedBrackets = 0;
        for (SearchMetaDataValuePanel panel : searchColumnPanels) {
            countOpenBrackets += panel.getCountOpenBrackets();
            countClosedBrackets += panel.getCountClosedBrackets();
        }
        return countOpenBrackets == countClosedBrackets;
    }

    private void search() {
        if (checkIsSearchValid()) {
            ControllerFactory.INSTANCE.getController(AdvancedSearchController.class).actionPerformed(null);
        }
    }

    private void listenToSearchPanels() {
        for (SearchMetaDataValuePanel panel : searchColumnPanels) {
            panel.getTextFieldValue().addKeyListener(new KeyListener() );
        }
    }

    private class KeyListener extends KeyAdapter {

        @Override
        public void keyTyped(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                search();
            }
        }
    }

    public SavedSearch createSavedSearch() {
        SavedSearch savedSearch = new SavedSearch();
        savedSearch.setName(searchName);
        savedSearch.setKeywords(getKeywords());
        setSavedSearchPanels(savedSearch);
        savedSearch.setCustomSql(textAreaCustomSqlQuery.getText().trim());
        savedSearch.setType(existsCustomSqlText()
                ? SavedSearch.Type.CUSTOM_SQL
                : SavedSearch.Type.KEYWORDS_AND_PANELS);
        return savedSearch;
    }

    public void setSavedSearch(SavedSearch search) {
        if (search == null) {
            throw new NullPointerException("search == null");
        }
        if (!search.isValid()) {
            return;
        }
        clearInput(true);
        isSavedSearch = true;
        setSearchName(search.getName());
        if (search.hasPanels()) {
            setSavedSearchPanels(search.getPanels());
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
        customSqlChanged = false;
        columnRemoved    = false;
    }

    private void setSavedSearchPanels(List<SavedSearchPanel> panels) {
        int panelCount = panels.size();
        removeAllColumns();
        ensureColumnCount(panelCount);
        int columnCount = searchColumnPanels.size();
        for (int i = 0; i < panelCount; i++) {
            if (i < columnCount) {
                searchColumnPanels.get(i).setSavedSearchPanel(panels.get(i));
            }
        }
        addPanelPadding();
    }

    private void removeAllColumns() {
        searchColumnPanels.clear();
        panelColumns.removeAll();
        searchPanelOfRemoveButton.clear();
    }

    private void setKeywordsToPanel(SavedSearch search) {
        panelKeywordsInput.setTexts(search.getKeywords());
        if (!existsSimpleSqlValue()) {
            setSelectedComponent(panelKeywords);
        }
    }

    private void setCustomSqlToPanel(SavedSearch search) {
        if (existsSimpleSqlValue() || existsKeywords()) {
            return;
        }
        ParamStatement stmt = search.createParamStatement();
        textAreaCustomSqlQuery.setText(stmt.getSql());
        setSelectedComponent(panelCustomSql);
    }

    private void setSelectedComponent(Component c) {
        tabbedPane.setSelectedComponent(c);
        persist();
    }

    private boolean existsSimpleSqlValue() {
        for (SearchMetaDataValuePanel panel : searchColumnPanels) {
            if (!panel.getValue().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean existsCustomSqlText() {
        return !textAreaCustomSqlQuery.getText().trim().isEmpty();
    }

    private void ensureColumnCount(int count) {
        int currentCount = searchColumnPanels.size();
        if (currentCount >= count) {
            return;
        }
        for (int i = currentCount; i < count; i++) {
            addColumn();
        }
    }

    private void addColumn() {
        GridBagConstraints gbc =  getColumnGridBagConstraints();
        SearchMetaDataValuePanel scPanel = new SearchMetaDataValuePanel();
        panelColumns.remove(panelPadding);
        panelColumns.add(scPanel, gbc);
        addPanelPadding();
        searchColumnPanels.add(scPanel);
        searchPanelOfRemoveButton.put(scPanel.buttonRemoveColumn, scPanel);
        scPanel.buttonRemoveColumn.addActionListener(new RemoveButtonListener());
        setFirstColumn();
        scPanel.getTextFieldValue().addKeyListener(new KeyListener());
        ComponentUtil.forceRepaint(this);
    }

    private GridBagConstraints getColumnGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridwidth  = GridBagConstraints.REMAINDER;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        return gbc;
    }

    private class RemoveButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            assert evt.getSource() instanceof JButton : evt.getSource();

            SearchMetaDataValuePanel panel = searchPanelOfRemoveButton.get((JButton) evt.getSource());

            if (panel != null) {
                removeColumn(panel);
            }
        }
    }

    private void removeColumn(SearchMetaDataValuePanel scPanel) {
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

    private void clearInput(boolean allPanels) {
        checkChanged();
        Component selComponent = tabbedPane.getSelectedComponent();
        if (allPanels || (selComponent == panelSimpleSql)) {
            for (SearchMetaDataValuePanel panel : searchColumnPanels) {
                panel.reset();
            }
        }
        if (allPanels || (selComponent == panelKeywords)) {
            panelKeywordsInput.setTexts(new ArrayList<String>());
        }
        if (allPanels || (selComponent == panelCustomSql)) {
            textAreaCustomSqlQuery.setText("");
        }
        setUndirty();
    }

    private void checkChanged() {
        if (columnRemoved || columnChanged() || customSqlChanged || panelKeywordsInput.isDirty()) {
            String message = Bundle.getString(AdvancedSearchPanel.class, "AdvancedSearchPanel.Confirm.SaveChanges");
            if (MessageDisplayer.confirmYesNo(this, message)) {
                save(createSavedSearch(), true);
            }
        }
    }

    private boolean columnChanged() {
        for (SearchMetaDataValuePanel panel : searchColumnPanels) {
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

    private void save() {
        if (!checkIsSearchValid()) {
            return;
        }
        save(createSavedSearch(), isSavedSearch);
    }

    private void saveAs() {
        if (!checkIsSearchValid()) {
            return;
        }
        String suggestName = searchName == null
                ? ""
                : searchName;
        String name = SavedSearchesUtil.getNotExistingName(suggestName);
        if ((name != null) &&!name.isEmpty()) {
            SavedSearch savedSearch = createSavedSearch();
            savedSearch.setName(name);
            save(savedSearch, false);
        }
    }

    private void save(SavedSearch savedSearch, boolean update) {
        boolean saved = update
                ? SavedSearchesUtil.update(savedSearch)
                : SavedSearchesUtil.insert(savedSearch);

        if (saved) {
            setSearchName(savedSearch.getName());
            setUndirty();
            isSavedSearch = true;
        }
    }

    private void setSavedSearchPanels(SavedSearch search) {
        List<SavedSearchPanel> panels = new ArrayList<>();
        int size = searchColumnPanels.size();
        int pIndex = 0;
        for (int index = 0; index < size; index++) {
            SearchMetaDataValuePanel panel = searchColumnPanels.get(index);
            SavedSearchPanel savedSearchPanel = panel.getSavedSearchPanel();
            if (savedSearchPanel.hasValue()) {
                savedSearchPanel.setPanelIndex(pIndex);
                pIndex++;
                panels.add(savedSearchPanel);
            }
        }
        search.setPanels(panels);
    }

    private void setSearchName(String name) {
        searchName = name;
        notifyNameChanged();
    }

    public void addNameListener(NameListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }
        ls.add(listener);
    }

    public void removeNameListener(NameListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }
        ls.remove(listener);
    }

    private void notifyNameChanged() {
        for (NameListener listener : ls.get()) {
            listener.nameChanged(searchName);
        }
    }

    private boolean existsKeywords() {
        return !getKeywords().isEmpty();
    }

    private List<String> getKeywords() {
        String textFieldText = panelKeywordsInput.getText();
        Collection<String> listText = panelKeywordsInput.getRepeatableText();
        List<String> keywords = new ArrayList<>(listText);
        if (!textFieldText.isEmpty()) {
            keywords.add(textFieldText);
        }
        return keywords;
    }

    public JButton getButtonSearch() {
        return buttonSearch;
    }

    public String getSearchName() {
        return searchName;
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

        tabbedPane = new javax.swing.JTabbedPane();
        panelKeywords = new javax.swing.JPanel();
        labelInfoKeywords = new javax.swing.JLabel();
        panelKeywordsInput = new org.jphototagger.program.module.editmetadata.EditRepeatableTextEntryPanel();
        panelKeywordsInput.setPrompt("");
        panelKeywordsInput.getTextArea().setTransferHandler(new org.jphototagger.program.datatransfer.DropTextComponentTransferHandler());
        panelKeywordsInput.getList().setTransferHandler(new org.jphototagger.program.datatransfer.DropListTransferHandler());
        panelSimpleSql = new javax.swing.JPanel();
        scrollPaneColumns = new javax.swing.JScrollPane();
        panelColumns = new javax.swing.JPanel();
        labelInfoDelete = new javax.swing.JLabel();
        buttonAddColumn = new javax.swing.JButton();
        panelCustomSql = new javax.swing.JPanel();
        labelCustomSqlInfo = new javax.swing.JLabel();
        scrollPaneCustomSqlQuery = new javax.swing.JScrollPane();
        textAreaCustomSqlQuery = new TabOrEnterLeavingTextArea();
        textAreaCustomSqlQuery.setTransferHandler(new org.jphototagger.program.datatransfer.DropTextComponentTransferHandler());
        textAreaCustomSqlQuery.getDocument().addDocumentListener(new CustomSqlChangeListener());
        panelButtons = new javax.swing.JPanel();
        buttonSaveSearch = new javax.swing.JButton();
        buttonSaveAs = new javax.swing.JButton();
        buttonResetColumns = new javax.swing.JButton();
        buttonSearch = new javax.swing.JButton();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        tabbedPane.setName("tabbedPane"); // NOI18N
        tabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPaneStateChanged(evt);
            }
        });
        tabbedPane.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tabbedPaneFocusGained(evt);
            }
        });

        panelKeywords.setName("panelKeywords"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/module/search/Bundle"); // NOI18N
        labelInfoKeywords.setText(bundle.getString("AdvancedSearchPanel.labelInfoKeywords.text")); // NOI18N
        labelInfoKeywords.setName("labelInfoKeywords"); // NOI18N

        panelKeywordsInput.setName("panelKeywordsInput"); // NOI18N

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

        panelSimpleSql.setName("panelSimpleSql"); // NOI18N

        scrollPaneColumns.setBorder(null);
        scrollPaneColumns.setName("scrollPaneColumns"); // NOI18N

        panelColumns.setName("panelColumns"); // NOI18N
        panelColumns.setLayout(new java.awt.GridBagLayout());
        scrollPaneColumns.setViewportView(panelColumns);

        labelInfoDelete.setForeground(new java.awt.Color(0, 0, 255));
        labelInfoDelete.setText(bundle.getString("AdvancedSearchPanel.labelInfoDelete.text")); // NOI18N
        labelInfoDelete.setName("labelInfoDelete"); // NOI18N

        buttonAddColumn.setText(bundle.getString("AdvancedSearchPanel.buttonAddColumn.text")); // NOI18N
        buttonAddColumn.setName("buttonAddColumn"); // NOI18N
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

        panelCustomSql.setName("panelCustomSql"); // NOI18N
        panelCustomSql.setLayout(new java.awt.GridBagLayout());

        labelCustomSqlInfo.setDisplayedMnemonic('k');
        labelCustomSqlInfo.setText(bundle.getString("AdvancedSearchPanel.labelCustomSqlInfo.text")); // NOI18N
        labelCustomSqlInfo.setName("labelCustomSqlInfo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelCustomSql.add(labelCustomSqlInfo, gridBagConstraints);

        scrollPaneCustomSqlQuery.setName("scrollPaneCustomSqlQuery"); // NOI18N

        textAreaCustomSqlQuery.setColumns(20);
        textAreaCustomSqlQuery.setLineWrap(true);
        textAreaCustomSqlQuery.setRows(2);
        textAreaCustomSqlQuery.setName("textAreaCustomSqlQuery"); // NOI18N
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

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridLayout(1, 0, 3, 0));

        buttonSaveSearch.setText(bundle.getString("AdvancedSearchPanel.buttonSaveSearch.text")); // NOI18N
        buttonSaveSearch.setName("buttonSaveSearch"); // NOI18N
        buttonSaveSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveSearchActionPerformed(evt);
            }
        });
        panelButtons.add(buttonSaveSearch);

        buttonSaveAs.setText(bundle.getString("AdvancedSearchPanel.buttonSaveAs.text")); // NOI18N
        buttonSaveAs.setName("buttonSaveAs"); // NOI18N
        buttonSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveAsActionPerformed(evt);
            }
        });
        panelButtons.add(buttonSaveAs);

        buttonResetColumns.setText(bundle.getString("AdvancedSearchPanel.buttonResetColumns.text")); // NOI18N
        buttonResetColumns.setName("buttonResetColumns"); // NOI18N
        buttonResetColumns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonResetColumnsActionPerformed(evt);
            }
        });
        panelButtons.add(buttonResetColumns);

        buttonSearch.setText(bundle.getString("AdvancedSearchPanel.buttonSearch.text")); // NOI18N
        buttonSearch.setName("buttonSearch"); // NOI18N
        panelButtons.add(buttonSearch);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 0);
        add(panelButtons, gridBagConstraints);
    }//GEN-END:initComponents

    private void buttonSaveSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveSearchActionPerformed
        save();
    }//GEN-LAST:event_buttonSaveSearchActionPerformed

    private void buttonSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveAsActionPerformed
        saveAs();
    }//GEN-LAST:event_buttonSaveAsActionPerformed

    private void buttonResetColumnsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonResetColumnsActionPerformed
        clearInput(false);
    }//GEN-LAST:event_buttonResetColumnsActionPerformed

    private void buttonAddColumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddColumnActionPerformed
        addColumn();
    }//GEN-LAST:event_buttonAddColumnActionPerformed

    private void tabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPaneStateChanged
        setFocusToInputInTab(tabbedPane.getSelectedComponent());
    }//GEN-LAST:event_tabbedPaneStateChanged

    private void tabbedPaneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tabbedPaneFocusGained
        setFocusToInputInTab(tabbedPane.getSelectedComponent());
    }//GEN-LAST:event_tabbedPaneFocusGained
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
    private org.jphototagger.program.module.editmetadata.EditRepeatableTextEntryPanel panelKeywordsInput;
    private javax.swing.JPanel panelSimpleSql;
    private javax.swing.JScrollPane scrollPaneColumns;
    private javax.swing.JScrollPane scrollPaneCustomSqlQuery;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextArea textAreaCustomSqlQuery;
    // End of variables declaration//GEN-END:variables
}
