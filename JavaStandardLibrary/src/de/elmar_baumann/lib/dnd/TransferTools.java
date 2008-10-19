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
    
     private static final String delimiter = "\n";

    /**
     * Returns the selected items in a
     * {@link java.awt.datatransfer.StringSelection}.
     * Each line is separated by a newline.
     * 
     * @param list list
     * @return 
     */
    public static Transferable getSelectedItemStringsTransferable(JList list) {
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
     * Returns the Integers of a list in a
     * {@link java.awt.datatransfer.StringSelection}.
     * Each line is separated by a newline.
     * 
     * @param list  list
     * @return <code>StringSelection</code>: A String within integers separated
     *         by a newline
     */
    public static Transferable getIntegerListTransferable(List<Integer> list) {
        StringBuffer buffer = new StringBuffer();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Integer index = list.get(i);
            buffer.append(index.toString());
            buffer.append(i < size - 1 ? delimiter : ""); // NOI18N
        }

        return new StringSelection(buffer.toString());
    }
}
