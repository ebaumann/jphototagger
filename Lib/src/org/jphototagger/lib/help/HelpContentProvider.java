package org.jphototagger.lib.help;

import org.jphototagger.api.collections.PositionProvider;

/**
 * @author Elmar Baumann
 */
public interface HelpContentProvider extends PositionProvider {

    /**
     *
     * @return path tho the XML-File, e.g. <code>"/org/jphototagger/plugin/cftc/help/contents.xml"</code>
     */
    String getHelpContentUrl();
}
