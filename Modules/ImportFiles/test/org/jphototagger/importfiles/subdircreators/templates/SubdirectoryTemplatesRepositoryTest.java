package org.jphototagger.importfiles.subdircreators.templates;

import java.io.File;
import org.jphototagger.lib.util.SystemProperties;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Elmar Baumann
 */
public class SubdirectoryTemplatesRepositoryTest {

    @Test
    public void testLoad() throws Exception {
        File repoFile = new File(SystemProperties.getTemporaryDir(), "jpt.SubdirectoryTemplatesRepositoryTest-load.xml");
        SubdirectoryTemplatesRepository repo = new SubdirectoryTemplatesRepository(repoFile);
        SubdirectoryTemplate template1 = new SubdirectoryTemplate();
        SubdirectoryTemplate template2 = new SubdirectoryTemplate();
        SubdirectoryTemplates templates = new SubdirectoryTemplates();

        template1.setDisplayName("displayname 1");
        template1.setTemplate("template 1");
        template2.setDisplayName("displayname 2");
        template2.setTemplate("template 2");
        templates.getTemplates().add(template1);
        templates.getTemplates().add(template2);

        try {
            SubdirectoryTemplates actual = repo.load();
            Assert.assertNull(actual);

            repo.save(templates);
            actual = repo.load();
            Assert.assertNotNull(findTemplateByDisplayname(templates, template1.getDisplayName()));
            Assert.assertNotNull(findTemplateByDisplayname(templates, template2.getDisplayName()));
        } finally {
            repoFile.delete();
        }
    }

    @Test
    public void testSave() throws Exception {
        // implicit tested via #testLoad()
    }

    private SubdirectoryTemplate findTemplateByDisplayname(SubdirectoryTemplates src, String displayname) {
        for (SubdirectoryTemplate template : src.getTemplates()) {
            if (displayname.equals(template.getDisplayName())) {
                return template;
            }
        }
        return null;
    }
}
