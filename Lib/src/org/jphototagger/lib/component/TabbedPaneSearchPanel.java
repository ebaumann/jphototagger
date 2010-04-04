/*
 * @(#)TabbedPaneSearchPanel.java    Created on 2010-02-25
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

package org.jphototagger.lib.component;

import org.jphototagger.lib.generics.Pair;
import org.jphototagger.lib.resource.JslBundle;
import org.jphototagger.lib.util.StringUtil;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;

/**
 * Displays in a list titles of tabbed panes of tabs matching a search word
 * (exactly one word, no phrases are currently supported). If a title is
 * selected, the tab will be displayed.
 * <p>
 * The search word is typed into a text field. All words in <code>JLabel</code>s,
 * <code>JCheckBox</code>es, <strong>JRadioButton</strong>s and
 * <code>TitledBorder</code>s will be grabbed automatically. For every tab
 * additional words can be added through
 * {@link #addSearchWordsTo(java.util.Collection, java.awt.Component)}.
 * <p>
 * <em>All labels, checkboxes and radio buttons within a tab have to be
 * horizontally resizable because their content will be replaced temporarily
 * with HTML text to highlight all matching words!</em>
 * <p>
 * Usage: Add this panel to a container with a <code>JTabbedPane</code> through
 * {@link #setParentPane(javax.swing.JTabbedPane)}. If the tabbed pane contains
 * other tabbed panes, they will be recognized too.
 * <p>
 * Bugs: Words in HTML texts containing tags will not be highlighted and HTML
 * tag names will be added as search words too.
 *
 * @author  Elmar Baumann
 */
public class TabbedPaneSearchPanel extends javax.swing.JPanel
        implements ComponentListener, DocumentListener {
    private static final long                        serialVersionUID =
        1668377464478972172L;
    private final Map<String, Collection<Component>> tabsOfWord       =
        new HashMap<String, Collection<Component>>();
    private final Collection<Component> tabsCache      =
        new ArrayList<Component>();
    private final Map<Object, String>   originalTextOf = new HashMap<Object,
                                                             String>();
    private final Map<Component, JTabbedPane> paneOfTab =
        new HashMap<Component, JTabbedPane>();
    private final Map<Component, String> titleOfTab =
        new LinkedHashMap<Component, String>();
    private final DefaultListModel listModel = new DefaultListModel();
    private Component              visibleComponent;
    private static final String    HL_SPAN_START =
        "<span style=\"color:#000000; background:#ffff000;\">";
    private static final String HL_SPAN_END = "</span>";

    public TabbedPaneSearchPanel() {
        initComponents();
        textFieldSearch.getDocument().addDocumentListener(this);
    }

    private void clear() {
        listenToTabs(false);
        tabsOfWord.clear();
        paneOfTab.clear();
        titleOfTab.clear();
        listModel.clear();
        originalTextOf.clear();
        tabsCache.clear();
        textFieldSearch.setText("");
    }

    public void setParentPane(JTabbedPane pane) {
        if (pane == null) {
            throw new NullPointerException("pane == null");
        }

        clear();
        traversePane(pane);
        setAllTabsToListAndCache();
        visibleComponent = pane.getSelectedComponent();
        traverseGetOriginalTexts(visibleComponent);
        listenToTabs(true);
    }

    private void selectTab(Component tab) {
        JTabbedPane pane = paneOfTab.get(tab);

        if (pane != null) {
            pane.setSelectedComponent(tab);
            selectPaneOfPane(pane);    // calls this: recursive
        }
    }

    private void selectPaneOfPane(JTabbedPane pane) {
        Component tab = getTabOfPane(pane);

        if (tab != null) {
            selectTab(tab);
        }
    }

    private Component getTabOfPane(JTabbedPane pane) {
        Component parent     = pane.getParent();
        Component prevParent = pane;

        while (parent != null) {
            if (parent instanceof JTabbedPane) {
                return prevParent;
            }

            prevParent = parent;
            parent     = parent.getParent();
        }

        return null;
    }

    private void traversePane(JTabbedPane pane) {
        int tabCount = pane.getTabCount();

        for (int i = 0; i < tabCount; i++) {
            Component tab      = pane.getComponentAt(i);
            String    tabTitle = pane.getTitleAt(i);

            titleOfTab.put(tab, tabTitle);
            paneOfTab.put(tab, pane);
            setWordsOf(tabTitle, tab);

            if (tab instanceof Container) {
                traverseContainer((Container) tab, tab);
            } else {
                traverseComponent(tab, pane);
            }
        }
    }

    private void traverseContainer(Container container, Component parentTab) {
        int componentCount = container.getComponentCount();

        for (int i = 0; i < componentCount; i++) {
            Component component = container.getComponent(i);

            if (component instanceof JTabbedPane) {
                traversePane((JTabbedPane) component);
            } else if (component instanceof Container) {
                traverseComponent(component, parentTab);
                traverseContainer((Container) component, parentTab);    // recursive
            } else {
                traverseComponent(component, parentTab);
            }
        }
    }

    private void traverseComponent(Component component, Component parentTab) {
        if (component instanceof JLabel) {
            setWordsOf((JLabel) component, parentTab);
        } else if (component instanceof JCheckBox) {
            setWordsOf((JCheckBox) component, parentTab);
        } else if (component instanceof JRadioButton) {
            setWordsOf((JRadioButton) component, parentTab);
        }

        if (component instanceof JComponent) {
            Border border = ((JComponent) component).getBorder();

            if (border instanceof TitledBorder) {
                setWordsOf((TitledBorder) border, parentTab);
            }
        }
    }

    private void setWordsOf(TitledBorder tBorder, Component tabOfBorder) {
        setWordsOf(tBorder.getTitle(), tabOfBorder);
    }

    private void setWordsOf(JRadioButton radioButton,
                            Component tabOfRadioButton) {
        setWordsOf(radioButton.getText(), tabOfRadioButton);
    }

    private void setWordsOf(JCheckBox checkBox, Component tabOfCheckBox) {
        setWordsOf(checkBox.getText(), tabOfCheckBox);
    }

    private void setWordsOf(JLabel label, Component tabOfLabel) {
        setWordsOf(label.getText(), tabOfLabel);
    }

    private void setWordsOf(String wordsString, Component parentTab) {
        for (String word : StringUtil.getWordsOf(wordsString)) {
            setTabOfWord(word.toLowerCase(), parentTab);
        }
    }

    private void setTabOfWord(String word, Component parentTab) {
        Collection<Component> parentTabs = tabsOfWord.get(word);

        if (parentTabs == null) {
            parentTabs = new ArrayList<Component>();
        }

        if (!parentTabs.contains(parentTab)) {
            parentTabs.add(parentTab);
        }

        tabsOfWord.put(word, parentTabs);
    }

    /**
     * Adds for a tab search words which are not contained as text in the tab
     * itself.
     *
     * @param words additional words <em>in lowercase</em>
     * @param tab   tab
     */
    public void addSearchWordsTo(Collection<? extends String> words,
                                 Component tab) {
        if (words == null) {
            throw new NullPointerException("words == null");
        }

        if (tab == null) {
            throw new NullPointerException("tab == null");
        }
        
        for (String word : words) {
            setTabOfWord(word, tab);
        }
    }

    public void focusSearchTextfield() {
        textFieldSearch.requestFocusInWindow();
    }

    private void search() {
        setToListTabsOfWord(textFieldSearch.getText().trim().toLowerCase());
    }

    private void setToListTabsOfWord(String word) {
        if (word.isEmpty()) {
            setAllTabsToListAndCache();
        } else {
            listModel.clear();

            for (Component tab : findTabs(word)) {
                listModel.addElement(tab);
            }
        }
    }

    private Collection<Component> findTabs(String word) {
        Collection<Component> tabs = new HashSet<Component>();

        for (String wd : tabsOfWord.keySet()) {
            if (wd.contains(word)) {
                tabs.addAll(tabsOfWord.get(wd));
            }
        }

        return tabs;
    }

    private void setAllTabsToListAndCache() {
        listModel.clear();

        for (Component tab : titleOfTab.keySet()) {
            listModel.addElement(tab);
            tabsCache.add(tab);
        }
    }

    private void setTabOfSelectedListValue() {
        Object value = list.getSelectedValue();

        if (value instanceof Component) {
            selectTab((Component) value);
        }
    }

    private void listenToTabs(boolean listen) {
        for (Component component : tabsCache) {
            if (listen) {
                component.addComponentListener(this);
            } else {
                component.removeComponentListener(this);
            }
        }
    }

    @Override
    public void insertUpdate(DocumentEvent evt) {
        startSearch();
    }

    @Override
    public void removeUpdate(DocumentEvent evt) {
        startSearch();
    }

    @Override
    public void changedUpdate(DocumentEvent evt) {
        startSearch();
    }

    private void startSearch() {
        search();

        if (visibleComponent != null) {
            setSearchWordHighlighted(visibleComponent);
        }
    }

    @Override
    public void componentResized(ComponentEvent evt) {

        // ignore
    }

    @Override
    public void componentMoved(ComponentEvent evt) {

        // ignore
    }

    @Override
    public void componentShown(ComponentEvent evt) {
        visibleComponent = evt.getComponent();
        traverseGetOriginalTexts(visibleComponent);
        setSearchWordHighlighted(evt.getComponent());
    }

    @Override
    public void componentHidden(ComponentEvent evt) {
        setOriginalTexts();
        originalTextOf.clear();    // release memory
        visibleComponent = null;
    }

    private void setOriginalTexts() {
        for (Object o : originalTextOf.keySet()) {
            String text = originalTextOf.get(o);

            if (o instanceof JLabel) {
                ((JLabel) o).setText(text);
            } else if (o instanceof JCheckBox) {
                ((JCheckBox) o).setText(text);
            } else if (o instanceof JRadioButton) {
                ((JRadioButton) o).setText(text);
            } else if (o instanceof TitledBorder) {
                ((TitledBorder) o).setTitle(text);
            }
        }
    }

    private void traverseGetOriginalTexts(Component component) {
        if (component instanceof Container) {
            Container container = (Container) component;

            getOriginalTextsOf(container);

            int compCount = container.getComponentCount();

            for (int i = 0; i < compCount; i++) {
                traverseGetOriginalTexts(container.getComponent(i));    // recursive
            }
        } else {
            getOriginalTextsOf(component);
        }
    }

    private void getOriginalTextsOf(Component component) {
        if (component instanceof JLabel) {
            originalTextOf.put(component, ((JLabel) component).getText());
        } else if (component instanceof JCheckBox) {
            originalTextOf.put(component, ((JCheckBox) component).getText());
        } else if (component instanceof JRadioButton) {
            originalTextOf.put(component, ((JRadioButton) component).getText());
        }
    }

    private void setSearchWordHighlighted(Component component) {
        setOriginalTexts();

        String word = textFieldSearch.getText().trim();

        if (word.isEmpty()) {
            return;
        }

        traverseHighlight(component, word);
    }

    private void traverseHighlight(Component component, String word) {
        if (component instanceof Container) {
            Container container = (Container) component;

            highlightIn(container, word);

            int compCount = container.getComponentCount();

            for (int i = 0; i < compCount; i++) {
                traverseHighlight(container.getComponent(i), word);    // recursive
            }
        } else {
            highlightIn(component, word);
        }
    }

    private void highlightIn(Component component, String word) {
        if (component instanceof JLabel) {
            JLabel lbl = (JLabel) component;

            if (lbl.getIcon() == null) {    // Icon + HTML-Code -> only spans displayed?
                lbl.setText(getHighlighted(lbl.getText().trim(), word));
            }
        } else if (component instanceof JCheckBox) {
            JCheckBox cb = (JCheckBox) component;

            cb.setText(getHighlighted(cb.getText().trim(), word));
        } else if (component instanceof JRadioButton) {
            JRadioButton rb = (JRadioButton) component;

            rb.setText(getHighlighted(rb.getText().trim(), word));
        }
    }

    private String getHighlighted(String s, String word) {
        if (isHtml(s)) {
            return getHighlightedHtml(s, word);
        } else {
            return "<html>" + getHighlightedIn(s, word) + "</html>";
        }
    }

    private boolean isHtml(String s) {
        return (s.length() > 5)
               && s.substring(1, 5).toLowerCase().equals("html");    // expected trimmed
    }

    private String getHighlightedIn(String s, String word) {
        List<Integer> indices = getHighlightStartIndices(s, word);

        if (indices.isEmpty()) {
            return s;
        }

        StringBuilder sb           = new StringBuilder();
        int           wLen         = word.length();
        int           iSize        = indices.size();
        int           beforeWIndex = 0;

        for (int i = 0; i < iSize; i++) {
            int wIndex = indices.get(i);

            if (beforeWIndex < wIndex) {
                sb.append(s.substring(beforeWIndex, wIndex));
            }

            beforeWIndex = wIndex + wLen;
            sb.append(HL_SPAN_START);
            sb.append(s.substring(wIndex, wIndex + wLen));
            sb.append(HL_SPAN_END);
        }

        if (beforeWIndex < s.length()) {
            sb.append(s.substring(beforeWIndex));
        }

        return sb.toString();
    }

    private List<Integer> getHighlightStartIndices(String s, String word) {
        String        sLower  = s.toLowerCase();
        String        wLower  = word.toLowerCase();
        int           sLen    = sLower.length();
        int           wLen    = word.length();
        List<Integer> indices = new ArrayList<Integer>();
        int           index   = sLower.indexOf(wLower, 0);

        while (index >= 0) {
            indices.add(index);
            index = (index + wLen < sLen)
                    ? sLower.indexOf(wLower, index + wLen)
                    : -1;
        }

        return indices;
    }

    private String getHighlightedHtml(String s, String word) {
        StringBuilder                sb          = new StringBuilder();
        List<Pair<Integer, Integer>> validRanges = getValidRangesInHtml(s);
        int                          bIndexText  = 0;
        int                          eIndexText  = 0;
        int                          bIndexHtml  = 0;
        int                          eIndexHtml  = 0;

        if (validRanges.isEmpty()) {
            return s;
        }

        for (Pair<Integer, Integer> range : validRanges) {
            eIndexHtml = range.getFirst();

            if (StringUtil.isSubstring(s, bIndexHtml, eIndexHtml)) {
                sb.append(s.substring(bIndexHtml, eIndexHtml));
            }

            bIndexText = range.getFirst();
            eIndexText = range.getSecond() + 1;
            bIndexHtml = eIndexText;

            if (StringUtil.isSubstring(s, bIndexText, eIndexText)) {
                sb.append(getHighlightedIn(s.substring(bIndexText, eIndexText),
                                           word));
            }
        }

        if (eIndexText < s.length() - 1) {
            sb.append(s.substring(eIndexText));
        }

        return sb.toString();
    }

    private List<Pair<Integer, Integer>> getValidRangesInHtml(String s) {
        List<Pair<Integer, Integer>> ranges = new ArrayList<Pair<Integer,
                                                  Integer>>();
        int len   = s.length();
        int start = 0;

        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);

            if ((c == '<') && (i > 0) && (start < len)) {
                ranges.add(new Pair<Integer, Integer>(start, i - 1));
            } else if (c == '>') {
                start = i + 1;
            }
        }

        return ranges;
    }

    private class TabTitleRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = -7474877709980199802L;

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list,
                               value, index, isSelected, cellHasFocus);

            if (value instanceof Component) {
                String tabTitle = titleOfTab.get((Component) value);

                if (tabTitle != null) {
                    label.setText(tabTitle);
                }
            }

            return label;
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
        labelSearch     = new javax.swing.JLabel();
        textFieldSearch = new javax.swing.JTextField();
        scrollPane      = new javax.swing.JScrollPane();
        list            = new javax.swing.JList();
        labelSearch.setDisplayedMnemonic('s');
        labelSearch.setLabelFor(textFieldSearch);
        labelSearch.setText(
            JslBundle.INSTANCE.getString(
                "TabbedPaneSearchPanel.labelSearch.text"));    // NOI18N
        list.setModel(listModel);
        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new TabTitleRenderer());
        list.addListSelectionListener(
            new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listValueChanged(evt);
            }
        });
        scrollPane.setViewportView(list);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);

        this.setLayout(layout);
        layout.setHorizontalGroup(layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addComponent(labelSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement
                    .RELATED)
                        .addComponent(textFieldSearch,
                                      javax.swing.GroupLayout.DEFAULT_SIZE, 77,
                                      Short.MAX_VALUE))
                                          .addComponent(scrollPane,
                                              javax.swing.GroupLayout
                                                  .DEFAULT_SIZE, 136,
                                                      Short.MAX_VALUE));
        layout.setVerticalGroup(layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout
                    .createParallelGroup(javax.swing.GroupLayout.Alignment
                        .BASELINE).addComponent(labelSearch)
                            .addComponent(textFieldSearch, javax.swing
                                .GroupLayout.PREFERRED_SIZE, javax.swing
                                .GroupLayout.DEFAULT_SIZE, javax.swing
                                .GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle
                                        .ComponentPlacement.RELATED)
                                            .addComponent(scrollPane, javax
                                                .swing.GroupLayout
                                                    .DEFAULT_SIZE, 180, Short
                                                        .MAX_VALUE)));
    }    // </editor-fold>//GEN-END:initComponents

    private void listValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listValueChanged
        if (!evt.getValueIsAdjusting()) {
            setTabOfSelectedListValue();
        }
    }//GEN-LAST:event_listValueChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel      labelSearch;
    private javax.swing.JList       list;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField  textFieldSearch;

    // End of variables declaration//GEN-END:variables
}
