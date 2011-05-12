package org.jphototagger.program.controller.metadata;

import java.awt.event.MouseEvent;
import javax.swing.JTabbedPane;
import org.jphototagger.program.event.listener.UserSettingsListener;
import org.jphototagger.program.event.UserSettingsEvent;
import org.jphototagger.program.model.TableModelIptc;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.awt.event.MouseAdapter;

import java.io.File;

import java.util.List;

import javax.swing.table.TableModel;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.app.MessageDisplayer;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerDisplayIptcUserSettings extends MouseAdapter implements UserSettingsListener {

    private final JTabbedPane metadataPane = GUI.getAppPanel().getTabbedPaneMetadata();

    public ControllerDisplayIptcUserSettings() {
        listen();
    }

    private void listen() {
        UserSettings.INSTANCE.addUserSettingsListener(this);
        metadataPane.addMouseListener(this);
    }

    @Override
    public void applySettings(UserSettingsEvent evt) {
        if (evt.getType().equals(UserSettingsEvent.Type.DISPLAY_IPTC)) {
            boolean displayIptc = UserSettings.INSTANCE.isDisplayIptc();

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

            if (model instanceof TableModelIptc) {
                final List<File> selFiles = GUI.getSelectedImageFiles();

                if (selFiles.size() == 1) {
                    final File file = selFiles.get(0);

                    EventQueueUtil.invokeInDispatchThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TableModelIptc) model).setFile(file);
                        }
                    });
                }
            }
        }
    }

    private void checkDisplayIptc() {
        boolean isDisplayIptc = UserSettings.INSTANCE.isDisplayIptc();

        if (!isDisplayIptc
            && MessageDisplayer.confirmYesNo(null, "ControllerDisplayIptcUserSettings.Confirm.DisplayIptc")) {
            UserSettings.INSTANCE.setDisplayIptc(true);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        String tooltipText = metadataPane.getToolTipText(e);

        if (AppPanel.DISABLED_IPTC_TAB_TOOLTIP_TEXT.equals(tooltipText)) {
            checkDisplayIptc();
        }
    }
}
