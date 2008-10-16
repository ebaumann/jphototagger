package de.elmar_baumann.imv.view.popupmenus;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.UserSettingsChangeListener;
import de.elmar_baumann.imv.resource.Bundle;
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
    private final String actionUpdateTextMetadata = Bundle.getString("PopupMenuPanelThumbnails.Action.UpdateOnlyTextMetadata");
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
    private JMenu menuOtherOpenImageApps = new JMenu(Bundle.getString("PopupMenuPanelThumbnails.menuOtherOpenImageApps.text"));
    private final JMenuItem itemUpdateAllMetadata = new JMenuItem(actionUpdateAllMetadata);
    private final JMenuItem itemUpdateTextMetadata = new JMenuItem(actionUpdateTextMetadata);
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
    private List<ActionListener> actionListenersOpenFilesWithOtherApp = new ArrayList<ActionListener>();
    private Map<String, Float> angleOfAction = new HashMap<String, Float>();
    private Map<String, File> otherImageOpenAppOfAction = new HashMap<String, File>();
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
        initMap();
        initItems();
        addItems();
    }

    private void initItems() {
        itemDeleteFromImageCollection.setEnabled(false);
    }

    private void addItems() {
        add(itemUpdateAllMetadata);
        add(itemUpdateTextMetadata);
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
     * Teilt dem Menü mit, dass gerade eine Bildsammlung angezeigt wird und
     * Menüpunkte mit entsprechenden Aktionen aktiviert werden können.
     * 
     * @param is true, wenn eine Bildsammlung angezeigt wird
     */
    public void setIsImageCollection(boolean is) {
        itemDeleteFromImageCollection.setEnabled(is);
    }

    /**
     * Liefert, ob eine Aktion besagt: Aktualisiere alle Metadaten der Bilder,
     * auch die Thumbnails.
     * 
     * @param action  Aktion
     * @return        true, wenn alle Metadaten der Bilder aktualisiert werden
     *                sollen
     */
    public boolean isUpdateAllMetadata(String action) {
        return action.equals(actionUpdateAllMetadata);
    }

    /**
     * Liefert, ob eine Aktion besagt: Aktualisiere die Text-Metadaten der
     * Bilder, <em>nicht</em> die Thumbnails.
     * 
     * @param action  Aktion
     * @return        true, wenn nur die Text-Metadaten der Bilder aktualisiert
     *                werden sollen
     */
    public boolean isUpdateTextMetadata(String action) {
        return action.equals(actionUpdateTextMetadata);
    }

    /**
     * Fügt einen Beobachter hinzu für das Ereignis:
     * Bilder der ausgewählten Thumbnails mit einer anderen Anwendung öffnen
     * (anstelle der Anwendung, die das Bild bei Doppelklick öffnet).
     * 
     * @param listener Beobachter
     */
    public void addActionListenerOpenFilesWithOtherApp(ActionListener listener) {
        actionListenersOpenFilesWithOtherApp.add(listener);
    }

    /**
     * Fügt einen Beobachter hinzu für das Ereignis:
     * Bilder der ausgewählten Thumbnails der Anwendung öffnen, die das Bild
     * bei Doppelklick öffnet.
     * 
     * @param listener Beobachter
     */
    public void addActionListenerOpenFilesWithStandardApp(ActionListener listener) {
        itemOpenFilesWithStandardApp.addActionListener(listener);
    }

    public void addActionListenerRenameInXmpColumns(ActionListener listener) {
        itemRenameInXmpColumns.addActionListener(listener);
    }

    /**
     * Fügt einen Beobachter hinzu für das Ereignis:
     * Eine neue Bildsammlung erzeugen aus den ausgewählten Thumbnails.
     * 
     * @param listener Beobachter
     */
    public void addActionListenerCreateImageCollection(ActionListener listener) {
        itemCreateImageCollection.addActionListener(listener);
    }

    /**
     * Fügt einen Beobachter hinzu für das Ereignis:
     * Ausgewählte Thumbnails einer Bildsammlung hinzufügen.
     * 
     * @param listener Beobachter
     */
    public void addActionListenerAddToImageCollection(ActionListener listener) {
        itemAddToImageCollection.addActionListener(listener);
    }

    /**
     * Fügt einen Beobachter hinzu für das Ereignis:
     * Ausgewählte Thumbnails von einer Bildsammlung löschen.
     * 
     * @param listener Beobachter
     */
    public void addActionListenerDeleteFromImageCollection(ActionListener listener) {
        itemDeleteFromImageCollection.addActionListener(listener);
    }

    /**
     * Fügt einen Beobachter hinzu für das Ereignis:
     * Alle Metadaten der ausgewählten Thumbnails aktualisieren, auch die
     * Thumbnailbilder.
     * 
     * @param listener Beobachter
     */
    public void addActionListenerUpdateAllMetadata(ActionListener listener) {
        itemUpdateAllMetadata.addActionListener(listener);
    }

    /**
     * Fügt einen Beobachter hinzu für das Ereignis:
     * Text-Metadaten der ausgewählten Thumbnails aktualisieren, <em>nicht</em>
     * die Thumbnailbilder.
     * 
     * @param listener Beobachter
     */
    public void addActionListenerUpdateTextMetadata(ActionListener listener) {
        itemUpdateTextMetadata.addActionListener(listener);
    }

    /**
     * Fügt einen Beobachter hinzu für das Ereignis:
     * Ausgewählte Thumbnails um 90 Grad im Uhreigersinn drehen.
     * 
     * @param listener Beobachter
     */
    public void addActionListenerRotateThumbnail90(ActionListener listener) {
        itemRotateThumbnai90.addActionListener(listener);
    }

    /**
     * Fügt einen Beobachter hinzu für das Ereignis:
     * Ausgewählte Thumbnails um 180 Grad im Uhreigersinn drehen.
     * 
     * @param listener Beobachter
     */
    public void addActionListenerRotateThumbnail180(ActionListener listener) {
        itemRotateThumbnai180.addActionListener(listener);
    }

    /**
     * Fügt einen Beobachter hinzu für das Ereignis:
     * Ausgewählte Thumbnails um 270 Grad im Uhreigersinn drehen.
     * 
     * @param listener Beobachter
     */
    public void addActionListenerRotateThumbnail270(ActionListener listener) {
        itemRotateThumbnai270.addActionListener(listener);
    }

    /**
     * Fügt einen Beobachter hinzu für das Ereignis:
     * Ausgewählte Thumbnails aus der Datenbank löschen.
     * 
     * @param listener Beobachter
     */
    public void addActionListenerDeleteThumbnail(ActionListener listener) {
        itemDeleteThumbnail.addActionListener(listener);
    }

    /**
     * Fügt einen Beobachter hinzu für die Aktion: Ausgewählte Dateien in ein
     * Verzeichnis kopieren.
     * 
     * @param listener Beobachter
     */
    public void addActionListenerCopySelectedFilesToDirectory(ActionListener listener) {
        itemCopySelectedFilesToDirectory.addActionListener(listener);
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
     * Entfernt einen Beobachter für das Ereignis:
     * Bilder der ausgewählten Thumbnails mit einer anderen Anwendung öffnen
     * (anstelle der Anwendung, die das Bild bei Doppelklick öffnet).
     * 
     * @param listener Beobachter
     */
    public void removeActionListenerOpenFilesOtherApps(ActionListener listener) {
        actionListenersOpenFilesWithOtherApp.remove(listener);
    }

    /**
     * Entfernt einen Beobachter für das Ereignis:
     * Bilder der ausgewählten Thumbnails der Anwendung öffnen, die das Bild
     * bei Doppelklick öffnet.
     * 
     * @param listener Beobachter
     */
    public void removeActionListenerOpenFiles(ActionListener listener) {
        itemOpenFilesWithStandardApp.removeActionListener(listener);
    }

    public void removeActionListenerRenameInXmpColumns(ActionListener listener) {
        itemRenameInXmpColumns.removeActionListener(listener);
    }

    /**
     * Entfernt einen Beobachter für das Ereignis:
     * Eine neue Bildsammlung erzeugen aus den ausgewählten Thumbnails.
     * 
     * @param listener Beobachter
     */
    public void removeActionListenerCreateImageCollection(ActionListener listener) {
        itemCreateImageCollection.removeActionListener(listener);
    }

    /**
     * Entfernt einen Beobachter für das Ereignis:
     * Ausgewählte Thumbnails einer Bildsammlung hinzufügen.
     * 
     * @param listener Beobachter
     */
    public void removeActionListenerAddToImageCollection(ActionListener listener) {
        itemAddToImageCollection.removeActionListener(listener);
    }

    /**
     * Entfernt einen Beobachter für das Ereignis:
     * Ausgewählte Thumbnails von einer Bildsammlung löschen.
     * 
     * @param listener Beobachter
     */
    public void removeActionListenerDeleteFromImageCollection(ActionListener listener) {
        itemDeleteFromImageCollection.removeActionListener(listener);
    }

    /**
     * Entfernt einen Beobachter für das Ereignis:
     * Alle Metadaten der ausgewählten Thumbnails aktualisieren, auch die
     * Thumbnailbilder.
     * 
     * @param listener Beobachter
     */
    public void removeActionListenerUpdateAllMetadata(ActionListener listener) {
        itemUpdateAllMetadata.removeActionListener(listener);
    }

    /**
     * Entfernt einen Beobachter für das Ereignis:
     * Text-Metadaten der ausgewählten Thumbnails aktualisieren, <em>nicht</em>
     * die Thumbnailbilder.
     * 
     * @param listener Beobachter
     */
    public void removeActionListenerUpdateTextMetadata(ActionListener listener) {
        itemUpdateTextMetadata.removeActionListener(listener);
    }

    /**
     * Entfernt einen Beobachter für das Ereignis:
     * Ausgewählte Thumbnails um 90 Grad im Uhreigersinn drehen.
     * 
     * @param listener Beobachter
     */
    public void removeActionListenerRotateThumbnail90(ActionListener listener) {
        itemRotateThumbnai90.removeActionListener(listener);
    }

    /**
     * Entfernt einen Beobachter für das Ereignis:
     * Ausgewählte Thumbnails um 180 Grad im Uhreigersinn drehen.
     * 
     * @param listener Beobachter
     */
    public void removeActionListenerRotateThumbnail180(ActionListener listener) {
        itemRotateThumbnai180.removeActionListener(listener);
    }

    /**
     * Entfernt einen Beobachter für das Ereignis:
     * Ausgewählte Thumbnails um 270 Grad im Uhreigersinn drehen.
     * 
     * @param listener Beobachter
     */
    public void removeActionListenerRotateThumbnail270(ActionListener listener) {
        itemRotateThumbnai270.removeActionListener(listener);
    }

    /**
     * Entfernt einen Beobachter für das Ereignis:
     * Ausgewählte Thumbnails aus der Datenbank löschen.
     * 
     * @param listener Beobachter
     */
    public void removeActionListenerDeleteThumbnail(ActionListener listener) {
        itemDeleteThumbnail.removeActionListener(listener);
    }

    /**
     * Entfernt einen Beobachter für die Aktion: Ausgewählte Dateien in ein
     * Verzeichnis kopieren.
     * 
     * @param listener Beobachter
     */
    public void removeActionListenerCopySelectedFilesToDirectory(ActionListener listener) {
        itemCopySelectedFilesToDirectory.removeActionListener(listener);
    }

    public void addActionListenerFileSystemDeleteFiles(ActionListener listener) {
        itemFileSystemDeleteFiles.addActionListener(listener);
    }

    public void removeActionListenerFileSystemDeleteFiles(ActionListener listener) {
        itemFileSystemDeleteFiles.removeActionListener(listener);
    }

    public void addActionListenerFileSystemRenameFiles(ActionListener listener) {
        itemFileSystemRenameFiles.addActionListener(listener);
    }

    public void removeActionListenerFileSystemRenameFiles(ActionListener listener) {
        itemFileSystemRenameFiles.removeActionListener(listener);
    }

    /**
     * Liefert den Winkel, um den das Thumbnail gedreht werden soll.
     * 
     * @param action  Aktion
     * @return        Winkel in Grad; 0 Grad, wenn das Kommando keine Drehung ist
     */
    public float getRotateAngle(String action) {
        Float angle = new Float(0);

        if (angleOfAction.containsKey(action)) {
            angle = angleOfAction.get(action);
        }

        return angle.floatValue();
    }

    private void initMap() {
        if (angleOfAction.isEmpty()) {
            angleOfAction.put(actionRotate90, new Float(90));
            angleOfAction.put(actionRotate180, new Float(180));
            angleOfAction.put(actionRotate270, new Float(270));
        }
    }
}
