package org.jphototagger.lib.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jphototagger.lib.util.SystemProperties;

import static org.junit.Assert.*;
import org.junit.Test;

/**
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

    @Test
    public void testToStringWithMaximumLength() {
        String filepath = "/home/elmar/bilder/2011/2011-06 Motorradtour Fränkische Schweiz/2011-06-12/2011-06-12-001.SRW";
        File file = new File(filepath);

        String string = FileUtil.toStringWithMaximumLength(file, 0);
        assertEquals("", string);

        string = FileUtil.toStringWithMaximumLength(file, 1);
        assertEquals(".", string);

        string = FileUtil.toStringWithMaximumLength(file, 2);
        assertEquals("..", string);

        string = FileUtil.toStringWithMaximumLength(file, 3);
        assertEquals("...", string);

        string = FileUtil.toStringWithMaximumLength(file, 4);
        assertEquals("...W", string);

        string = FileUtil.toStringWithMaximumLength(file, 18);
        assertEquals("...1-06-12-001.SRW", string);

        string = FileUtil.toStringWithMaximumLength(file, Integer.MAX_VALUE);
        assertEquals(filepath, string);

        string = FileUtil.toStringWithMaximumLength(file, 95);
        assertEquals(filepath, string);

        string = FileUtil.toStringWithMaximumLength(file, 94);
        assertEquals(filepath, string);

        string = FileUtil.toStringWithMaximumLength(file, 22);
        assertEquals(".../2011-06-12-001.SRW", string);

        string = FileUtil.toStringWithMaximumLength(file, 53);
        assertEquals("/home/elmar/bilder/2011/2011-06.../2011-06-12-001.SRW", string);
    }

    @Test
    public void testInSameDirectory() {
        assertTrue(FileUtil.inSameDirectory(Collections.<File>emptyList()));
        assertTrue(FileUtil.inSameDirectory(Arrays.asList(new File("/home/elmar/bla.txt"))));
        assertTrue(FileUtil.inSameDirectory(Arrays.asList(
                new File("/home/elmar/bla.txt"), new File("/home/elmar/blubb.txt"), new File("/home/elmar/blob.txt"))));
        assertFalse(FileUtil.inSameDirectory(Arrays.asList(
                new File("/home/elmar/bla.txt"), new File("/home/elmar/blubb.txt"), new File("/root/blob.txt"))));
    }


    @Test
    public void testGetMd5HexOfFileContent() throws Exception {
        InputStream is = null;
        OutputStream os = null;
        File outfile = new File(SystemProperties.getTemporaryDir() + File.separator + "JPT-temp-test-md5hex");
        if (outfile.exists()) {
            throw new RuntimeException("File exists: " + outfile);
        }
        try {
            is = FileUtilTest.class.getResourceAsStream("md5file1"); // text file
            os = new FileOutputStream(outfile);
            IoUtil.fromIsToOs(is, os);
            String expected = "646168e06ba20d739d3d9bb76487db86";
            String actual = FileUtil.getMd5HexOfFileContent(outfile);
            assertEquals(expected, actual);
            FileUtil.writeStringAsFile("", outfile); // empty file
            expected = "d41d8cd98f00b204e9800998ecf8427e";
            actual = FileUtil.getMd5HexOfFileContent(outfile);
            assertEquals(expected, actual);
            is = FileUtilTest.class.getResourceAsStream("md5file2"); // binary file
            os = new FileOutputStream(outfile);
            IoUtil.fromIsToOs(is, os);
            expected = "7ffb89fac9a950e844ea940d266dd26d";
            actual = FileUtil.getMd5HexOfFileContent(outfile);
            assertEquals(expected, actual);
        } finally {
            IoUtil.close(is);
            IoUtil.close(os);
            outfile.delete();
        }

    }
}
