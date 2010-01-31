/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.app;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.event.UserSettingsEvent;
import de.elmar_baumann.jpt.event.listener.AppExitListener;
import de.elmar_baumann.jpt.event.listener.UserSettingsListener;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.frames.AppFrame;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import de.elmar_baumann.lib.util.Settings;

/**
 * Reads and writes persistent important settings of {@link AppPanel} and
 * {@link AppFrame}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-15
 */
public final class AppWindowPersistence
        implements AppExitListener,
                   UserSettingsListener
    {

    // Strings has to be equals to that in AppPanel!
    private static final String KEY_DIVIDER_LOCATION_MAIN       = "AppPanel.DividerLocationMain";
    private static final String KEY_DIVIDER_LOCATION_THUMBNAILS = "AppPanel.DividerLocationThumbnails";
    private static final String KEY_KEYWORDS_VIEW               = "AppPanel.KeywordsView";

    // Not a singleton: init() gets cards of AppPanel that is not static
    public AppWindowPersistence() {
        listen();
    }

    private void listen() {
        AppLifeCycle.INSTANCE.addAppExitListener(this);
    }

    public void readAppFrameFromProperties() {
        AppFrame appFrame = GUI.INSTANCE.getAppFrame();

        UserSettings.INSTANCE.getSettings().applySizeAndLocation(appFrame);
        appFrame.pack();
    }

    public void readAppPanelFromProperties() {
        AppPanel appPanel  = GUI.INSTANCE.getAppPanel();

        UserSettings.INSTANCE.getSettings().applySettings(appPanel, null);

        appPanel.setEnabledIptcTab(UserSettings.INSTANCE.isDisplayIptc());
        selectFastSearch(appPanel);
    }

    private void selectFastSearch(AppPanel appPanel) {
        ComponentUtil.forceRepaint(appPanel.getComboBoxFastSearch());
        appPanel.getTextAreaSearch().requestFocusInWindow();
    }

    @Override
    public void appWillExit() {
        writeAppProperties();
    }

    private void writeAppProperties() {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();
        Settings settings = UserSettings.INSTANCE.getSettings();

        settings.set(appPanel, null);
        settings.set(appPanel.getSplitPaneMain().getDividerLocation(), KEY_DIVIDER_LOCATION_MAIN);
        settings.set(appPanel.getSplitPaneThumbnailsMetadata().getDividerLocation(), KEY_DIVIDER_LOCATION_THUMBNAILS);

        appPanel.getPanelEditKeywords().writeProperties();
        UserSettings.INSTANCE.writeToFile();
    }

    @Override
    public void applySettings(UserSettingsEvent evt) {
        if (evt.getType().equals(UserSettingsEvent.Type.DISPLAY_IPTC)) {
            GUI.INSTANCE.getAppPanel().setEnabledIptcTab(UserSettings.INSTANCE.isDisplayIptc());
        }
    }
}
