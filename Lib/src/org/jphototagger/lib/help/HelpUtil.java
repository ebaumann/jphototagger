package org.jphototagger.lib.help;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openide.util.Lookup;

import org.jphototagger.lib.api.PositionProviderAscendingComparator;

/**
 * @author Elmar Baumann
 */
public final class HelpUtil {

    /**
     * Looks up for a {@link HelpDisplayer} implementation and calls
     * {@link HelpDisplayer#displayHelp(java.lang.String)}.
     *
     * @param url URL to display
     */
    public static void displayHelp(String url) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }

        HelpDisplayer helpDisplay = Lookup.getDefault().lookup(HelpDisplayer.class);

        if (helpDisplay != null) {
            helpDisplay.displayHelp(url);
        }
    }

    /**
     * Looks up all {@link HelpContentProvider}s content URLs, creates nodes
     * with a {@link HelpIndexParser} and merges them.
     *
     * @return merged node
     */
    public static HelpNode createNodeFromHelpContentProviders() {
        HelpNode rootNode = null;
        List<HelpContentProvider> providers = new ArrayList<HelpContentProvider>(Lookup.getDefault().lookupAll(HelpContentProvider.class));
        Collections.sort(providers, PositionProviderAscendingComparator.INSTANCE);
        for (HelpContentProvider provider : providers) {
            String helpContentsUrl = provider.getHelpContentUrl();
            HelpIndexParser helpIndexParser = new HelpIndexParser(helpContentsUrl);
            HelpNode helpNode = helpIndexParser.parse(HelpUtil.class.getResourceAsStream(helpContentsUrl));
            if (rootNode == null) {
                rootNode = helpNode;
            } else {
                addChildrenToNode(helpNode, rootNode);
            }
        }
        return rootNode;
    }

    private static void addChildrenToNode(HelpNode fromHelpNode, HelpNode toHelpNode) {
        int childCount = fromHelpNode.getChildCount();
        for (int index = 0; index < childCount; index++) {
            Object child = fromHelpNode.getChild(index);
            if (child instanceof HelpNode) {
                HelpNode helpNode = (HelpNode) child;
                toHelpNode.addNode(helpNode);
            } else if (child instanceof HelpPage) {
                HelpPage helpPage = (HelpPage) child;
                toHelpNode.addPage(helpPage);
            }
        }
    }

    private HelpUtil() {
    }
}