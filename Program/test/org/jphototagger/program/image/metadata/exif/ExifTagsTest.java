package org.jphototagger.program.image.metadata.exif;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elmar Baumann
 */
public class ExifTagsTest {

    @Test
    public void testWriteToFile() throws Exception {
        File file = getFile();
        try {
            ExifTag exifTag1 = new ExifTag(125, 250, 500, 1000L, new byte[]{0x3C, 0x78, 0x3A}, "abc", 47, "bla", ExifMetadata.IfdType.EXIF);
            ExifTag exifTag2 = new ExifTag(521, 52, 5, 1L, new byte[]{0x30, 0x75, 0x0A}, "def", 74, "blu", ExifMetadata.IfdType.GPS);
            ExifTags exifTags = new ExifTags();

            exifTags.addExifTag(exifTag1);
            exifTags.addExifTag(exifTag2);

            exifTags.writeToFile(file);

            ExifTags exifTagsRead = ExifTags.readFromFile(file);
            List<ExifTag> exifTags1 = new ArrayList<ExifTag>(exifTagsRead.getExifTags());

            assertEquals(2, exifTags1.size());

            ExifTag exifTag1Read = exifTagsRead.exifTagById(125);
            ExifTag exifTag2Read = exifTagsRead.exifTagById(521);

            assertExifTagsEquals(exifTag1, exifTag1Read);
            assertExifTagsEquals(exifTag2, exifTag2Read);
        } finally {
            file.delete();
        }
    }

    private void assertExifTagsEquals(ExifTag exifTag1, ExifTag exifTag2) {
        assertEquals(exifTag1.byteOrder(), exifTag2.byteOrder());
        assertEquals(exifTag1.byteOrderId(), exifTag2.byteOrderId());
        assertEquals(exifTag1.dataType(), exifTag2.dataType());
        assertEquals(exifTag1.id(), exifTag2.id());
        assertEquals(exifTag1.idValue(), exifTag2.idValue());
        assertEquals(exifTag1.ifdType(), exifTag2.ifdType());
        assertEquals(exifTag1.name(), exifTag2.name());
        assertArrayEquals(exifTag1.rawValue(), exifTag2.rawValue());
        assertEquals(exifTag1.stringValue(), exifTag2.stringValue());
        assertEquals(exifTag1.valueCount(), exifTag2.valueCount());
        assertEquals(exifTag1.valueOffset(), exifTag2.valueOffset());
    }

    @Test
    public void testReadFromFile() throws Exception {
        // tested through testWriteToFile()
    }

    private File getFile() {
        String tmpdir = System.getProperty("java.io.tmpdir");

        return new File(tmpdir + File.separator + "ExifTagsTest.xml");
    }

}