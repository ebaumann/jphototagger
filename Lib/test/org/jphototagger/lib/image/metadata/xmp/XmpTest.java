package org.jphototagger.lib.image.metadata.xmp;

import org.jphototagger.lib.image.metadata.xmp.Xmp;
import com.adobe.xmp.properties.XMPPropertyInfo;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.File;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Elmar Baumann
 */
public class XmpTest {
    public XmpTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception {}

    /**
     * Test of getPropertyValueFrom method, of class Xmp.
     */
    @Test
    public void testGetPropertyValueFrom() {
        System.out.println("getPropertyValueFrom");

        File xmpFile =
            new File(XmpTest.class.getProtectionDomain().getCodeSource()
                .getLocation().getPath() + File.separator + "org"
                                         + File.separator + "jphototagger"
                                         + File.separator + "lib"
                                         + File.separator + "image"
                                         + File.separator + "metadata"
                                         + File.separator + "xmp"
                                         + File.separator + "test.xmp");
        List<XMPPropertyInfo> xmpPropertyInfos =
            Xmp.getPropertyInfosOfSidecarFile(xmpFile);

        assertEquals(Arrays.asList("dc:creator"),
                     Xmp.getPropertyValuesFrom(xmpPropertyInfos,
                         Xmp.PropertyValue.DC_CREATOR));
        assertEquals(Arrays
            .asList("lr:hierarchicalSubject 1", "lr:hierarchicalSubject 2",
                    "lr:hierarchicalSubject 3"), Xmp
                        .getPropertyValuesFrom(xmpPropertyInfos,
                            Xmp.PropertyValue.LR_HIERARCHICAL_SUBJECTS));
        assertEquals(
            Arrays.asList("dc:subject 1", "dc:subject 2", "dc:subject 3"),
            Xmp.getPropertyValuesFrom(
                xmpPropertyInfos, Xmp.PropertyValue.DC_SUBJECT));
        assertEquals(Arrays.asList("dc:title"),
                     Xmp.getPropertyValuesFrom(xmpPropertyInfos,
                         Xmp.PropertyValue.DC_TITLE));
        assertEquals(Arrays.asList("dc:description"),
                     Xmp.getPropertyValuesFrom(xmpPropertyInfos,
                         Xmp.PropertyValue.DC_DESCRIPTION));
        assertEquals(Arrays.asList("dc:rights"),
                     Xmp.getPropertyValuesFrom(xmpPropertyInfos,
                         Xmp.PropertyValue.DC_RIGHTS));
        assertEquals(Arrays.asList("photoshop:Headline"),
                     Xmp.getPropertyValuesFrom(xmpPropertyInfos,
                         Xmp.PropertyValue.PHOTOSHOP_HEADLINE));
        assertEquals(Arrays.asList("photoshop:AuthorsPosition"),
                     Xmp.getPropertyValuesFrom(xmpPropertyInfos,
                         Xmp.PropertyValue.PHOTOSHOP_AUTHORS_POSITION));
        assertEquals(Arrays.asList("photoshop:City"),
                     Xmp.getPropertyValuesFrom(xmpPropertyInfos,
                         Xmp.PropertyValue.PHOTOSHOP_CITY));
        assertEquals(Arrays.asList("photoshop:State"),
                     Xmp.getPropertyValuesFrom(xmpPropertyInfos,
                         Xmp.PropertyValue.PHOTOSHOP_STATE));
        assertEquals(Arrays.asList("photoshop:Country"),
                     Xmp.getPropertyValuesFrom(xmpPropertyInfos,
                         Xmp.PropertyValue.PHOTOSHOP_COUNTRY));
        assertEquals(Arrays.asList("photoshop:Credit"),
                     Xmp.getPropertyValuesFrom(xmpPropertyInfos,
                         Xmp.PropertyValue.PHOTOSHOP_CREDIT));
        assertEquals(Arrays.asList("photoshop:Source"),
                     Xmp.getPropertyValuesFrom(xmpPropertyInfos,
                         Xmp.PropertyValue.PHOTOSHOP_SOURCE));
        assertEquals(Arrays.asList("photoshop:TransmissionReference"),
                     Xmp.getPropertyValuesFrom(xmpPropertyInfos,
                         Xmp.PropertyValue.PHOTOSHOP_TRANSMISSION_REFERENCE));
        assertEquals(Arrays.asList("photoshop:Instructions"),
                     Xmp.getPropertyValuesFrom(xmpPropertyInfos,
                         Xmp.PropertyValue.PHOTOSHOP_INSTRUCTIONS));
        assertEquals(Arrays.asList("photoshop:CaptionWriter"),
                     Xmp.getPropertyValuesFrom(xmpPropertyInfos,
                         Xmp.PropertyValue.PHOTOSHOP_CAPTION_WRITER));
        assertEquals(Arrays.asList("Iptc4xmpCore:Location"),
                     Xmp.getPropertyValuesFrom(xmpPropertyInfos,
                         Xmp.PropertyValue.IPTC4_XMP_CORE_LOCATION));
        assertEquals(Arrays.asList("2010-02-15"),
                     Xmp.getPropertyValuesFrom(xmpPropertyInfos,
                         Xmp.PropertyValue.IPTC4_XMP_CORE_DATE_CREATED));
        assertEquals("photoshop:CaptionWriter",
                     Xmp.getPropertyValueFrom(xmpPropertyInfos,
                         Xmp.PropertyValue.PHOTOSHOP_CAPTION_WRITER));
    }
}
