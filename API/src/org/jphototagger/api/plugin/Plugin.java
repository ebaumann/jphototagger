package org.jphototagger.api.plugin;

import java.awt.Component;

import javax.swing.Icon;

/**
 * Extensions for JPhotoTagger invoked on demand.
 * <p>
 * Plugins will be invoked through the Java Service Provider Interface.
 *
 * @author Elmar Baumann
 */
public interface Plugin {

    /**
     *
     * @return Localized display name
     */
    String getDisplayName();

    /**
     *
     * @return Localized description
     */
    String getDescription();

    /**
     *
     * @return Icon or null
     */
    Icon getIcon();

    /**
     *
     * @return Component for JPhotoTagger's settings dialog or null
     */
    Component getSettingsComponent();

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
     *        the plugin has no help
     */
    public String getHelpContentsPath();

    /**
     * Returns the Name of the first help page which will be selected if the
     * user see calls the plugin's help.
     *
     * The help page is a file in the same directory as
     * {@link #getHelpContentsPath()}.
     *
     * @return help page name, e.g. <code>"index.html"</code> or null if the
     *         plugin does not provide help
     */
    String getFirstHelpPageName();
}
