package org.jphototagger.program.controller.misc;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.frames.AppFrame;
import org.jphototagger.program.view.panels.AppPanel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.EnumMap;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;

/**
 * Controls the action: Go to ...
 *
 * @author Elmar Baumann
 */
public final class ControllerGoTo implements ActionListener {
    private final Map<AppFrame.GoTo, Component> componentOfGoTo =
        new EnumMap<AppFrame.GoTo, Component>(AppFrame.GoTo.class);
    private final Map<AppFrame.GoTo, JTabbedPane> tabbedPaneOfGoTo =
        new EnumMap<AppFrame.GoTo, JTabbedPane>(AppFrame.GoTo.class);

    // Not static (timing)
    private void initMaps() {
        AppPanel appPanel = GUI.getAppPanel();

        componentOfGoTo.put(AppFrame.GoTo.IMAGE_COLLECTIONS,
                            appPanel.getTabSelectionImageCollections());
        componentOfGoTo.put(AppFrame.GoTo.DIRECTORIES,
                            appPanel.getTabSelectionDirectories());
        componentOfGoTo.put(AppFrame.GoTo.FAVORITES,
                            appPanel.getTabSelectionFavoriteDirectories());
        componentOfGoTo.put(AppFrame.GoTo.SAVED_SEARCHES,
                            appPanel.getTabSelectionSavedSearches());
        componentOfGoTo.put(AppFrame.GoTo.KEYWORDS_SEL,
                            appPanel.getTabSelectionKeywords());
        componentOfGoTo.put(AppFrame.GoTo.TIMELINE,
                            appPanel.getTabSelectionTimeline());
        componentOfGoTo.put(AppFrame.GoTo.MISC_METADATA,
                            appPanel.getTabSelectionMiscMetadata());
        componentOfGoTo.put(AppFrame.GoTo.NO_METADATA,
                            appPanel.getTabSelectionNoMetadata());
        componentOfGoTo.put(AppFrame.GoTo.EDIT_PANELS,
                            appPanel.getTabMetadataEdit());
        componentOfGoTo.put(AppFrame.GoTo.EXIF_METADATA,
                            appPanel.getTabMetadataExif());
        componentOfGoTo.put(AppFrame.GoTo.IPTC_METADATA,
                            appPanel.getTabMetadataIptc());
        componentOfGoTo.put(AppFrame.GoTo.XMP_METADATA,
                            appPanel.getTabMetadataXmp());
        componentOfGoTo.put(AppFrame.GoTo.KEYWORDS_EDIT,
                            appPanel.getTabEditKeywords());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.IMAGE_COLLECTIONS,
                             appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.DIRECTORIES,
                             appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.FAVORITES,
                             appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.SAVED_SEARCHES,
                             appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.KEYWORDS_SEL,
                             appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.TIMELINE,
                             appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.MISC_METADATA,
                             appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.NO_METADATA,
                             appPanel.getTabbedPaneSelection());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.EDIT_PANELS,
                             appPanel.getTabbedPaneMetadata());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.EXIF_METADATA,
                             appPanel.getTabbedPaneMetadata());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.IPTC_METADATA,
                             appPanel.getTabbedPaneMetadata());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.XMP_METADATA,
                             appPanel.getTabbedPaneMetadata());
        tabbedPaneOfGoTo.put(AppFrame.GoTo.KEYWORDS_EDIT,
                             appPanel.getTabbedPaneMetadata());
    }

    public ControllerGoTo() {
        initMaps();
        listen();
    }

    private void listen() {
        AppFrame appFrame = GUI.getAppFrame();

        for (AppFrame.GoTo gt : AppFrame.GoTo.values()) {
            appFrame.getMenuItemOfGoto(gt).addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        selectComponent((JMenuItem) evt.getSource());
    }

    private void selectComponent(JMenuItem item) {
        AppFrame.GoTo goTo = GUI.getAppFrame().getGotoOfMenuItem(item);

        if (tabbedPaneOfGoTo.containsKey(goTo)) {
            tabbedPaneOfGoTo.get(goTo).setSelectedComponent(
                componentOfGoTo.get(goTo));
            componentOfGoTo.get(goTo).requestFocusInWindow();
        } else if (goTo.equals(AppFrame.GoTo.FAST_SEARCH)) {
            GUI.getSearchTextArea().requestFocusInWindow();
        } else if (goTo.equals(AppFrame.GoTo.THUMBNAILS_PANEL)) {
            GUI.getThumbnailsPanel().requestFocusInWindow();
        }

        if (goTo.equals(AppFrame.GoTo.EDIT_PANELS)) {
            GUI.getEditPanel().setFocusToFirstEditField();
        }
    }
}
