package org.jphototagger.program.module.search;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.search.SearchComponent;
import org.jphototagger.domain.metadata.selections.AutoCompleteDataOfMetaDataValue;
import org.jphototagger.domain.metadata.selections.FastSearchMetaDataValues;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.repository.FindRepository;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.repository.event.xmp.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.ThumbnailsDisplayer;
import org.jphototagger.domain.thumbnails.event.ThumbnailsChangedEvent;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.ImageTextArea;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.Autocomplete;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.ListUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.swing.util.TreeUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.misc.AutocompleteUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.jphototagger.resources.Icons;
import org.jphototagger.resources.Images;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = SearchComponent.class)
public class FastSearchPanel extends PanelExt implements ActionListener, SearchComponent {

    private static final long serialVersionUID = 1L;
    private static final String DELIMITER_SEARCH_WORDS = ";";
    private final Map<JTree, List<TreePath>> selectedTreePaths = new HashMap<>();
    private final Map<JList<?>, List<?>> selectedListValues = new HashMap<>();
    private final ButtonRestoreDisabler buttonRestoreDisabler;
    private final FindRepository findRepo = Lookup.getDefault().lookup(FindRepository.class);
    private final SelectSearchTextAreaAction selectSearchTextAreaAction = new SelectSearchTextAreaAction();
    private final Autocomplete autocomplete;
    private boolean isAutocomplete;

    public FastSearchPanel() {
        initComponents();
        if (getPersistedAutocomplete()) {
            autocomplete = new Autocomplete(isAutocompleteFastSearchIgnoreCase());
            autocomplete.setTransferFocusForward(false);
        } else {
            autocomplete = null;
            isAutocomplete = false;
        }
        buttonRestoreDisabler = new ButtonRestoreDisabler();
        postInitComponents();
    }

    private void postInitComponents() {
        setAutocomplete(true);
        initTextArea();
        MnemonicUtil.setMnemonics(this);
        toggleDisplaySearchButton();
        listen();
    }

    private void initTextArea() {
        Border border = UIManager.getBorder("TextField.border");
        textAreaSearch.setBorder(border == null
                ? BorderFactory.createLineBorder(Color.BLACK)
                : border);
        Font font = UIManager.getFont("TextField.font");
        if (font != null) {
            textAreaSearch.setFont(font);
        }
        ((ImageTextArea) textAreaSearch).setImage(Images.getLocalizedImage("/org/jphototagger/resources/images/textfield_search.png"));
        ((ImageTextArea) textAreaSearch).setConsumeEnter(true);
    }

    private boolean isAutocompleteFastSearchIgnoreCase() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(DomainPreferencesKeys.KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE)
                : true;
    }

    private boolean getPersistedAutocomplete() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                : true;
    }

    private void listen() {
        AnnotationProcessor.process(this);
        textAreaSearch.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    search();
                }
            }
        });
        searchButton.addActionListener(this);
        fastSearchComboBox.addActionListener(this);
        AnnotationProcessor.process(this);
    }

    public void setAutocomplete(boolean ac) {
        if (autocomplete == null) {
            return;
        }
        isAutocomplete = ac;
        if (ac) {
            decorateTextFieldSearch();
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (isAutocomplete && (evt.getSource() == fastSearchComboBox) && (fastSearchComboBox.getSelectedIndex() >= 0)) {
            decorateTextFieldSearch();
        } else if (evt.getSource() == searchButton) {
            search();
        }
    }

    private void decorateTextFieldSearch() {
        if (autocomplete == null || !getPersistedAutocomplete()) {
            return;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                autocomplete.decorate(textAreaSearch, isSearchAllDefinedMetaDataValues()
                        ? AutoCompleteDataOfMetaDataValue.INSTANCE.getFastSearchData().get()
                        : AutoCompleteDataOfMetaDataValue.INSTANCE.get(getSearchMetaDataValue()).get(), true);
            }
        }, "JPhotoTagger: Updating autocomplete for search text field").start();
    }

    private void clearSelection() {
        setSelectedTreePaths(TreeUtil.clearSelection(GUI.getAppPanel().getSelectionTrees()));
        setSelectedListValues(ListUtil.clearSelection(GUI.getAppPanel().getSelectionLists()));
        buttonRestoreSelection.setEnabled(itemsWereSelected());
    }

    private boolean itemsWereSelected() {
        synchronized (selectedTreePaths) {
            if (!selectedTreePaths.isEmpty()) {
                return true;
            }
        }
        synchronized(selectedListValues) {
            if (!selectedListValues.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void setSelectedTreePaths(Map<JTree, List<TreePath>> selectedTreePaths) {
        synchronized (this.selectedTreePaths) {
            this.selectedTreePaths.clear();
            this.selectedTreePaths.putAll(selectedTreePaths);
        }
    }

    private void setSelectedListValues(Map<JList<?>, List<?>> selectedListIndices) {
        synchronized(this.selectedListValues) {
            this.selectedListValues.clear();
            this.selectedListValues.putAll(selectedListIndices);
        }
    }

    private void restoreSelection() {
        synchronized (selectedTreePaths) {
            for (JTree tree : selectedTreePaths.keySet()) {
                List<TreePath> paths = selectedTreePaths.get(tree);
                tree.setSelectionPaths(paths.toArray(new TreePath[paths.size()]));
            }
        }
        synchronized(selectedListValues) {
            for (JList<?> list : selectedListValues.keySet()) {
                ListUtil.setSelectedValues(list, selectedListValues.get(list));
            }
        }
    }

    private void search() {
        search(textAreaSearch.getText());
    }

    private void search(final String searchText) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            private final ImageFilesRepository imageFileRepo = Lookup.getDefault().lookup(ImageFilesRepository.class);

            @Override
            public void run() {
                String userInput = searchText.trim();
                if (!userInput.isEmpty()) {
                    WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
                    waitDisplayer.show();
                    clearSelection();
                    List<File> imageFiles = searchFiles(userInput);
                    if (imageFiles != null) {
                        setTitle(userInput);
                        ThumbnailsDisplayer tDisplayer = Lookup.getDefault().lookup(ThumbnailsDisplayer.class);
                        tDisplayer.displayFiles(imageFiles, OriginOfDisplayedThumbnails.FILES_FOUND_BY_FAST_SEARCH);
                    }
                    waitDisplayer.hide();
                }
            }

            private void setTitle(String userInput) {
                String title = Bundle.getString(FastSearchPanel.class, "FastSearchController.AppFrame.Title.FastSearch", userInput);
                MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
                mainWindowManager.setMainWindowTitle(title);
            }

            private List<File> searchFiles(String userInput) {
                if (isSearchAllDefinedMetaDataValues()) {
                    return findRepo.findImageFilesLikeOr(FastSearchMetaDataValues.get(), userInput);
                } else {
                    List<String> searchWords = getSearchWords(userInput);
                    MetaDataValue searchValue = getSearchMetaDataValue();
                    if (searchValue == null) {
                        return null;
                    }
                    boolean isKeywordSearch = searchValue.equals(XmpDcSubjectsSubjectMetaDataValue.INSTANCE);
                    if (searchWords.size() == 1) {
                        if (isKeywordSearch) {
                            return new ArrayList<>(imageFileRepo.findImageFilesContainingDcSubject(searchWords.get(0), true));
                        } else {
                            return findRepo.findImageFilesLikeOr(Arrays.asList(searchValue), userInput);
                        }
                    } else if (searchWords.size() > 1) {
                        if (isKeywordSearch) {
                            return new ArrayList<>(imageFileRepo.findImageFilesContainingAllDcSubjects(searchWords));
                        } else {
                            return new ArrayList<>(imageFileRepo.findImageFilesContainingAllWordsInMetaDataValue(searchWords, searchValue));
                        }
                    } else {
                        return null;
                    }
                }
            }
        });
    }

    private List<String> getSearchWords(String userInput) {
        List<String> words = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(userInput, DELIMITER_SEARCH_WORDS);
        while (st.hasMoreTokens()) {
            words.add(st.nextToken().trim());
        }
        return words;
    }

    private MetaDataValue getSearchMetaDataValue() {
        assert !isSearchAllDefinedMetaDataValues() : "More than one search value!";
        if (isSearchAllDefinedMetaDataValues()) {
            return null;
        }
        return (MetaDataValue) fastSearchComboBox.getSelectedItem();
    }

    @EventSubscriber(eventClass = ThumbnailsPanelRefreshEvent.class)
    public void refresh(ThumbnailsPanelRefreshEvent evt) {
        if (textAreaSearch.isEnabled()) {
            OriginOfDisplayedThumbnails origin = evt.getOriginOfDisplayedThumbnails();
            if (origin.isFilesFoundByFastSearch()) {
                search(textAreaSearch.getText());
            }
        }
    }

    @EventSubscriber(eventClass = ThumbnailsChangedEvent.class)
    public void thumbnailsChanged(ThumbnailsChangedEvent evt) {
        OriginOfDisplayedThumbnails origin = evt.getOriginOfDisplayedThumbnails();
        if (!origin.isFilesFoundByFastSearch()) {
            textAreaSearch.setText("");
        }
    }

    private boolean isSearchAllDefinedMetaDataValues() {
        Object selItem = fastSearchComboBox.getSelectedItem();
        return (selItem != null) && selItem.equals(FastSearchComboBoxModel.ALL_DEFINED_META_DATA_VALUES);
    }

    private void addAutocompleteWordsOf(Xmp xmp) {
        if (!getPersistedAutocomplete()) {
            return;
        }
        if (isSearchAllDefinedMetaDataValues()) {
            AutocompleteUtil.addFastSearchAutocompleteData(autocomplete, xmp);
        } else {
            AutocompleteUtil.addAutocompleteData(getSearchMetaDataValue(), autocomplete, xmp);
        }
    }

    private boolean isAutocomplete() {
        return autocomplete != null && getPersistedAutocomplete();
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(XmpInsertedEvent evt) {
        addAutocompleteWordsOf(evt.getXmp());
    }

    @EventSubscriber(eventClass = XmpDeletedEvent.class)
    public void xmpDeleted(XmpDeletedEvent evt) {
        addAutocompleteWordsOf(evt.getXmp());
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(XmpUpdatedEvent evt) {
        addAutocompleteWordsOf(evt.getUpdatedXmp());
    }

    private boolean isDisplaySearchButton() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(AppPreferencesKeys.KEY_UI_DISPLAY_SEARCH_BUTTON)
                ? prefs.getBoolean(AppPreferencesKeys.KEY_UI_DISPLAY_SEARCH_BUTTON)
                : true;
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void userPropertyChanged(PreferencesChangedEvent evt) {
        String key = evt.getKey();
        if (AppPreferencesKeys.KEY_UI_DISPLAY_SEARCH_BUTTON.equals(key)) {
            toggleDisplaySearchButton();
        }
    }

    private void toggleDisplaySearchButton() {
        int zOrder = getComponentZOrder(searchButton);
        boolean containsButton = zOrder >= 0;
        boolean displaySearchButton = isDisplaySearchButton();
        boolean toDo = containsButton && !displaySearchButton || !containsButton && displaySearchButton;
        if (!toDo) {
            return;
        }
        if (displaySearchButton) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.fill = java.awt.GridBagConstraints.BOTH;
            gbc.weighty = 1.0;
            gbc.insets = UiFactory.insets(3, 3, 0, 0);
            add(searchButton, gbc);
        } else {
            remove(searchButton);
        }
        ComponentUtil.forceRepaint(this);
    }

    @Override
    public Action getSelectSearchComponentAction() {
        return selectSearchTextAreaAction;
    }

    @Override
    public Component getSearchComponent() {
        return this;
    }

    private class SelectSearchTextAreaAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private SelectSearchTextAreaAction() {
            super(Bundle.getString(SelectSearchTextAreaAction.class, "SelectSearchTextAreaAction.Name"));
            putValue(SMALL_ICON, Icons.getIcon("icon_search.png"));
            putValue(ACCELERATOR_KEY, KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_F));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            textAreaSearch.requestFocusInWindow();
        }
    }

    private final class ButtonRestoreDisabler implements ListSelectionListener, TreeSelectionListener {

        private ButtonRestoreDisabler() {
            for (JList<?> list : GUI.getAppPanel().getSelectionLists()) {
                list.addListSelectionListener(this);
            }
            for (JTree tree : GUI.getAppPanel().getSelectionTrees()) {
                tree.addTreeSelectionListener(this);
            }
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting() && e.getFirstIndex() >= 0) {
                buttonRestoreSelection.setEnabled(false);
            }
        }

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            if (e.isAddedPath()) {
                buttonRestoreSelection.setEnabled(false);
            }
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        fastSearchComboBox = UiFactory.comboBox();
        textAreaSearch = new ImageTextArea();
        searchButton = UiFactory.button();
        buttonRestoreSelection = UiFactory.button();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        fastSearchComboBox.setModel(new org.jphototagger.program.module.search.FastSearchComboBoxModel());
        fastSearchComboBox.setName("fastSearchComboBox"); // NOI18N
        fastSearchComboBox.setRenderer(new org.jphototagger.program.module.search.FastSearchMetaDataValuesListCellRenderer());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(fastSearchComboBox, gridBagConstraints);

        textAreaSearch.setRows(1);
        textAreaSearch.setMinimumSize(UiFactory.dimension(0, 18));
        textAreaSearch.setName("textAreaSearch"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        add(textAreaSearch, gridBagConstraints);

        searchButton.setText(Bundle.getString(getClass(), "FastSearchPanel.searchButton.text")); // NOI18N
        searchButton.setMargin(UiFactory.insets(1, 1, 1, 1));
        searchButton.setName("searchButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 0, 0);
        add(searchButton, gridBagConstraints);

        buttonRestoreSelection.setIcon(org.jphototagger.resources.Icons.getIcon("icon_restore_selection.png"));
        buttonRestoreSelection.setToolTipText(Bundle.getString(getClass(), "FastSearchPanel.buttonRestoreSelection.toolTipText")); // NOI18N
        buttonRestoreSelection.setEnabled(false);
        buttonRestoreSelection.setMargin(UiFactory.insets(0, 0, 0, 0));
        buttonRestoreSelection.setName("buttonRestoreSelection"); // NOI18N
        buttonRestoreSelection.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRestoreSelectionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 0, 0);
        add(buttonRestoreSelection, gridBagConstraints);
    }//GEN-END:initComponents

    private void buttonRestoreSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRestoreSelectionActionPerformed
        restoreSelection();
        setSelectedTreePaths(Collections.<JTree, List<TreePath>>emptyMap());
        setSelectedListValues(Collections.<JList<?>, List<?>>emptyMap());
    }//GEN-LAST:event_buttonRestoreSelectionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonRestoreSelection;
    private javax.swing.JComboBox<Object> fastSearchComboBox;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextArea textAreaSearch;
    // End of variables declaration//GEN-END:variables
}
