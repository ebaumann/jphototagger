package org.jphototagger.xmp;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.adobe.xmp.properties.XMPPropertyInfo;

import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Elmar Baumann
 */
public class XmpTest {
    public XmpTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception {}

    /**
     * Test of getPropertyValueFrom method, of class XmpProperties.
     */
    @Test
    public void testGetPropertyValueFrom() {
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
            XmpProperties.getPropertyInfosOfSidecarFile(xmpFile);

        assertEquals(Arrays.asList("dc:creator"),
                     XmpProperties.getPropertyValuesFrom(xmpPropertyInfos,
                         XmpProperties.PropertyValue.DC_CREATOR));
        assertEquals(Arrays
            .asList("lr:hierarchicalSubject 1", "lr:hierarchicalSubject 2",
                    "lr:hierarchicalSubject 3"), XmpProperties
                        .getPropertyValuesFrom(xmpPropertyInfos,
                            XmpProperties.PropertyValue.LR_HIERARCHICAL_SUBJECTS));
        assertEquals(
            Arrays.asList("dc:subject 1", "dc:subject 2", "dc:subject 3"),
            XmpProperties.getPropertyValuesFrom(
                xmpPropertyInfos, XmpProperties.PropertyValue.DC_SUBJECT));
        assertEquals(Arrays.asList("dc:title"),
                     XmpProperties.getPropertyValuesFrom(xmpPropertyInfos,
                         XmpProperties.PropertyValue.DC_TITLE));
        assertEquals(Arrays.asList("dc:description"),
                     XmpProperties.getPropertyValuesFrom(xmpPropertyInfos,
                         XmpProperties.PropertyValue.DC_DESCRIPTION));
        assertEquals(Arrays.asList("dc:rights"),
                     XmpProperties.getPropertyValuesFrom(xmpPropertyInfos,
                         XmpProperties.PropertyValue.DC_RIGHTS));
        assertEquals(Arrays.asList("photoshop:Headline"),
                     XmpProperties.getPropertyValuesFrom(xmpPropertyInfos,
                         XmpProperties.PropertyValue.PHOTOSHOP_HEADLINE));
        assertEquals(Arrays.asList("photoshop:AuthorsPosition"),
                     XmpProperties.getPropertyValuesFrom(xmpPropertyInfos,
                         XmpProperties.PropertyValue.PHOTOSHOP_AUTHORS_POSITION));
        assertEquals(Arrays.asList("photoshop:City"),
                     XmpProperties.getPropertyValuesFrom(xmpPropertyInfos,
                         XmpProperties.PropertyValue.PHOTOSHOP_CITY));
        assertEquals(Arrays.asList("photoshop:State"),
                     XmpProperties.getPropertyValuesFrom(xmpPropertyInfos,
                         XmpProperties.PropertyValue.PHOTOSHOP_STATE));
        assertEquals(Arrays.asList("photoshop:Country"),
                     XmpProperties.getPropertyValuesFrom(xmpPropertyInfos,
                         XmpProperties.PropertyValue.PHOTOSHOP_COUNTRY));
        assertEquals(Arrays.asList("photoshop:Credit"),
                     XmpProperties.getPropertyValuesFrom(xmpPropertyInfos,
                         XmpProperties.PropertyValue.PHOTOSHOP_CREDIT));
        assertEquals(Arrays.asList("photoshop:Source"),
                     XmpProperties.getPropertyValuesFrom(xmpPropertyInfos,
                         XmpProperties.PropertyValue.PHOTOSHOP_SOURCE));
        assertEquals(Arrays.asList("photoshop:TransmissionReference"),
                     XmpProperties.getPropertyValuesFrom(xmpPropertyInfos,
                         XmpProperties.PropertyValue.PHOTOSHOP_TRANSMISSION_REFERENCE));
        assertEquals(Arrays.asList("photoshop:Instructions"),
                     XmpProperties.getPropertyValuesFrom(xmpPropertyInfos,
                         XmpProperties.PropertyValue.PHOTOSHOP_INSTRUCTIONS));
        assertEquals(Arrays.asList("photoshop:CaptionWriter"),
                     XmpProperties.getPropertyValuesFrom(xmpPropertyInfos,
                         XmpProperties.PropertyValue.PHOTOSHOP_CAPTION_WRITER));
        assertEquals(Arrays.asList("Iptc4xmpCore:Location"),
                     XmpProperties.getPropertyValuesFrom(xmpPropertyInfos,
                         XmpProperties.PropertyValue.IPTC4_XMP_CORE_LOCATION));
        assertEquals(Arrays.asList("2010-02-15"),
                     XmpProperties.getPropertyValuesFrom(xmpPropertyInfos,
                         XmpProperties.PropertyValue.IPTC4_XMP_CORE_DATE_CREATED));
        assertEquals("photoshop:CaptionWriter",
                     XmpProperties.getPropertyValueFrom(xmpPropertyInfos,
                         XmpProperties.PropertyValue.PHOTOSHOP_CAPTION_WRITER));
    }
}
