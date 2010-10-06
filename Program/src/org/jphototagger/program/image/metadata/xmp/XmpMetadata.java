/*
 * @(#)XmpMetadata.java    Created on 2008-10-05
 *
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

package org.jphototagger.program.image.metadata.xmp;

import com.adobe.xmp.options.IteratorOptions;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.options.SerializeOptions;
import com.adobe.xmp.properties.XMPPropertyInfo;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPIterator;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;

import com.imagero.reader.iptc.IPTCEntryMeta;

import org.jphototagger.lib.generics.Pair;
import org.jphototagger.lib.image.metadata.xmp.XmpFileReader;
import org.jphototagger.lib.io.FileLock;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.mapping
    .IptcEntryXmpPathStartMapping;
import org.jphototagger.program.database.metadata.mapping
    .XmpColumnNamespaceUriMapping;
import org.jphototagger.program.database.metadata.mapping
    .XmpColumnXmpArrayNameMapping;
import org.jphototagger.program.database.metadata.mapping
    .XmpColumnXmpDataTypeMapping;
import org.jphototagger.program.database.metadata.mapping
    .XmpColumnXmpDataTypeMapping.XmpValueType;
import org.jphototagger.program.database.metadata.selections.EditColumns;
import org.jphototagger.program.database.metadata.selections.XmpInDatabase;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpLastModified;
import org.jphototagger.program.io.RuntimeUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Gets and sets XMP metadata from image files and XMP sidecar files and to
 * XMP sidecar files.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class XmpMetadata {
    private static final List<String> KNOWN_NAMESPACES =
        new ArrayList<String>();

    static {
        KNOWN_NAMESPACES.add("Iptc4xmpCore");
        KNOWN_NAMESPACES.add("aux");
        KNOWN_NAMESPACES.add("crs");
        KNOWN_NAMESPACES.add("dc");
        KNOWN_NAMESPACES.add("exif");
        KNOWN_NAMESPACES.add("lr");
        KNOWN_NAMESPACES.add("photoshop");
        KNOWN_NAMESPACES.add("tiff");
        KNOWN_NAMESPACES.add("xap");
        KNOWN_NAMESPACES.add("xapRights");
    }

    /**
     * Location of the XMP metadata.
     */
    private enum XmpLocation {

        /** The XMP metadta is embedded into the data file */
        EMBEDDED,

        /** The XMP metadta exists in a sidecar file */
        SIDECAR_FILE
    }

    private XmpMetadata() {}

    /**
     * Returns all {@link XMPPropertyInfo}s of a specific namespace.
     *
     * @param propertyInfos XMP property infos of arbitrary namespaces
     * @param namespace     namespace
     * @return              property infos of <code>propertyInfos</code>
     *                      matching that namespace
     */
    public static List<XMPPropertyInfo> filterPropertyInfosOfNamespace(
            List<XMPPropertyInfo> propertyInfos, String namespace) {
        if (propertyInfos == null) {
            throw new NullPointerException("propertyInfos == null");
        }

        if (namespace == null) {
            throw new NullPointerException("namespace == null");
        }

        List<XMPPropertyInfo> propertyInfosNs =
            new ArrayList<XMPPropertyInfo>();

        for (XMPPropertyInfo propertyInfo : propertyInfos) {
            if (propertyInfo.getNamespace().equals(namespace)) {
                propertyInfosNs.add(propertyInfo);
            }
        }

        return propertyInfosNs;
    }

    /**
     * Returns XMP metadata embedded in an image file.
     *
     * @param imageFile image file or null
     * @return          Metadata or null if the image file has no XMP metadata
     *                  or on errors while reading
     */
    public static List<XMPPropertyInfo> getEmbeddedPropertyInfos(
            File imageFile) {
        if ((imageFile == null) ||!imageFile.exists()) {
            return null;
        }

        return getPropertyInfosOfXmpString(getEmbeddedXmpAsString(imageFile));
    }

    /**
     * Returns XMP metadata of a sidecar file.
     *
     * @param sidecarFile sidecar file. Can be null.
     * @return            Metadata or null if the sidecar file is null or on
     *                    errors while reading
     * @throws IOException
     */
    public static List<XMPPropertyInfo> getPropertyInfosOfSidecarFile(
            File sidecarFile)
            throws IOException {
        if ((sidecarFile == null) ||!sidecarFile.exists()) {
            return null;
        }

        return getPropertyInfosOfXmpString(
            getXmpAsStringOfSidecarFile(sidecarFile));
    }

    private static List<XMPPropertyInfo> getPropertyInfosOfXmpString(
            String xmp) {
        List<XMPPropertyInfo> propertyInfos = new ArrayList<XMPPropertyInfo>();

        try {
            if ((xmp != null) && (xmp.length() > 0)) {
                XMPMeta xmpMeta = XMPMetaFactory.parseFromString(xmp);

                if (xmpMeta != null) {
                    addXmpPropertyInfosTo(xmpMeta, propertyInfos);
                }
            }
        } catch (Exception ex) {
            propertyInfos = null;
            AppLogger.logSevere(XmpMetadata.class, ex);
        }

        return propertyInfos;
    }

    /**
     * Adds XMP property infos of XMP metadata to a list of property infos.
     *
     * @param toXmpMeta          XMP metadata
     * @param toXmpPropertyInfos list of property infos to retrieve the
     *                           properties from XMP metadata
     */
    private static void addXmpPropertyInfosTo(XMPMeta fromXmpMeta,
            List<XMPPropertyInfo> toXmpPropertyInfos) {
        try {
            for (XMPIterator it = fromXmpMeta.iterator(); it.hasNext(); ) {
                XMPPropertyInfo xmpPropertyInfo = (XMPPropertyInfo) it.next();

                if (hasContent(xmpPropertyInfo)) {
                    toXmpPropertyInfos.add(xmpPropertyInfo);
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(XmpMetadata.class, ex);
        }
    }

    private static boolean hasContent(XMPPropertyInfo xmpPropertyInfo) {
        return !xmpPropertyInfo.getOptions().isQualifier()
               && (xmpPropertyInfo.getPath() != null)
               && (xmpPropertyInfo.getValue() != null)
               && (xmpPropertyInfo.getValue().toString().length() > 0);
    }

    /**
     * Suggests a sidecar file for an image.
     * <p>
     * If the image already has a sidecar file it will be returned. If the image
     * doesn't have a sidecar file, the a sidecar file will be created with the
     * same name as the image file with exception of the suffix: it's
     * <code>xmp</code>. If the image file has the name <code>dog.jpg</code>,
     * the sidecar file name is <code>dog.xmp</code>.
     *
     * @param  imageFile image file or null
     * @return           sidecar file
     */
    public static File suggestSidecarFile(File imageFile) {
        File existingFile = getSidecarFile(imageFile);

        if (existingFile != null) {
            return existingFile;
        }

        String absolutePath   = imageFile.getAbsolutePath();
        int    indexExtension = absolutePath.lastIndexOf('.');

        if (indexExtension > 0) {
            return new File(absolutePath.substring(0, indexExtension + 1)
                            + "xmp");
        } else {
            return new File(absolutePath + ".xmp");
        }
    }

    /**
     * Returns an image's sidecar file (only) if the image has a sidecar file.
     *
     * @param imageFile image file or null
     * @return          sidecar file or null, if the image doesn't have a
     *                  sidecar file
     */
    public static File getSidecarFile(File imageFile) {
        if (imageFile == null) {
            return null;
        }

        String absolutePath   = imageFile.getAbsolutePath();
        int    indexExtension = absolutePath.lastIndexOf('.');

        // > 0: ".suffix" is not a file of type "suffix"
        if (indexExtension > 0) {
            File sidecarFile = new File(absolutePath.substring(0,
                                   indexExtension + 1) + "xmp");

            if (sidecarFile.exists() && sidecarFile.isFile()) {
                return sidecarFile;
            }
        }

        return null;
    }

    private static String getEmbeddedXmpAsString(File imageFile) {
        if ((imageFile == null) ||!imageFile.exists()) {
            return null;
        }

        AppLogger.logInfo(XmpMetadata.class,
                          "XmpMetadata.Info.ReadEmbeddedXmp", imageFile);

        return XmpFileReader.readFile(imageFile);
    }

    private static String getXmpAsStringOfSidecarFile(File sidecarFile)
            throws IOException {
        if ((sidecarFile == null) ||!sidecarFile.exists()) {
            return null;
        }

        AppLogger.logInfo(XmpMetadata.class,
                          "XmpMetadata.Info.ReadSidecarFile", sidecarFile);

        return FileUtil.getContentAsString(sidecarFile, "UTF-8");
    }

    /**
     * Returns wheter a string is a known XMP namespace.
     *
     * @param string string
     * @return       true if that string is a known namespace
     */
    public static boolean isKnownNamespace(String string) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }

        return KNOWN_NAMESPACES.contains(string);
    }

    /**
     * Returns all {@link XMPPropertyInfo}s matching a {@link IPTCEntryMeta}.
     *
     * @param  matchingIptcEntryMeta IPTC entry metadata
     * @param  propertyInfos      arbitrary property infos
     * @return                    property infos of <code>propertyInfos</code>
     *                            matching that metadata
     */
    public static List<XMPPropertyInfo> filterPropertyInfosOfIptcEntryMeta(
            List<XMPPropertyInfo> propertyInfos,
            IPTCEntryMeta matchingIptcEntryMeta) {
        if (propertyInfos == null) {
            throw new NullPointerException("propertyInfos == null");
        }

        if (matchingIptcEntryMeta == null) {
            throw new NullPointerException("matchingIptcEntryMeta == null");
        }

        List<XMPPropertyInfo> filteredPropertyInfos =
            new ArrayList<XMPPropertyInfo>();
        String startsWith =
            IptcEntryXmpPathStartMapping.getXmpPathStartOfIptcEntryMeta(
                matchingIptcEntryMeta);

        for (XMPPropertyInfo propertyInfo : propertyInfos) {
            if (propertyInfo.getPath().startsWith(startsWith)) {
                filteredPropertyInfos.add(propertyInfo);
            }
        }

        return filteredPropertyInfos;
    }

    /**
     * Returns whether an image has a sidecar file and the sidecar file exists
     * in the same directory as the image file.
     *
     * @param  imageFile image file or null
     * @return           true if the image has a sidecar file
     */
    public static boolean hasImageASidecarFile(File imageFile) {
        return getSidecarFile(imageFile) != null;
    }

    /**
     * Returns whether a sidecar file can be written to the file system.
     * <p>
     * This is true if the sidecar file exists and is writable or if it does not
     * exist and the directory is writable.
     *
     * @param  imageFile image file or null
     * @return           true if possible
     */
    public static boolean canWriteSidecarFileForImageFile(File imageFile) {
        if (imageFile != null) {
            File directory   = imageFile.getParentFile();
            File sidecarFile = getSidecarFile(imageFile);

            if (sidecarFile != null) {
                return sidecarFile.canWrite();
            } else if (directory != null) {
                return directory.canWrite();
            }
        }

        return false;
    }

    /**
     * Writes the values of a {@link Xmp} instance as or into a XMP sidecar
     * file.
     *
     * @param  fromXmp       XMP metadata
     * @param  toSidecarFile sidecar file
     * @return               true if successfully written
     */
    public static boolean writeXmpToSidecarFile(Xmp fromXmp,
            File toSidecarFile) {
        if (fromXmp == null) {
            throw new NullPointerException("fromXmp == null");
        }

        if (toSidecarFile == null) {
            throw new NullPointerException("toSidecarFile == null");
        }

        try {
            XMPMeta toXmpMeta = getXmpMetaOfSidecarFile(toSidecarFile);

            deleteAllEditableMetadataFrom(toXmpMeta);
            setMetadata(fromXmp, toXmpMeta);

            return writeSidecarFile(toXmpMeta, toSidecarFile);
        } catch (Exception ex) {
            AppLogger.logSevere(XmpMetadata.class, ex);

            return false;
        }
    }

    private static void setMetadata(Xmp fromXmp, XMPMeta toXmpMeta)
            throws XMPException {
        for (Column column : EditColumns.get()) {
            String namespaceUri =
                XmpColumnNamespaceUriMapping.getNamespaceUriOfColumn(column);
            String arrayName =
                XmpColumnXmpArrayNameMapping.getXmpArrayNameOfColumn(column);

            copyMetadata(fromXmp, toXmpMeta, column, namespaceUri, arrayName);
        }
    }

    private static XMPMeta getXmpMetaOfSidecarFile(File sidecarFile)
            throws XMPException, IOException {
        if (sidecarFile.exists()) {
            String xmp = FileUtil.getContentAsString(sidecarFile, "UTF-8");

            if ((xmp != null) &&!xmp.trim().isEmpty()) {
                return XMPMetaFactory.parseFromString(xmp);
            }
        }

        return XMPMetaFactory.create();
    }

    /**
     * Deletes from an <code>XMPMeta</code> instance all data an user can edit.
     *
     * @param toXmpMeta XMP metadata
     */
    private static void deleteAllEditableMetadataFrom(XMPMeta xmpMeta) {
        List<Column> editableXmpColumns = EditColumns.get();

        for (Column editableColumn : editableXmpColumns) {
            String namespaceUri =
                XmpColumnNamespaceUriMapping.getNamespaceUriOfColumn(
                    editableColumn);
            String propertyName =
                XmpColumnXmpArrayNameMapping.getXmpArrayNameOfColumn(
                    editableColumn);

            xmpMeta.deleteProperty(namespaceUri, propertyName);
        }
    }

    /**
     * Sets metadata of a {@link Xmp} instance to a {@link XMPMeta} instance.
     *
     * @param column        part of data to set
     * @param fromXmp       <code>Xmp</code> metadata to set from
     * @param toXmpMeta     <code>XMPMeta</code> metadata to set to
     * @param namespaceUri  URI of namespase in <code>XMPMeta</code> to set
     * @param propertyName     array name to set within the URI
     * @throws XMPException if the namespace or uri or data is invalid
     */
    private static void copyMetadata(Xmp fromXmp, XMPMeta toXmpMeta,
                                     Column column, String namespaceUri,
                                     String arrayName)
            throws XMPException {
        Object xmpValue = fromXmp.getValue(column);

        if (xmpValue != null) {
            if (xmpValue instanceof String) {
                String value = (String) xmpValue;

                // 2009-08-02: No side effects if value is clear
                // ("orphaned data"), because previous metadata was deleted
                if (XmpColumnXmpDataTypeMapping.isText(column)
                        &&!value.trim().isEmpty()) {
                    toXmpMeta.setProperty(namespaceUri, arrayName, value);
                } else if (XmpColumnXmpDataTypeMapping.isLanguageAlternative(
                        column)) {
                    toXmpMeta.setLocalizedText(namespaceUri, arrayName, "",
                                               "x-default", value);
                }
            } else if (xmpValue instanceof List<?>) {
                @SuppressWarnings("unchecked") List<String> values =
                    (List<String>) xmpValue;

                Collections.sort(values);

                for (String value : values) {
                    String trimmedValue = value.trim();

                    if (!doesArrayItemExist(toXmpMeta, namespaceUri, arrayName,
                                            trimmedValue)) {
                        toXmpMeta.appendArrayItem(
                            namespaceUri, arrayName,
                            getArrayPropertyOptionsOf(column), trimmedValue,
                            null);
                    }
                }
            } else if (xmpValue instanceof Long) {
                Long value = (Long) xmpValue;

                toXmpMeta.setProperty(namespaceUri, arrayName,
                                      Long.toString(value));
            } else {
                AppLogger.logWarning(XmpMetadata.class,
                                     "XmpMetadata.Error.WriteSetMetadata",
                                     xmpValue.getClass());
            }
        }
    }

    private static boolean doesArrayItemExist(XMPMeta xmpMeta,
            String namespaceUri, String propertyName, String item)
            throws XMPException {
        if (xmpMeta.doesPropertyExist(namespaceUri, propertyName)) {
            for (XMPIterator it = xmpMeta.iterator(namespaceUri, propertyName,
                    new IteratorOptions());
                    it.hasNext(); ) {
                XMPPropertyInfo xmpPropertyInfo = (XMPPropertyInfo) it.next();

                if (xmpPropertyInfo != null) {
                    Object value = xmpPropertyInfo.getValue();

                    if (value != null) {
                        String itemValue = value.toString().trim();

                        if (itemValue.equals(item)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private static PropertyOptions getArrayPropertyOptionsOf(Column xmpColumn) {
        XmpValueType valueType =
            XmpColumnXmpDataTypeMapping.getXmpValueTypeOfColumn(xmpColumn);

        if (valueType.equals(XmpValueType.BAG_TEXT)) {
            return new PropertyOptions().setArray(true);
        } else if (valueType.equals(XmpValueType.SEQ_PROPER_NAME)) {
            return new PropertyOptions().setArrayOrdered(true);
        } else if (valueType.equals(XmpValueType.LANG_ALT)) {
            return new PropertyOptions().setArrayAlternate(true);
        } else {
            assert false : valueType;

            return null;
        }
    }

    private static boolean writeSidecarFile(XMPMeta fromXmpMeta,
            File toSidecarFile) {
        FileOutputStream out = null;

        if (!RuntimeUtil.lockLogWarning(toSidecarFile, XmpMetadata.class)) {
            return false;
        }

        try {
            out = new FileOutputStream(toSidecarFile);
            out.getChannel().lock();
            XMPMetaFactory.serialize(
                fromXmpMeta, out,
                new SerializeOptions().setPadding(10).setOmitPacketWrapper(
                    true));

            return true;
        } catch (Exception ex) {
            AppLogger.logSevere(XmpMetadata.class, ex);

            return false;
        } finally {
            FileLock.INSTANCE.unlock(toSidecarFile, XmpMetadata.class);

            if (out != null) {
                try {
                    out.close();
                } catch (Exception ex) {
                    AppLogger.logSevere(XmpMetadata.class, ex);
                }
            }
        }
    }

    /**
     * Returns XMP metadata of a image file.
     * <p>
     * If the image has a sidecar file, it's metadata will be read. If the image
     * hasn't a sidecar file but embedded XMP metadata, the embedded XMP
     * metadata will be read.
     *
     * @param  imageFile image file or null
     * @return           XMP metadata of the image file or null
     * @throws           IOException
     */
    public static Xmp getXmpFromSidecarFileOf(File imageFile)
            throws IOException {
        if ((imageFile == null) ||!hasImageASidecarFile(imageFile)) {
            return null;
        }

        return getXmp(getPropertyInfosOfSidecarFile(getSidecarFile(imageFile)),
                      imageFile, XmpLocation.SIDECAR_FILE);
    }

    /**
     * Returns XMP embedded in the image file.
     *
     * @param   imageFile image file
     * @return            embedded XMP metadata or null if no XMP is embedded or
     *                    when errors occur while reading the file
     */
    public static Xmp getEmbeddedXmp(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        String xmpString = getEmbeddedXmpAsString(imageFile);

        return (xmpString == null)
               ? null
               : getXmp(getPropertyInfosOfXmpString(xmpString), imageFile,
                        XmpLocation.EMBEDDED);
    }

    /**
     * Puts into a map property infos where the map key is a string of
     * {@link XmpInDatabase#getPathPrefixes()}. The values are
     * {@link XMPPropertyInfo} instances with path prefixes matching the key.
     *
     * @param  xmpPropertyInfos unordered property infos
     * @return                   ordered property infos
     */
    public static Map<String,
            List<XMPPropertyInfo>> getOrderedPropertyInfosForDatabaseOf(
                List<XMPPropertyInfo> xmpPropertyInfos) {
        if (xmpPropertyInfos == null) {
            throw new NullPointerException("xmpPropertyInfos == null");
        }

        Map<String, List<XMPPropertyInfo>> propertyInfoWithPathStart =
            new HashMap<String, List<XMPPropertyInfo>>();
        Set<String> pathPrefixes = XmpInDatabase.getPathPrefixes();

        for (String pathPrefix : pathPrefixes) {
            for (XMPPropertyInfo propertyInfo : xmpPropertyInfos) {
                if (propertyInfo.getPath().startsWith(pathPrefix)) {
                    List<XMPPropertyInfo> infos =
                        propertyInfoWithPathStart.get(pathPrefix);

                    if (infos == null) {
                        infos = new ArrayList<XMPPropertyInfo>();
                        infos.add(propertyInfo);
                        propertyInfoWithPathStart.put(pathPrefix, infos);
                    } else {
                        infos.add(propertyInfo);
                    }
                }
            }
        }

        return propertyInfoWithPathStart;
    }

    private static Xmp getXmp(List<XMPPropertyInfo> xmpPropertyInfos,
                              File areFromXmpImageFile,
                              XmpLocation xmpLocation) {
        Xmp xmp = null;

        if (xmpPropertyInfos != null) {
            xmp = new Xmp();

            for (XMPPropertyInfo xmpPropertyInfo : xmpPropertyInfos) {
                String path   = xmpPropertyInfo.getPath();
                Object value  = xmpPropertyInfo.getValue();
                Column column = XmpColumnXmpArrayNameMapping.findColumn(path);

                if ((value != null) && (column != null)
                        && (column.getDataType() != null)) {
                    try {
                        xmp.setValue(
                            column,
                            column.getDataType().parseString(value.toString()));
                    } catch (Exception ex) {
                        AppLogger.logSevere(XmpMetadata.class, ex);
                    }
                }
            }

            setLastModified(xmpLocation, xmp, areFromXmpImageFile);
        }

        return xmp;
    }

    private static void setLastModified(XmpLocation xmpType, Xmp xmp,
            File imageFile) {
        File sidecarFile = getSidecarFile(imageFile);

        if (xmpType.equals(XmpLocation.SIDECAR_FILE) && (sidecarFile != null)) {
            xmp.setValue(ColumnXmpLastModified.INSTANCE,
                         sidecarFile.lastModified());
        } else if (xmpType.equals(XmpLocation.EMBEDDED) && imageFile.exists()) {
            xmp.setValue(ColumnXmpLastModified.INSTANCE,
                         imageFile.lastModified());
        }
    }

    /**
     * Returns pairs of image files and their sidecar files.
     *
     * @param  imageFiles image files
     * @return            pairs of image files with their corresponding sidecar
     *                    files.
     *                    {@link org.jphototagger.lib.generics.Pair#getFirst()}
     *                    returns a reference to an image file object in the
     *                    <code>imageFiles</code> list.
     *                    {@link org.jphototagger.lib.generics.Pair#getSecond()}
     *                    returns the sidecar file of the referenced image file
     *                    or null if the image file has no sidecar file.
     */
    public static List<Pair<File, File>> getImageFilesWithSidecarFiles(
            List<File> imageFiles) {
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        List<Pair<File, File>> filePairs = new ArrayList<Pair<File, File>>();

        for (File imageFile : imageFiles) {
            File sidecarFile = getSidecarFile(imageFile);

            filePairs.add(new Pair<File, File>(imageFile, (sidecarFile == null)
                    ? null
                    : sidecarFile));
        }

        return filePairs;
    }
}
