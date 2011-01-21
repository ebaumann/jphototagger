package org.jphototagger.lib.io;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Elmar Baumann
 */
public class FileUtilTest {
    public FileUtilTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception {}

    /**
     * Test of getIndexOf method, of class FileUtil.
     */
    @Test
    public void testGetIndexOf() {
        byte[] search = {
            0x3C, 0x3F, 0x78, 0x70, 0x61, 0x63, 0x6B, 0x65, 0x74, 0x20, 0x62,
            0x65, 0x67, 0x69, 0x6E, 0x3D
        };
        long             result;
        RandomAccessFile file = null;

        try {
            file = new RandomAccessFile(FileUtil.getFileOfPackage(getClass(),
                    "FileUtilTest_001"), "r");
            result = FileUtil.getIndexOf(file, search);
            file.close();
            assertEquals(-1L, result);
            file = new RandomAccessFile(FileUtil.getFileOfPackage(getClass(),
                    "FileUtilTest_002"), "r");
            result = FileUtil.getIndexOf(file, search);
            file.close();
            assertEquals(0L, result);
            file = new RandomAccessFile(FileUtil.getFileOfPackage(getClass(),
                    "FileUtilTest_003"), "r");
            result = FileUtil.getIndexOf(file, search);
            file.close();
            assertEquals(18L, result);
            file = new RandomAccessFile(FileUtil.getFileOfPackage(getClass(),
                    "FileUtilTest_003"), "r");
            file.seek(17);
            result = FileUtil.getIndexOf(file, search);
            file.close();
            assertEquals(18L, result);
            file = new RandomAccessFile(FileUtil.getFileOfPackage(getClass(),
                    "FileUtilTest_003"), "r");
            file.seek(18);
            result = FileUtil.getIndexOf(file, search);
            file.close();
            assertEquals(18L, result);
            file = new RandomAccessFile(FileUtil.getFileOfPackage(getClass(),
                    "FileUtilTest_003"), "r");
            file.seek(19);
            result = FileUtil.getIndexOf(file, search);
            file.close();
            assertEquals(-1L, result);
            file = new RandomAccessFile(FileUtil.getFileOfPackage(getClass(),
                    "FileUtilTest_004"), "r");
            result = FileUtil.getIndexOf(file, search);
            file.close();
            assertEquals(0L, result);
            file = new RandomAccessFile(FileUtil.getFileOfPackage(getClass(),
                    "FileUtilTest_005"), "r");
            result = FileUtil.getIndexOf(file, search);
            file.close();
            assertEquals(5L, result);
            file = new RandomAccessFile(FileUtil.getFileOfPackage(getClass(),
                    "FileUtilTest_003"), "r");

            byte[] search2 = { 0x00, 0x00, 0x00, 0x00, 0x00 };

            result = FileUtil.getIndexOf(file, search2);
            file.close();
            assertEquals(-1, result);
            file = new RandomAccessFile(FileUtil.getFileOfPackage(getClass(),
                    "FileUtilTest_003"), "r");

            // pattern much larger than the file
            byte[] search3 = {
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00
            };

            result = FileUtil.getIndexOf(file, search3);
            file.close();
            assertEquals(-1, result);
        } catch (IOException ex) {
            Logger.getLogger(FileUtilTest.class.getName()).log(Level.SEVERE,
                             null, ex);
            fail("Exception");
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException ex) {
                    Logger.getLogger(FileUtilTest.class.getName()).log(
                        Level.SEVERE, null, ex);
                }
            }
        }
    }
}
