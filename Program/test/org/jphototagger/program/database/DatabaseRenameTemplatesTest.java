package org.jphototagger.program.database;

import org.jphototagger.program.app.SplashScreen;
import org.jphototagger.domain.RenameTemplate;
import org.jphototagger.program.data.RenameTemplateTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Set;

/**
 *
 * @author Elmar Baumann
 */
public class DatabaseRenameTemplatesTest {

    private final DatabaseRenameTemplates db = DatabaseRenameTemplates.INSTANCE;

    public DatabaseRenameTemplatesTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        SplashScreen.INSTANCE.init();
        ConnectionPool.INSTANCE.init();
        DatabaseTables.INSTANCE.createTables();
        DatabaseRenameTemplates.INSTANCE.delete(RenameTemplateTest.createTemplate().getName());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        DatabaseRenameTemplates.INSTANCE.delete(RenameTemplateTest.createTemplate().getName());
        DatabaseRenameTemplates.INSTANCE.delete("New name");
        DatabaseMaintainance.INSTANCE.shutdown();
    }

    /**
     * Test of insert method, of class DatabaseRenameTemplates.
     */
    @Test
    public void testInsert() {
        RenameTemplate template = RenameTemplateTest.createTemplate();

        template.setId(null);
        db.insert(template);
        assertNotNull(template.getId());
        template = db.find(template.getName());
        template.setId(Long.valueOf(0));
        RenameTemplateTest.assertEqualsCreated(template);
        db.delete(template.getName());
    }

    /**
     * Test of update method, of class DatabaseRenameTemplates.
     */
    @Test
    public void testUpdate() {
        RenameTemplate template = RenameTemplateTest.createTemplate();

        template.setId(null);
        db.insert(template);
        template.setName("New name");
        db.update(template);

        RenameTemplate tmpl = db.find(template.getName());

        assertEquals(template.getName(), tmpl.getName());
        db.delete("New name");
    }

    /**
     * Test of delete method, of class DatabaseRenameTemplates.
     */
    @Test
    public void testDelete() {
        RenameTemplate template = RenameTemplateTest.createTemplate();

        template.setId(null);
        db.insert(template);
        db.delete(template.getName());
        assertNull(db.find(template.getName()));
    }

    /**
     * Test of getAll method, of class DatabaseRenameTemplates.
     */
    @Test
    public void testGetAll() {
        RenameTemplate template1 = RenameTemplateTest.createTemplate();
        RenameTemplate template2 = new RenameTemplate(template1);

        template1.setName("quaffel@buffel");
        template2.setName("XYZ@@yyy");
        template1.setId(null);
        db.insert(template1);
        template2.setId(null);
        db.insert(template2);

        Set<RenameTemplate> all = db.getAll();

        db.delete(template1.getName());
        db.delete(template2.getName());
        assertTrue(all.contains(template1));
        assertTrue(all.contains(template2));
    }

    /**
     * Test of find method, of class DatabaseRenameTemplates.
     */
    @Test
    public void testFind() {
        RenameTemplate template = RenameTemplateTest.createTemplate();

        db.delete(template.getName());
        template.setId(null);
        db.insert(template);
        assertEquals(template.getName(), db.find(template.getName()).getName());
        db.delete(template.getName());
    }

    /**
     * Test of exists method, of class DatabaseRenameTemplates.
     */
    @Test
    public void testExists() {
        RenameTemplate template = RenameTemplateTest.createTemplate();

        db.delete(template.getName());
        template.setId(null);
        db.insert(template);
        assertTrue(db.exists(template.getName()));
        db.delete(template.getName());
    }
}
