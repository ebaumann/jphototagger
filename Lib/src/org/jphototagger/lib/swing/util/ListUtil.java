package org.jphototagger.lib.swing.util;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import org.jdesktop.swingx.JXList;
import org.jphototagger.lib.util.ArrayUtil;

/**
 * @author Elmar Baumann
 */
public final class ListUtil {

    private ListUtil() {
    }

    /**
     * Clears alls selected items in all lists.
     *
     * @param lists  lists
     * @return Previous selected values of lists with selections
     */
    @SuppressWarnings("rawtypes")
    public static Map<JList<?>, List<?>> clearSelection(List<? extends JList> lists) {
        if (lists == null) {
            throw new NullPointerException("lists == null");
        }
        Map<JList<?>, List<?>> selectedIndices = new HashMap<>();
        for (JList<?> list : lists) {
            if (!list.isSelectionEmpty()) {
                List<?> selValues = list.getSelectedValuesList();
                if (!selValues.isEmpty()) { // should not be necessary, "safety belt"
                    selectedIndices.put(list, selValues);
                }
                list.clearSelection();
            }
        }
        return selectedIndices;
    }

    public static void setSelectedValues(JList<?> list, List<?> values) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }
        if (values == null) {
            throw new NullPointerException("values == null");
        }
        List<Integer> indices = getIndicesOfValues(list, values);
        list.setSelectedIndices(ArrayUtil.toIntArray(indices));
    }

    public static List<Integer> getIndicesOfValues(JList<?> list, List<?> values) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }
        if (values == null) {
            throw new NullPointerException("values == null");
        }
        List<Integer> indices = new ArrayList<>();
        ListModel<?> model = list.getModel();
        boolean isJXList = list instanceof JXList;
        boolean isDefaultListModel = model instanceof DefaultListModel<?>;
        for (Object value : values) {
            if (value == null) {
                continue;
            }
            int index = -1;
            if (isDefaultListModel) {
                index = ((DefaultListModel<?>) model).indexOf(value);
                if (index >= 0 && isJXList) {
                    index = ((JXList) list).convertIndexToView(index);
                }
            } else {
                for (int i = 0; index < 0 && i < model.getSize(); i++) {
                    if (value.equals(model.getElementAt(i))) {
                        index = i;
                    }
                }
            }
            if (index >= 0) {
                indices.add(index);
            }
        }
        return indices;
    }

    /**
     * Returns the first item of a list model with a specific text.
     *
     * @param  text  searched item text
     * @param  model list model
     * @return list item or null if not found
     */
    public static Object getFirstItemWithText(String text, DefaultListModel<?> model) {
        if (text == null) {
            throw new NullPointerException("text == null");
        }
        if (model == null) {
            throw new NullPointerException("model == null");
        }
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
    public static void setToken(String str, String delim, DefaultListModel<Object> model) {
        if (str == null) {
            throw new NullPointerException("str == null");
        }
        if (delim == null) {
            throw new NullPointerException("delim == null");
        }
        if (model == null) {
            throw new NullPointerException("model == null");
        }
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
    public static String getTokenString(DefaultListModel<?> model, String delim) {
        if (model == null) {
            throw new NullPointerException("model == null");
        }
        if (delim == null) {
            throw new NullPointerException("delim == null");
        }
        StringBuilder buffer = new StringBuilder();
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            buffer.append(model.get(i).toString()).append((i < size - 1)
                    ? delim
                    : "");
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
    static public <T> void insertSorted(DefaultListModel<Object> model, T o, Comparator<T> c, int startIndex, int endIndex) {
        if (model == null) {
            throw new NullPointerException("model == null");
        }
        if (o == null) {
            throw new NullPointerException("o == null");
        }
        if (c == null) {
            throw new NullPointerException("c == null");
        }
        synchronized (model) {
            if (!model.contains(o)) {
                int size = model.getSize();
                boolean inserted = false;
                for (int i = startIndex; !inserted && (i <= endIndex) && (i < size); i++) {
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
     * Swaps two list elements into a {@code javax.swing.DefaultListModel}.
     *
     * @param  model               model with the elements
     * @param  indexFirstElement   index of the first element
     * @param  indexSecondElement  index of the second element
     * @return true if swapped
     */
    static public boolean swapModelElements(DefaultListModel<Object> model, int indexFirstElement, int indexSecondElement) {
        if (model == null) {
            throw new NullPointerException("model == null");
        }
        int size = model.getSize();
        boolean canSwap = (indexFirstElement >= 0) && (indexFirstElement < size) && (indexSecondElement >= 0)
                && (indexSecondElement < size) && (indexSecondElement != indexFirstElement);
        if (!canSwap) {
            return false;
        }
        Object firstElement = model.get(indexFirstElement);
        Object secondElement = model.get(indexSecondElement);
        model.set(indexFirstElement, secondElement);
        model.set(indexSecondElement, firstElement);
        return true;
    }

    /**
     * Returns wheter a list model contains a string. Uses the
     * {@code java.lang.Object#toString()} method for comparison.
     *
     * @param model   model
     * @param string  string to find
     * @return        true if the model contains that string
     */
    public static boolean containsString(ListModel<?> model, String string) {
        if (model == null) {
            throw new NullPointerException("model == null");
        }
        if (string == null) {
            throw new NullPointerException("string == null");
        }
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            Object o = model.getElementAt(i);

            if ((o != null) && o.toString().equals(string)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the item index below a mouse position got by a mouse event.
     *
     * @param  evt mouse event within a {@code JList}
     * @return   item index below the mouse position or -1 if below the mouse
     *           position isn't an item
     */
    public static int getItemIndex(MouseEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }
        Object source = evt.getSource();
        if (source instanceof JList) {
            int mousePosX = evt.getX();
            int mousePosY = evt.getY();
            return ((JList) source).locationToIndex(new Point(mousePosX, mousePosY));
        }
        return -1;
    }

    public static int getSelectionCount(JList<?> list) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }
        ListSelectionModel selectionModel = list.getSelectionModel();
        int minSelectionIndex = selectionModel.getMinSelectionIndex();
        int maxSelectionIndex = selectionModel.getMaxSelectionIndex();
        if (minSelectionIndex < 0 || maxSelectionIndex < 0) {
            return 0;
        }
        return maxSelectionIndex - minSelectionIndex + 1;
    }

    /**
     * Returns the string value of a list item got from the list's model.
     *
     * @param  list      list
     * @param  listIndex index of the list item
     * @return           string or null if the index isn't valid
     */
    public static String getItemString(JList<?> list, int listIndex) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }
        if (listIndex >= 0) {
            int modelIndex = listIndex;
            if (list instanceof JXList) {
                modelIndex = ((JXList) list).convertIndexToModel(listIndex);
            }
            Object o = list.getModel().getElementAt(modelIndex);
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
    public static List<String> toStringList(ListModel<?> model) {
        if (model == null) {
            throw new NullPointerException("model == null");
        }
        int size = model.getSize();
        List<String> list = new ArrayList<>(size);
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
    public static int select(JList<?> list, Object element, int startIndex) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }
        if (element == null) {
            throw new NullPointerException("element == null");
        }
        int selModelIndex = -1;
        ListModel<?> model = list.getModel();
        int size = model.getSize();
        for (int i = startIndex; (selModelIndex == -1) && (i < size); i++) {
            Object o = model.getElementAt(i);
            if (element.equals(o)) {
                selModelIndex = i;
            }
        }
        if (selModelIndex >= 0) {
            if (list instanceof JXList) {
                JXList jxList = (JXList) list;
                selModelIndex = jxList.convertIndexToView(selModelIndex);
            }
            list.setSelectedIndex(selModelIndex);
            list.ensureIndexIsVisible(selModelIndex);
        }
        return selModelIndex;
    }

    /**
     * Returns from an array of indices the valid indices of a {@code JList}.
     *
     * @param  indices indices
     * @param  list    list
     * @return         valid indices
     */
    public static List<Integer> getExistingIndicesOf(int[] indices, JList<?> list) {
        if (indices == null) {
            throw new NullPointerException("indices == null");
        }
        if (list == null) {
            throw new NullPointerException("list == null");
        }
        List<Integer> existingIndices = new ArrayList<>(indices.length);
        int elementCount = list.getModel().getSize();
        for (int index : indices) {
            if ((index >= 0) && (index < elementCount)) {
                existingIndices.add(index);
            }
        }
        return existingIndices;
    }

    /**
     * Returns all indices of specific list items where the
     * <code>toString()</code> of the items will be compared with strings.
     *
     * @param model     model
     * @param toStrings values of the <code>toString()</code> method of the
     *                  model's objects
     * @return          all indices or empty list
     */
    public static List<Integer> getModelIndicesOfItems(ListModel<?> model, Collection<? extends String> toStrings) {
        if (model == null) {
            throw new NullPointerException("model == null");
        }
        if (toStrings == null) {
            throw new NullPointerException("toStrings == null");
        }
        List<Integer> indices = new ArrayList<>(toStrings.size());
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            Object element = model.getElementAt(i);
            if ((element != null) && toStrings.contains(element.toString())) {
                indices.add(i);
            }
        }
        return indices;
    }

    /**
     * Selects an item in the list with the nearest index to a given index.
     *
     * @param  list  list
     * @param  index index
     * @throws       IllegalArgumentException if index is negative
     */
    public static void selectNearestIndex(JList<?> list, int index) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Negative index: " + index);
        }
        int size = list.getModel().getSize();
        if (size > 0) {
            list.setSelectedIndex(Math.min(size - 1, index));
        }
    }

    public static List<Integer> convertModelIndicesToListIndices(List<? extends Integer> modelIndices, JXList list) {
        if (modelIndices == null) {
            throw new NullPointerException("modelIndices == null");
        }
        if (list == null) {
            throw new NullPointerException("list == null");
        }
        List<Integer> listIndices = new ArrayList<>();
        for (Integer modelIndex : modelIndices) {
            int listIndex = list.convertIndexToView(modelIndex);
            listIndices.add(listIndex);
        }
        return listIndices;
    }

    public static <E> List<E> getElements(DefaultListModel<E> model) {
        if (model == null) {
            throw new NullPointerException("model == null");
        }
        List<E> elements = new ArrayList<>(model.size());
        for (Enumeration<E> e = model.elements(); e.hasMoreElements();) {
            elements.add(e.nextElement());
        }
        return elements;
    }

    public static <E> void sort(DefaultListModel<E> model, Comparator<E> comp) {
        if (model == null) {
            throw new NullPointerException("model == null");
        }
        if (comp == null) {
            throw new NullPointerException("comp == null");
        }
        List<E> elements = getElements(model);
        Collections.sort(elements, comp);
        model.clear();
        for (E element : elements) {
            model.addElement(element);
        }
    }
}
