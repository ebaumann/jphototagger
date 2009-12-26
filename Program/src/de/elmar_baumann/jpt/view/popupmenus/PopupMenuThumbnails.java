/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.view.popupmenus;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.data.Program;
import de.elmar_baumann.jpt.database.DatabasePrograms;
import de.elmar_baumann.jpt.event.UserSettingsChangeEvent;
import de.elmar_baumann.jpt.event.listener.UserSettingsChangeListener;
import de.elmar_baumann.jpt.plugin.Plugin;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.lib.util.Lookup;
import de.elmar_baumann.lib.image.util.IconUtil;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
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
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class PopupMenuThumbnails extends JPopupMenu
        implements UserSettingsChangeListener {

    public static final PopupMenuThumbnails     INSTANCE                             = new PopupMenuThumbnails();
    private final       JMenu                   menuMisc                             = new JMenu(Bundle.getString("PopupMenuThumbnails.DisplayName.MenuMisc"));
    private final       JMenu                   menuPrograms                         = new JMenu(Bundle.getString("PopupMenuThumbnails.DisplayName.MenuOtherOpenImageApps"));
    private final       JMenu                   menuImageCollection                  = new JMenu(Bundle.getString("PopupMenuThumbnails.DisplayName.MenuImageCollection"));
    private final       JMenu                   menuRotateThumbnail                  = new JMenu(Bundle.getString("PopupMenuThumbnails.DisplayName.MenuRotateThumbnail"));
    private final       JMenu                   menuPlugins                          = new JMenu(Bundle.getString("PopupMenuThumbnails.DisplayName.MenuPlugins"));
    private final       JMenu                   menuRating                           = new JMenu(Bundle.getString("PopupMenuThumbnails.DisplayName.menuRating"));
    private final       JMenu                   menuSelection                        = new JMenu(Bundle.getString("PopupMenuThumbnails.DisplayName.MenuSelection"));
    private final       JMenu                   menuFsOps                            = new JMenu(Bundle.getString("PopupMenuThumbnails.DisplayName.MenuFileSystemOps"));
    private final       JMenuItem               itemUpdateMetadata                   = new JMenuItem();
    private final       JMenuItem               itemUpdateThumbnail                  = new JMenuItem();
    private final       JMenuItem               itemIptcToXmp                        = new JMenuItem();
    private final       JMenuItem               itemCreateImageCollection            = new JMenuItem();
    private final       JMenuItem               itemAddToImageCollection             = new JMenuItem();
    private final       JMenuItem               itemDeleteFromImageCollection        = new JMenuItem();
    private final       JMenuItem               itemRotateThumbnai90                 = new JMenuItem();
    private final       JMenuItem               itemRotateThumbnai180                = new JMenuItem();
    private final       JMenuItem               itemRotateThumbnail270               = new JMenuItem();
    private final       JMenuItem               itemDeleteImageFromDatabase          = new JMenuItem();
    private final       JMenuItem               itemOpenFilesWithStandardApp         = new JMenuItem();
    private final       JMenuItem               itemFileSystemCopyToDirectory        = new JMenuItem();
    private final       JMenuItem               itemFileSystemDeleteFiles            = new JMenuItem();
    private final       JMenuItem               itemFileSystemRenameFiles            = new JMenuItem();
    private final       JMenuItem               itemFileSystemMoveFiles              = new JMenuItem();
    private final       JMenuItem               itemRefresh                          = new JMenuItem();
    private final       JMenuItem               itemPick                             = new JMenuItem();
    private final       JMenuItem               itemReject                           = new JMenuItem();
    private final       JMenuItem               itemRating0                          = new JMenuItem();
    private final       JMenuItem               itemRating1                          = new JMenuItem();
    private final       JMenuItem               itemRating2                          = new JMenuItem();
    private final       JMenuItem               itemRating3                          = new JMenuItem();
    private final       JMenuItem               itemRating4                          = new JMenuItem();
    private final       JMenuItem               itemRating5                          = new JMenuItem();
    private final       JMenuItem               itemCopyMetadata                     = new JMenuItem();
    private final       JMenuItem               itemCopyToClipboard                  = new JMenuItem();
    private final       JMenuItem               itemCutToClipboard                   = new JMenuItem();
    private final       JMenuItem               itemPasteFromClipboard               = new JMenuItem();
    private final       JMenuItem               itemPasteMetadata                    = new JMenuItem();
    // End menu items
    private final       List<ActionListener>    actionListenersOpenFilesWithOtherApp = new ArrayList<ActionListener>();
    private final       Map<JMenuItem, Program> programOfMenuItem                    = new HashMap<JMenuItem, Program>();
    private final       Map<JMenuItem, String>  TEXT_OF_ITEM                         = new HashMap<JMenuItem, String>();
    private final       Map<JMenuItem, Icon>    ICON_OF_ITEM                         = new HashMap<JMenuItem, Icon>();
    private final       Map<JMenuItem, Long>    RATING_OF_ITEM                       = new HashMap<JMenuItem, Long>();

    private void initRatingOfItem() {
        RATING_OF_ITEM.put(itemRating0, Long.valueOf(0));
        RATING_OF_ITEM.put(itemRating1, Long.valueOf(1));
        RATING_OF_ITEM.put(itemRating2, Long.valueOf(2));
        RATING_OF_ITEM.put(itemRating3, Long.valueOf(3));
        RATING_OF_ITEM.put(itemRating4, Long.valueOf(4));
        RATING_OF_ITEM.put(itemRating5, Long.valueOf(5));
    }

    private void initItemTexts() {
        TEXT_OF_ITEM.put(itemUpdateMetadata           , Bundle.getString("PopupMenuThumbnails.DisplayName.Action.UpdateMetadata"));
        TEXT_OF_ITEM.put(itemUpdateThumbnail          , Bundle.getString("PopupMenuThumbnails.DisplayName.Action.UpdateThumbnail"));
        TEXT_OF_ITEM.put(itemIptcToXmp                , Bundle.getString("PopupMenuThumbnails.DisplayName.Action.IptcToXmp"));
        TEXT_OF_ITEM.put(itemCreateImageCollection    , Bundle.getString("PopupMenuThumbnails.DisplayName.Action.CreateImageCollection"));
        TEXT_OF_ITEM.put(itemAddToImageCollection     , Bundle.getString("PopupMenuThumbnails.DisplayName.Action.AddToImageCollection"));
        TEXT_OF_ITEM.put(itemDeleteFromImageCollection, Bundle.getString("PopupMenuThumbnails.DisplayName.Action.DeleteFromImageCollection"));
        TEXT_OF_ITEM.put(itemRotateThumbnai90         , Bundle.getString("PopupMenuThumbnails.DisplayName.Action.Rotate.90"));
        TEXT_OF_ITEM.put(itemRotateThumbnai180        , Bundle.getString("PopupMenuThumbnails.DisplayName.Action.Rotate.180"));
        TEXT_OF_ITEM.put(itemRotateThumbnail270       , Bundle.getString("PopupMenuThumbnails.DisplayName.Action.Rotate.270"));
        TEXT_OF_ITEM.put(itemOpenFilesWithStandardApp , Bundle.getString("PopupMenuThumbnails.DisplayName.Action.OpenFiles"));
        TEXT_OF_ITEM.put(itemDeleteImageFromDatabase  , Bundle.getString("PopupMenuThumbnails.DisplayName.Action.DeleteImageFromDatabase"));
        TEXT_OF_ITEM.put(itemFileSystemCopyToDirectory, Bundle.getString("PopupMenuThumbnails.DisplayName.Action.FileSystemCopyToDirectory"));
        TEXT_OF_ITEM.put(itemFileSystemDeleteFiles    , Bundle.getString("PopupMenuThumbnails.DisplayName.Action.FileSystemDeleteFiles"));
        TEXT_OF_ITEM.put(itemFileSystemRenameFiles    , Bundle.getString("PopupMenuThumbnails.DisplayName.Action.FileSystemRename"));
        TEXT_OF_ITEM.put(itemFileSystemMoveFiles      , Bundle.getString("PopupMenuThumbnails.DisplayName.Action.FileSystemMove"));
        TEXT_OF_ITEM.put(itemRefresh                  , Bundle.getString("PopupMenuThumbnails.DisplayName.Action.Refresh"));
        TEXT_OF_ITEM.put(itemPick                     , Bundle.getString("PopupMenuThumbnails.DisplayName.Action.Pick"));
        TEXT_OF_ITEM.put(itemReject                   , Bundle.getString("PopupMenuThumbnails.DisplayName.Action.Reject"));
        TEXT_OF_ITEM.put(itemRating0                  , Bundle.getString("PopupMenuThumbnails.DisplayName.Rating0"));
        TEXT_OF_ITEM.put(itemRating1                  , Bundle.getString("PopupMenuThumbnails.DisplayName.Rating1"));
        TEXT_OF_ITEM.put(itemRating2                  , Bundle.getString("PopupMenuThumbnails.DisplayName.Rating2"));
        TEXT_OF_ITEM.put(itemRating3                  , Bundle.getString("PopupMenuThumbnails.DisplayName.Rating3"));
        TEXT_OF_ITEM.put(itemRating4                  , Bundle.getString("PopupMenuThumbnails.DisplayName.Rating4"));
        TEXT_OF_ITEM.put(itemRating5                  , Bundle.getString("PopupMenuThumbnails.DisplayName.Rating5"));
        TEXT_OF_ITEM.put(itemCopyMetadata             , Bundle.getString("PopupMenuThumbnails.DisplayName.ItemCopyMetadata"));
        TEXT_OF_ITEM.put(itemCopyToClipboard          , Bundle.getString("PopupMenuThumbnails.DisplayName.ItemCopyToClipboard"));
        TEXT_OF_ITEM.put(itemCutToClipboard           , Bundle.getString("PopupMenuThumbnails.DisplayName.ItemCutToClipboard"));
        TEXT_OF_ITEM.put(itemPasteFromClipboard       , Bundle.getString("PopupMenuThumbnails.DisplayName.ItemPasteFromClipboard"));
        TEXT_OF_ITEM.put(itemPasteMetadata            , Bundle.getString("PopupMenuThumbnails.DisplayName.ItemPasteMetadata"));
    }

    private void initItemIcons() {
        ICON_OF_ITEM.put(itemAddToImageCollection     , AppLookAndFeel.getIcon("icon_imagecollection_add_to.png"));
        ICON_OF_ITEM.put(itemCreateImageCollection    , AppLookAndFeel.getIcon("icon_imagecollection.png"));
        ICON_OF_ITEM.put(itemDeleteFromImageCollection, AppLookAndFeel.getIcon("icon_imagecollection_remove_from.png"));
        ICON_OF_ITEM.put(itemDeleteImageFromDatabase  , AppLookAndFeel.getIcon("icon_database_delete_from.png"));
        ICON_OF_ITEM.put(itemFileSystemCopyToDirectory, AppLookAndFeel.getIcon("icon_copy_to_folder.png"));
        ICON_OF_ITEM.put(itemFileSystemDeleteFiles    , AppLookAndFeel.getIcon("icon_delete.png"));
        ICON_OF_ITEM.put(itemFileSystemMoveFiles      , AppLookAndFeel.getIcon("icon_move_to_folder.png"));
        ICON_OF_ITEM.put(itemFileSystemRenameFiles    , AppLookAndFeel.getIcon("icon_rename.png"));
        ICON_OF_ITEM.put(itemRotateThumbnai180        , AppLookAndFeel.getIcon("icon_rotate_180.png"));
        ICON_OF_ITEM.put(itemRotateThumbnail270       , AppLookAndFeel.getIcon("icon_rotate_270.png"));
        ICON_OF_ITEM.put(itemRotateThumbnai90         , AppLookAndFeel.getIcon("icon_rotate_90.png"));
        ICON_OF_ITEM.put(itemUpdateMetadata           , AppLookAndFeel.getIcon("icon_metadata_refresh.png"));
        ICON_OF_ITEM.put(itemUpdateThumbnail          , AppLookAndFeel.getIcon("icon_image_refresh.png"));
        ICON_OF_ITEM.put(itemRefresh                  , AppLookAndFeel.getIcon("icon_refresh.png"));
        ICON_OF_ITEM.put(itemIptcToXmp                , AppLookAndFeel.getIcon("icon_iptc.png"));
        ICON_OF_ITEM.put(itemPick                     , AppLookAndFeel.getIcon("icon_picked.png"));
        ICON_OF_ITEM.put(itemReject                   , AppLookAndFeel.getIcon("icon_rejected.png"));
        ICON_OF_ITEM.put(menuRating                   , AppLookAndFeel.getIcon("icon_xmp_rating_set.png"));
        ICON_OF_ITEM.put(itemRating0                  , AppLookAndFeel.getIcon("icon_xmp_rating_remove.png"));
        ICON_OF_ITEM.put(itemRating1                  , AppLookAndFeel.getIcon("icon_xmp_rating_1.png"));
        ICON_OF_ITEM.put(itemRating2                  , AppLookAndFeel.getIcon("icon_xmp_rating_2.png"));
        ICON_OF_ITEM.put(itemRating3                  , AppLookAndFeel.getIcon("icon_xmp_rating_3.png"));
        ICON_OF_ITEM.put(itemRating4                  , AppLookAndFeel.getIcon("icon_xmp_rating_4.png"));
        ICON_OF_ITEM.put(itemRating5                  , AppLookAndFeel.getIcon("icon_xmp_rating_5.png"));
        ICON_OF_ITEM.put(itemCopyMetadata             , AppLookAndFeel.getIcon("icon_copy_metadata.png"));
        ICON_OF_ITEM.put(itemCopyToClipboard          , AppLookAndFeel.getIcon("icon_copy_to_clipboard.png"));
        ICON_OF_ITEM.put(itemCutToClipboard           , AppLookAndFeel.getIcon("icon_cut_to_clipboard.png"));
        ICON_OF_ITEM.put(itemPasteFromClipboard       , AppLookAndFeel.getIcon("icon_paste_from_clipboard.png"));
        ICON_OF_ITEM.put(itemPasteMetadata            , AppLookAndFeel.getIcon("icon_paste_metadata.png"));
    }

    private void addItems() {
        add(itemUpdateThumbnail);
        add(itemUpdateMetadata);


        menuMisc.add(itemIptcToXmp);
        menuMisc.add(itemDeleteImageFromDatabase);
        add(menuMisc);

        add(new JSeparator());
        add(itemOpenFilesWithStandardApp);
        add(menuPrograms);
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

        menuRotateThumbnail.add(itemRotateThumbnai90);
        menuRotateThumbnail.add(itemRotateThumbnai180);
        menuRotateThumbnail.add(itemRotateThumbnail270);
        add(menuRotateThumbnail);
        
        menuFsOps.add(itemCopyToClipboard);
        menuFsOps.add(itemCutToClipboard);
        menuFsOps.add(itemPasteFromClipboard);
        menuFsOps.add(itemCopyMetadata);
        menuFsOps.add(itemPasteMetadata);
        menuFsOps.add(new JSeparator());
        menuFsOps.add(itemFileSystemCopyToDirectory);
        menuFsOps.add(itemFileSystemRenameFiles);
        menuFsOps.add(itemFileSystemMoveFiles);
        menuFsOps.add(itemFileSystemDeleteFiles);
        add(menuFsOps);

        add(new JSeparator());
        add(itemRefresh);
    }

    private void addPluginItems() {
        Icon iconPlugin = AppLookAndFeel.getIcon("icon_plugin.png");
        menuPlugins.setIcon(iconPlugin);
        add(menuPlugins);
        Logger     logger     = Logger.getLogger("de.elmar_baumann.jpt.plugin");
        Properties properties = UserSettings.INSTANCE.getProperties();
        for (Plugin plugin : Lookup.lookupAll(Plugin.class)) {
            plugin.setProperties(properties);
            plugin.setLogger(logger);
            plugin.putValue(Action.SMALL_ICON, iconPlugin);
            menuPlugins.add(plugin);
        }
    }

    private PopupMenuThumbnails() {
        init();
    }

    private void setStandardAppIcon() {
        File app = new File(UserSettings.INSTANCE.getDefaultImageOpenApp());
        if (app.exists()) {
            itemOpenFilesWithStandardApp.setIcon(IconUtil.getSystemIcon(app));
        }
    }

    public void addOtherPrograms() {
        menuPrograms.removeAll();
        programOfMenuItem.clear();
        List<Program> programs = DatabasePrograms.INSTANCE.getAll(false);
        if (!programs.isEmpty()) {
            for (Program program : programs) {
                String alias = program.getAlias();
                JMenuItem item = new JMenuItem(alias);
                for (ActionListener listener : actionListenersOpenFilesWithOtherApp) {
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

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        if (evt.getType().equals(UserSettingsChangeEvent.Type.OTHER_IMAGE_OPEN_APPS)) {
            addOtherPrograms();
        }
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

    public JMenuItem getItemRotateThumbnai270() {
        return itemRotateThumbnail270;
    }

    public JMenuItem getItemRotateThumbnai90() {
        return itemRotateThumbnai90;
    }

    public JMenuItem getItemUpdateMetadata() {
        return itemUpdateMetadata;
    }

    public JMenuItem getItemUpdateThumbnail() {
        return itemUpdateThumbnail;
    }

    public JMenu getMenuOtherOpenImageApps() {
        return menuPrograms;
    }

    public JMenu getMenuPlugins() {
        return menuPlugins;
    }

    public JMenuItem getItemRefresh() {
        return itemRefresh;
    }

    public JMenuItem getItemIptcToXmp() {
        return itemIptcToXmp;
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

    public JMenu getMenuRating() {
        return menuRating;
    }

    public Long getRatingOfItem(JMenuItem item) {
        return RATING_OF_ITEM.get(item);
    }

    public Set<JMenuItem> getPluginMenuItems() {
        Set<JMenuItem> items     = new HashSet<JMenuItem>();
        int            itemCount = menuPlugins.getItemCount();

        for (int i = 0; i < itemCount; i++) {
            JMenuItem item = menuPlugins.getItem(i);
            if (item != null && item.getAction() instanceof Plugin) {
                items.add(item);
            }
        }
        return items;
    }

    public synchronized void addActionListenerOpenFilesWithOtherApp(ActionListener listener) {
        actionListenersOpenFilesWithOtherApp.add(listener);
        addOtherPrograms();
    }

    public Program getProgram(Object source) {
        assert source instanceof JMenuItem : "Not a JMenuItem: " + source;
        if (source instanceof JMenuItem) {
            return programOfMenuItem.get((JMenuItem) source);

        }
        return null;
    }

    private void init() {
        initRatingOfItem();
        initItemTexts();
        initItemIcons();
        addItems();
        setTexts();
        setIcons();
        setItemsEnabled();
        setAccelerators();
    }

    private void setItemsEnabled() {
        itemDeleteFromImageCollection.setEnabled(false);
    }

    private void setTexts() {
        for (JMenuItem item : TEXT_OF_ITEM.keySet()) {
            item.setText(TEXT_OF_ITEM.get(item));
        }
    }

    private void setIcons() {
        setStandardAppIcon();
        for (JMenuItem item : ICON_OF_ITEM.keySet()) {
            item.setIcon(ICON_OF_ITEM.get(item));
        }
    }

    private void setAccelerators() {
        itemDeleteFromImageCollection.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        itemFileSystemDeleteFiles    .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        itemFileSystemRenameFiles    .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        itemRefresh                  .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        itemCopyToClipboard          .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        itemCutToClipboard           .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        itemPasteFromClipboard       .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        itemCopyMetadata             .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
        itemPasteMetadata            .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
        itemPick                     .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0));
        itemReject                   .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0));
        itemRating0                  .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, 0));
        itemRating1                  .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, 0));
        itemRating2                  .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, 0));
        itemRating3                  .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, 0));
        itemRating4                  .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, 0));
        itemRating5                  .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, 0));
    }
}
