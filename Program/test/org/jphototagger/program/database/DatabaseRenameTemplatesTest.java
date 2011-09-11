package org.jphototagger.program.database;

import java.util.Set;

import org.jphototagger.domain.repository.RenameTemplatesRepository;
import org.jphototagger.domain.repository.RepositoryMaintainance;
import org.jphototagger.domain.templates.RenameTemplate;
import org.jphototagger.program.app.SplashScreen;
import org.jphototagger.program.data.RenameTemplateTest;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openide.util.Lookup;

/**
 *
 * @author Elmar Baumann
 */
public class DatabaseRenameTemplatesTest {

    private final RenameTemplatesRepository repo = Lookup.getDefault().lookup(RenameTemplatesRepository.class);

    public DatabaseRenameTemplatesTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        SplashScreen.INSTANCE.init();
        ConnectionPool.INSTANCE.init();
        DatabaseTables.INSTANCE.createTables();
        DatabaseRenameTemplates.INSTANCE.deleteRenameTemplate(RenameTemplateTest.createTemplate().getName());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        DatabaseRenameTemplates.INSTANCE.deleteRenameTemplate(RenameTemplateTest.createTemplate().getName());
        DatabaseRenameTemplates.INSTANCE.deleteRenameTemplate("New name");
        Lookup.getDefault().lookup(RepositoryMaintainance.class).shutdownRepository();
    }

    /**
     * Test of insertRenameTemplate method, of class DatabaseRenameTemplates.
     */
    @Test
    public void testInsert() {
        RenameTemplate template = RenameTemplateTest.createTemplate();

        template.setId(null);
        repo.insertRenameTemplate(template);
        assertNotNull(template.getId());
        template = repo.findRenameTemplate(template.getName());
        template.setId(Long.valueOf(0));
        RenameTemplateTest.assertEqualsCreated(template);
        repo.deleteRenameTemplate(template.getName());
    }

    /**
     * Test of updateRenameTemplate method, of class DatabaseRenameTemplates.
     */
    @Test
    public void testUpdate() {
        RenameTemplate template = RenameTemplateTest.createTemplate();

        template.setId(null);
        repo.insertRenameTemplate(template);
        template.setName("New name");
        repo.updateRenameTemplate(template);

        RenameTemplate tmpl = repo.findRenameTemplate(template.getName());

        assertEquals(template.getName(), tmpl.getName());
        repo.deleteRenameTemplate("New name");
    }

    /**
     * Test of deleteRenameTemplate method, of class DatabaseRenameTemplates.
     */
    @Test
    public void testDelete() {
        RenameTemplate template = RenameTemplateTest.createTemplate();

        template.setId(null);
        repo.insertRenameTemplate(template);
        repo.deleteRenameTemplate(template.getName());
        assertNull(repo.findRenameTemplate(template.getName()));
    }

    /**
     * Test of getAllRenameTemplates method, of class DatabaseRenameTemplates.
     */
    @Test
    public void testGetAll() {
        RenameTemplate template1 = RenameTemplateTest.createTemplate();
        RenameTemplate template2 = new RenameTemplate(template1);

        template1.setName("quaffel@buffel");
        template2.setName("XYZ@@yyy");
        template1.setId(null);
        repo.insertRenameTemplate(template1);
        template2.setId(null);
        repo.insertRenameTemplate(template2);

        Set<RenameTemplate> all = repo.getAllRenameTemplates();

        repo.deleteRenameTemplate(template1.getName());
        repo.deleteRenameTemplate(template2.getName());
        assertTrue(all.contains(template1));
        assertTrue(all.contains(template2));
    }

    /**
     * Test of findRenameTemplate method, of class DatabaseRenameTemplates.
     */
    @Test
    public void testFind() {
        RenameTemplate template = RenameTemplateTest.createTemplate();

        repo.deleteRenameTemplate(template.getName());
        template.setId(null);
        repo.insertRenameTemplate(template);
        assertEquals(template.getName(), repo.findRenameTemplate(template.getName()).getName());
        repo.deleteRenameTemplate(template.getName());
    }

    /**
     * Test of existsRenameTemplate method, of class DatabaseRenameTemplates.
     */
    @Test
    public void testExists() {
        RenameTemplate template = RenameTemplateTest.createTemplate();

        repo.deleteRenameTemplate(template.getName());
        template.setId(null);
        repo.insertRenameTemplate(template);
        assertTrue(repo.existsRenameTemplate(template.getName()));
        repo.deleteRenameTemplate(template.getName());
    }
}
