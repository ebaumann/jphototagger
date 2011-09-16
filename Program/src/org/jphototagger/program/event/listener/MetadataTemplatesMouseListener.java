package org.jphototagger.program.event.listener;

import javax.swing.ListSelectionModel;

import org.jdesktop.swingx.JXList;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.MetadataTemplatesPopupMenu;

/**
 * Do not use this class as a template for other implementations! Instead extend
 * a popup menu from {@link org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * @author Elmar Baumann
 */
public final class MetadataTemplatesMouseListener extends ListMouseListener {

    public MetadataTemplatesMouseListener() {
        setPopupAlways(true);
    }

    @Override
    protected void showPopup(JXList list, int x, int y) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }

        assert list.getSelectionMode() == ListSelectionModel.SINGLE_SELECTION;
        MetadataTemplatesPopupMenu.INSTANCE.setSelIndex(getIndex());
        MetadataTemplatesPopupMenu.INSTANCE.setList(list);
        enableItems();
        MetadataTemplatesPopupMenu.INSTANCE.show(list, x, y);
    }

    private void enableItems() {
        boolean clickOnItem = getIndex() >= 0;

        MetadataTemplatesPopupMenu.INSTANCE.getItemDelete().setEnabled(clickOnItem);
        MetadataTemplatesPopupMenu.INSTANCE.getItemEdit().setEnabled(clickOnItem);
        MetadataTemplatesPopupMenu.INSTANCE.getItemRename().setEnabled(clickOnItem);
        MetadataTemplatesPopupMenu.INSTANCE.getItemSetToSelImages().setEnabled(
                GUI.getThumbnailsPanel().isAFileSelected());
    }
}
