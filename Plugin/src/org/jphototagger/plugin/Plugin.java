package org.jphototagger.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.List;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.ServiceLookup;
import org.jphototagger.services.core.ProgressBarProvider;

/**
 * Base class for Plugins processing selected files. JPhotoTagger presents the
 * capability of this plugin to the user if files are selected and calls
 * {@link #processFiles(List)}. Examples are transferring images to a web service
 * or converting image files into other file formats.
 * <p>
 * For adding components (windows) and menu items to JPhotoTagger's GUI, implement
 * a service defined in Project <strong>JPhotoTagger: Services</strong>, package
 * {@code org.jphototagger.services.plugin}, see the JDK documentation for
 * <a href="http://java.sun.com/j2se/1.4.2/docs/guide/jar/jar.html#Service%20Provider">Service Provider</a>.
 * The project <strong>JPhotoTagger: Library</strong>'s class {@code ServiceLookup} has
 * methods for getting one or all implementations of a specific service.
 * <p>
 * <strong>Important notice:</strong> A plugin shall use interfaces and Java Services.
 *  E.g. if a subproject can deliver thumbnails, it implements a thumbnail provider, publish it
 * in the <code>META-INF.services</code> folder and the plugin asks for an implementation of the
 * thumbnail provider interface.
 *
 * @author Elmar Baumann
 */
public abstract class Plugin {

    private JProgressBar progressBar;
    private final List<File> files = new ArrayList<File>();
    private final Set<PluginListener> pluginListeners = new CopyOnWriteArraySet<PluginListener>();
    private boolean pBarStringPainted;

    /**
     * Adds a listener.
     *
     * @param listener listener. If the action runs in the background, it's
     *                 important to call
     *                 {@link PluginListener#action(PluginEvent)} with
     *                 one or more of <code>Event#FINISHED_...</code> arguments
     *                 so that it can release resources
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
    protected void notifyPluginListeners(PluginEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }

        if (pluginListeners.size() > 0) {
            for (PluginListener listener : pluginListeners) {
                listener.action(event);
            }
        }

        if (event.getType().isFinished()) {
            releaseProgressBar();
        }
    }

    public abstract void processFiles(List<File> files);

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

    private void getProgressBarFromService() {
        if (progressBar != null) {
            return;
        }

        ProgressBarProvider progressBarProvider = ServiceLookup.lookup(ProgressBarProvider.class);

        if (progressBarProvider != null) {
            progressBar = progressBarProvider.getProgressBar(this);
        }
    }

    private void releaseProgressBar() {
        if (progressBar == null) {
            return;
        }

        ProgressBarProvider progressBarProvider = ServiceLookup.lookup(ProgressBarProvider.class);

        if (progressBarProvider != null) {
            progressBarProvider.releaseProgressBar(progressBar, this);
        }

        progressBar = null;
    }

    /**
     * Paints the progress bar start event.
     *
     * @param minimum miniumum
     * @param maximum maximum
     * @param value   current value
     * @param string  string to paint onto progress bar or null
     */
    protected void progressStarted(int minimum, int maximum, int value, String string) {
        getProgressBarFromService();
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
    protected void progressPerformed(int minimum, int maximum, int value, String string) {
        getProgressBarFromService();
        setProgressBar(minimum, maximum, value, string);
    }

    /**
     * Paints the progress bar end event.
     */
    protected void progressEnded() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            public void run() {
                if (progressBar != null) {
                    if (progressBar.isStringPainted()) {
                        progressBar.setString("");
                    }

                    progressBar.setStringPainted(pBarStringPainted);
                    progressBar.setValue(0);
                    releaseProgressBar();
                }
            }
        });
    }

    private void setProgressBar(final int minimum, final int maximum, final int value, final String string) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

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
