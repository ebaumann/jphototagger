package org.jphototagger.plugin;

import java.awt.EventQueue;
import org.jphototagger.lib.generics.Pair;

import java.awt.Image;

import java.io.File;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * Base class for Plugins.
 *
 * @author Elmar Baumann
 */
public abstract class Plugin {
    private Properties             properties;
    private JProgressBar           progressBar;
    private final Map<File, Image> thumbnailOfFile = new LinkedHashMap<File,
                                                         Image>();
    private final Set<PluginListener> pluginListeners =
        new CopyOnWriteArraySet<PluginListener>();
    private boolean pBarStringPainted;

    /**
     * Sets a progress bar.
     *
     * @param progressBar progress bar for displaying the current progress.
     *                    <em>Can be null!</em>
     */
    public void setProgressBar(JProgressBar progressBar) {
        this.progressBar = progressBar;

        if (progressBar != null) {
            pBarStringPainted = progressBar.isStringPainted();
        }
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
     * Sets properties to set persistent values.
     *
     * @param properties properties read and written persistent. The key
     *                   has to be unique, e.g. a combination of the fully
     *                   qualified class and field name
     */
    public void setProperties(Properties properties) {
        if (properties == null) {
            throw new NullPointerException("properties == null");
        }

        this.properties = properties;
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
     * @param files files to process. The first element of the pair is the image
     *              file, the second it's thumbnail or null
     */
    public void setFiles(List<Pair<File, Image>> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }

        synchronized (files) {
            thumbnailOfFile.clear();

            for (Pair<File, Image> tnOfFile : files) {
                File  file = tnOfFile.getFirst();
                Image tn   = tnOfFile.getSecond();

                if (file == null) {
                    throw new IllegalArgumentException("File is null!");
                }

                thumbnailOfFile.put(file, tn);
            }
        }
    }

    /**
     * Returns the files to process.
     *
     * @return files
     */
    public List<File> getFiles() {
        synchronized (thumbnailOfFile) {
            return new ArrayList<File>(thumbnailOfFile.keySet());
        }
    }

    /**
     * Returns a thumbnail of a file.
     *
     * @param  file file
     * @return      thumbnail or null
     */
    public Image getThumbnail(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        return thumbnailOfFile.get(file);
    }

    /**
     * Adds a listener.
     *
     * @param listener listener. If the action runs in the background, it's
     *                 important to call
     *                 {@link PluginListener#action(PluginEvent)} with
     *                 one or more of <code>Event#FINISHED_...</code> arguments
     *                 so that it can release resources such as the process'
     *                 progress bar.
     */
    public void addPluginListener(PluginListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        pluginListeners.add(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener listener to remove
     */
    public void removePluginListener(PluginListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        pluginListeners.remove(listener);
    }

    /**
     * Notifies all added plugin listeners that a specific event has occured.
     *
     * @param event event
     */
    public void notifyPluginListeners(PluginEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }

        if (pluginListeners.size() > 0) {
            for (PluginListener listener : pluginListeners) {
                listener.action(event);
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
     * Returns an icon of this plugin.
     *
     * @return icon or null if this plugin has not an icon. Default: null.
     */
    public Icon getIcon() {
        return null;
    }

    /**
     * Returns all actions of this plugin (all what the plugin can do).
     *
     * @return actions ordered for presentation in a menu
     */
    public abstract List<? extends Action> getActions();

    /**
     * Returns the path to the XML contents file of the plugin's help.
     *
     * The contents file lists html filenames relative to itself following the
     * syntax in <code>helpindex.dtd</code>, which has to be in the same
     * directory. A sample file set exists in the package
     * <code>org.jphototagger.plugin.help</code>.
     * <p>
     * Hint: To support multiple languages You can return paths depending on
     * the Locale.
     *
     * @return help contents file path, e.g.
     *        <code>"/com/myname/jpt/plugin/doc/en/contents.xml"</code> or null
     *        the plugin has no help. This class returns null.
     */
    public String getHelpContentsPath() {
        return null;
    }

    /**
     * Returns the Name of the first help page which will be selected if the
     * user see calls the plugin's help.
     *
     * The help page is a file in the same directory as
     * {@link #getHelpContentsPath()}.
     *
     * @return help page name, e.g. <code>"index.html"</code> or null if the
     *         plugin does not provide help. This class return null.
     */
    public String getFirstHelpPageName() {
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
     * Paints the progress bar start event.
     *
     * @param minimum miniumum
     * @param maximum maximum
     * @param value   current value
     * @param string  string to paint onto progress bar or null
     */
    public void progressStarted(int minimum, int maximum, int value,
                                String string) {
        setProgressBar(0, maximum, value, string);
    }

    /**
     * Paints a progress bar progress event.
     *
     * @param minimum minimum
     * @param maximum maximum
     * @param value   current value
     * @param string  string to paint onto progress bar or null
     */
    public void progressPerformed(int minimum, int maximum, int value,
                                  String string) {
        setProgressBar(minimum, maximum, value, string);
    }

    /**
     * Paints the progress bar progress event.
     */
    public void progressEnded() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (progressBar != null) {
                    if (progressBar.isStringPainted()) {
                        progressBar.setString("");
                    }

                    progressBar.setStringPainted(pBarStringPainted);
                    progressBar.setValue(0);
                }
            }
        });
    }

    private void setProgressBar(final int minimum, final int maximum,
                                final int value, final String string) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (progressBar != null) {
                    progressBar.setMinimum(minimum);
                    progressBar.setMaximum(maximum);
                    progressBar.setValue(value);

                    if (string != null) {
                        progressBar.setStringPainted(true);
                        progressBar.setString(string);
                    }
                }
            }
        });
    }
}
