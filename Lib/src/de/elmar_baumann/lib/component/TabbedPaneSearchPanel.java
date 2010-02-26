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
package de.elmar_baumann.lib.component;

import de.elmar_baumann.lib.resource.JslBundle;
import de.elmar_baumann.lib.util.StringUtil;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-02-25
 */
public class TabbedPaneSearchPanel extends javax.swing.JPanel implements DocumentListener {

    private static final long                        serialVersionUID = 1668377464478972172L;
    private final Map<String, Collection<Component>> tabsOfWord       = new HashMap<String, Collection<Component>>();
    private final Map<Component, JTabbedPane>        paneOfTab        = new HashMap<Component, JTabbedPane>();
    private final Map<Component, String>             titleOfTab       = new LinkedHashMap<Component, String>();
    private final DefaultListModel                   listModel        = new DefaultListModel();

    public TabbedPaneSearchPanel() {
        initComponents();
        textFieldSearch.getDocument().addDocumentListener(this);
    }

    private void clear() {
        tabsOfWord.clear();
        paneOfTab.clear();
        titleOfTab.clear();
        listModel.clear();
        textFieldSearch.setText("");
    }

    public void setParentPane(JTabbedPane pane) {
        clear();
        traversePane(pane);
        setAllTabsToList();
    }

    private void selectTab(Component tab) {
        JTabbedPane pane = paneOfTab.get(tab);
        if (pane != null) {
            pane.setSelectedComponent(tab);
            selectPaneOfPane(pane); // calls this: recursive
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
            Component tab = pane.getComponentAt(i);
            String tabTitle = pane.getTitleAt(i);
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
                traverseContainer((Container) component, parentTab); // recursive
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

    private void setWordsOf(JRadioButton radioButton, Component tabOfRadioButton) {
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

    public void addSearchWordsTo(Collection<? extends String> words, Component tab) {
        for (String word : words) {
            setTabOfWord(word, tab);
        }
    }

    private void search() {
        setToListTabsOfWord(textFieldSearch.getText().trim().toLowerCase());
    }

    private void setToListTabsOfWord(String word) {
        if (word.isEmpty()) {
            setAllTabsToList();
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

    private void setAllTabsToList() {
        listModel.clear();
        for (Component tab : titleOfTab.keySet()) {
            listModel.addElement(tab);
        }
    }

    private void setTabOfSelectedListValue() {
        Object value = list.getSelectedValue();
        if (value instanceof Component) {
            selectTab((Component) value);
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        search();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        search();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        search();
    }

    private class TabTitleRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = -7474877709980199802L;

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Component) {
                String tabTitle = titleOfTab.get((Component) value);
                if (tabTitle != null) {
                    label.setText(tabTitle);
                }
            }

            return label;
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

        labelSearch = new javax.swing.JLabel();
        textFieldSearch = new javax.swing.JTextField();
        scrollPane = new javax.swing.JScrollPane();
        list = new javax.swing.JList();

        labelSearch.setText(JslBundle.INSTANCE.getString("TabbedPaneSearchPanel.labelSearch.text")); // NOI18N

        list.setModel(listModel);
        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new TabTitleRenderer());
        list.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listValueChanged(evt);
            }
        });
        scrollPane.setViewportView(list);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(labelSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE))
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelSearch)
                    .addComponent(textFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void listValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listValueChanged
        if (!evt.getValueIsAdjusting()) {
            setTabOfSelectedListValue();
        }
    }//GEN-LAST:event_listValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel labelSearch;
    private javax.swing.JList list;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField textFieldSearch;
    // End of variables declaration//GEN-END:variables
}
