package org.jphototagger.program.module.iptc;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.JTabbedPane;
import javax.swing.table.TableModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.iptc.IptcPreferencesKeys;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.AppPanel;
import org.jphototagger.program.module.thumbnails.ThumbnailsPanel;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class DisplayIptcUserSettingsController extends MouseAdapter {

    private final JTabbedPane metadataPane = GUI.getAppPanel().getTabbedPaneMetadata();

    public DisplayIptcUserSettingsController() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
        metadataPane.addMouseListener(this);
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void applySettings(PreferencesChangedEvent evt) {
        if (IptcPreferencesKeys.KEY_DISPLAY_IPTC.equals(evt.getKey())) {
            boolean displayIptc = (Boolean) evt.getNewValue();

            setEnabledIptcTab(displayIptc);

            if (displayIptc && GUI.getAppPanel().isTabMetadataIptcSelected()) {
                displayIptc();
            }
        }
    }

    private void setEnabledIptcTab(final boolean displayIptc) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                GUI.getAppPanel().setEnabledIptcTab(displayIptc);
            }
        });
    }

    private void displayIptc() {
        AppPanel appPanel = GUI.getAppPanel();
        ThumbnailsPanel tnPanel = appPanel.getPanelThumbnails();

        if (tnPanel.getSelectionCount() == 1) {
            final TableModel model = appPanel.getTableIptc().getModel();

            if (model instanceof IptcTableModel) {
                final List<File> selFiles = GUI.getSelectedImageFiles();

                if (selFiles.size() == 1) {
                    final File file = selFiles.get(0);

                    EventQueueUtil.invokeInDispatchThread(new Runnable() {

                        @Override
                        public void run() {
                            ((IptcTableModel) model).setFile(file);
                        }
                    });
                }
            }
        }
    }

    private void checkDisplayIptc() {
        boolean isDisplayIptc = isDisplayIptc();
        Component parentComponent = null;
        String message = Bundle.getString(DisplayIptcUserSettingsController.class, "DisplayIptcUserSettingsController.Confirm.DisplayIptc");

        if (!isDisplayIptc && MessageDisplayer.confirmYesNo(parentComponent, message)) {
            setDisplayIptc(true);
        }
    }

    private boolean isDisplayIptc() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(IptcPreferencesKeys.KEY_DISPLAY_IPTC)
                ? storage.getBoolean(IptcPreferencesKeys.KEY_DISPLAY_IPTC)
                : false;
    }

    private void setDisplayIptc(boolean display) {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setBoolean(IptcPreferencesKeys.KEY_DISPLAY_IPTC, display);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        String tooltipText = metadataPane.getToolTipText(e);

        if (AppPanel.DISABLED_IPTC_TAB_TOOLTIP_TEXT.equals(tooltipText)) {
            checkDisplayIptc();
        }
    }
}