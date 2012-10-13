package org.jphototagger.lib.help;

import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Elmar Baumann
 */
public class HelpSearchTest {

    private final HelpPage helpPage1 = new HelpPage();
    private final HelpPage helpPage2 = new HelpPage();
    private final HelpPage helpPage3 = new HelpPage();

    @Test
    public void testFindHelpPagesMatching() {
        HelpSearch helpSearch = new HelpSearch(createHelpNodes());
        helpSearch.startIndexing();
        List<HelpPage> foundHelpPages = helpSearch.findHelpPagesMatching("Help Page");
        assertEquals(3, foundHelpPages.size());
        assertTrue(foundHelpPages.contains(helpPage1));
        assertTrue(foundHelpPages.contains(helpPage2));
        assertTrue(foundHelpPages.contains(helpPage3));
        foundHelpPages = helpSearch.findHelpPagesMatching("helppage1onlycontent");
        assertEquals(1, foundHelpPages.size());
        assertTrue(foundHelpPages.contains(helpPage1));
        assertFalse(foundHelpPages.contains(helpPage2));
        foundHelpPages = helpSearch.findHelpPagesMatching("head");
        assertTrue(foundHelpPages.isEmpty());
    }

    private HelpNode createHelpNodes() {
        HelpNode rootNode = new HelpNode();
        HelpNode firstChildNode = new HelpNode();
        helpPage1.setTitle("Title Help Page 1");
        helpPage2.setTitle("Title Help Page 2");
        helpPage3.setTitle("Title Help Page 2");
        helpPage1.setUrl("/org/jphototagger/lib/help/HelpPage1.html");
        helpPage2.setUrl("/org/jphototagger/lib/help/HelpPage2.html");
        helpPage3.setUrl("/org/jphototagger/lib/help/HelpPage3.html");
        rootNode.addPage(helpPage1);
        rootNode.addPage(helpPage2);
        firstChildNode.addPage(helpPage3);
        rootNode.addNode(firstChildNode);
        return rootNode;
    }
}
