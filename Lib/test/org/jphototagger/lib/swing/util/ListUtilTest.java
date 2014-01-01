package org.jphototagger.lib.swing.util;

import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultListModel;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Elmar Baumann
 */
public class ListUtilTest {

    @Test
    public void testGetElements() {
        List<String> src = Arrays.asList("a", "b", "C");
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String s : src) {
            model.addElement(s);
        }
        List<String> got = ListUtil.getElements(model);
        for (int i = 0; i < model.size(); i++) {
            assertEquals(src.get(i), got.get(i));
        }
    }

    @Test
    public void testSort() {
        List<String> src = Arrays.asList("z", "a", "q");
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String s : src) {
            model.addElement(s);
        }
        ListUtil.sort(model, String.CASE_INSENSITIVE_ORDER);
        assertEquals("a", model.get(0));
        assertEquals("q", model.get(1));
        assertEquals("z", model.get(2));
    }
}
