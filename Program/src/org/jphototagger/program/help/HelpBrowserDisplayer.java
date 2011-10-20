package org.jphototagger.program.help;

import java.net.URL;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.help.HelpBrowser;
import org.jphototagger.lib.help.HelpBrowserEvent;
import org.jphototagger.lib.help.HelpBrowserListener;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
final class HelpBrowserDisplayer {

    private static final String HELP_CONTENTS_URL = "/org/jphototagger/program/resource/doc/de/contents.xml";
    private static final String KEY_CURRENT_URL = ShowHelpAction.class.getName() + ".CurrentURL";
    private static String currentHelpContentsUrl;

    static {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        currentHelpContentsUrl = preferences.getString(KEY_CURRENT_URL);
        HelpBrowser.INSTANCE.addHelpBrowserListener(new ContentsHelpBrowserListener());
    }

    static void browseHelp(String helpContentsUrl) {
        if (StringUtil.hasContent(helpContentsUrl)) {
            synchronized (HelpBrowser.class) {
                currentHelpContentsUrl = helpContentsUrl;
            }
        }
        setHelpBrowsersContentUrl();
        showHelpBrowser();
    }

    private static void setHelpBrowsersContentUrl() {
        boolean helpBrowserHasNoContent = HelpBrowser.INSTANCE.getContentsUrl() == null;
        boolean helpBrowserDisplaysOtherContent = !helpBrowserHasNoContent && HelpBrowser.INSTANCE.getContentsUrl().equals(HELP_CONTENTS_URL);
        if (helpBrowserHasNoContent || !helpBrowserDisplaysOtherContent) {
            HelpBrowser.INSTANCE.setContentsUrl(HELP_CONTENTS_URL);
        }
    }

    private static void showHelpBrowser() {
        setDisplayUrlToHelpBrowser();
        ComponentUtil.show(HelpBrowser.INSTANCE);
    }

    private static synchronized void setDisplayUrlToHelpBrowser() {
        if (StringUtil.hasContent(currentHelpContentsUrl)) {
            HelpBrowser.INSTANCE.setDisplayUrl(currentHelpContentsUrl);
        }
    }

    private static void setInBrowserSelectedUrlAsCurrentHelpContentsUrl(HelpBrowserEvent evt) {
        URL url = evt.getUrl();
        if (!url.getProtocol().startsWith("http")) {
            synchronized (HelpBrowser.class) {
                currentHelpContentsUrl = HelpBrowser.getLastPathComponent(url);
            }
            persistCurrentUrl();
        }
    }

    private static void persistCurrentUrl() {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        synchronized (HelpBrowser.class) {
            preferences.setString(KEY_CURRENT_URL, currentHelpContentsUrl);
        }
    }

    private static class ContentsHelpBrowserListener implements HelpBrowserListener {

        @Override
        public void actionPerformed(HelpBrowserEvent evt) {
            if (evt.getType().equals(HelpBrowserEvent.Type.URL_CHANGED)) {
                setInBrowserSelectedUrlAsCurrentHelpContentsUrl(evt);
            }
        }
    }

    private HelpBrowserDisplayer() {
    }
}
