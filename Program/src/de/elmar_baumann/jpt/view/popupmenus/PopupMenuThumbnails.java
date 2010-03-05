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

package de.elmar_baumann.jpt.view.popupmenus;

import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.data.Program;
import de.elmar_baumann.jpt.database.DatabasePrograms;
import de.elmar_baumann.jpt.database.DatabasePrograms.Type;
import de.elmar_baumann.jpt.event.DatabaseProgramsEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseProgramsListener;
import de.elmar_baumann.jpt.event.listener.UserSettingsListener;
import de.elmar_baumann.jpt.event.UserSettingsEvent;
import de.elmar_baumann.jpt.factory.PluginManager;
import de.elmar_baumann.jpt.helper.ActionsHelper;
import de.elmar_baumann.jpt.plugin.Plugin;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.lib.image.util.IconUtil;

import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

/**
 * Popup menu of the thumbnails panel.
 *
 * @author  Elmar Baumann, Tobias Stening
 * @version 2008-10-05
 */
public final class PopupMenuThumbnails extends JPopupMenu
        implements UserSettingsListener, DatabaseProgramsListener {
    public static final PopupMenuThumbnails INSTANCE =
        new PopupMenuThumbnails();
    private static final long serialVersionUID = 1415777088897583494L;
    private final JMenu       menuRefresh      =
        new JMenu(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.MenuRefresh"));
    private final JMenu menuMetadata =
        new JMenu(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.MenuMetadata"));
    private final JMenu menuPrograms =
        new JMenu(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.MenuOtherOpenImageApps"));
    private final JMenu menuImageCollection =
        new JMenu(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.MenuImageCollection"));
    private final JMenu menuRotateThumbnail =
        new JMenu(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.MenuRotateThumbnail"));
    private final JMenu menuPlugins =
        new JMenu(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.MenuPlugins"));
    private final JMenu menuRating =
        new JMenu(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.menuRating"));
    private final JMenu menuSelection =
        new JMenu(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.MenuSelection"));
    private final JMenu menuFsOps =
        new JMenu(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.MenuFileSystemOps"));
    private final JMenu     menuActions        = ActionsHelper.actionsAsMenu();
    private final JMenuItem itemUpdateMetadata =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.Action.UpdateMetadata"));
    private final JMenuItem itemUpdateThumbnail =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.Action.UpdateThumbnail"));
    private final JMenuItem itemIptcToXmp =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.Action.IptcToXmp"));
    private final JMenuItem itemExifToXmp =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.Action.ExifToXmp"));
    private final JMenuItem itemCreateImageCollection =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuThumbnails.DisplayName.Action.CreateImageCollection"), AppLookAndFeel
                        .getIcon("icon_imagecollection.png"));
    private final JMenuItem itemAddToImageCollection =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuThumbnails.DisplayName.Action.AddToImageCollection"), AppLookAndFeel
                        .getIcon("icon_imagecollection_add_to.png"));
    private final JMenuItem itemDeleteFromImageCollection =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuThumbnails.DisplayName.Action.DeleteFromImageCollection"), AppLookAndFeel
                        .getIcon("icon_imagecollection_remove_from.png"));
    private final JMenuItem itemRotateThumbnail90 =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuThumbnails.DisplayName.Action.Rotate.90"), AppLookAndFeel
                        .getIcon("icon_rotate_90.png"));
    private final JMenuItem itemRotateThumbnai180 =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuThumbnails.DisplayName.Action.Rotate.180"), AppLookAndFeel
                        .getIcon("icon_rotate_180.png"));
    private final JMenuItem itemRotateThumbnail270 =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuThumbnails.DisplayName.Action.Rotate.270"), AppLookAndFeel
                        .getIcon("icon_rotate_270.png"));
    private final JMenuItem itemDeleteImageFromDatabase =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.Action.DeleteImageFromDatabase"));
    private final JMenuItem itemOpenFilesWithStandardApp =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.Action.OpenFiles"));
    private final JMenuItem itemFileSystemCopyToDirectory =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.Action.FileSystemCopyToDirectory"));
    private final JMenuItem itemFileSystemDeleteFiles =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.Action.FileSystemDeleteFiles"));
    private final JMenuItem itemFileSystemRenameFiles =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.Action.FileSystemRename"));
    private final JMenuItem itemFileSystemMoveFiles =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.Action.FileSystemMove"));
    private final JMenuItem itemRefresh =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.Action.Refresh"));
    private final JMenuItem itemPick =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuThumbnails.DisplayName.Action.Pick"), AppLookAndFeel
                        .getIcon("icon_picked.png"));
    private final JMenuItem itemReject =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuThumbnails.DisplayName.Action.Reject"), AppLookAndFeel
                        .getIcon("icon_rejected.png"));
    private final JMenuItem itemRating0 =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuThumbnails.DisplayName.Rating0"), AppLookAndFeel
                        .getIcon("icon_xmp_rating_remove.png"));
    private final JMenuItem itemRating1 =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuThumbnails.DisplayName.Rating1"), AppLookAndFeel
                        .getIcon("icon_xmp_rating_1.png"));
    private final JMenuItem itemRating2 =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuThumbnails.DisplayName.Rating2"), AppLookAndFeel
                        .getIcon("icon_xmp_rating_2.png"));
    private final JMenuItem itemRating3 =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuThumbnails.DisplayName.Rating3"), AppLookAndFeel
                        .getIcon("icon_xmp_rating_3.png"));
    private final JMenuItem itemRating4 =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuThumbnails.DisplayName.Rating4"), AppLookAndFeel
                        .getIcon("icon_xmp_rating_4.png"));
    private final JMenuItem itemRating5 =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuThumbnails.DisplayName.Rating5"), AppLookAndFeel
                        .getIcon("icon_xmp_rating_5.png"));
    private final JMenuItem itemCopyMetadata =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.ItemCopyMetadata"));
    private final JMenuItem itemCopyToClipboard =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.ItemCopyToClipboard"));
    private final JMenuItem itemCutToClipboard =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.ItemCutToClipboard"));
    private final JMenuItem itemPasteFromClipboard =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.ItemPasteFromClipboard"));
    private final JMenuItem itemPasteMetadata =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.ItemPasteMetadata"));
    private final JMenuItem itemSelectAll =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.ItemSelectAll"));
    private final JMenuItem itemSelectNothing =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuThumbnails.DisplayName.ItemSelectNothing"));

    // End menu items
    private final List<ActionListener> actionListenersOpenFilesWithOtherApp =
        new ArrayList<ActionListener>();
    private final Map<JMenuItem, Program> programOfMenuItem =
        new HashMap<JMenuItem, Program>();
    private final Map<JMenuItem, Long> RATING_OF_ITEM = new HashMap<JMenuItem,
                                                            Long>();
    private final Map<JMenuItem, Action> ACTION_OF_ITEM =
        new HashMap<JMenuItem, Action>();
    private final Map<JMenuItem, Plugin> PLUGIN_OF_ITEM =
        new HashMap<JMenuItem, Plugin>();

    private void initRatingOfItem() {
        RATING_OF_ITEM.put(itemRating0, Long.valueOf(0));
        RATING_OF_ITEM.put(itemRating1, Long.valueOf(1));
        RATING_OF_ITEM.put(itemRating2, Long.valueOf(2));
        RATING_OF_ITEM.put(itemRating3, Long.valueOf(3));
        RATING_OF_ITEM.put(itemRating4, Long.valueOf(4));
        RATING_OF_ITEM.put(itemRating5, Long.valueOf(5));
    }

    private void addItems() {
        menuRefresh.add(itemUpdateThumbnail);
        menuRefresh.add(itemUpdateMetadata);
        menuRefresh.add(itemDeleteImageFromDatabase);
        menuRefresh.add(itemRefresh);
        add(menuRefresh);
        menuRotateThumbnail.add(itemRotateThumbnail90);
        menuRotateThumbnail.add(itemRotateThumbnai180);
        menuRotateThumbnail.add(itemRotateThumbnail270);
        add(menuRotateThumbnail);
        add(new JSeparator());
        add(itemOpenFilesWithStandardApp);
        add(menuPrograms);
        add(menuActions);
        addPluginItems();
        add(new JSeparator());
        menuRating.add(itemRating0);
        menuRating.add(itemRating1);
        menuRating.add(itemRating2);
        menuRating.add(itemRating3);
        menuRating.add(itemRating4);
        menuRating.add(itemRating5);
        add(menuRating);
        menuSelection.add(itemPick);
        menuSelection.add(itemReject);
        add(menuSelection);
        menuImageCollection.add(itemCreateImageCollection);
        menuImageCollection.add(itemAddToImageCollection);
        menuImageCollection.add(itemDeleteFromImageCollection);
        add(menuImageCollection);
        menuMetadata.add(itemCopyMetadata);
        menuMetadata.add(itemPasteMetadata);
        menuMetadata.add(new JSeparator());
        menuMetadata.add(itemIptcToXmp);
        menuMetadata.add(itemExifToXmp);
        add(menuMetadata);
        itemPasteMetadata.setEnabled(false);
        menuFsOps.add(itemCopyToClipboard);
        menuFsOps.add(itemCutToClipboard);
        menuFsOps.add(itemPasteFromClipboard);
        menuFsOps.add(new JSeparator());
        menuFsOps.add(itemFileSystemCopyToDirectory);
        menuFsOps.add(new JSeparator());
        menuFsOps.add(itemFileSystemRenameFiles);
        menuFsOps.add(itemFileSystemMoveFiles);
        menuFsOps.add(itemFileSystemDeleteFiles);
        menuFsOps.add(new JSeparator());
        menuFsOps.add(itemSelectAll);
        menuFsOps.add(itemSelectNothing);
        add(menuFsOps);
    }

    private void addPluginItems() {
        if (!PluginManager.INSTANCE.hasPlugins()) {
            return;
        }

        add(menuPlugins);

        for (Plugin plugin : PluginManager.INSTANCE.getPlugins()) {
            addItemsOf(plugin);
        }
    }

    private void addItemsOf(Plugin plugin) {
        List<? extends Action> pluginActions = plugin.getActions();
        int                    actionCount   = pluginActions.size();
        List<JMenuItem>        pluginItems   =
            new ArrayList<JMenuItem>(actionCount);

        for (Action action : pluginActions) {
            Icon      actionIcon = (Icon) action.getValue(Action.SMALL_ICON);
            String    actionName = action.getValue(Action.NAME).toString();
            JMenuItem pluginItem = new JMenuItem(actionName, actionIcon);

            pluginItems.add(pluginItem);
            ACTION_OF_ITEM.put(pluginItem, action);
        }

        for (JMenuItem pluginItem : pluginItems) {
            PLUGIN_OF_ITEM.put(pluginItem, plugin);
        }

        if (pluginItems.size() == 1) {
            menuPlugins.add(pluginItems.get(0));
        } else if (pluginItems.size() > 1) {    // Adding submenu
            JMenu pluginMenu = new JMenu(plugin.getName());

            for (JMenuItem pluginItem : pluginItems) {
                pluginMenu.add(pluginItem);
            }

            menuPlugins.add(pluginMenu);
        }
    }

    private PopupMenuThumbnails() {
        init();
    }

    public void setOtherPrograms() {
        menuPrograms.removeAll();
        programOfMenuItem.clear();

        List<Program> programs = DatabasePrograms.INSTANCE.getAll(Type.PROGRAM);

        if (!programs.isEmpty()) {
            for (Program program : programs) {
                String    alias = program.getAlias();
                JMenuItem item  = new JMenuItem(alias);

                for (ActionListener listener :
                        actionListenersOpenFilesWithOtherApp) {
                    item.addActionListener(listener);
                }

                menuPrograms.add(item);

                if (program.getFile().exists()) {
                    item.setIcon(IconUtil.getSystemIcon(program.getFile()));
                }

                programOfMenuItem.put(item, program);
            }
        }

        menuPrograms.setEnabled(menuPrograms.getItemCount() > 0);
    }

    public Action getActionOfItem(JMenuItem item) {
        return ACTION_OF_ITEM.get(item);
    }

    @Override
    public void actionPerformed(DatabaseProgramsEvent event) {
        if (isSetOtherPrograms(event.getType())) {
            setOtherPrograms();
        }
    }

    private boolean isSetOtherPrograms(DatabaseProgramsEvent.Type type) {
        return type.equals(DatabaseProgramsEvent.Type.PROGRAM_DELETED)
               || type.equals(DatabaseProgramsEvent.Type.PROGRAM_INSERTED)
               || type.equals(DatabaseProgramsEvent.Type.PROGRAM_UPDATED);
    }

    @Override
    public void applySettings(UserSettingsEvent evt) {
        if (evt.getType().equals(
                UserSettingsEvent.Type.DEFAULT_IMAGE_OPEN_APP)) {
            itemOpenFilesWithStandardApp.setEnabled(
                existsStandardImageOpenApp());
        }
    }

    private boolean existsStandardImageOpenApp() {
        String app = UserSettings.INSTANCE.getDefaultImageOpenApp();

        return (app != null) &&!app.trim().isEmpty() && new File(app).exists();
    }

    public JMenuItem getItemAddToImageCollection() {
        return itemAddToImageCollection;
    }

    public JMenuItem getItemFileSystemCopyToDirectory() {
        return itemFileSystemCopyToDirectory;
    }

    public JMenuItem getItemCreateImageCollection() {
        return itemCreateImageCollection;
    }

    public JMenuItem getItemDeleteFromImageCollection() {
        return itemDeleteFromImageCollection;
    }

    public JMenuItem getItemDeleteImageFromDatabase() {
        return itemDeleteImageFromDatabase;
    }

    public JMenuItem getItemFileSystemDeleteFiles() {
        return itemFileSystemDeleteFiles;
    }

    public JMenuItem getItemFileSystemMoveFiles() {
        return itemFileSystemMoveFiles;
    }

    public JMenuItem getItemFileSystemRenameFiles() {
        return itemFileSystemRenameFiles;
    }

    public JMenuItem getItemOpenFilesWithStandardApp() {
        return itemOpenFilesWithStandardApp;
    }

    public JMenuItem getItemRotateThumbnai180() {
        return itemRotateThumbnai180;
    }

    public JMenuItem getItemRotateThumbnail270() {
        return itemRotateThumbnail270;
    }

    public JMenuItem getItemRotateThumbnail90() {
        return itemRotateThumbnail90;
    }

    public JMenuItem getItemUpdateMetadata() {
        return itemUpdateMetadata;
    }

    public JMenuItem getItemUpdateThumbnail() {
        return itemUpdateThumbnail;
    }

    public JMenuItem getItemRefresh() {
        return itemRefresh;
    }

    public JMenuItem getItemIptcToXmp() {
        return itemIptcToXmp;
    }

    public JMenuItem getItemExifToXmp() {
        return itemExifToXmp;
    }

    public JMenuItem getItemPick() {
        return itemPick;
    }

    public JMenuItem getItemCopyMetadata() {
        return itemCopyMetadata;
    }

    public JMenuItem getItemPasteMetadata() {
        return itemPasteMetadata;
    }

    public JMenuItem getItemCopyToClipboard() {
        return itemCopyToClipboard;
    }

    public JMenuItem getItemPasteFromClipboard() {
        return itemPasteFromClipboard;
    }

    public JMenuItem getItemCutToClipboard() {
        return itemCutToClipboard;
    }

    public JMenuItem getItemReject() {
        return itemReject;
    }

    public JMenuItem getItemRating0() {
        return itemRating0;
    }

    public JMenuItem getItemRating1() {
        return itemRating1;
    }

    public JMenuItem getItemRating2() {
        return itemRating2;
    }

    public JMenuItem getItemRating3() {
        return itemRating3;
    }

    public JMenuItem getItemRating4() {
        return itemRating4;
    }

    public JMenuItem getItemRating5() {
        return itemRating5;
    }

    public JMenuItem getItemSelectAll() {
        return itemSelectAll;
    }

    public JMenuItem getItemSelectNothing() {
        return itemSelectNothing;
    }

    public JMenu getMenuRating() {
        return menuRating;
    }

    public JMenu getMenuPlugins() {
        return menuPlugins;
    }

    public JMenu getMenuFsOps() {
        return menuFsOps;
    }

    public JMenu getMenuImageCollection() {
        return menuImageCollection;
    }

    public JMenu getMenuMetadata() {
        return menuMetadata;
    }

    public JMenu getMenuPrograms() {
        return menuPrograms;
    }

    public JMenu getMenuActions() {
        return menuActions;
    }

    public JMenu getMenuRefresh() {
        return menuRefresh;
    }

    public JMenu getMenuRotateThumbnail() {
        return menuRotateThumbnail;
    }

    public JMenu getMenuSelection() {
        return menuSelection;
    }

    public Long getRatingOfItem(JMenuItem item) {
        return RATING_OF_ITEM.get(item);
    }

    public Set<JMenuItem> getPluginMenuItems() {
        return PLUGIN_OF_ITEM.keySet();
    }

    public Plugin getPluginOfItem(JMenuItem item) {
        return PLUGIN_OF_ITEM.get(item);
    }

    public synchronized void addActionListenerOpenFilesWithOtherApp(
            ActionListener listener) {
        actionListenersOpenFilesWithOtherApp.add(listener);
        setOtherPrograms();
    }

    public Program getProgram(Object source) {
        if (source instanceof JMenuItem) {
            return programOfMenuItem.get((JMenuItem) source);
        }

        return null;
    }

    private void init() {
        initRatingOfItem();
        addItems();
        setItemsEnabled();
        setAccelerators();
        UserSettings.INSTANCE.addUserSettingsListener(this);
        DatabasePrograms.INSTANCE.addListener(this);
    }

    private void setItemsEnabled() {
        itemDeleteFromImageCollection.setEnabled(false);
    }

    private void setAccelerators() {
        itemDeleteFromImageCollection.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        itemFileSystemDeleteFiles.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        itemFileSystemRenameFiles.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        itemRefresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        itemCopyToClipboard.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        itemCutToClipboard.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                InputEvent.CTRL_MASK));
        itemPasteFromClipboard.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        itemCopyMetadata.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
        itemPasteMetadata.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
        itemPick.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0));
        itemReject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0));
        itemRating0.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, 0));
        itemRating1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, 0));
        itemRating2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, 0));
        itemRating3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, 0));
        itemRating4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, 0));
        itemRating5.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, 0));
        itemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                InputEvent.CTRL_MASK));
    }
}
