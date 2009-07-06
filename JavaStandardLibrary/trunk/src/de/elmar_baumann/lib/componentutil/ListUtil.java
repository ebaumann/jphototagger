package de.elmar_baumann.lib.componentutil;

import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 * Utils for {@link javax.swing.JList}.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/19
 */
public final class ListUtil {

    /**
     * Clears alls selected items in all lists.
     * 
     * @param lists  lists
     */
    public static void clearSelection(List<JList> lists) {
        if (lists == null)
            throw new NullPointerException("lists == null"); // NOI18N

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
            throw new NullPointerException("text == null"); // NOI18N
        if (model == null)
            throw new NullPointerException("model == null"); // NOI18N

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
            throw new NullPointerException("str == null"); // NOI18N
        if (delim == null)
            throw new NullPointerException("delim == null"); // NOI18N
        if (model == null)
            throw new NullPointerException("model == null"); // NOI18N

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
            throw new NullPointerException("model == null"); // NOI18N
        if (delim == null)
            throw new NullPointerException("delim == null"); // NOI18N

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
     * Inserts an element into a model in a sort order.
     * 
     * @param model      model
     * @param o          object to insert
     * @param c          comparator
     * @param startIndex start index. List items before that index ignored.
     */
    @SuppressWarnings("unchecked")
    static public void insertSorted(
            DefaultListModel model, Object o, Comparator c, int startIndex) {
        if (model == null)
            throw new NullPointerException("model == null"); // NOI18N
        if (o == null)
            throw new NullPointerException("o == null"); // NOI18N
        if (c == null)
            throw new NullPointerException("c == null"); // NOI18N

        synchronized (model) {
            if (!model.contains(o)) {
                int size = model.getSize();
                boolean inserted = false;
                for (int i = 0; !inserted && i < size; i++) {
                    if (c.compare(o, model.get(i)) < 0) {
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

    private ListUtil() {
    }
}
