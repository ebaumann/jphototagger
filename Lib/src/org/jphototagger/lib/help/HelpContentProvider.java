package org.jphototagger.lib.help;

/**
 * @author Elmar Baumann
 */
public interface HelpContentProvider {

    /**
     *
     * @return path tho the XML-File, e.g. <code>"/org/jphototagger/plugin/cftc/help/contents.xml"</code>
     */
    String getHelpContentUrl();
}
