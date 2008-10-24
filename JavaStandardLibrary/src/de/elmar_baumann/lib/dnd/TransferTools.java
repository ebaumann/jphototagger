package de.elmar_baumann.lib.dnd;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.List;
import javax.swing.JList;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/17
 */
public class TransferTools {

    /**
     * Returns the selected items in a {@link java.awt.datatransfer.StringSelection}.
     * Each item is separated by a delimiter.
     * 
     * @param  list       list
     * @param  delimiter  delimiter between the item strings
     * @return <code>StringSelection</code>: A String within value strings,
     *         separated by <code>delimiter</code>
     */
    public static Transferable getSelectedItemStringsTransferable(JList list, String delimiter) {
        Object[] values = list.getSelectedValues();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < values.length; i++) {
            Object val = values[i];
            buffer.append(val == null ? "" : val.toString());
            buffer.append(i != values.length - 1 ? delimiter : ""); // NOI18N
        }
        return new StringSelection(buffer.toString());
    }

    /**
     * Returns the Integers of a list in a {@link java.awt.datatransfer.StringSelection}.
     * Each integer is separated by a delimiter.
     * 
     * @param  list      list
     * @param delimiter  delimiter
     * @return <code>StringSelection</code>: A String within integer token
     *         separated by <code>delimiter</code>
     */
    public static Transferable getIntegerListTransferable(List<Integer> list, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Integer integer = list.get(i);
            buffer.append(integer.toString());
            buffer.append(i < size - 1 ? delimiter : ""); // NOI18N
        }
        return new StringSelection(buffer.toString());
    }

    /**
     * Returns the Strings of a list in a {@link java.awt.datatransfer.StringSelection}.
     * Each string is separated by a delimiter.
     * 
     * @param  list      list
     * @param delimiter  delimiter
     * @return <code>StringSelection</code>: A String within integer token
     *         separated by <code>delimiter</code>
     */
    public static Transferable getStringListTransferable(List<String> list, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            String string = list.get(i);
            buffer.append(string);
            buffer.append(i < size - 1 ? delimiter : ""); // NOI18N
        }
        return new StringSelection(buffer.toString());
    }
}
