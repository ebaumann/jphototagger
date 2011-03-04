package org.jphototagger.program.view.panels;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.event.DocumentEvent;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.data.SavedSearchPanel;
import org.jphototagger.program.data.ParamStatement;
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
import java.awt.event.KeyAdapter;

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
import javax.swing.event.DocumentListener;
import org.jphototagger.program.controller.search.ControllerAdvancedSearch;
import org.jphototagger.program.event.listener.impl.ListenerSupport;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.helper.SavedSearchesHelper;

/**
 *
 * @author Elmar Baumann
 */
public final class AdvancedSearchPanel extends javax.swing.JPanel implements Persistence {
    private static final String KEY_SELECTED_TAB_INDEX = "AdvancedSearchPanel.SelectedTabIndex";
    private static final long serialVersionUID = -4036432653670374380L;
    private final List<SearchColumnPanel> searchColumnPanels = new LinkedList<SearchColumnPanel>();
    private final Map<Component, Component> defaultInputOfComponent = new HashMap<Component, Component>();
    private final Map<JButton, SearchColumnPanel> searchPanelOfRemoveButton = new HashMap<JButton, SearchColumnPanel>();
    private String searchName = JptBundle.INSTANCE.getString("AdvancedSearchPanel.UndefinedName");
    private boolean isSavedSearch;
    private boolean columnRemoved;
    private boolean customSqlChanged;
    private final transient ListenerSupport<NameListener> ls = new ListenerSupport<NameListener>();
    private final JPanel panelPadding = new JPanel();

    public interface NameListener {
        void nameChanged(String newName);
    }

    public AdvancedSearchPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        panelPadding.setSize(10, 2);
        listenToSearchPanels();
        setAutocomplete();
        panelKeywordsInput.setBundleKeyPosRenameDialog("AdvancedSearchPanel.Keywords.RenameDialog.Pos");
        setDefaultInputOfComponent();
        setFocusToInputInTab(tabbedPane.getSelectedComponent());
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setAutocomplete() {
        if (UserSettings.INSTANCE.isAutocomplete()) {
            panelKeywordsInput.setAutocomplete();
        }
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

            SearchColumnPanel scp = searchColumnPanels.get(0);

            defaultInputOfComponent.put(panelSimpleSql, scp.getTextFieldValue());
        }
    }

    public void empty() {
        removeAllColumns();
        emptyKeywordsPanel();
        textAreaCustomSqlQuery.setText("");
    }

    private void emptyKeywordsPanel() {
        ListModel model = panelKeywordsInput.getList().getModel();

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
        isSavedSearch = false;
        columnRemoved = false;
        customSqlChanged = false;
        setSearchName(JptBundle.INSTANCE.getString("AdvancedSearchPanel.UndefinedName"));
    }

    @Override
    public void readProperties() {
        if (UserSettings.INSTANCE.getProperties().containsKey(KEY_SELECTED_TAB_INDEX)) {
            tabbedPane.setSelectedIndex(UserSettings.INSTANCE.getSettings().getInt(KEY_SELECTED_TAB_INDEX));
        }
    }

    @Override
    public void writeProperties() {
        UserSettings.INSTANCE.getSettings().set(tabbedPane.getSelectedIndex(), KEY_SELECTED_TAB_INDEX);
        UserSettings.INSTANCE.writeToFile();
    }

    private boolean checkIsSearchValid() {
        if (!checkConsistent()) {
            return false;
        }
        if (existsCustomSqlText()) {
            return true;
        }

        boolean valid = existsKeywords();
        int columnCount = searchColumnPanels.size();
        int index = 0;

        while (!valid && (index < columnCount)) {
            valid = !searchColumnPanels.get(index++).getValue().isEmpty();
        }

        valid = valid && checkBrackets();

        if (!valid) {
            MessageDisplayer.error(this, "AdvancedSearchPanel.Error.InvalidQuery");
        }

        return valid;
    }

    private boolean checkConsistent() {
        if (existsCustomSqlText() && (existsKeywords() || existsSimpleSqlValue())) {
            MessageDisplayer.error(this, "AdvancedSearchPanel.Error.Inconsistent");
            return false;
        }

        return true;
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

    private void search() {
        if (checkIsSearchValid()) {
            ControllerFactory.INSTANCE.getController(ControllerAdvancedSearch.class).actionPerformed(null);
        }
    }

    private void listenToSearchPanels() {
        for (SearchColumnPanel panel : searchColumnPanels) {
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
        panelKeywordsInput.setText(search.getKeywords());

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
        writeProperties();
    }

    private boolean existsSimpleSqlValue() {

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

    private void ensureColumnCount(int count) {
        int currentCount = 0;

        currentCount = searchColumnPanels.size();

        if (currentCount >= count) {
            return;
        }

        for (int i = currentCount; i < count; i++) {
            addColumn();
        }
    }

    private void addColumn() {
        GridBagConstraints gbc =  getColumnGridBagConstraints();
        SearchColumnPanel scPanel = new SearchColumnPanel();

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

            SearchColumnPanel panel = searchPanelOfRemoveButton.get((JButton) evt.getSource());

            if (panel != null) {
                removeColumn(panel);
            }
        }
    }

    private void removeColumn(SearchColumnPanel scPanel) {
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
        if (columnRemoved || columnChanged() || customSqlChanged || panelKeywordsInput.isDirty()) {

            if (MessageDisplayer.confirmYesNo(this, "AdvancedSearchPanel.Confirm.SaveChanges")) {
                save(createSavedSearch(), true);
            }
        }
    }

    private boolean columnChanged() {
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

        String name = SavedSearchesHelper.getNotExistingName(suggestName);

        if ((name != null) &&!name.isEmpty()) {
            SavedSearch savedSearch = createSavedSearch();
            savedSearch.setName(name);
            save(savedSearch, false);
        }
    }

    private void save(SavedSearch savedSearch, boolean update) {
        boolean saved = update
                ? SavedSearchesHelper.update(savedSearch)
                : SavedSearchesHelper.insert(savedSearch);
        
        if (saved) {
            setSearchName(savedSearch.getName());
            setPanelsUnchanged();
            isSavedSearch = true;
        }
    }

    private void setPanelsUnchanged() {
        for (SearchColumnPanel panel : searchColumnPanels) {
            panel.setChanged(false);
        }
    }

    private void setSavedSearchPanels(SavedSearch search) {
        List<SavedSearchPanel> panels = new ArrayList<SavedSearchPanel>();
        int size = searchColumnPanels.size();
        int pIndex = 0;

        for (int index = 0; index < size; index++) {
            SearchColumnPanel panel = searchColumnPanels.get(index);
            SavedSearchPanel savedSearchPanel = panel.getSavedSearchPanel();

            if (savedSearchPanel.hasValue()) {
                savedSearchPanel.setPanelIndex(pIndex++);
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
        List<String> keywords = new ArrayList<String>(listText);

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

        labelInfoKeywords.setText(JptBundle.INSTANCE.getString("AdvancedSearchPanel.labelInfoKeywords.text")); // NOI18N
        labelInfoKeywords.setName("labelInfoKeywords"); // NOI18N

        panelKeywordsInput.setName("panelKeywordsInput"); // NOI18N

        javax.swing.GroupLayout panelKeywordsLayout = new javax.swing.GroupLayout(panelKeywords);
        panelKeywords.setLayout(panelKeywordsLayout);
        panelKeywordsLayout.setHorizontalGroup(
            panelKeywordsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKeywordsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelKeywordsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelKeywordsInput, javax.swing.GroupLayout.DEFAULT_SIZE, 636, Short.MAX_VALUE)
                    .addComponent(labelInfoKeywords, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        panelKeywordsLayout.setVerticalGroup(
            panelKeywordsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKeywordsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInfoKeywords, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelKeywordsInput, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab(JptBundle.INSTANCE.getString("AdvancedSearchPanel.panelKeywords.TabConstraints.tabTitle"), panelKeywords); // NOI18N

        panelSimpleSql.setName("panelSimpleSql"); // NOI18N

        scrollPaneColumns.setBorder(null);
        scrollPaneColumns.setName("scrollPaneColumns"); // NOI18N

        panelColumns.setName("panelColumns"); // NOI18N
        panelColumns.setLayout(new java.awt.GridBagLayout());
        scrollPaneColumns.setViewportView(panelColumns);

        labelInfoDelete.setForeground(new java.awt.Color(0, 0, 255));
        labelInfoDelete.setText(JptBundle.INSTANCE.getString("AdvancedSearchPanel.labelInfoDelete.text")); // NOI18N
        labelInfoDelete.setName("labelInfoDelete"); // NOI18N

        buttonAddColumn.setText(JptBundle.INSTANCE.getString("AdvancedSearchPanel.buttonAddColumn.text")); // NOI18N
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
                    .addComponent(scrollPaneColumns, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 636, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSimpleSqlLayout.createSequentialGroup()
                        .addComponent(labelInfoDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 341, Short.MAX_VALUE)
                        .addComponent(buttonAddColumn)))
                .addContainerGap())
        );
        panelSimpleSqlLayout.setVerticalGroup(
            panelSimpleSqlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSimpleSqlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneColumns, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSimpleSqlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonAddColumn)
                    .addComponent(labelInfoDelete))
                .addContainerGap())
        );

        tabbedPane.addTab(JptBundle.INSTANCE.getString("AdvancedSearchPanel.panelSimpleSql.TabConstraints.tabTitle"), panelSimpleSql); // NOI18N

        panelCustomSql.setName("panelCustomSql"); // NOI18N
        panelCustomSql.setLayout(new java.awt.GridBagLayout());

        labelCustomSqlInfo.setDisplayedMnemonic('k');
        labelCustomSqlInfo.setText(JptBundle.INSTANCE.getString("AdvancedSearchPanel.labelCustomSqlInfo.text")); // NOI18N
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

        tabbedPane.addTab(JptBundle.INSTANCE.getString("AdvancedSearchPanel.panelCustomSql.TabConstraints.tabTitle"), panelCustomSql); // NOI18N

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

        buttonSaveSearch.setText(JptBundle.INSTANCE.getString("AdvancedSearchPanel.buttonSaveSearch.text")); // NOI18N
        buttonSaveSearch.setName("buttonSaveSearch"); // NOI18N
        buttonSaveSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveSearchActionPerformed(evt);
            }
        });

        buttonSaveAs.setText(JptBundle.INSTANCE.getString("AdvancedSearchPanel.buttonSaveAs.text")); // NOI18N
        buttonSaveAs.setName("buttonSaveAs"); // NOI18N
        buttonSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveAsActionPerformed(evt);
            }
        });

        buttonResetColumns.setText(JptBundle.INSTANCE.getString("AdvancedSearchPanel.buttonResetColumns.text")); // NOI18N
        buttonResetColumns.setName("buttonResetColumns"); // NOI18N
        buttonResetColumns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonResetColumnsActionPerformed(evt);
            }
        });

        buttonSearch.setText(JptBundle.INSTANCE.getString("AdvancedSearchPanel.buttonSearch.text")); // NOI18N
        buttonSearch.setName("buttonSearch"); // NOI18N

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
    private org.jphototagger.program.view.panels.EditRepeatableTextEntryPanel panelKeywordsInput;
    private javax.swing.JPanel panelSimpleSql;
    private javax.swing.JScrollPane scrollPaneColumns;
    private javax.swing.JScrollPane scrollPaneCustomSqlQuery;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextArea textAreaCustomSqlQuery;
    // End of variables declaration//GEN-END:variables
}
