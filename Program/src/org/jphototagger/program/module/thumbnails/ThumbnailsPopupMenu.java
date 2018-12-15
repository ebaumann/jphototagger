package org.jphototagger.program.module.thumbnails;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.plugin.fileprocessor.FileProcessorPlugin;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.programs.ProgramType;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.domain.repository.event.programs.ProgramDeletedEvent;
import org.jphototagger.domain.repository.event.programs.ProgramInsertedEvent;
import org.jphototagger.domain.repository.event.programs.ProgramUpdatedEvent;
import org.jphototagger.domain.thumbnails.ThumbnailsPopupMenuItemProvider;
import org.jphototagger.lib.api.LayerUtil;
import org.jphototagger.lib.api.PositionProviderAscendingComparator;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.factory.FileProcessorPluginManager;
import org.jphototagger.program.module.actions.ActionsUtil;
import org.jphototagger.program.module.programs.AddProgramController;
import org.jphototagger.program.plugins.PluginAction;
import org.jphototagger.resources.Icons;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * Popup menu of the thumbnails panel.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class ThumbnailsPopupMenu extends JPopupMenu {

    public static final ImageIcon ICON_IMAGE_COLLECTION = Icons.getIcon("icon_imagecollection.png");
    private static final ImageIcon ICON_IMAGE_COLLECTION_ADD_TO = Icons.getIcon("icon_imagecollection_add_to.png");
    private static final ImageIcon ICON_IMAGE_COLLECTION_REMOVE_FROM = Icons.getIcon("icon_imagecollection_remove_from.png");
    private static final ImageIcon ICON_PICKED = Icons.getIcon("icon_picked.png");
    private static final ImageIcon ICON_REJECTED = Icons.getIcon("icon_rejected.png");
    private static final ImageIcon ICON_ROTATE_180 = Icons.getIcon("icon_rotate_180.png");
    private static final ImageIcon ICON_ROTATE_270 = Icons.getIcon("icon_rotate_270.png");
    private static final ImageIcon ICON_ROTATE_90 = Icons.getIcon("icon_rotate_90.png");
    private static final ImageIcon ICON_XMP_RATING_1 = Icons.getIcon("icon_xmp_rating_1.png");
    private static final ImageIcon ICON_XMP_RATING_2 = Icons.getIcon("icon_xmp_rating_2.png");
    private static final ImageIcon ICON_XMP_RATING_3 = Icons.getIcon("icon_xmp_rating_3.png");
    private static final ImageIcon ICON_XMP_RATING_4 = Icons.getIcon("icon_xmp_rating_4.png");
    private static final ImageIcon ICON_XMP_RATING_5 = Icons.getIcon("icon_xmp_rating_5.png");
    private static final ImageIcon ICON_XMP_RATING_REMOVE = Icons.getIcon("icon_xmp_rating_remove.png");
    private static final long serialVersionUID = 1L;
    private static final AddProgramController ADD_PROGRAM_ACTION = new AddProgramController();
    private final JMenu menuRefresh = UiFactory.menu(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.MenuRefresh"));
    private final JMenu menuPrograms = UiFactory.menu(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.MenuOtherOpenImageApps"));
    private final JMenu menuMetadata = UiFactory.menu(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.MenuMetadata"));
    private final JMenu menuImageCollection = UiFactory.menu(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.MenuImageCollection"));
    private final JMenu menuRotateThumbnail = UiFactory.menu(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.MenuRotateThumbnail"));
    private final JMenu menuRating = UiFactory.menu(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.menuRating"));
    private final JMenu menuPlugins = UiFactory.menu(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.MenuPlugins"));
    private final JMenu menuSelection = UiFactory.menu(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.MenuSelection"));
    private final JMenu menuFsOps = UiFactory.menu(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.MenuFileSystemOps"));
    private final JMenu menuActions = ActionsUtil.actionsAsMenu();
    private final JMenuItem itemUpdateThumbnail = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Action.UpdateThumbnail"));
    private final JMenuItem itemUpdateMetadata = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Action.UpdateMetadata"));
    private final JMenuItem itemSelectNothing = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.ItemSelectNothing"));
    private final JMenuItem itemSelectAll = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.ItemSelectAll"));
    private final JMenuItem itemRotateThumbnail90 = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Action.Rotate.90"), ICON_ROTATE_90);
    private final JMenuItem itemRotateThumbnail270 = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Action.Rotate.270"), ICON_ROTATE_270);
    private final JMenuItem itemRotateThumbnai180 = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Action.Rotate.180"), ICON_ROTATE_180);
    private final JMenuItem itemReject = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Action.Reject"), ICON_REJECTED);
    private final JMenuItem itemRefresh = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Action.Refresh"), Icons.ICON_REFRESH);
    private final JMenuItem itemRating5 = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Rating5"), ICON_XMP_RATING_5);
    private final JMenuItem itemRating4 = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Rating4"), ICON_XMP_RATING_4);
    private final JMenuItem itemRating3 = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Rating3"), ICON_XMP_RATING_3);
    private final JMenuItem itemRating2 = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Rating2"), ICON_XMP_RATING_2);
    private final JMenuItem itemRating1 = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Rating1"), ICON_XMP_RATING_1);
    private final JMenuItem itemRating0 = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Rating0"), ICON_XMP_RATING_REMOVE);
    private final JMenuItem itemPick = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Action.Pick"), ICON_PICKED);
    private final JMenuItem itemPasteMetadata = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.ItemPasteMetadata"), Icons.ICON_PASTE);
    private final JMenuItem itemPasteFromClipboard = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.ItemPasteFromClipboard"), Icons.ICON_PASTE);
    private final JMenuItem itemOpenFilesWithStandardApp = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Action.OpenFiles"));
    private final JMenuItem itemFileSystemRenameFiles = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Action.FileSystemRename"));
    private final JMenuItem itemFileSystemMoveFiles = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Action.FileSystemMove"));
    private final JMenuItem itemFileSystemDeleteFiles = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Action.FileSystemDeleteFiles"), Icons.ICON_DELETE);
    private final JMenuItem itemFileSystemCopyToDirectory = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Action.FileSystemCopyToDirectory"), Icons.ICON_COPY);
    private final JMenuItem itemDeleteImageFromRepository = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Action.DeleteImageFromRepository"));
    private final JMenuItem itemDeleteFromImageCollection = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Action.DeleteFromImageCollection"), ICON_IMAGE_COLLECTION_REMOVE_FROM);
    private final JMenuItem itemCutToClipboard = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.ItemCutToClipboard"), Icons.ICON_CUT);
    private final JMenuItem itemCreateImageCollection = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Action.CreateImageCollection"), ICON_IMAGE_COLLECTION);
    private final JMenuItem itemCopyToClipboard = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.ItemCopyToClipboard"), Icons.ICON_COPY);
    private final JMenuItem itemCopyMetadata = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.ItemCopyMetadata"), Icons.ICON_COPY);
    private final JMenuItem itemAddToImageCollection = UiFactory.menuItem(Bundle.getString(ThumbnailsPopupMenu.class, "ThumbnailsPopupMenu.DisplayName.Action.AddToImageCollection"), ICON_IMAGE_COLLECTION_ADD_TO);
    // End menu items
    private final List<ActionListener> actionListenersOpenFilesWithOtherApp = new ArrayList<>();
    private final Map<JMenuItem, Program> programOfMenuItem = new HashMap<>();
    private final Map<JMenuItem, Long> RATING_OF_ITEM = new HashMap<>();
    private final Map<JMenuItem, FileProcessorPlugin> FILE_PROCESSOR_PLUGIN_OF_ITEM = new HashMap<>();
    private final Map<JMenuItem, Action> ACTION_OF_ITEM = new HashMap<>();
    public static final ThumbnailsPopupMenu INSTANCE = new ThumbnailsPopupMenu();

    private ThumbnailsPopupMenu() {
        init();
    }

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
        menuRefresh.add(itemDeleteImageFromRepository);
        menuRefresh.add(itemRefresh);
        add(menuRefresh);
        menuRotateThumbnail.add(itemRotateThumbnail90);
        menuRotateThumbnail.add(itemRotateThumbnail270);
        menuRotateThumbnail.add(itemRotateThumbnai180);
        add(menuRotateThumbnail);
        add(new Separator());
        add(itemOpenFilesWithStandardApp);
        add(menuPrograms);
        add(menuActions);
        addPluginItems();
        add(new Separator());
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
        add(menuMetadata);
        itemPasteMetadata.setEnabled(false);
        menuFsOps.add(itemCopyToClipboard);
        menuFsOps.add(itemCutToClipboard);
        menuFsOps.add(itemPasteFromClipboard);
        menuFsOps.add(new Separator());
        menuFsOps.add(itemFileSystemCopyToDirectory);
        menuFsOps.add(new Separator());
        menuFsOps.add(itemFileSystemRenameFiles);
        menuFsOps.add(itemFileSystemMoveFiles);
        menuFsOps.add(itemFileSystemDeleteFiles);
        menuFsOps.add(new Separator());
        menuFsOps.add(itemSelectAll);
        menuFsOps.add(itemSelectNothing);
        add(menuFsOps);
        lookupItems();
    }

    private void lookupItems() {
        Collection<? extends ThumbnailsPopupMenuItemProvider> providers =
                Lookup.getDefault().lookupAll(ThumbnailsPopupMenuItemProvider.class);

        List<MenuItemProvider> rootItemProviders = new ArrayList<>();
        List<MenuItemProvider> refreshItemProviders = new ArrayList<>();
        List<MenuItemProvider> fileOperationItemProviders = new ArrayList<>();
        List<MenuItemProvider> metaDataItemProviders = new ArrayList<>();
        for (ThumbnailsPopupMenuItemProvider provider : providers) {
            rootItemProviders.addAll(provider.getRootMenuItems());
            refreshItemProviders.addAll(provider.getRefreshMenuItems());
            metaDataItemProviders.addAll(provider.getMetaDataMenuItems());
            fileOperationItemProviders.addAll(provider.getFileOperationsMenuItems());
        }
        insertMenuItems(rootItemProviders, null);
        insertMenuItems(refreshItemProviders, menuRefresh);
        insertMenuItems(metaDataItemProviders, menuMetadata);
        insertMenuItems(fileOperationItemProviders, menuFsOps);
    }

    private void insertMenuItems(List<MenuItemProvider> menuItemProviders, JMenu intoMenu) {
        Collections.sort(menuItemProviders, PositionProviderAscendingComparator.INSTANCE);
        LayerUtil.logWarningIfNotUniquePositions(menuItemProviders);
        for (MenuItemProvider menuItemProvider : menuItemProviders) {
            int intoItemCount = intoMenu == null ? getComponentCount() : intoMenu.getItemCount();
            int itemProviderPosition = menuItemProvider.getPosition();
            int itemIndex = itemProviderPosition <= intoItemCount ? itemProviderPosition : intoItemCount;
            if (menuItemProvider.isSeparatorBefore()) {
                if (intoMenu == null) {
                    add(new Separator(), itemIndex);
                } else {
                    intoMenu.add(new Separator(), itemIndex);
                }
                itemIndex++;
            }
            JMenuItem menuItem = menuItemProvider.getMenuItem();
            if (intoMenu == null) {
                add(menuItem, itemIndex);
            } else {
                intoMenu.add(menuItem, itemIndex);
            }
        }
    }

    private void addPluginItems() {
        if (!FileProcessorPluginManager.INSTANCE.hasEnabledPlugins()) {
            return;
        }

        add(menuPlugins);

        for (FileProcessorPlugin plugin : FileProcessorPluginManager.INSTANCE.getEnabledPlugins()) {
            if (plugin.isAvailable()) {
                addItemsOf(plugin);
            }
        }
    }

    private void addItemsOf(FileProcessorPlugin plugin) {
        PluginAction<FileProcessorPlugin> pluginAction = new PluginAction<>(plugin);
        JMenuItem pluginItem = UiFactory.menuItem(pluginAction);

        ACTION_OF_ITEM.put(pluginItem, pluginAction);
        FILE_PROCESSOR_PLUGIN_OF_ITEM.put(pluginItem, plugin);

        menuPlugins.add(pluginItem);
    }

    public void setOtherPrograms() {
        menuPrograms.removeAll();
        programOfMenuItem.clear();

        ProgramsRepository repo = Lookup.getDefault().lookup(ProgramsRepository.class);
        List<Program> programs = repo.findAllPrograms(ProgramType.PROGRAM);

        if (!programs.isEmpty()) {
            for (Program program : programs) {
                String alias = program.getAlias();
                JMenuItem item = UiFactory.menuItem(alias);

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

        menuPrograms.add(ADD_PROGRAM_ACTION);
    }

    public Action getActionOfItem(JMenuItem item) {
        return ACTION_OF_ITEM.get(item);
    }

    @EventSubscriber(eventClass = ProgramDeletedEvent.class)
    public void programDeleted(final ProgramDeletedEvent evt) {
        updatePrograms(evt.getProgram());
    }

    @EventSubscriber(eventClass = ProgramInsertedEvent.class)
    public void programInserted(final ProgramInsertedEvent evt) {
        updatePrograms(evt.getProgram());
    }

    @EventSubscriber(eventClass = ProgramUpdatedEvent.class)
    public void programUpdated(final ProgramUpdatedEvent evt) {
        updatePrograms(evt.getProgram());
    }

    private void updatePrograms(Program updatedProgram) {
        if (!updatedProgram.isAction()) {
            setOtherPrograms();
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

    public JMenuItem getItemDeleteImageFromRepository() {
        return itemDeleteImageFromRepository;
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

    public Set<JMenuItem> getFileProcessorPluginMenuItems() {
        return FILE_PROCESSOR_PLUGIN_OF_ITEM.keySet();
    }

    public FileProcessorPlugin getFileProcessorPluginOfItem(JMenuItem item) {
        return FILE_PROCESSOR_PLUGIN_OF_ITEM.get(item);
    }

    public synchronized void addActionListenerOpenFilesWithOtherApp(ActionListener listener) {
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
        UiFactory.configure(this);
        initRatingOfItem();
        addItems();
        itemDeleteFromImageCollection.setEnabled(false);
        setAccelerators();
        AnnotationProcessor.process(this);
    }

    private void setAccelerators() {
        itemDeleteFromImageCollection.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_DELETE));
        itemFileSystemDeleteFiles.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_DELETE));
        itemFileSystemRenameFiles.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F2));
        itemRefresh.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F5));
        itemCopyToClipboard.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_C));
        itemCutToClipboard.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_X));
        itemPasteFromClipboard.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_V));
        itemCopyMetadata.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_C));
        itemPasteMetadata.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_V));
        itemPick.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_P));
        itemReject.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_R));
        itemRating0.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_0));
        itemRating1.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_1));
        itemRating2.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_2));
        itemRating3.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_3));
        itemRating4.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_4));
        itemRating5.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_5));
        itemSelectAll.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_A));
    }
}
