package org.jphototagger.lib.util;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Elmar Baumann
 */
public class StringEscapeUtilTest {

    @Test
    public void testEscapeHTML() {
        String html = "";
        String expected = "";
        String escapedHtml = HtmlUtil.escapeHTML(html);

        assertEquals(expected, escapedHtml);

        html = "<";
        expected = "&lt;";
        escapedHtml = HtmlUtil.escapeHTML(html);
        assertEquals(expected, escapedHtml);

        html = "\"If a > b and b > c then is c < a\"";
        expected = "&quot;If a &gt; b and b &gt; c then is c &lt; a&quot;";
        escapedHtml = HtmlUtil.escapeHTML(html);
        assertEquals(expected, escapedHtml);

        html = "(a && b) is a&&b";
        expected = "(a &amp;&amp; b) is a&amp;&amp;b";
        escapedHtml = HtmlUtil.escapeHTML(html);
        assertEquals(expected, escapedHtml);
    }
}
