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
package de.elmar_baumann.lib.image.metadata.xmp;

import com.adobe.xmp.properties.XMPPropertyInfo;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elmar Baumann
 */
public class XmpTest {

    public XmpTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of getPropertyValueFrom method, of class Xmp.
     */
    @Test
    public void testGetPropertyValueFrom() {
        System.out.println("getPropertyValueFrom");

        File xmpFile = new File(XmpTest.class.getProtectionDomain().getCodeSource().getLocation().getPath() +
                File.separator +  "de" + File.separator +  "elmar_baumann" +
                File.separator +  "lib" + File.separator +  "image" + File.separator + "metadata" +
                File.separator + "xmp" + File.separator + "test.xmp");
        List<XMPPropertyInfo> xmpPropertyInfos = Xmp.getPropertyInfosOfSidecarFile(xmpFile);

        assertEquals(Arrays.asList("dc:creator"), Xmp.getPropertyValuesFrom(xmpPropertyInfos, Xmp.PropertyValue.DC_CREATOR));
        assertEquals(Arrays.asList("lr:hierarchicalSubject 1", "lr:hierarchicalSubject 2", "lr:hierarchicalSubject 3"), Xmp.getPropertyValuesFrom(xmpPropertyInfos, Xmp.PropertyValue.LR_HIERARCHICAL_SUBJECTS));
        assertEquals(Arrays.asList("dc:subject 1", "dc:subject 2", "dc:subject 3"), Xmp.getPropertyValuesFrom(xmpPropertyInfos, Xmp.PropertyValue.DC_SUBJECT));
        assertEquals(Arrays.asList("dc:title"), Xmp.getPropertyValuesFrom(xmpPropertyInfos, Xmp.PropertyValue.DC_TITLE));
        assertEquals(Arrays.asList("dc:description"), Xmp.getPropertyValuesFrom(xmpPropertyInfos, Xmp.PropertyValue.DC_DESCRIPTION));
        assertEquals(Arrays.asList("dc:rights"), Xmp.getPropertyValuesFrom(xmpPropertyInfos, Xmp.PropertyValue.DC_RIGHTS));
        assertEquals(Arrays.asList("photoshop:Headline"), Xmp.getPropertyValuesFrom(xmpPropertyInfos, Xmp.PropertyValue.PHOTOSHOP_HEADLINE));
        assertEquals(Arrays.asList("photoshop:AuthorsPosition"), Xmp.getPropertyValuesFrom(xmpPropertyInfos, Xmp.PropertyValue.PHOTOSHOP_AUTHORS_POSITION));
        assertEquals(Arrays.asList("photoshop:City"), Xmp.getPropertyValuesFrom(xmpPropertyInfos, Xmp.PropertyValue.PHOTOSHOP_CITY));
        assertEquals(Arrays.asList("photoshop:State"), Xmp.getPropertyValuesFrom(xmpPropertyInfos, Xmp.PropertyValue.PHOTOSHOP_STATE));
        assertEquals(Arrays.asList("photoshop:Country"), Xmp.getPropertyValuesFrom(xmpPropertyInfos, Xmp.PropertyValue.PHOTOSHOP_COUNTRY));
        assertEquals(Arrays.asList("photoshop:Credit"), Xmp.getPropertyValuesFrom(xmpPropertyInfos, Xmp.PropertyValue.PHOTOSHOP_CREDIT));
        assertEquals(Arrays.asList("photoshop:Source"), Xmp.getPropertyValuesFrom(xmpPropertyInfos, Xmp.PropertyValue.PHOTOSHOP_SOURCE));
        assertEquals(Arrays.asList("photoshop:TransmissionReference"), Xmp.getPropertyValuesFrom(xmpPropertyInfos, Xmp.PropertyValue.PHOTOSHOP_TRANSMISSION_REFERENCE));
        assertEquals(Arrays.asList("photoshop:Instructions"), Xmp.getPropertyValuesFrom(xmpPropertyInfos, Xmp.PropertyValue.PHOTOSHOP_INSTRUCTIONS));
        assertEquals(Arrays.asList("photoshop:CaptionWriter"), Xmp.getPropertyValuesFrom(xmpPropertyInfos, Xmp.PropertyValue.PHOTOSHOP_CAPTION_WRITER));
        assertEquals(Arrays.asList("Iptc4xmpCore:Location"), Xmp.getPropertyValuesFrom(xmpPropertyInfos, Xmp.PropertyValue.IPTC4_XMP_CORE_LOCATION));
        assertEquals(Arrays.asList("2010-02-15"), Xmp.getPropertyValuesFrom(xmpPropertyInfos, Xmp.PropertyValue.IPTC4_XMP_CORE_DATE_CREATED));

        assertEquals("photoshop:CaptionWriter", Xmp.getPropertyValueFrom(xmpPropertyInfos, Xmp.PropertyValue.PHOTOSHOP_CAPTION_WRITER));
    }
}