package org.jphototagger.program.event.listener.impl;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsList;
import org.jdesktop.swingx.JXList;

/**
 * Do not use this class as a template for other implementations! Instead extend
 * a popup menu from {@link org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * @author Elmar Baumann
 */
public final class MouseListenerKeywordsList extends MouseListenerList {
    private final PopupMenuKeywordsList popup = PopupMenuKeywordsList.INSTANCE;

    @Override
    protected void showPopup(JXList list, int x, int y) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }

        popup.setSelIndex(getIndex());
        popup.setList(list);
        setEnabled();
        popup.show(list, x, y);
    }

    private void setEnabled() {
        boolean editable = GUI.getAppPanel().getEditMetadataPanels().isEditable();

        popup.getItemAddToEditPanel().setEnabled(editable);
        popup.getItemRemoveFromEditPanel().setEnabled(editable);
    }
}
