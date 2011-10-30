package org.jphototagger.program.help;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.lib.help.HelpBrowser;
import org.jphototagger.lib.help.HelpDisplay;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = HelpDisplay.class)
public final class HelpBrowserDisplayer implements HelpDisplay {

    private static final String HELP_CONTENTS_URL = "/org/jphototagger/program/resource/doc/de/contents.xml";
    private static final HelpBrowser HELP_BROWSER = new HelpBrowser();

    static {
        HELP_BROWSER.setContentsUrl(HELP_CONTENTS_URL);
    }

    static void browseHelp(String helpPageUrl) {
        if (StringUtil.hasContent(helpPageUrl)) {
            synchronized (HelpBrowser.class) {
                HELP_BROWSER.setDisplayUrl(helpPageUrl);
            }
        }
        ComponentUtil.show(HELP_BROWSER);
    }

    @Override
    public void showHelp(String url) {
        browseHelp(url);
    }
}
