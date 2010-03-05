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

import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.properties.XMPPropertyInfo;
import com.adobe.xmp.XMPConst;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPIterator;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Methods for accessing XMP (Extensible Metadata Platform).
 * <p>
 * A project using this class has to include <code>XMPCore.jar</code> in the
 * <code>lib</code> directory of this project.
 *
 * @author  Elmar Baumann
 * @version 2010-02-15
 */
public final class Xmp {
    public enum Namespace {
        CAMERA_RAW(XMPConst.NS_CAMERARAW, "crs"),
        DUBLIN_CORE(XMPConst.NS_DC, "dc"), EXIF(XMPConst.NS_EXIF, "exif"),
        IPTC_CORE(XMPConst.NS_IPTCCORE, "Iptc4xmpCore"),
        LIGHTROOM("http://ns.adobe.com/lightroom/1.0/", "lr"),
        PHOTOSHOP(XMPConst.NS_PHOTOSHOP, "photoshop"),
        TIFF(XMPConst.NS_TIFF, "tiff"), XMP_BASIC(XMPConst.NS_XMP, "xap"),
        ;

        private final String uri;
        private final String prefix;

        private Namespace(String uri, String prefix) {
            this.uri    = uri;
            this.prefix = prefix;
        }

        public String getUri() {
            return uri;
        }

        public String getPrefix() {
            return prefix;
        }
    }

    public enum PropertyValueType {
        BAG_TEXT, BOOLEAN, CLOSED_CHOICE, COLORANT, DATE, DIMENSIONS, FONT,
        INTEGER, LANG_ALT, LOCALE, MIME_TYPE, PROPER_NAME, REAL,
        SEQ_PROPER_NAME,
        TEXT, THUMBNAIL, URI, URL, XPATH,
        ;

        public boolean isBagText() {
            return this.equals(BAG_TEXT);
        }

        public boolean isLangAlt() {
            return this.equals(LANG_ALT);
        }

        public boolean isProperName() {
            return this.equals(PROPER_NAME);
        }

        public boolean isSeqProperName() {
            return this.equals(SEQ_PROPER_NAME);
        }

        public boolean isText() {
            return this.equals(TEXT);
        }

        public boolean isArray() {
            return this.equals(BAG_TEXT) || this.equals(SEQ_PROPER_NAME)
                   || this.equals(LANG_ALT);
        }

        public PropertyOptions getArrayPropertyOptions() {
            switch (this) {
            case BAG_TEXT :
                return new PropertyOptions().setArray(true);

            case SEQ_PROPER_NAME :
                return new PropertyOptions().setArrayOrdered(true);

            case LANG_ALT :
                return new PropertyOptions().setArrayAlternate(true);

            default :
                return null;
            }
        }
    }

    public enum PropertyValue {
        DC_CREATOR(Namespace.DUBLIN_CORE, "dc:creator",
                   PropertyValueType.SEQ_PROPER_NAME),
        DC_DESCRIPTION(Namespace.DUBLIN_CORE, "dc:description",
                       PropertyValueType.LANG_ALT),
        DC_RIGHTS(Namespace.DUBLIN_CORE, "dc:rights",
                  PropertyValueType.LANG_ALT),
        DC_SUBJECT(Namespace.DUBLIN_CORE, "dc:subject",
                   PropertyValueType.BAG_TEXT),
        DC_TITLE(Namespace.DUBLIN_CORE, "dc:title", PropertyValueType.LANG_ALT),
        IPTC4_XMP_CORE_COUNTRY_CODE(Namespace.IPTC_CORE,
                                    "Iptc4xmpCore:CountryCode",
                                    PropertyValueType.CLOSED_CHOICE),
        IPTC4_XMP_CORE_DATE_CREATED(Namespace.IPTC_CORE,
                                    "Iptc4xmpCore:DateCreated",
                                    PropertyValueType.DATE),
        IPTC4_XMP_CORE_LOCATION(Namespace.IPTC_CORE, "Iptc4xmpCore:Location",
                                PropertyValueType.TEXT),
        LR_HIERARCHICAL_SUBJECTS(Namespace.LIGHTROOM, "lr:hierarchicalSubject",
                                 PropertyValueType.BAG_TEXT),
        PHOTOSHOP_AUTHORS_POSITION(Namespace.PHOTOSHOP,
                                   "photoshop:AuthorsPosition",
                                   PropertyValueType.TEXT),
        PHOTOSHOP_CAPTION_WRITER(Namespace.PHOTOSHOP,
                                 "photoshop:CaptionWriter",
                                 PropertyValueType.PROPER_NAME),
        PHOTOSHOP_CITY(Namespace.PHOTOSHOP, "photoshop:City",
                       PropertyValueType.TEXT),
        PHOTOSHOP_COUNTRY(Namespace.PHOTOSHOP, "photoshop:Country",
                          PropertyValueType.TEXT),
        PHOTOSHOP_CREDIT(Namespace.PHOTOSHOP, "photoshop:Credit",
                         PropertyValueType.TEXT),
        PHOTOSHOP_HEADLINE(Namespace.PHOTOSHOP, "photoshop:Headline",
                           PropertyValueType.TEXT),
        PHOTOSHOP_INSTRUCTIONS(Namespace.PHOTOSHOP, "photoshop:Instructions",
                               PropertyValueType.TEXT),
        PHOTOSHOP_SOURCE(Namespace.PHOTOSHOP, "photoshop:Source",
                         PropertyValueType.TEXT),
        PHOTOSHOP_STATE(Namespace.PHOTOSHOP, "photoshop:State",
                        PropertyValueType.TEXT),
        PHOTOSHOP_TRANSMISSION_REFERENCE(
            Namespace.PHOTOSHOP, "photoshop:TransmissionReference",
            PropertyValueType.TEXT), XAP_RATING(
                Namespace.XMP_BASIC, "xap:Rating",
                PropertyValueType.CLOSED_CHOICE)
        ;

        private final String            path;
        private final Namespace         namesapce;
        private final PropertyValueType propertyValueType;

        private PropertyValue(Namespace namesapce, String path,
                              PropertyValueType propertyValueType) {
            this.path              = path;
            this.namesapce         = namesapce;
            this.propertyValueType = propertyValueType;
        }

        public String getPath() {
            return path;
        }

        public Namespace getNamespace() {
            return namesapce;
        }

        public PropertyValueType getPropertyValueType() {
            return propertyValueType;
        }

        public PropertyOptions getArrayPropertyOptions() {
            if (propertyValueType.equals(PropertyValueType.BAG_TEXT)) {
                return new PropertyOptions().setArray(true);
            } else if (propertyValueType.equals(
                    PropertyValueType.SEQ_PROPER_NAME)) {
                return new PropertyOptions().setArrayOrdered(true);
            } else if (propertyValueType.equals(PropertyValueType.LANG_ALT)) {
                return new PropertyOptions().setArrayAlternate(true);
            } else {
                assert false : "Unknown value type: " + propertyValueType;

                return null;
            }
        }
    }

    static {
        try {
            XMPMetaFactory.getSchemaRegistry().registerNamespace(
                Namespace.LIGHTROOM.getUri(), Namespace.LIGHTROOM.getPrefix());
        } catch (Exception ex) {
            Logger.getLogger(Xmp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a sidecar file of an image file if it does exist.
     *
     * @param  imageFile image file
     * @return           sidecar file or null if that image file does not have
     *                   a sidecar file
     * @throws           NullPointerException if <code>imageFile</code> is null
     */
    public static File getSidecarfileOf(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        String imgFilename    = imageFile.getName();
        int    indexExtension = imgFilename.lastIndexOf(".");

        if (indexExtension > 0) {
            File sidecarFile = new File(imgFilename.substring(0,
                                   indexExtension + 1) + "xmp");

            if (sidecarFile.exists()) {
                return sidecarFile;
            }
        }

        return null;
    }

    /**
     * Returns the property infos of a XMP string.
     *
     * @param xmpString string with valid XMP content
     * @return          property infos or null on errors
     * @throws          NullPointerException if <code>xmpString</code> is null
     */
    public static List<XMPPropertyInfo> getPropertyInfosOfXmpString(
            String xmpString) {
        try {
            List<XMPPropertyInfo> propertyInfos =
                new ArrayList<XMPPropertyInfo>();
            XMPMeta xmpMeta = XMPMetaFactory.parseFromString(xmpString);

            if (xmpMeta == null) {
                return null;
            }

            for (XMPIterator it = xmpMeta.iterator(); it.hasNext(); ) {
                XMPPropertyInfo xmpPropertyInfo = (XMPPropertyInfo) it.next();

                propertyInfos.add(xmpPropertyInfo);
            }

            return propertyInfos;
        } catch (XMPException ex) {
            Logger.getLogger(Xmp.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Returns the property infos of a sidecar file.
     *
     * @param  sidecarFile sidcar file
     * @return             property infos or null if the image file does not
     *                     have a sidecar file or on errors
     * @throws             NullPointerException if <code>sidecarFile</code> is null
     */
    public static List<XMPPropertyInfo> getPropertyInfosOfSidecarFile(
            File sidecarFile) {
        if (sidecarFile == null) {
            throw new NullPointerException("sidecarFile == null");
        }

        if (!sidecarFile.exists()) {
            return null;
        }

        List<XMPPropertyInfo> propertyInfos = new ArrayList<XMPPropertyInfo>();
        FileInputStream       fis           = null;

        try {
            fis = new FileInputStream(sidecarFile);

            XMPMeta xmpMeta = XMPMetaFactory.parse(fis);

            if (xmpMeta == null) {
                return null;
            }

            for (XMPIterator it = xmpMeta.iterator(); it.hasNext(); ) {
                XMPPropertyInfo xmpPropertyInfo = (XMPPropertyInfo) it.next();

                propertyInfos.add(xmpPropertyInfo);
            }

            return propertyInfos;
        } catch (Exception ex) {
            Logger.getLogger(Xmp.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(Xmp.class.getName()).log(Level.SEVERE,
                                     null, ex);
                }
            }
        }

        return null;
    }

    /**
     * Returns specific property values from a collection of property infos.
     * <p>
     * Usage example for getting all Dublin Core subjects (keywords) from a XMP
     * sidecar file:
     * <pre>
     * List&lt;XMPPropertyInfo&gt; xmpPropertyInfos = Xmp.getPropertyInfosOfSidecarFile(xmpFile);
     * List&lt;String&gt; dcSubjects = Xmp.getPropertyValuesFrom(xmpPropertyInfos, Xmp.PropertyValue.DC_SUBJECT);
     * </pre>
     *
     * @param  xmpPropertyInfos property infos
     * @param  propertyValue    value to retrieve
     * @return                  one or more values (e.g. Dublin core subjects)
     *                          or an empty list if the property infos do not
     *                          contain that value
     */
    public static List<String> getPropertyValuesFrom(
            Collection<? extends XMPPropertyInfo> xmpPropertyInfos,
            PropertyValue propertyValue) {
        if (xmpPropertyInfos == null) {
            throw new NullPointerException("xmpPropertyInfos == null");
        }

        if (propertyValue == null) {
            throw new NullPointerException("propertyValue == null");
        }

        List<String> values = new ArrayList<String>();

        for (XMPPropertyInfo xmpPropertyInfo : xmpPropertyInfos) {
            Object value       = xmpPropertyInfo.getValue();
            String stringValue = (value == null)
                                 ? null
                                 : value.toString().trim();

            if ((xmpPropertyInfo.getNamespace() != null) && (xmpPropertyInfo
                    .getPath() != null) &&!xmpPropertyInfo.getOptions()
                    .isQualifier() && xmpPropertyInfo.getNamespace()
                        .equals(propertyValue.getNamespace()
                            .getUri()) && xmpPropertyInfo.getPath()
                                .startsWith(propertyValue
                                    .getPath()) && (stringValue != null) &&!stringValue
                                        .isEmpty()) {
                values.add(stringValue);
            }
        }

        return values;
    }

    /**
     * Calls {@link #getPropertyValuesFrom(java.util.Collection, de.elmar_baumann.lib.image.metadata.xmp.Xmp.PropertyValue)}
     * and returns the first value.
     * <p>
     * Usage for not repeatable values.
     * <p>
     * Usage example for the headline (title) from a XMP sidecar file:
     * <pre>
     * List&lt;XMPPropertyInfo&gt; xmpPropertyInfos = Xmp.getPropertyInfosOfSidecarFile(xmpFile);
     * String headline = Xmp.getPropertyValueFrom(xmpPropertyInfos, Xmp.PropertyValue.PHOTOSHOP_HEADLINE);
     * </pre>
     *
     * @param xmpPropertyInfos property infos
     * @param propertyValue    value to retrieve
     * @return                 first value or empty string
     */
    public static String getPropertyValueFrom(
            Collection<? extends XMPPropertyInfo> xmpPropertyInfos,
            PropertyValue propertyValue) {
        List<String> values = getPropertyValuesFrom(xmpPropertyInfos,
                                  propertyValue);

        return values.isEmpty()
               ? ""
               : values.get(0);
    }

    private Xmp() {}
}
