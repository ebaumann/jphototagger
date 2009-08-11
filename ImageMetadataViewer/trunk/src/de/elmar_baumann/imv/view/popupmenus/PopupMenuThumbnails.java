package de.elmar_baumann.imv.view.popupmenus;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.database.DatabasePrograms;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.listener.UserSettingsChangeListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.image.util.IconUtil;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static final PopupMenuThumbnails INSTANCE = new PopupMenuThumbnails();
    private final JMenu menuPrograms = new JMenu(Bundle.getString(
            "PopupMenuThumbnails.DisplayName.menuOtherOpenImageApps")); // NOI18N
    private final JMenu menuRating = new JMenu(Bundle.getString(
            "PopupMenuThumbnails.DisplayName.menuRating")); // NOI18N
    private final JMenuItem itemUpdateMetadata = new JMenuItem();
    private final JMenuItem itemUpdateThumbnail = new JMenuItem();
    private final JMenuItem itemIptcToXmp = new JMenuItem();
    private final JMenuItem itemCreateImageCollection = new JMenuItem();
    private final JMenuItem itemAddToImageCollection = new JMenuItem();
    private final JMenuItem itemDeleteFromImageCollection = new JMenuItem();
    private final JMenuItem itemRotateThumbnai90 = new JMenuItem();
    private final JMenuItem itemRotateThumbnai180 = new JMenuItem();
    private final JMenuItem itemRotateThumbnail270 = new JMenuItem();
    private final JMenuItem itemDeleteImageFromDatabase = new JMenuItem();
    private final JMenuItem itemOpenFilesWithStandardApp = new JMenuItem();
    private final JMenuItem itemFileSystemCopyToDirectory = new JMenuItem();
    private final JMenuItem itemFileSystemDeleteFiles = new JMenuItem();
    private final JMenuItem itemFileSystemRenameFiles = new JMenuItem();
    private final JMenuItem itemFileSystemMoveFiles = new JMenuItem();
    private final JMenuItem itemRefresh = new JMenuItem();
    private final JMenuItem itemPick = new JMenuItem();
    private final JMenuItem itemReject = new JMenuItem();
    private final JMenuItem itemRating0 = new JMenuItem();
    private final JMenuItem itemRating1 = new JMenuItem();
    private final JMenuItem itemRating2 = new JMenuItem();
    private final JMenuItem itemRating3 = new JMenuItem();
    private final JMenuItem itemRating4 = new JMenuItem();
    private final JMenuItem itemRating5 = new JMenuItem();
    // End menu items
    private final List<ActionListener> actionListenersOpenFilesWithOtherApp =
            new ArrayList<ActionListener>();
    private final Map<JMenuItem, Program> programOfMenuItem =
            new HashMap<JMenuItem, Program>();
    private final Map<JMenuItem, String> TEXT_OF_ITEM =
            new HashMap<JMenuItem, String>();
    private final Map<JMenuItem, Icon> ICON_OF_ITEM =
            new HashMap<JMenuItem, Icon>();
    private final Map<JMenuItem, Long> RATING_OF_ITEM =
            new HashMap<JMenuItem, Long>();

    private void initRatingOfItem() {
        RATING_OF_ITEM.put(itemRating0, Long.valueOf(0));
        RATING_OF_ITEM.put(itemRating1, Long.valueOf(1));
        RATING_OF_ITEM.put(itemRating2, Long.valueOf(2));
        RATING_OF_ITEM.put(itemRating3, Long.valueOf(3));
        RATING_OF_ITEM.put(itemRating4, Long.valueOf(4));
        RATING_OF_ITEM.put(itemRating5, Long.valueOf(5));
    }

    private void initItemTexts() {
        TEXT_OF_ITEM.put(itemUpdateMetadata, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Action.UpdateMetadata")); // NOI18N
        TEXT_OF_ITEM.put(itemUpdateThumbnail, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Action.UpdateThumbnail")); // NOI18N
        TEXT_OF_ITEM.put(itemIptcToXmp, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Action.IptcToXmp")); // NOI18N
        TEXT_OF_ITEM.put(itemCreateImageCollection, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Action.CreateImageCollection")); // NOI18N
        TEXT_OF_ITEM.put(itemAddToImageCollection, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Action.AddToImageCollection")); // NOI18N
        TEXT_OF_ITEM.put(itemDeleteFromImageCollection,
                Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Action.DeleteFromImageCollection")); // NOI18N
        TEXT_OF_ITEM.put(itemRotateThumbnai90, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Action.Rotate.90")); // NOI18N
        TEXT_OF_ITEM.put(itemRotateThumbnai180, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Action.Rotate.180")); // NOI18N
        TEXT_OF_ITEM.put(itemRotateThumbnail270, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Action.Rotate.270")); // NOI18N
        TEXT_OF_ITEM.put(itemOpenFilesWithStandardApp, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Action.OpenFiles")); // NOI18N
        TEXT_OF_ITEM.put(itemDeleteImageFromDatabase,
                Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Action.DeleteImageFromDatabase")); // NOI18N
        TEXT_OF_ITEM.put(itemFileSystemCopyToDirectory,
                Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Action.FileSystemCopyToDirectory")); // NOI18N
        TEXT_OF_ITEM.put(itemFileSystemDeleteFiles, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Action.FileSystemDeleteFiles")); // NOI18N
        TEXT_OF_ITEM.put(itemFileSystemRenameFiles, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Action.FileSystemRename")); // NOI18N
        TEXT_OF_ITEM.put(itemFileSystemMoveFiles, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Action.FileSystemMove")); // NOI18N
        TEXT_OF_ITEM.put(itemRefresh, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Action.Refresh")); // NOI18N
        TEXT_OF_ITEM.put(itemPick, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Action.Pick")); // NOI18N
        TEXT_OF_ITEM.put(itemReject, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Action.Reject")); // NOI18N
        TEXT_OF_ITEM.put(itemRating0, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Rating0")); // NOI18N
        TEXT_OF_ITEM.put(itemRating1, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Rating1")); // NOI18N
        TEXT_OF_ITEM.put(itemRating2, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Rating2")); // NOI18N
        TEXT_OF_ITEM.put(itemRating3, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Rating3")); // NOI18N
        TEXT_OF_ITEM.put(itemRating4, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Rating4")); // NOI18N
        TEXT_OF_ITEM.put(itemRating5, Bundle.getString(
                "PopupMenuThumbnails.DisplayName.Rating5")); // NOI18N
    }

    private void initItemIcons() {
        ICON_OF_ITEM.put(itemAddToImageCollection, AppIcons.getIcon(
                "icon_imagecollection_add_to.png")); // NOI18N
        ICON_OF_ITEM.put(itemCreateImageCollection, AppIcons.getIcon(
                "icon_imagecollection.png")); // NOI18N
        ICON_OF_ITEM.put(itemDeleteFromImageCollection, AppIcons.getIcon(
                "icon_imagecollection_remove_from.png")); // NOI18N
        ICON_OF_ITEM.put(itemDeleteImageFromDatabase, AppIcons.getIcon(
                "icon_database_delete_from.png")); // NOI18N
        ICON_OF_ITEM.put(itemFileSystemCopyToDirectory, AppIcons.getIcon(
                "icon_copy_to_folder.png")); // NOI18N
        ICON_OF_ITEM.put(itemFileSystemDeleteFiles, AppIcons.getIcon(
                "icon_delete.png")); // NOI18N
        ICON_OF_ITEM.put(itemFileSystemMoveFiles, AppIcons.getIcon(
                "icon_move_to_folder.png")); // NOI18N
        ICON_OF_ITEM.put(itemFileSystemRenameFiles, AppIcons.getIcon(
                "icon_rename.png")); // NOI18N
        ICON_OF_ITEM.put(itemRotateThumbnai180, AppIcons.getIcon(
                "icon_rotate_180.png")); // NOI18N
        ICON_OF_ITEM.put(itemRotateThumbnail270, AppIcons.getIcon(
                "icon_rotate_270.png")); // NOI18N
        ICON_OF_ITEM.put(itemRotateThumbnai90, AppIcons.getIcon(
                "icon_rotate_90.png")); // NOI18N
        ICON_OF_ITEM.put(itemUpdateMetadata, AppIcons.getIcon(
                "icon_metadata_refresh.png")); // NOI18N
        ICON_OF_ITEM.put(itemUpdateThumbnail, AppIcons.getIcon(
                "icon_image_refresh.png")); // NOI18N
        ICON_OF_ITEM.put(itemRefresh, AppIcons.getIcon("icon_refresh.png")); // NOI18N
        ICON_OF_ITEM.put(itemIptcToXmp, AppIcons.getIcon("icon_iptc.png")); // NOI18N
        ICON_OF_ITEM.put(itemPick, AppIcons.getIcon("icon_picked.png")); // NOI18N
        ICON_OF_ITEM.put(itemReject, AppIcons.getIcon("icon_rejected.png")); // NOI18N
        ICON_OF_ITEM.put(menuRating, AppIcons.getIcon("icon_xmp_rating_set.png")); // NOI18N
        ICON_OF_ITEM.put(itemRating0, AppIcons.getIcon(
                "icon_xmp_rating_remove.png")); // NOI18N
        ICON_OF_ITEM.put(itemRating1, AppIcons.getIcon("icon_xmp_rating_1.png")); // NOI18N
        ICON_OF_ITEM.put(itemRating2, AppIcons.getIcon("icon_xmp_rating_2.png")); // NOI18N
        ICON_OF_ITEM.put(itemRating3, AppIcons.getIcon("icon_xmp_rating_3.png")); // NOI18N
        ICON_OF_ITEM.put(itemRating4, AppIcons.getIcon("icon_xmp_rating_4.png")); // NOI18N
        ICON_OF_ITEM.put(itemRating5, AppIcons.getIcon("icon_xmp_rating_5.png")); // NOI18N
    }

    private void addItems() {
        addRatingItems();
        add(itemUpdateThumbnail);
        add(itemUpdateMetadata);
        add(itemIptcToXmp);
        add(itemDeleteImageFromDatabase);
        add(new JSeparator());
        add(itemOpenFilesWithStandardApp);
        add(menuPrograms);
        add(new JSeparator());
        add(menuRating);
        add(itemPick);
        add(itemReject);
        add(new JSeparator());
        add(itemCreateImageCollection);
        add(itemAddToImageCollection);
        add(itemDeleteFromImageCollection);
        add(new JSeparator());
        add(itemRotateThumbnai90);
        add(itemRotateThumbnai180);
        add(itemRotateThumbnail270);
        add(new JSeparator());
        add(itemFileSystemCopyToDirectory);
        add(itemFileSystemRenameFiles);
        add(itemFileSystemMoveFiles);
        add(itemFileSystemDeleteFiles);
        add(new JSeparator());
        add(itemRefresh);
    }

    private void addRatingItems() {
        menuRating.add(itemRating0);
        menuRating.add(itemRating1);
        menuRating.add(itemRating2);
        menuRating.add(itemRating3);
        menuRating.add(itemRating4);
        menuRating.add(itemRating5);
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

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        if (evt.getType().equals(
                UserSettingsChangeEvent.Type.OTHER_IMAGE_OPEN_APPS)) {
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

    public JMenuItem getItemRefresh() {
        return itemRefresh;
    }

    public JMenuItem getItemIptcToXmp() {
        return itemIptcToXmp;
    }

    public JMenuItem getItemPick() {
        return itemPick;
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

    public synchronized void addActionListenerOpenFilesWithOtherApp(
            ActionListener listener) {
        actionListenersOpenFilesWithOtherApp.add(listener);
        addOtherPrograms();
    }

    public Program getProgram(Object source) {
        assert source instanceof JMenuItem : "Not a JMenuItem: " + source; // NOI18N
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
        itemDeleteFromImageCollection.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        itemFileSystemDeleteFiles.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        itemFileSystemRenameFiles.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        itemRefresh.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        itemPick.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0));
        itemReject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0));
        itemRating0.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, 0));
        itemRating1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, 0));
        itemRating2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, 0));
        itemRating3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, 0));
        itemRating4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, 0));
        itemRating5.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, 0));
    }
}
