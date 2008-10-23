package de.elmar_baumann.imv.view.popupmenus;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.UserSettingsChangeListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.types.DatabaseUpdate;
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
public class PopupMenuPanelThumbnails extends JPopupMenu
    implements UserSettingsChangeListener {

    private final String actionUpdateAllMetadata = Bundle.getString("PopupMenuPanelThumbnails.Action.UpdateAllMetadata");
    private final String actionUpdateThumbnail = Bundle.getString("PopupMenuPanelThumbnails.Action.UpdateThumbnail");
    private final String actionUpdateXmp = Bundle.getString("PopupMenuPanelThumbnails.Action.UpdateXmp");
    private final String actionCreateImageCollection = Bundle.getString("PopupMenuPanelThumbnails.Action.CreateImageCollection");
    private final String actionAddToImageCollection = Bundle.getString("PopupMenuPanelThumbnails.Action.AddToImageCollection");
    private final String actionDeleteFromImageCollection = Bundle.getString("PopupMenuPanelThumbnails.Action.DeleteFromImageCollection");
    private final String actionRotate90 = Bundle.getString("PopupMenuPanelThumbnails.Action.Rotate.90");
    private final String actionRotate180 = Bundle.getString("PopupMenuPanelThumbnails.Action.Rotate.180");
    private final String actionRotate270 = Bundle.getString("PopupMenuPanelThumbnails.Action.Rotate.270");
    private final String actionOpenFiles = Bundle.getString("PopupMenuPanelThumbnails.Action.OpenFiles");
    private final String actionDeleteImageFromDatabase = Bundle.getString("PopupMenuPanelThumbnails.Action.DeleteImageFromDatabase");
    private final String actionCopySelectedFilesToDirectory = Bundle.getString("PopupMenuPanelThumbnails.Action.CopySelectedFilesToDirectory");
    private final String actionRenameInXmpColumns = Bundle.getString("PopupMenuPanelThumbnails.Action.RenameInXmpColumns");
    private final String actionFileSystemDeleteFiles = Bundle.getString("PopupMenuPanelThumbnails.Action.FileSystemDeleteFiles");
    private final String actionFileSystemRenameFiles = Bundle.getString("PopupMenuPanelThumbnails.Action.FileSystemRename");
    private final String actionFileSystemMoveFiles = Bundle.getString("PopupMenuPanelThumbnails.Action.FileSystemMove");
    private JMenu menuOtherOpenImageApps = new JMenu(Bundle.getString("PopupMenuPanelThumbnails.menuOtherOpenImageApps.text"));
    private final JMenuItem itemUpdateAllMetadata = new JMenuItem(actionUpdateAllMetadata);
    private final JMenuItem itemUpdateThumbnail = new JMenuItem(actionUpdateThumbnail);
    private final JMenuItem itemUpdateXmp = new JMenuItem(actionUpdateXmp);
    private final JMenuItem itemRenameInXmpColumns = new JMenuItem(actionRenameInXmpColumns);
    private final JMenuItem itemCreateImageCollection = new JMenuItem(actionCreateImageCollection);
    private final JMenuItem itemAddToImageCollection = new JMenuItem(actionAddToImageCollection);
    private final JMenuItem itemDeleteFromImageCollection = new JMenuItem(actionDeleteFromImageCollection);
    private final JMenuItem itemRotateThumbnai90 = new JMenuItem(actionRotate90);
    private final JMenuItem itemRotateThumbnai180 = new JMenuItem(actionRotate180);
    private final JMenuItem itemRotateThumbnai270 = new JMenuItem(actionRotate270);
    private final JMenuItem itemDeleteThumbnail = new JMenuItem(actionDeleteImageFromDatabase);
    private final JMenuItem itemOpenFilesWithStandardApp = new JMenuItem(actionOpenFiles);
    private final JMenuItem itemCopySelectedFilesToDirectory = new JMenuItem(actionCopySelectedFilesToDirectory);
    private final JMenuItem itemFileSystemDeleteFiles = new JMenuItem(actionFileSystemDeleteFiles);
    private final JMenuItem itemFileSystemRenameFiles = new JMenuItem(actionFileSystemRenameFiles);
    private final JMenuItem itemFileSystemMoveFiles = new JMenuItem(actionFileSystemMoveFiles);
    private List<ActionListener> actionListenersOpenFilesWithOtherApp = new ArrayList<ActionListener>();
    private Map<JMenuItem, Float> angleOfItem = new HashMap<JMenuItem, Float>();
    private Map<String, File> otherImageOpenAppOfAction = new HashMap<String, File>();
    private Map<JMenuItem, DatabaseUpdate> databaseUpdateOfMenuItem = new HashMap<JMenuItem, DatabaseUpdate>();
    private Content content = Content.Undefined;
    private static PopupMenuPanelThumbnails instance = new PopupMenuPanelThumbnails();

    /**
     * Liefert die einzige Klasseninstanz.
     * 
     * @return Instanz
     */
    public static PopupMenuPanelThumbnails getInstance() {
        return instance;
    }

    private PopupMenuPanelThumbnails() {
        initMaps();
        initItems();
        addItems();
    }

    private void initItems() {
        itemDeleteFromImageCollection.setEnabled(false);
    }

    private void addItems() {
        add(itemUpdateXmp);
        add(itemUpdateThumbnail);
        add(itemUpdateAllMetadata);
        add(itemDeleteThumbnail);
        add(itemRenameInXmpColumns);
        add(new JSeparator());
        add(itemOpenFilesWithStandardApp);
        add(menuOtherOpenImageApps);
        add(new JSeparator());
        add(itemCreateImageCollection);
        add(itemAddToImageCollection);
        add(itemDeleteFromImageCollection);
        add(new JSeparator());
        add(itemRotateThumbnai90);
        add(itemRotateThumbnai180);
        add(itemRotateThumbnai270);
        add(new JSeparator());
        add(itemCopySelectedFilesToDirectory);
        add(itemFileSystemRenameFiles);
        add(itemFileSystemMoveFiles);
        add(itemFileSystemDeleteFiles);
    }

    public void addOtherOpenImageApps() {
        menuOtherOpenImageApps.removeAll();
        List<File> apps = UserSettings.getInstance().getOtherImageOpenApps();
        if (!apps.isEmpty()) {
            for (File appFile : apps) {
                String filename = appFile.getName();
                JMenuItem item = new JMenuItem(filename);
                for (ActionListener listener : actionListenersOpenFilesWithOtherApp) {
                    item.addActionListener(listener);
                }
                menuOtherOpenImageApps.add(item);
                otherImageOpenAppOfAction.put(filename, appFile);
            }
        }
        menuOtherOpenImageApps.setEnabled(menuOtherOpenImageApps.getItemCount() > 0);
    }

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        if (evt.getType().equals(UserSettingsChangeEvent.Type.OtherImageOpenApps)) {
            addOtherOpenImageApps();
        }
    }

    /**
     * Sets the content, menu items not related to the content will be
     * disabled.
     * 
     * @param content  content
     */
    public void setContent(Content content) {
        this.content = content;
        setEnabledContent();
    }

    private void setEnabledContent() {
        itemDeleteFromImageCollection.setEnabled(
            content.equals(Content.ImageCollection));
    }

    public void addActionListenerOpenFilesWithOtherApp(ActionListener listener) {
        actionListenersOpenFilesWithOtherApp.add(listener);
    }

    public void addActionListenerOpenFilesWithStandardApp(ActionListener listener) {
        itemOpenFilesWithStandardApp.addActionListener(listener);
    }

    public void addActionListenerRenameInXmpColumns(ActionListener listener) {
        itemRenameInXmpColumns.addActionListener(listener);
    }

    public void addActionListenerCreateImageCollection(ActionListener listener) {
        itemCreateImageCollection.addActionListener(listener);
    }

    public void addActionListenerAddToImageCollection(ActionListener listener) {
        itemAddToImageCollection.addActionListener(listener);
    }

    public void addActionListenerDeleteFromImageCollection(ActionListener listener) {
        itemDeleteFromImageCollection.addActionListener(listener);
    }

    public void addActionListenerUpdateAllMetadata(ActionListener listener) {
        itemUpdateAllMetadata.addActionListener(listener);
    }

    public void addActionListenerUpdateXmp(ActionListener listener) {
        itemUpdateXmp.addActionListener(listener);
    }

    public void addActionListenerRotateThumbnail90(ActionListener listener) {
        itemRotateThumbnai90.addActionListener(listener);
    }

    public void addActionListenerRotateThumbnail180(ActionListener listener) {
        itemRotateThumbnai180.addActionListener(listener);
    }

    public void addActionListenerRotateThumbnail270(ActionListener listener) {
        itemRotateThumbnai270.addActionListener(listener);
    }

    public void addActionListenerDeleteThumbnail(ActionListener listener) {
        itemDeleteThumbnail.addActionListener(listener);
    }

    public void addActionListenerCopySelectedFilesToDirectory(ActionListener listener) {
        itemCopySelectedFilesToDirectory.addActionListener(listener);
    }

    public void addActionListenerFileSystemDeleteFiles(ActionListener listener) {
        itemFileSystemDeleteFiles.addActionListener(listener);
    }

    public void addActionListenerFileSystemRenameFiles(ActionListener listener) {
        itemFileSystemRenameFiles.addActionListener(listener);
    }

    public void addActionListenerFileSystemMoveFiles(ActionListener listener) {
        itemFileSystemMoveFiles.addActionListener(listener);
    }

    public void addActionListenerUpdateThumbnail(ActionListener listener) {
        itemUpdateThumbnail.addActionListener(listener);
    }

    /**
     * Liefert die Anwendung, die ein Bild öffnen soll.
     * 
     * @param action  Aktion
     * @return        Datei der Anwendung oder null, falls das Kommando sich
     *                nicht auf eine Anwendung bezieht
     */
    public File getOtherOpenImageApp(String action) {
        return otherImageOpenAppOfAction.get(action);
    }

    /**
     * Liefert den Winkel, um den das Thumbnail gedreht werden soll.
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

    public DatabaseUpdate getDatabaseUpdateOf(Object item) {
        return databaseUpdateOfMenuItem.get(item);
    }

    private void initMaps() {
        angleOfItem.put(itemRotateThumbnai90, new Float(90));
        angleOfItem.put(itemRotateThumbnai180, new Float(180));
        angleOfItem.put(itemRotateThumbnai270, new Float(270));

        databaseUpdateOfMenuItem.put(itemUpdateAllMetadata, DatabaseUpdate.Complete);
        databaseUpdateOfMenuItem.put(itemUpdateXmp, DatabaseUpdate.Xmp);
        databaseUpdateOfMenuItem.put(itemUpdateThumbnail, DatabaseUpdate.Thumbnail);
    }
}
