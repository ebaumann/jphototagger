package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.plugin.Plugin;
import de.elmar_baumann.imv.plugin.PluginListener;
import de.elmar_baumann.imv.plugin.PluginListener.Event;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JMenuItem;

/**
 * Listens to items of {@link PopupMenuThumbnails#getMenuPlugins()} and sets
 * resources on action.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-27
 */
public final class ControllerPlugins implements ActionListener {

    public ControllerPlugins() {
        listen();
    }

    private void listen() {
        for (JMenuItem item : PopupMenuThumbnails.INSTANCE.getPluginMenuItems()) {
            item.addActionListener(this);
            Action action = item.getAction();
            if (action instanceof Plugin) {
                ((Plugin) action).addProcessListener(new Listener());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        List<File> selFiles = GUI.INSTANCE.getAppPanel().getPanelThumbnails().
                getSelectedFiles();
        if (selFiles.size() > 0 && src instanceof JMenuItem) {
            Action action = ((JMenuItem) src).getAction();
            if (action instanceof Plugin) {
                Plugin plugin = (Plugin) action;
                plugin.setFiles(selFiles);
                plugin.actionPerformed(e);
            }
        }
    }

    private class Listener implements PluginListener {

        @Override
        public void action(Set<Event> events) {
            if (Plugin.filesChanged(events)) {
                GUI.INSTANCE.getAppPanel().getPanelThumbnails().refresh();
            }
            if (Plugin.isFinished(events)) {
                UserSettings.INSTANCE.writeToFile();
            }
        }
    }
}
