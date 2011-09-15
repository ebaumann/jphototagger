package org.jphototagger.program.view.popupmenus;

import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.jphototagger.domain.metadata.search.SavedSearch;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppLookAndFeel;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@link org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * @author Elmar Baumann
 */
public final class SavedSearchesPopupMenu extends JPopupMenu {

    private static final long serialVersionUID = 3540766100829834971L;
    public static final SavedSearchesPopupMenu INSTANCE = new SavedSearchesPopupMenu();
    private final JMenuItem itemDelete = new JMenuItem(Bundle.getString(SavedSearchesPopupMenu.class, "SavedSearchesPopupMenu.DisplayName.Action.Delete"), AppLookAndFeel.ICON_DELETE);
    private final JMenuItem itemEdit = new JMenuItem(Bundle.getString(SavedSearchesPopupMenu.class, "SavedSearchesPopupMenu.DisplayName.Action.Edit"), AppLookAndFeel.ICON_EDIT);
    private final JMenuItem itemCreate = new JMenuItem(Bundle.getString(SavedSearchesPopupMenu.class, "SavedSearchesPopupMenu.DisplayName.Action.New"), AppLookAndFeel.ICON_NEW);
    private final JMenuItem itemRename = new JMenuItem(Bundle.getString(SavedSearchesPopupMenu.class, "SavedSearchesPopupMenu.DisplayName.Action.Rename"), AppLookAndFeel.ICON_RENAME);
    private transient SavedSearch savedSearch;

    private SavedSearchesPopupMenu() {
        init();
    }

    public JMenuItem getItemCreate() {
        return itemCreate;
    }

    public JMenuItem getItemDelete() {
        return itemDelete;
    }

    public JMenuItem getItemRename() {
        return itemRename;
    }

    public JMenuItem getItemEdit() {
        return itemEdit;
    }

    /**
     * Setzt die gespeicherte Suche.
     *
     * @param savedSearch Gespeicherte Suche. Default: null.
     */
    public void setSavedSearch(SavedSearch savedSearch) {
        this.savedSearch = savedSearch;
    }

    /**
     * Liefert die gespeicherte Suche.
     *
     * @return Gespeicherte Suche
     */
    public SavedSearch getSavedSearch() {
        return savedSearch;
    }

    private void init() {
        addItems();
        setAccelerators();
    }

    private void addItems() {
        add(itemCreate);
        add(itemEdit);
        add(itemRename);
        add(itemDelete);
    }

    private void setAccelerators() {
        itemCreate.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_N));
        itemEdit.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_E));
        itemDelete.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_DELETE));
        itemRename.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F2));
    }
}
