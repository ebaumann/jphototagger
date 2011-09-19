package org.jphototagger.lib.io;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Elmar Baumann
 */
public class FileUtilTest {

    @Test
    public void testGetAbsolutePathnamePrefix() {
        String path = "";
        String gotPath = FileUtil.getAbsolutePathnamePrefix(path);

        assertTrue(gotPath.isEmpty());

        path = "a";
        gotPath = FileUtil.getAbsolutePathnamePrefix(path);
        assertEquals(path, gotPath);

        path = "c.txt";
        gotPath = FileUtil.getAbsolutePathnamePrefix(path);
        assertEquals("c", gotPath);

        path = "/a/b/c.txt";
        gotPath = FileUtil.getAbsolutePathnamePrefix(path);
        assertEquals("/a/b/c", gotPath);
    }

    @Test
    public void testFilesWithEqualBasenames() {
        File file1 = new File("/a/b/c.txt");
        File file2 = new File("/a/b/c.asc");
        File file3 = new File("/a/b/c.xmp");
        File file4 = new File("/a/b/b.jpg");
        List<File> files = Collections.emptyList();
        List<File> gotFiles = FileUtil.getFilesWithEqualBasenames(files, "");

        assertTrue(gotFiles.isEmpty());

        files = Arrays.asList(file1);
        gotFiles = FileUtil.getFilesWithEqualBasenames(files, "");
        assertTrue(gotFiles.isEmpty());

        files = Arrays.asList(file1, file2, file3, file4);
        gotFiles = FileUtil.getFilesWithEqualBasenames(files, "");
        assertEquals(3, gotFiles.size());
        assertTrue(gotFiles.contains(file1));
        assertTrue(gotFiles.contains(file2));
        assertTrue(gotFiles.contains(file3));
        assertFalse(gotFiles.contains(file4));

        files = Arrays.asList(file1, file2, file3, file4);
        gotFiles = FileUtil.getFilesWithEqualBasenames(files, "XMP");
        assertEquals(2, gotFiles.size());
        assertTrue(gotFiles.contains(file1));
        assertTrue(gotFiles.contains(file2));
        assertFalse(gotFiles.contains(file3));
        assertFalse(gotFiles.contains(file4));

        files = Arrays.asList(file1, file3, file4);
        gotFiles = FileUtil.getFilesWithEqualBasenames(files, "XMP");
        assertTrue(gotFiles.isEmpty());

        files = Arrays.asList(file1, file3, file4);
        gotFiles = FileUtil.getFilesWithEqualBasenames(files, "");
        assertEquals(2, gotFiles.size());
        assertTrue(gotFiles.contains(file1));
        assertTrue(gotFiles.contains(file3));
        assertFalse(gotFiles.contains(file4));
    }
}
