/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.lib.io;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tests the Class {@link de.elmar_baumann. }.
 *
 * @author Elmar Baumann
 * @version 2010/02/19
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
        System.out.println("getIndexOf");

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
