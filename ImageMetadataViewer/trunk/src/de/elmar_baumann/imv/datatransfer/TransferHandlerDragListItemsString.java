package de.elmar_baumann.imv.datatransfer;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 * Creates a {@link Transferable} with selected list items as string.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-02
 */
public final class TransferHandlerDragListItemsString extends TransferHandler {

    /**
     * Every exported string starts with this prefix if dragged from a keywords
     * list
     */
    public static final String PREFIX_KEYWORDS =
            TransferHandlerDragListItemsString.class.getName() + "_KEYWORDS";
    /**
     * Every exported string starts with this prefix if dragged from a
     * categories list
     */
    public static final String PREFIX_CATEGORIES =
            TransferHandlerDragListItemsString.class.getName() + "_CATEGORIES";
    /**
     * Every exported string starts with this prefix if dragged from a
     * any other list
     */
    public static final String PREFIX_ANY =
            TransferHandlerDragListItemsString.class.getName() + "_ANY";
    /**
     * Delimiter between transferred strings
     */
    public static final String DELIMITER = "|";
    /**
     * Transfer handler for keyword lists
     */
    public static final TransferHandlerDragListItemsString KEYWORDS =
            new TransferHandlerDragListItemsString(PREFIX_KEYWORDS);
    /**
     * Transfer handler for keyword lists
     */
    public static final TransferHandlerDragListItemsString CATEGORIES =
            new TransferHandlerDragListItemsString(PREFIX_CATEGORIES);
    /**
     * Transfer handler for keyword lists
     */
    public static final TransferHandlerDragListItemsString ANY =
            new TransferHandlerDragListItemsString(PREFIX_ANY);
    private final String prefix;

    private TransferHandlerDragListItemsString(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Returns all selected items in a string started with {@link #PREFIX} and
     * delimited by {@link #DELIMITER}.
     * 
     * @param  c component
     * @return   transferrable
     */
    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JList) {
            JList list = (JList) c;
            Object[] selValues = list.getSelectedValues();
            if (selValues != null) {
                return new StringSelection(toString(selValues));
            }
        }
        return null;
    }

    public static boolean isPrefix(String s) {
        return s.equals(PREFIX_KEYWORDS) ||
                s.equals(PREFIX_CATEGORIES) ||
                s.equals(PREFIX_ANY);
    }

    public static boolean startsWithPrefix(String s) {
        return s.startsWith(PREFIX_KEYWORDS + DELIMITER) ||
                s.startsWith(PREFIX_CATEGORIES + DELIMITER) ||
                s.startsWith(PREFIX_ANY + DELIMITER);
    }

    private String toString(Object[] values) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        for (Object o : values) {
            sb.append(DELIMITER +
                    (o == null
                     ? ""
                     : o.toString()));
        }
        return sb.toString();
    }

    @Override
    public boolean canImport(TransferSupport support) {
        return false;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }
}
