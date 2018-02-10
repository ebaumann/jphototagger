package org.jphototagger.resources;

import java.util.Locale;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author Elmar Baumann
 */
public class ResourcesCommonTest {

    @Test
    public void testToLocalizedPath() {
        final String lang = Locale.getDefault().getLanguage();
        String path = "image.png";
        String expResult = lang + "/" + path;
        String result = ResourcesCommon.toLocalizedPath(path);

        assertEquals(expResult, result);
        path = "/org/jphototagger/program/resoure/images/image.png";
        expResult = "/org/jphototagger/program/resoure/images/" + lang + "/image.png";
        result = ResourcesCommon.toLocalizedPath(path);
        assertEquals(expResult, result);
    }
}
