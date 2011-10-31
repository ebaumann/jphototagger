package org.jphototagger.program.help;

import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

import org.jphototagger.lib.help.HelpBrowser;
import org.jphototagger.lib.help.HelpContentProvider;
import org.jphototagger.lib.help.HelpDisplayer;
import org.jphototagger.lib.help.HelpNode;
import org.jphototagger.lib.help.HelpUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
@ServiceProviders({
    @ServiceProvider(service = HelpDisplayer.class),
    @ServiceProvider(service = HelpContentProvider.class)
})
public final class HelpBrowserDisplayer implements HelpDisplayer, HelpContentProvider {

    private static final HelpBrowser HELP_BROWSER;

    static {
        HelpNode rootNode = HelpUtil.createNodeFromHelpContentProviders();
        HELP_BROWSER = new HelpBrowser(rootNode);
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
    public void displayHelp(String url) {
        browseHelp(url);
    }

    @Override
    public String getHelpContentUrl() {
        return "/org/jphototagger/program/resource/doc/de/contents.xml";
    }

    @Override
    public int getPosition() {
        return Integer.MIN_VALUE;
    }
}
