package org.jphototagger.lib.io;

import org.junit.Test;
import java.io.File;
import java.util.Arrays;
import static org.junit.Assert.*;

/**
 * @author Elmar Baumann
 */
public class DeleteOutOfDateFilesTest {

    @Test
    public void testStart() throws Exception {
        String tmpDir = System.getProperty("java.io.tmpdir");
        String filename = tmpDir + File.separator + "DeleteOutOfDateFilesTest";
        File file = new File(filename);

        try {
            file.createNewFile();
            long createLastModified = file.lastModified();

            // The following block may fail, shall not under "usual" circumstances
            DeleteOutOfDateFiles deleteOutOfDateFiles = new DeleteOutOfDateFiles(Arrays.asList(file), 3000L);
            assertTrue(file.exists());
            int countDeleted = deleteOutOfDateFiles.start(null);
            assertEquals(0, countDeleted);
            assertTrue(file.exists());

            Thread.sleep(3500);
            long nextLastModified = file.lastModified();
            assertEquals(createLastModified, nextLastModified);
            countDeleted = deleteOutOfDateFiles.start(null);
            assertEquals(1, countDeleted);
            assertFalse(file.exists());
        } finally {
            file.delete();
        }
    }
}
