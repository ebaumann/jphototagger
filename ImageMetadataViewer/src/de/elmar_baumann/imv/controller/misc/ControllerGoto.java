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
public class ControllerGoto extends Controller implements ActionListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();
    private EditMetadataPanelsArray editPanels = appPanel.getEditPanelsArray();
    private JTextField textFieldSearch = appPanel.getTextFieldSearch();
    private AppFrame appFrame = Panels.getInstance().getAppFrame();
    private Map<AppFrame.Goto, Component> componentOfGoto = new HashMap<AppFrame.Goto, Component>();
    private Map<AppFrame.Goto, JTabbedPane> tabbedPaneOfGoto = new HashMap<AppFrame.Goto, JTabbedPane>();

    private void initMaps() {
        componentOfGoto.put(AppFrame.Goto.Categories, appPanel.getTabSelectionCategories());
        componentOfGoto.put(AppFrame.Goto.ImageCollections, appPanel.getTabSelectionImageCollections());
        componentOfGoto.put(AppFrame.Goto.Directories, appPanel.getTabSelectionDirectories());
        componentOfGoto.put(AppFrame.Goto.FavoriteDirectories, appPanel.getTabSelectionFavoriteDirectories());
        componentOfGoto.put(AppFrame.Goto.SavedSearches, appPanel.getTabSelectionSavedSearches());
        componentOfGoto.put(AppFrame.Goto.Keywords, appPanel.getTabSelectionKeywords());

        componentOfGoto.put(AppFrame.Goto.EditPanels, appPanel.getTabMetadataEdit());
        componentOfGoto.put(AppFrame.Goto.ExifMetadata, appPanel.getTabMetadataExif());
        componentOfGoto.put(AppFrame.Goto.IptcMetadata, appPanel.getTabMetadataIptc());
        componentOfGoto.put(AppFrame.Goto.XmpMetadata, appPanel.getTabMetadataXmp());

        tabbedPaneOfGoto.put(AppFrame.Goto.Categories, appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoto.put(AppFrame.Goto.ImageCollections, appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoto.put(AppFrame.Goto.Directories, appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoto.put(AppFrame.Goto.FavoriteDirectories, appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoto.put(AppFrame.Goto.SavedSearches, appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoto.put(AppFrame.Goto.Keywords, appPanel.getTabbedPaneSelection());

        tabbedPaneOfGoto.put(AppFrame.Goto.EditPanels, appPanel.getTabbedPaneMetadata());
        tabbedPaneOfGoto.put(AppFrame.Goto.ExifMetadata, appPanel.getTabbedPaneMetadata());
        tabbedPaneOfGoto.put(AppFrame.Goto.IptcMetadata, appPanel.getTabbedPaneMetadata());
        tabbedPaneOfGoto.put(AppFrame.Goto.XmpMetadata, appPanel.getTabbedPaneMetadata());
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
        } else if (gt.equals(AppFrame.Goto.FastSearch)) {
            textFieldSearch.requestFocus();
        } else if (gt.equals(AppFrame.Goto.ThumbnailsPanel)) {
            thumbnailsPanel.requestFocus();
        }
        if (gt.equals(AppFrame.Goto.EditPanels)) {
            editPanels.setFocusToFirstEditField();
        }
    }
}
