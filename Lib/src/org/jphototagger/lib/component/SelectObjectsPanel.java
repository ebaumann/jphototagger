/*
 * @(#)SelectObjectsPanel.java    Created on 2010-03-03
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * Panel to select objects from a collection of objects.
 * <p>
 * Contains {@link JCheckBox}es to select the objects.
 * <p>
 * <em>If a GUI builder sets a layout to this panel, ensure that it's
 * {@link GridBagLayout}!</em>
 *
 * @author  Elmar Baumann
 */
public final class SelectObjectsPanel extends JPanel implements ActionListener {
    private static final long            serialVersionUID =
        -273547657004917436L;
    private static final String          DELIM_SEL_INDICES = ",";
    private final List<JCheckBox>        checkBoxes =
        new ArrayList<JCheckBox>();
    private final Map<JCheckBox, Object> objectOfCheckBox =
        new LinkedHashMap<JCheckBox, Object>();
    private final Set<SelectionListener> listeners =
        new CopyOnWriteArraySet<SelectionListener>();
    private String     keySelIndices;
    private Properties properties;
    private int        componentCount;

    public SelectObjectsPanel() {
        init();
    }

    /**
     * Properties for setting and getting selected indices.
     * <p>
     * Usage only, if the count and order of objects is always the same. The
     * order is defined through the order of
     * {@link #add(java.lang.Object, java.lang.String)} calls.
     *
     * @param properties    properties
     * @param keySelIndices key in <code>properties</code> for setting and
     *                      getting the selected indices
     * @see                 #applyPropertiesSelectedIndices()
     */
    public SelectObjectsPanel(Properties properties, String keySelIndices) {
        if (properties == null) {
            throw new NullPointerException("properties == null");
        }

        if (keySelIndices == null) {
            throw new NullPointerException("keySelIndices == null");
        }

        this.properties    = properties;
        this.keySelIndices = keySelIndices;
        init();
    }

    private void init() {
        setLayout(new GridBagLayout());
    }

    public static class SelectionEvent {
        private final Object selectedObject;
        private final int    selectionCount;

        public SelectionEvent(Object selectedObject, int selectionCount) {
            this.selectedObject = selectedObject;
            this.selectionCount = selectionCount;
        }

        public Object getSelectedObject() {
            return selectedObject;
        }

        public int getSelectionCount() {
            return selectionCount;
        }
    }


    /**
     * A selection listener will be notified, if the user did select an object.
     */
    public interface SelectionListener {
        public void objectSelected(SelectionEvent evt);
    }


    public void addSelectionListener(SelectionListener listener) {
        listeners.add(listener);
    }

    public void removeSelectionListener(SelectionListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(Object selObject) {
        SelectionEvent evt = new SelectionEvent(selObject, getSelectionCount());

        for (SelectionListener listener : listeners) {
            listener.objectSelected(evt);
        }
    }

    /**
     * Removes all components and clears the component count. Do <em>not</em>
     * call {@link #removeAll()}!
     */
    public void clear() {
        removeAll();
        checkBoxes.clear();
        objectOfCheckBox.clear();
        componentCount = -1;
    }

    /**
     * Returns the "real" object count (i.e. <em>not</em> the object count set
     * via {@link #setObjectCount(int)}).
     *
     * @return object count
     */
    public int getObjectCount() {
        return checkBoxes.size();
    }

    /**
     * Returns the count of selected objects.
     *
     * @return selection count
     */
    public int getSelectionCount() {
        int count = 0;

        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                count++;
            }
        }

        return count;
    }

    /**
     * Sets the properties for setting and getting selected indices.
     * <p>
     * Usage only, if the count and order of objects is always the same. The
     * order is defined through the order of
     * {@link #add(java.lang.Object, java.lang.String)} calls.
     *
     * @param properties    properties
     * @param keySelIndices key in <code>properties</code> for setting and
     *                      getting the selected indices
     * @see                 #applyPropertiesSelectedIndices()
     */
    public void setProperties(Properties properties, String keySelIndices) {
        this.properties    = properties;
        this.keySelIndices = keySelIndices;
    }

    public void add(Object object, String displayName) {
        if (object == null) {
            throw new NullPointerException("object == null");
        }

        if (displayName == null) {
            throw new NullPointerException("displayName == null");
        }

        addCheckBox(object, displayName);
    }

    /**
     * Sets, how often {@link #add(java.lang.Object, java.lang.String)} will be
     * called.
     * <p>
     * If set, the layout manager ensures, that the check boxes are not
     * centered within this panel but aligned to the top left edge of this
     * panel.
     * <p>
     * <em>Do not call this method, if add() may be called later!</em>
     *
     * @param count
     */
    public void setObjectCount(int count) {
        componentCount = count;
    }

    private void addCheckBox(Object object, String displayName) {
        JCheckBox checkBox = new JCheckBox(displayName);

        checkBox.addActionListener(this);
        checkBoxes.add(checkBox);
        objectOfCheckBox.put(checkBox, object);
        add(checkBox, getGbcCheckBox());
    }

    private GridBagConstraints getGbcCheckBox() {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx   = GridBagConstraints.REMAINDER;
        gbc.gridy   = GridBagConstraints.RELATIVE;
        gbc.anchor  = GridBagConstraints.NORTHWEST;
        gbc.fill    = GridBagConstraints.NONE;
        gbc.weightx = 1;

        if ((componentCount > 0) && (checkBoxes.size() == componentCount)) {
            gbc.weighty = 1;
        }

        return gbc;
    }

    public List<Object> getSelectedObjects() {
        List<Object> selObjects =
            new ArrayList<Object>(objectOfCheckBox.size());

        for (JCheckBox checkBox : objectOfCheckBox.keySet()) {
            if (checkBox.isSelected()) {
                selObjects.add(objectOfCheckBox.get(checkBox));
            }
        }

        return selObjects;
    }

    /**
     * Selects all check boxes if their index is in the properties set via
     * {@link SelectObjectsPanel#SelectObjectsPanel(Properties, String)}
     */
    public void applyPropertiesSelectedIndices() {
        if ((properties == null) ||!properties.containsKey(keySelIndices)) {
            return;
        }

        StringTokenizer st =
            new StringTokenizer(properties.getProperty(keySelIndices),
                                DELIM_SEL_INDICES);
        int[] indices = new int[st.countTokens()];
        int   i       = 0;

        while (st.hasMoreElements()) {
            try {
                indices[i++] = Integer.parseInt(st.nextToken());
            } catch (NumberFormatException ex) {
                Logger.getLogger(SelectObjectsPanel.class.getName()).log(
                    Level.SEVERE, null, ex);

                return;
            }
        }

        int size = checkBoxes.size();

        for (int index : indices) {
            if ((index >= 0) && (index < size)) {
                checkBoxes.get(index).setSelected(true);
            }
        }
    }

    /**
     * Selects or unselects all objects.
     *
     * @param selected true if select all, false if unselect all
     */
    public void setSelectedAll(boolean selected) {
        for (JCheckBox checkBox : checkBoxes) {
            checkBox.setSelected(selected);
        }

        writeSelectedIndicesToProperties();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();

        if (source instanceof JCheckBox) {
            writeSelectedIndicesToProperties();
            notifyListeners(objectOfCheckBox.get((JCheckBox) source));
        }
    }

    private void writeSelectedIndicesToProperties() {
        if (properties == null) {
            return;
        }

        int           size = checkBoxes.size();
        StringBuilder sb   = new StringBuilder();

        for (int i = 0; i < size; i++) {
            if (checkBoxes.get(i).isSelected()) {
                sb.append((i == 0)
                          ? ""
                          : DELIM_SEL_INDICES);
                sb.append(Integer.toString(i));
            }
        }

        properties.setProperty(keySelIndices, sb.toString());
    }
}
