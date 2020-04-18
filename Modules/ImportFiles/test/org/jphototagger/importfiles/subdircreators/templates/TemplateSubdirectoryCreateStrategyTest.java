package org.jphototagger.importfiles.subdircreators.templates;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static org.jphototagger.importfiles.subdircreators.templates.TemplateSubdirectoryCreateStrategy.FILE_DATE_DAY;
import static org.jphototagger.importfiles.subdircreators.templates.TemplateSubdirectoryCreateStrategy.FILE_DATE_MONTH;
import static org.jphototagger.importfiles.subdircreators.templates.TemplateSubdirectoryCreateStrategy.FILE_DATE_YEAR;
import org.jphototagger.lib.util.SystemProperties;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Elmar Baumann
 */
public class TemplateSubdirectoryCreateStrategyTest {

    @Test
    public void testSuggestSubdirectoryName() throws IOException {
        SubdirectoryTemplate template = new SubdirectoryTemplate();
        TemplateSubdirectoryCreateStrategy strategy = new TemplateSubdirectoryCreateStrategy(template);
        File file = new File(SystemProperties.getTemporaryDir(), "jpt-TemplateSubdirectoryCreateStrategyTest");
        String customPrefix = "Test" + File.separator;
        boolean fileCreated = false;

        template.setTemplate(customPrefix + FILE_DATE_YEAR + "-" + FILE_DATE_MONTH + "-" + FILE_DATE_DAY);

        try {
            fileCreated = file.createNewFile();
            Date date = new Date(file.lastModified());
            SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd");
            String expected = customPrefix + df.format(date);
            String actual = strategy.suggestSubdirectoryName(file);
            Assert.assertEquals(expected, actual);
        } finally {
            if (fileCreated) {
                file.delete();
            }
        }
    }
}
