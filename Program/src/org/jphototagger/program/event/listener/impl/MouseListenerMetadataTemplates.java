package org.jphototagger.program.event.listener.impl;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuMetadataTemplates;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

/**
 * Do not use this class as a template for other implementations! Instead extend
 * a popup menu from {@link org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * @author Elmar Baumann
 */
public final class MouseListenerMetadataTemplates extends MouseListenerList {
    public MouseListenerMetadataTemplates() {
        setPopupAlways(true);
    }

    @Override
    protected void showPopup(JList list, int x, int y) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }

        assert list.getSelectionMode() == ListSelectionModel.SINGLE_SELECTION;
        PopupMenuMetadataTemplates.INSTANCE.setSelIndex(getIndex());
        PopupMenuMetadataTemplates.INSTANCE.setList(list);
        enableItems();
        PopupMenuMetadataTemplates.INSTANCE.show(list, x, y);
    }

    private void enableItems() {
        boolean clickOnItem = getIndex() >= 0;

        PopupMenuMetadataTemplates.INSTANCE.getItemDelete().setEnabled(clickOnItem);
        PopupMenuMetadataTemplates.INSTANCE.getItemEdit().setEnabled(clickOnItem);
        PopupMenuMetadataTemplates.INSTANCE.getItemRename().setEnabled(clickOnItem);
        PopupMenuMetadataTemplates.INSTANCE.getItemSetToSelImages().setEnabled(
            GUI.getThumbnailsPanel().isFileSelected());
    }
}
