package org.jphototagger.program.help;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.help.HelpBrowser;
import org.jphototagger.lib.help.HelpBrowserEvent;
import org.jphototagger.lib.help.HelpBrowserListener;
import org.jphototagger.lib.help.HelpContentProvider;
import org.jphototagger.lib.help.HelpDisplayer;
import org.jphototagger.lib.help.HelpNode;
import org.jphototagger.lib.help.HelpUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
@ServiceProviders({
    @ServiceProvider(service = HelpDisplayer.class),
    @ServiceProvider(service = HelpContentProvider.class)
})
public final class HelpBrowserDisplayer implements HelpDisplayer, HelpContentProvider {

    private static final String KEY_CURRENT_URL = HelpBrowserDisplayer.class.getName() + ".CurrentURL";
    private static final HelpBrowser HELP_BROWSER;

    static {
        HelpNode rootNode = HelpUtil.createNodeFromHelpContentProviders();
        HELP_BROWSER = new HelpBrowser(rootNode);
        HELP_BROWSER.setTitle(Bundle.getString(HelpBrowserDisplayer.class, "HelpBrowserDisplayer.Title"));
        restoreCurrentUrl();
        HELP_BROWSER.addHelpBrowserListener(new ContentsHelpBrowserListener());
    }

    static void browseHelp(String helpPageUrl) {
        if (StringUtil.hasContent(helpPageUrl)) {
            synchronized (HelpBrowser.class) {
                HELP_BROWSER.setDisplayUrl(helpPageUrl);
            }
        } else {
            restoreCurrentUrl();
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

    private static void restoreCurrentUrl() {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        if (preferences.containsKey(KEY_CURRENT_URL)) {
            String currentUrl = preferences.getString(KEY_CURRENT_URL);
            synchronized (HelpBrowser.class) {
                HELP_BROWSER.setDisplayUrl(currentUrl);
            }
        }
    }

    private static void persistCurrentUrl(String currentUrl) {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        preferences.setString(KEY_CURRENT_URL, currentUrl);
    }

    private static class ContentsHelpBrowserListener implements HelpBrowserListener {

        @Override
        public void actionPerformed(HelpBrowserEvent evt) {
            if (evt.getType().equals(HelpBrowserEvent.Type.URL_CHANGED)) {
                String currentUrl = HELP_BROWSER.toPageUrl(evt.getUrl());
                persistCurrentUrl(currentUrl);
            }
        }
    }
}
