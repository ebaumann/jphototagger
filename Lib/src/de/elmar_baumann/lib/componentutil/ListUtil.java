/*
 * JPhotoTagger tags and finds images fast.
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
package de.elmar_baumann.lib.componentutil;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

/**
 * Utils for {@link javax.swing.JList}.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann
 * @version 2008-10-19
 */
public final class ListUtil {

    /**
     * Clears alls selected items in all lists.
     *
     * @param lists  lists
     */
    public static void clearSelection(List<JList> lists) {
        if (lists == null)
            throw new NullPointerException("lists == null");

        for (JList list : lists) {
            if (!list.isSelectionEmpty()) {
                list.clearSelection();
            }
        }
    }

    /**
     * Returns the first item of a list model with a specific text.
     *
     * @param  text  searched item text
     * @param  model list model
     * @return list item or null if not found
     */
    public static Object getFirstItemWithText(String text,
            DefaultListModel model) {
        if (text == null)
            throw new NullPointerException("text == null");
        if (model == null)
            throw new NullPointerException("model == null");

        Object item = null;
        int size = model.size();
        for (int i = 0; i < size; i++) {
            Object itemOfModel = model.get(i);
            if (text.equals(itemOfModel.toString())) {
                return itemOfModel;
            }
        }
        return item;
    }

    /**
     * Replaces the elements of a list model with elements from a token string.
     *
     * @param str    string
     * @param delim  delimiter between the tokens in <code>string</code>
     * @param model  model
     */
    public static void setToken(String str, String delim, DefaultListModel model) {
        if (str == null)
            throw new NullPointerException("str == null");
        if (delim == null)
            throw new NullPointerException("delim == null");
        if (model == null)
            throw new NullPointerException("model == null");

        StringTokenizer tokenizer = new StringTokenizer(str, delim);
        model.clear();
        while (tokenizer.hasMoreTokens()) {
            model.addElement(tokenizer.nextToken());
        }
    }

    /**
     * Returns a token string with each element in a list model.
     *
     * @param  model  model
     * @param  delim  delimiter between the list elements in the returned string
     * @return token  string
     */
    public static String getTokenString(DefaultListModel model, String delim) {
        if (model == null)
            throw new NullPointerException("model == null");
        if (delim == null)
            throw new NullPointerException("delim == null");

        StringBuffer buffer = new StringBuffer();
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            buffer.append(model.get(i).toString() + (i < size - 1
                    ? delim
                    : ""));
        }
        return buffer.toString();
    }

    /**
     * Inserts an element sorted into a list model.
     *
     * @param <T>
     * @param model      model
     * @param o          object to insert
     * @param c          comparator
     * @param startIndex start index. List items before ignored.
     * @param endIndex   end index. List items behind ignored.
     */
    @SuppressWarnings("unchecked")
    static public <T> void  insertSorted(DefaultListModel model, T o, Comparator<T> c, int startIndex, int endIndex) {
        if (model == null) throw new NullPointerException("model == null");
        if (o == null)     throw new NullPointerException("o == null");
        if (c == null)     throw new NullPointerException("c == null");

        synchronized (model) {
            if (!model.contains(o)) {
                int size = model.getSize();
                boolean inserted = false;
                for (int i = startIndex; !inserted && i <= endIndex && i < size;
                        i++) {
                    if (c.compare(o, (T) model.get(i)) < 0) {
                        model.add(i, o);
                        inserted = true;
                    }
                }
                if (!inserted) {
                    model.addElement(o);
                }
            }
        }
    }

    /**
     * Swaps two list elements into a {@link javax.swing.DefaultListModel}.
     *
     * @param  model               model with the elements
     * @param  indexFirstElement   index of the first element
     * @param  indexSecondElement  index of the second element
     * @return true if swapped
     */
    static public boolean swapModelElements(DefaultListModel model,
            int indexFirstElement,
            int indexSecondElement) {
        if (model == null)
            throw new NullPointerException("model == null");
        int size = model.getSize();
        boolean canSwap = indexFirstElement >= 0 && indexFirstElement < size &&
                indexSecondElement >= 0 && indexSecondElement < size &&
                indexSecondElement != indexFirstElement;
        if (!canSwap) return false;
        Object firstElement = model.get(indexFirstElement);
        Object secondElement = model.get(indexSecondElement);
        model.set(indexFirstElement, secondElement);
        model.set(indexSecondElement, firstElement);
        return true;
    }

    /**
     * Returns wheter a list model contains a string. Uses the
     * {@link java.lang.Object#toString()} method for comparison.
     *
     * @param model   model
     * @param string  string to find
     * @return        true if the model contains that string
     */
    public static boolean containsString(ListModel model, String string) {
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            Object o = model.getElementAt(i);
            if (o != null && o.toString().equals(string)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the item index below a mouse position got by a mouse event.
     *
     * @param  e mouse event within a {@link JList}
     * @return   item index below the mouse position or -1 if below the mouse
     *           position isn't an item
     */
    public static int getItemIndex(MouseEvent e) {
        Object source = e.getSource();
        if (source instanceof JList) {
            int mousePosX = e.getX();
            int mousePosY = e.getY();
            return ((JList) source).locationToIndex(
                    new Point(mousePosX, mousePosY));
        }
        return -1;
    }

    /**
     * Returns the string value of a list item got from the list's model.
     *
     * @param  list  list
     * @param  index index of the list item
     * @return       string or null if the index isn't valid
     */
    public static String getItemString(JList list, int index) {
        if (index >= 0) {
            Object o = list.getModel().getElementAt(index);
            if (o instanceof String) {
                return (String) o;
            }
        }
        return null;
    }

    /**
     * Returns the strings of a list model.
     *
     * Calls <code>toString()</code> of every model element.
     *
     * @param  model model
     * @return       strings of that model
     */
    public static List<String> toStringList(ListModel model) {
        int size = model.getSize();
        List<String> list = new ArrayList<String>(size);
        for (int i = 0; i < size; i++) {
            list.add(model.getElementAt(i).toString());
        }
        return list;
    }

    /**
     * Selects in a list a specific element.
     *
     * @param list       list
     * @param element    element to select. Uses <code>equals()</code> to
     *                   compare this element with the list's model elements
     * @param startIndex index of the first element to compare
     * @return           index of selected element or -1 if no element was
     *                   selected
     */
    public static int select(JList list, Object element, int startIndex) {
        int selIndex = -1;
        ListModel model = list.getModel();
        int size = model.getSize();
        for (int i = startIndex; selIndex == -1 && i < size; i++) {
            Object o = model.getElementAt(i);
            if (element.equals(o)) {
                selIndex = i;
            }
        }
        if (selIndex >= 0) {
            list.setSelectedIndex(selIndex);
            list.ensureIndexIsVisible(selIndex);
        }
        return selIndex;
    }

    /**
     * Returns from an array of indices the valid indices of a {@code JList}.
     *
     * @param  indices indices
     * @param  list    list
     * @return         valid indices
     */
    public static List<Integer> getExistingIndicesOf(int[] indices, JList list) {

        if (indices == null) throw new NullPointerException("indices == null"); // NOI18N
        if (list    == null) throw new NullPointerException("list == null"); // NOI18N

        List<Integer> existingIndices = new ArrayList<Integer>(indices.length);

        int elementCount = list.getModel().getSize();

        for (int index : indices) {
            if (index >= 0 && index < elementCount) {
                existingIndices.add(index);
            }
        }

        return existingIndices;
    }

    /**
     * Returns all indices of specific list items where the <code>toString()</code>
     * of the items will be compared with strings.
     *
     * @param model     model
     * @param toStrings values of the <code>toString()</code> method of the
     *                  model's objects
     * @return          all indices or empty list
     */
    public static List<Integer> getIndicesOfItems(ListModel model, Collection<? extends String> toStrings) {

        List<Integer> indices = new ArrayList<Integer>(toStrings.size());
        int           size     = model.getSize();

        for (int i = 0; i < size; i++) {
            Object element = model.getElementAt(i);
            if (element != null && toStrings.contains(element.toString())) {
                indices.add(i);
            }
        }
        return indices;
    }

    private ListUtil() {
    }
}
