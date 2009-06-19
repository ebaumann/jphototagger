package de.elmar_baumann.imv.view.popupmenus;

import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.database.DatabasePrograms;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.listener.UserSettingsChangeListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.tasks.InsertImageFilesIntoDatabase;
import de.elmar_baumann.lib.image.icon.IconUtil;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EnumSet;
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
public final class PopupMenuPanelThumbnails extends JPopupMenu
        implements UserSettingsChangeListener {

    private final String actionUpdateMetadata = Bundle.getString("PopupMenuPanelThumbnails.Action.UpdateMetadata");
    private final String actionUpdateThumbnail = Bundle.getString("PopupMenuPanelThumbnails.Action.UpdateThumbnail");
    private final String actionCreateImageCollection = Bundle.getString("PopupMenuPanelThumbnails.Action.CreateImageCollection");
    private final String actionAddToImageCollection = Bundle.getString("PopupMenuPanelThumbnails.Action.AddToImageCollection");
    private final String actionDeleteFromImageCollection = Bundle.getString("PopupMenuPanelThumbnails.Action.DeleteFromImageCollection");
    private final String actionRotate90 = Bundle.getString("PopupMenuPanelThumbnails.Action.Rotate.90");
    private final String actionRotate180 = Bundle.getString("PopupMenuPanelThumbnails.Action.Rotate.180");
    private final String actionRotate270 = Bundle.getString("PopupMenuPanelThumbnails.Action.Rotate.270");
    private final String actionOpenFiles = Bundle.getString("PopupMenuPanelThumbnails.Action.OpenFiles");
    private final String actionDeleteImageFromDatabase = Bundle.getString("PopupMenuPanelThumbnails.Action.DeleteImageFromDatabase");
    private final String actionFileSystemCopyToDirectory = Bundle.getString("PopupMenuPanelThumbnails.Action.FileSystemCopyToDirectory");
    private final String actionFileSystemDeleteFiles = Bundle.getString("PopupMenuPanelThumbnails.Action.FileSystemDeleteFiles");
    private final String actionFileSystemRenameFiles = Bundle.getString("PopupMenuPanelThumbnails.Action.FileSystemRename");
    private final String actionFileSystemMoveFiles = Bundle.getString("PopupMenuPanelThumbnails.Action.FileSystemMove");
    private final JMenu menuPrograms = new JMenu(Bundle.getString("PopupMenuPanelThumbnails.menuOtherOpenImageApps.text"));
    private final JMenuItem itemUpdateMetadata = new JMenuItem(actionUpdateMetadata);
    private final JMenuItem itemUpdateThumbnail = new JMenuItem(actionUpdateThumbnail);
    private final JMenuItem itemCreateImageCollection = new JMenuItem(actionCreateImageCollection);
    private final JMenuItem itemAddToImageCollection = new JMenuItem(actionAddToImageCollection);
    private final JMenuItem itemDeleteFromImageCollection = new JMenuItem(actionDeleteFromImageCollection);
    private final JMenuItem itemRotateThumbnai90 = new JMenuItem(actionRotate90);
    private final JMenuItem itemRotateThumbnai180 = new JMenuItem(actionRotate180);
    private final JMenuItem itemRotateThumbnai270 = new JMenuItem(actionRotate270);
    private final JMenuItem itemDeleteImageFromDatabase = new JMenuItem(actionDeleteImageFromDatabase);
    private final JMenuItem itemOpenFilesWithStandardApp = new JMenuItem(actionOpenFiles);
    private final JMenuItem itemFileSystemCopyToDirectory = new JMenuItem(actionFileSystemCopyToDirectory);
    private final JMenuItem itemFileSystemDeleteFiles = new JMenuItem(actionFileSystemDeleteFiles);
    private final JMenuItem itemFileSystemRenameFiles = new JMenuItem(actionFileSystemRenameFiles);
    private final JMenuItem itemFileSystemMoveFiles = new JMenuItem(actionFileSystemMoveFiles);
    private final List<ActionListener> actionListenersOpenFilesWithOtherApp = new ArrayList<ActionListener>();
    private final Map<JMenuItem, Float> angleOfItem = new HashMap<JMenuItem, Float>();
    private final Map<JMenuItem, EnumSet<InsertImageFilesIntoDatabase.Insert>> databaseUpdateOfMenuItem = new HashMap<JMenuItem, EnumSet<InsertImageFilesIntoDatabase.Insert>>();
    private final Map<JMenuItem, Program> programOfMenuItem = new HashMap<JMenuItem, Program>();
    public static final PopupMenuPanelThumbnails INSTANCE = new PopupMenuPanelThumbnails();

    private PopupMenuPanelThumbnails() {
        initMaps();
        initItems();
        addItems();
    }

    private void initItems() {
        itemDeleteFromImageCollection.setEnabled(false);
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

    public synchronized void addActionListenerOpenFilesWithOtherApp(ActionListener listener) {
        actionListenersOpenFilesWithOtherApp.add(listener);
        addOtherPrograms();
    }

    public synchronized void addActionListenerOpenFilesWithStandardApp(ActionListener listener) {
        itemOpenFilesWithStandardApp.addActionListener(listener);
    }

    public synchronized void addActionListenerCreateImageCollection(ActionListener listener) {
        itemCreateImageCollection.addActionListener(listener);
    }

    public synchronized void addActionListenerAddToImageCollection(ActionListener listener) {
        itemAddToImageCollection.addActionListener(listener);
    }

    public synchronized void addActionListenerDeleteFromImageCollection(ActionListener listener) {
        itemDeleteFromImageCollection.addActionListener(listener);
    }

    public synchronized void addActionListenerUpdateMetadata(ActionListener listener) {
        itemUpdateMetadata.addActionListener(listener);
    }

    public synchronized void addActionListenerRotateThumbnail90(ActionListener listener) {
        itemRotateThumbnai90.addActionListener(listener);
    }

    public synchronized void addActionListenerRotateThumbnail180(ActionListener listener) {
        itemRotateThumbnai180.addActionListener(listener);
    }

    public synchronized void addActionListenerRotateThumbnail270(ActionListener listener) {
        itemRotateThumbnai270.addActionListener(listener);
    }

    public synchronized void addActionListenerDeleteThumbnail(ActionListener listener) {
        itemDeleteImageFromDatabase.addActionListener(listener);
    }

    public synchronized void addActionListenerCopySelectedFilesToDirectory(ActionListener listener) {
        itemFileSystemCopyToDirectory.addActionListener(listener);
    }

    public synchronized void addActionListenerFileSystemDeleteFiles(ActionListener listener) {
        itemFileSystemDeleteFiles.addActionListener(listener);
    }

    public synchronized void addActionListenerFileSystemRenameFiles(ActionListener listener) {
        itemFileSystemRenameFiles.addActionListener(listener);
    }

    public synchronized void addActionListenerFileSystemMoveFiles(ActionListener listener) {
        itemFileSystemMoveFiles.addActionListener(listener);
    }

    public synchronized void addActionListenerUpdateThumbnail(ActionListener listener) {
        itemUpdateThumbnail.addActionListener(listener);
    }

    public boolean isDeleteFiles(Object source) {
        return source == itemFileSystemDeleteFiles;
    }

    public Program getProgram(Object source) {
        return programOfMenuItem.get(source);
    }

    /**
     * Liefert den Winkel, um den das THUMBNAIL gedreht werden soll.
     * 
     * @param item  Item, das die Aktion auslöste
     * @return      Winkel in Grad; 0 Grad, wenn das Kommando keine Drehung ist
     */
    public float getRotateAngle(Object item) {
        Float angle = new Float(0);

        if (angleOfItem.containsKey(item)) {
            angle = angleOfItem.get(item);
        }

        return angle.floatValue();
    }

    public EnumSet<InsertImageFilesIntoDatabase.Insert> getMetadataToInsertIntoDatabase(Object item) {
        return databaseUpdateOfMenuItem.get(item);
    }

    private void initMaps() {
        angleOfItem.put(itemRotateThumbnai90, new Float(90));
        angleOfItem.put(itemRotateThumbnai180, new Float(180));
        angleOfItem.put(itemRotateThumbnai270, new Float(270));

        databaseUpdateOfMenuItem.put(
            itemUpdateMetadata, EnumSet.of(
            InsertImageFilesIntoDatabase.Insert.EXIF,
            InsertImageFilesIntoDatabase.Insert.XMP));
        databaseUpdateOfMenuItem.put(
            itemUpdateThumbnail, EnumSet.of(
            InsertImageFilesIntoDatabase.Insert.THUMBNAIL));
    }
}
