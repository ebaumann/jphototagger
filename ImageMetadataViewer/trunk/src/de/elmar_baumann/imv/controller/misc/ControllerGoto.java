package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * Controls the action: Go to ...
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/10
 */
public final class ControllerGoto extends Controller implements ActionListener {

    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();
    private final EditMetadataPanelsArray editPanels = appPanel.getEditPanelsArray();
    private final JTextField textFieldSearch = appPanel.getTextFieldSearch();
    private final AppFrame appFrame = Panels.getInstance().getAppFrame();
    private final Map<AppFrame.Goto, Component> componentOfGoto = new HashMap<AppFrame.Goto, Component>();
    private final Map<AppFrame.Goto, JTabbedPane> tabbedPaneOfGoto = new HashMap<AppFrame.Goto, JTabbedPane>();

    private void initMaps() {
        componentOfGoto.put(AppFrame.Goto.CATEGORIES, appPanel.getTabSelectionCategories());
        componentOfGoto.put(AppFrame.Goto.IMAGE_COLLECTIONS, appPanel.getTabSelectionImageCollections());
        componentOfGoto.put(AppFrame.Goto.DIRECTORIES, appPanel.getTabSelectionDirectories());
        componentOfGoto.put(AppFrame.Goto.FAVORITE_DIRECTORIES, appPanel.getTabSelectionFavoriteDirectories());
        componentOfGoto.put(AppFrame.Goto.SAVED_SEARCHES, appPanel.getTabSelectionSavedSearches());
        componentOfGoto.put(AppFrame.Goto.KEYWORDS, appPanel.getTabSelectionKeywords());

        componentOfGoto.put(AppFrame.Goto.EDIT_PANELS, appPanel.getTabMetadataEdit());
        componentOfGoto.put(AppFrame.Goto.EXIF_METADATA, appPanel.getTabMetadataExif());
        componentOfGoto.put(AppFrame.Goto.IPTC_METADATA, appPanel.getTabMetadataIptc());
        componentOfGoto.put(AppFrame.Goto.XMP_METADATA, appPanel.getTabMetadataXmp());

        tabbedPaneOfGoto.put(AppFrame.Goto.CATEGORIES, appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoto.put(AppFrame.Goto.IMAGE_COLLECTIONS, appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoto.put(AppFrame.Goto.DIRECTORIES, appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoto.put(AppFrame.Goto.FAVORITE_DIRECTORIES, appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoto.put(AppFrame.Goto.SAVED_SEARCHES, appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoto.put(AppFrame.Goto.KEYWORDS, appPanel.getTabbedPaneSelection());

        tabbedPaneOfGoto.put(AppFrame.Goto.EDIT_PANELS, appPanel.getTabbedPaneMetadata());
        tabbedPaneOfGoto.put(AppFrame.Goto.EXIF_METADATA, appPanel.getTabbedPaneMetadata());
        tabbedPaneOfGoto.put(AppFrame.Goto.IPTC_METADATA, appPanel.getTabbedPaneMetadata());
        tabbedPaneOfGoto.put(AppFrame.Goto.XMP_METADATA, appPanel.getTabbedPaneMetadata());
    }

    public ControllerGoto() {
        initMaps();
        listenToActionSources();
    }

    private void listenToActionSources() {
        for (AppFrame.Goto gt : AppFrame.Goto.values()) {
            appFrame.getMenuItemOfGoto(gt).addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            selectComponent((JMenuItem) e.getSource());
        }
    }

    private void selectComponent(JMenuItem item) {
        AppFrame.Goto gt = appFrame.getGotoOfMenuItem(item);
        if (tabbedPaneOfGoto.containsKey(gt)) {
            tabbedPaneOfGoto.get(gt).setSelectedComponent(componentOfGoto.get(gt));
            componentOfGoto.get(gt).requestFocus();
        } else if (gt.equals(AppFrame.Goto.FAST_SEARCH)) {
            textFieldSearch.requestFocus();
        } else if (gt.equals(AppFrame.Goto.THUMBNAILS_PANEL)) {
            thumbnailsPanel.requestFocus();
        }
        if (gt.equals(AppFrame.Goto.EDIT_PANELS)) {
            editPanels.setFocusToFirstEditField();
        }
    }
}
