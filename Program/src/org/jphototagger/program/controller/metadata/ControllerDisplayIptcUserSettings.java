package org.jphototagger.program.controller.metadata;

import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.JTabbedPane;
import org.jphototagger.domain.event.UserPropertyChangedEvent;
import org.jphototagger.program.model.TableModelIptc;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.util.List;
import javax.swing.table.TableModel;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.storage.Storage;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.openide.util.Lookup;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerDisplayIptcUserSettings extends MouseAdapter {

    private final JTabbedPane metadataPane = GUI.getAppPanel().getTabbedPaneMetadata();

    public ControllerDisplayIptcUserSettings() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
        metadataPane.addMouseListener(this);
    }

    @EventSubscriber(eventClass = UserPropertyChangedEvent.class)
    public void applySettings(UserPropertyChangedEvent evt) {
        if (Storage.KEY_DISPLAY_IPTC.equals(evt.getPropertyKey())) {
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
        boolean isDisplayIptc = isDisplayIptc();
        Component parentComponent = null;
        String message = Bundle.getString(ControllerDisplayIptcUserSettings.class, "ControllerDisplayIptcUserSettings.Confirm.DisplayIptc");

        if (!isDisplayIptc && MessageDisplayer.confirmYesNo(parentComponent, message)) {
            setDisplayIptc(true);
        }
    }

    private boolean isDisplayIptc() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        return storage.containsKey(Storage.KEY_DISPLAY_IPTC)
                ? storage.getBoolean(Storage.KEY_DISPLAY_IPTC)
                : false;
    }

    private void setDisplayIptc(boolean display) {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        storage.setBoolean(Storage.KEY_DISPLAY_IPTC, display);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        String tooltipText = metadataPane.getToolTipText(e);

        if (AppPanel.DISABLED_IPTC_TAB_TOOLTIP_TEXT.equals(tooltipText)) {
            checkDisplayIptc();
        }
    }
}
