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

            ExifTag exifTag1Read = exifTagsRead.findExifTagByTagId(125);
            ExifTag exifTag2Read = exifTagsRead.findExifTagByTagId(521);

            assertExifTagsEquals(exifTag1, exifTag1Read);
            assertExifTagsEquals(exifTag2, exifTag2Read);
        } finally {
            file.delete();
        }
    }

    private void assertExifTagsEquals(ExifTag exifTag1, ExifTag exifTag2) {
        assertEquals(exifTag1.convertByteOrderIdToByteOrder(), exifTag2.convertByteOrderIdToByteOrder());
        assertEquals(exifTag1.getByteOrderId(), exifTag2.getByteOrderId());
        assertEquals(exifTag1.convertDataTypeIdToExifDataType(), exifTag2.convertDataTypeIdToExifDataType());
        assertEquals(exifTag1.convertTagIdToEnumId(), exifTag2.convertTagIdToEnumId());
        assertEquals(exifTag1.getTagId(), exifTag2.getTagId());
        assertEquals(exifTag1.getIfdType(), exifTag2.getIfdType());
        assertEquals(exifTag1.getName(), exifTag2.getName());
        assertArrayEquals(exifTag1.getRawValue(), exifTag2.getRawValue());
        assertEquals(exifTag1.getStringValue(), exifTag2.getStringValue());
        assertEquals(exifTag1.getValueCount(), exifTag2.getValueCount());
        assertEquals(exifTag1.getValueOffset(), exifTag2.getValueOffset());
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