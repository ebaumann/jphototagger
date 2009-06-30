package de.elmar_baumann.imv.view.popupmenus;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.database.DatabasePrograms;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.listener.UserSettingsChangeListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.image.icon.IconUtil;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

/**
 * Popupmenü für das Thumbnailpanel
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class PopupMenuThumbnails extends JPopupMenu
        implements UserSettingsChangeListener {

    private static final String DISPLAY_NAME_ACTION_UPDATE_METADATA =
            Bundle.getString("PopupMenuPanelThumbnails.Action.UpdateMetadata");
    private static final String DISPLAY_NAME_ACTION_UPDATE_THUMBNAIL =
            Bundle.getString("PopupMenuPanelThumbnails.Action.UpdateThumbnail");
    private static final String DISPLAY_NAME_ACTION_CREATE_IMAGE_COLLECTION =
            Bundle.getString(
            "PopupMenuPanelThumbnails.Action.CreateImageCollection");
    private static final String DISPLAY_NAME_ACTION_ADD_TO_IMAGE_COLLECTION =
            Bundle.getString(
            "PopupMenuPanelThumbnails.Action.AddToImageCollection");
    private static final String DISPLAY_NAME_ACTION_DELETE_FROM_IMAGE_COLLECTION =
            Bundle.getString(
            "PopupMenuPanelThumbnails.Action.DeleteFromImageCollection");
    private static final String DISPLAY_NAME_ACTION_ROTATE_90_DEGREES =
            Bundle.getString("PopupMenuPanelThumbnails.Action.Rotate.90");
    private static final String DISPLAY_NAME_ACTION_ROTATE_180_DEGREES =
            Bundle.getString("PopupMenuPanelThumbnails.Action.Rotate.180");
    private static final String DISPLAY_NAME_ACTION_ROTATE_270_DEGREES =
            Bundle.getString("PopupMenuPanelThumbnails.Action.Rotate.270");
    private static final String DISPLAY_NAME_ACTION_OPEN_FILES = Bundle.
            getString(
            "PopupMenuPanelThumbnails.Action.OpenFiles");
    private static final String DISPLAY_NAME_ACTION_DELETE_IMAGE_FROM_DATABASE =
            Bundle.getString(
            "PopupMenuPanelThumbnails.Action.DeleteImageFromDatabase");
    private static final String DISPLAY_NAME_ACTION_FILESYSTEM_COPY_TO_FOLDER =
            Bundle.getString(
            "PopupMenuPanelThumbnails.Action.FileSystemCopyToDirectory");
    private static final String DISPLAY_NAME_ACTION_FILESYSTEM_DELETE_FILES =
            Bundle.getString(
            "PopupMenuPanelThumbnails.Action.FileSystemDeleteFiles");
    private static final String DISPLAY_NAME_ACTION_FILESYSTEM_RENAME_FILES =
            Bundle.getString("PopupMenuPanelThumbnails.Action.FileSystemRename");
    private static final String DISPLAY_NAME_ACTION_FILESYSTEM_MOVE_FILES =
            Bundle.getString("PopupMenuPanelThumbnails.Action.FileSystemMove");
    private final JMenu menuPrograms = new JMenu(Bundle.getString(
            "PopupMenuPanelThumbnails.menuOtherOpenImageApps.text"));
    private final JMenuItem itemUpdateMetadata = new JMenuItem(
            DISPLAY_NAME_ACTION_UPDATE_METADATA);
    private final JMenuItem itemUpdateThumbnail = new JMenuItem(
            DISPLAY_NAME_ACTION_UPDATE_THUMBNAIL);
    private final JMenuItem itemCreateImageCollection = new JMenuItem(
            DISPLAY_NAME_ACTION_CREATE_IMAGE_COLLECTION);
    private final JMenuItem itemAddToImageCollection = new JMenuItem(
            DISPLAY_NAME_ACTION_ADD_TO_IMAGE_COLLECTION);
    private final JMenuItem itemDeleteFromImageCollection = new JMenuItem(
            DISPLAY_NAME_ACTION_DELETE_FROM_IMAGE_COLLECTION);
    private final JMenuItem itemRotateThumbnai90 = new JMenuItem(
            DISPLAY_NAME_ACTION_ROTATE_90_DEGREES);
    private final JMenuItem itemRotateThumbnai180 = new JMenuItem(
            DISPLAY_NAME_ACTION_ROTATE_180_DEGREES);
    private final JMenuItem itemRotateThumbnai270 = new JMenuItem(
            DISPLAY_NAME_ACTION_ROTATE_270_DEGREES);
    private final JMenuItem itemDeleteImageFromDatabase = new JMenuItem(
            DISPLAY_NAME_ACTION_DELETE_IMAGE_FROM_DATABASE);
    private final JMenuItem itemOpenFilesWithStandardApp = new JMenuItem(
            DISPLAY_NAME_ACTION_OPEN_FILES);
    private final JMenuItem itemFileSystemCopyToDirectory = new JMenuItem(
            DISPLAY_NAME_ACTION_FILESYSTEM_COPY_TO_FOLDER);
    private final JMenuItem itemFileSystemDeleteFiles = new JMenuItem(
            DISPLAY_NAME_ACTION_FILESYSTEM_DELETE_FILES);
    private final JMenuItem itemFileSystemRenameFiles = new JMenuItem(
            DISPLAY_NAME_ACTION_FILESYSTEM_RENAME_FILES);
    private final JMenuItem itemFileSystemMoveFiles = new JMenuItem(
            DISPLAY_NAME_ACTION_FILESYSTEM_MOVE_FILES);
    private final List<ActionListener> actionListenersOpenFilesWithOtherApp =
            new ArrayList<ActionListener>();
    private final Map<JMenuItem, Program> programOfMenuItem =
            new HashMap<JMenuItem, Program>();
    public static final PopupMenuThumbnails INSTANCE =
            new PopupMenuThumbnails();

    private PopupMenuThumbnails() {
        initItems();
        setIcons();
        addItems();
    }

    private void initItems() {
        itemDeleteFromImageCollection.setEnabled(false);
    }

    private void setIcons() {
        itemAddToImageCollection.setIcon(AppIcons.getIcon(
                "icon_imagecollection_add_to.png"));
        itemCreateImageCollection.setIcon(AppIcons.getIcon(
                "icon_imagecollection.png"));
        itemDeleteFromImageCollection.setIcon(AppIcons.getIcon(
                "icon_imagecollection_remove_from.png"));
        itemDeleteImageFromDatabase.setIcon(AppIcons.getIcon(
                "icon_database_delete_from.png"));
        itemFileSystemCopyToDirectory.setIcon(
                AppIcons.getIcon("icon_copy_to_folder.png"));
        itemFileSystemDeleteFiles.setIcon(AppIcons.getIcon(
                "icon_edit_delete.png"));
        itemFileSystemMoveFiles.setIcon(AppIcons.getIcon(
                "icon_move_to_folder.png"));
        itemFileSystemRenameFiles.setIcon(AppIcons.getIcon("icon_rename.png"));
        setStandardAppIcon();
        itemRotateThumbnai180.setIcon(AppIcons.getIcon("icon_rotate_180.png"));
        itemRotateThumbnai270.setIcon(AppIcons.getIcon("icon_rotate_270.png"));
        itemRotateThumbnai90.setIcon(AppIcons.getIcon("icon_rotate_90.png"));
        itemUpdateMetadata.setIcon(AppIcons.getIcon("icon_metadata_refresh.png"));
        itemUpdateThumbnail.setIcon(AppIcons.getIcon("icon_image_refresh.png"));
    }

    private void setStandardAppIcon() {
        File app = new File(UserSettings.INSTANCE.getDefaultImageOpenApp());
        if (app.exists()) {
            itemOpenFilesWithStandardApp.setIcon(IconUtil.getSystemIcon(app));
        }
    }

    private void addItems() {
        add(itemUpdateThumbnail);
        add(itemUpdateMetadata);
        add(itemDeleteImageFromDatabase);
        add(new JSeparator());
        add(itemOpenFilesWithStandardApp);
        add(menuPrograms);
        add(new JSeparator());
        add(itemCreateImageCollection);
        add(itemAddToImageCollection);
        add(itemDeleteFromImageCollection);
        add(new JSeparator());
        add(itemRotateThumbnai90);
        add(itemRotateThumbnai180);
        add(itemRotateThumbnai270);
        add(new JSeparator());
        add(itemFileSystemCopyToDirectory);
        add(itemFileSystemRenameFiles);
        add(itemFileSystemMoveFiles);
        add(itemFileSystemDeleteFiles);
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
        return itemRotateThumbnai270;
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

    public synchronized void addActionListenerOpenFilesWithOtherApp(
            ActionListener listener) {
        actionListenersOpenFilesWithOtherApp.add(listener);
        addOtherPrograms();
    }

    public Program getProgram(Object source) {
        return programOfMenuItem.get(source);
    }
}
