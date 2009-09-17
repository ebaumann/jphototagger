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
package de.elmar_baumann.imv.plugin;

import de.elmar_baumann.imv.plugin.PluginListener.Event;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * Base class for Plugins.
 *
 * A plugin processes image files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-27
 */
public abstract class Plugin extends AbstractAction {

    private Logger logger;
    private Properties properties;
    private JProgressBar progressBar;
    private final List<File> files = new ArrayList<File>();
    private final Set<PluginListener> pluginListeners =
            Collections.synchronizedSet(new HashSet<PluginListener>());

    protected Plugin() {
        putValues();
    }

    private void putValues() {
        putValue(Action.NAME, getName());
    }

    /**
     * Sets a logger for logging status or error messages.
     *
     * @param logger logger
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Sets a progress bar.
     *
     * @param progressBar progress bar for displaying the current progress.
     *                    <em>Can be null!</em>
     */
    public void setProgressBar(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    /**
     * Sets properties to set persistent values.
     *
     * @param properties properties read and written persistent. The key
     *                   has to be unique, e.g. a combination of the fully
     *                   qualified class and field name
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Returns the logger.
     *
     * @return logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Returns the progress bar.
     *
     * @return progress bar or null
     */
    public JProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * Returns the properties.
     *
     * @return properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Sets the files to process.
     *
     * @param files files to process
     */
    public void setFiles(List<File> files) {
        synchronized (files) {
            this.files.clear();
            this.files.addAll(files);
        }
    }

    /**
     * Returns the files to process.
     *
     * @return files
     */
    protected List<File> getFiles() {
        return files;
    }

    /**
     * Adds a listener.
     *
     * @param listener listener. If the action runs in the background, it's
     *                 important to call
     *                 {@link PluginListener#action(java.util.Set)} with
     *                 one or more of <code>Event#FINISHED_...</code> arguments
     *                 so that it can release resources such as the process'
     *                 progress bar.
     */
    public void addPluginListener(PluginListener listener) {
        pluginListeners.add(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener listener to remove
     */
    public void removePluginListener(PluginListener listener) {
        pluginListeners.remove(listener);
    }

    /**
     * Notifies all added plugin listeners that a specific event has occured.
     *
     * @param events events
     */
    protected void notifyPluginListeners(Event... events) {
        if (pluginListeners.size() > 0) {
            Set<Event> evts = new HashSet<Event>(Arrays.asList(events));
            synchronized (pluginListeners) {
                for (PluginListener listener : pluginListeners) {
                    listener.action(evts);
                }
            }
        }
    }

    /**
     * Returns the <strong>localized</strong> short name of the plugin for
     * displaying in menu items, buttons, tabs.
     *
     * @return name
     */
    public abstract String getName();

    /**
     * Returns a <strong>localized</strong> text, what the plugin does.
     *
     * @return text
     */
    public abstract String getDescription();

    /**
     * Returns the path to the XML contents file of the plugin's help.
     *
     * The contents file lists html filenames relative to itself following the
     * syntax in <code>helpindex.dtd</code>, which has to be in the same
     * directory. A sample file set exists in the package
     * <code>de.elmar_baumann.imv.plugin.help</code>.
     * <p>
     * Hint: To support multiple languages You can return paths depending on
     * the Locale.
     *
     * @return help contents file path, e.g.
     *        <code>"/com/myname/imv/plugin/doc/en/contents.xml"</code> or null
     *        the plugin has no help. This class returns null.
     */
    public String getHelpContentsPath() {
        return null;
    }

    /**
     * Returns a panel where the user can configure this plugin.
     * <p>
     * Uses the properties of {@link #setProperties(java.util.Properties)} to
     * read and write them persistent.
     *
     * @return panel or null if the plugin does not provide a settings panel.
     *         This class returns null.
     */
    public JPanel getSettingsPanel() {
        return null;
    }

    /**
     * Returns whether the plugin's action has been finished.
     * <p>
     * @param   events events got through
     *                 {@link PluginListener#action(java.util.Set)}
     * @return         true if the plugin has been finished
     */
    public static boolean isFinished(Collection<Event> events) {
        return events.contains(Event.FINISHED_ERRORS) ||
                events.contains(Event.FINISHED_NO_ERRORS) ||
                events.contains(Event.FINISHED_FILES_CHANGED);
    }

    /**
     * Returns whether the plugin's action has been changed files.
     *
     * @param   events events got through
     *                 {@link PluginListener#action(java.util.Set)}
     * @return         true if the plugin has been changed files
     */
    public static boolean filesChanged(Collection<Event> events) {
        return events.contains(Event.FINISHED_FILES_CHANGED);
    }

    /**
     * Returns whether the plugin's action has been finished with no errors.
     *
     * @param   events events got through
     *                 {@link PluginListener#action(java.util.Set)}
     * @return         true if the plugin has been finished with no errors
     */
    public static boolean finishedNoErrors(Collection<Event> events) {
        return events.contains(Event.FINISHED_NO_ERRORS);
    }
}
