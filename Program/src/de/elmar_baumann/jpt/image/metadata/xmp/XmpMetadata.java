/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.image.metadata.xmp;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPIterator;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.options.IteratorOptions;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.options.SerializeOptions;
import com.adobe.xmp.properties.XMPPropertyInfo;
import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.mapping.IptcEntryXmpPathStartMapping;
import de.elmar_baumann.jpt.database.metadata.mapping.XmpColumnNamespaceUriMapping;
import de.elmar_baumann.jpt.database.metadata.mapping.XmpColumnXmpDataTypeMapping;
import de.elmar_baumann.jpt.database.metadata.mapping.XmpColumnXmpDataTypeMapping.XmpValueType;
import de.elmar_baumann.jpt.database.metadata.mapping.XmpColumnXmpArrayNameMapping;
import de.elmar_baumann.jpt.database.metadata.selections.EditColumns;
import de.elmar_baumann.jpt.database.metadata.selections.XmpInDatabase;
import de.elmar_baumann.jpt.helper.KeywordsHelper;
import de.elmar_baumann.jpt.io.IoUtil;
import de.elmar_baumann.lib.image.metadata.xmp.XmpFileReader;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.generics.Pair;
import de.elmar_baumann.lib.io.FileLock;
import de.elmar_baumann.lib.util.StringUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gets and sets XMP metadata from image files and XMP sidecar files and to
 * XMP sidecar files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class XmpMetadata {

    private static final List<String> KNOWN_NAMESPACES = new ArrayList<String>();

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

        try {
            XMPMetaFactory.getSchemaRegistry().registerNamespace(Namespace.LIGHTROOM.getUri(), Namespace.LIGHTROOM.getPrefix());
        } catch (Exception ex) {
            AppLogger.logSevere(XmpMetadata.class, ex);
        }
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

    /**
     * Returns all {@link XMPPropertyInfo}s of a specific namespace.
     *
     * @param propertyInfos XMP property infos of arbitrary namespaces
     * @param namespace     namespace
     * @return              property infos of <code>propertyInfos</code>
     *                      matching that namespace
     */
    public static List<XMPPropertyInfo> filterPropertyInfosOfNamespace(List<XMPPropertyInfo> propertyInfos, String namespace) {

        List<XMPPropertyInfo> propertyInfosNs = new ArrayList<XMPPropertyInfo>();

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
     * @param imageFilename name of the image file
     * @return              Metadata or null if the image file has no XMP
     *                      metadata or on errors while reading
     */
    public static List<XMPPropertyInfo> getEmbeddedPropertyInfos(String imageFilename) {

        if (imageFilename == null || !FileUtil.existsFile(new File(imageFilename))) return null;

        return getPropertyInfosOfXmpString(getEmbeddedXmpAsString(imageFilename));
    }

    /**
     * Returns XMP metadata of a sidecar file.
     *
     * @param sidecarFile sidecar file. Can be null.
     * @return            Metadata or null if the sidecar file is null or on
     *                    errors while reading
     */
    public static List<XMPPropertyInfo> getPropertyInfosOfSidecarFile(File sidecarFile) {

        if (sidecarFile == null || !sidecarFile.exists()) return null;

        return getPropertyInfosOfXmpString(getXmpAsStringOfSidecarFile(sidecarFile));
    }

    private static List<XMPPropertyInfo> getPropertyInfosOfXmpString(String xmp) {
        List<XMPPropertyInfo> propertyInfos = new ArrayList<XMPPropertyInfo>();
        try {
            if (xmp != null && xmp.length() > 0) {
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
     * @param toXmpPropertyInfos list of property infos to retrieve the properties
     *                            from XMP metadata
     */
    private static void addXmpPropertyInfosTo(XMPMeta fromXmpMeta, List<XMPPropertyInfo> toXmpPropertyInfos) {
        try {
            for (XMPIterator it = fromXmpMeta.iterator(); it.hasNext();) {
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
        return !xmpPropertyInfo.getOptions().isQualifier() &&
                xmpPropertyInfo.getPath()  != null &&
                xmpPropertyInfo.getValue() != null &&
                xmpPropertyInfo.getValue().toString().length() > 0;
    }

    /**
     * Suggests a sidecar filename for an image. If the image already has a
     * sidecar file it's name will be returned. If the image does'nt have a
     * sidecar file, the sidecar file has the same name as the image file with
     * exception of the suffix: it's <code>xmp</code>. When the image file has
     * the name <code>dog.jpg</code> the sidecar name is <code>dog.xmp</code>.
     *
     * @param  imageFilename name of the image file
     * @return               sidecar filename
     */
    public static String suggestSidecarFilename(String imageFilename) {
        String existingFilename = getSidecarFilename(imageFilename);
        if (existingFilename != null) return existingFilename;

        int indexExtension = imageFilename.lastIndexOf(".");
        if (indexExtension > 0) {
            return imageFilename.substring(0, indexExtension + 1) + "xmp";
        } else {
            return imageFilename + ".xmp";
        }
    }

    /**
     * Returns the name of an image's sidecar file (only) if the image has
     * a sidecar file.
     *
     * @param imageFilename image filename
     * @return              name of the sidecar file or null, if the image
     *                      hasn't a sidecar file
     */
    public static String getSidecarFilename(String imageFilename) {

        if (imageFilename == null) return null;

        int indexExtension = imageFilename.lastIndexOf(".");

        if (indexExtension > 0) {

            String sidecarFilename = imageFilename.substring(0, indexExtension + 1) + "xmp";

            if (FileUtil.existsFile(sidecarFilename)) return sidecarFilename;
        }
        return null;
    }

    private static String getEmbeddedXmpAsString(String imageFilename) {

        if (imageFilename == null || !FileUtil.existsFile(new File(imageFilename))) return null;

        AppLogger.logInfo(XmpMetadata.class, "XmpMetadata.Info.ReadEmbeddedXmp", imageFilename);

        return XmpFileReader.readFile(imageFilename);
    }

    private static String getXmpAsStringOfSidecarFile(File sidecarFile) {

        if (sidecarFile == null || !sidecarFile.exists()) return null;

        AppLogger.logInfo(XmpMetadata.class, "XmpMetadata.Info.ReadSidecarFile", sidecarFile);

        return FileUtil.getFileContentAsString(sidecarFile, "UTF-8");
    }

    /**
     * Returns wheter a string is a known XMP namespace.
     *
     * @param string string
     * @return       true if that string is a known namespace
     */
    public static boolean isKnownNamespace(String string) {
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
             List<XMPPropertyInfo> propertyInfos, IPTCEntryMeta matchingIptcEntryMeta) {

        List<XMPPropertyInfo> filteredPropertyInfos = new ArrayList<XMPPropertyInfo>();
        String                startsWith            = IptcEntryXmpPathStartMapping.getXmpPathStartOfIptcEntryMeta(matchingIptcEntryMeta);

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
     * @param  imageFilename name of the image file
     * @return               true if the image has a sidecar file
     */
    public static boolean hasImageASidecarFile(String imageFilename) {
        return getSidecarFilename(imageFilename) != null;
    }

    /**
     * Returns whether a sidecar file can be written to the file system. This
     * is true if the sidecar file exists an is writable or if it does not
     * exist and the directory is writable.
     *
     * @param  imageFilename  name of the image file
     * @return                true if possible
     */
    public static boolean canWriteSidecarFileForImageFile(String imageFilename) {
        if (imageFilename != null) {
            File   directory       = new File(imageFilename).getParentFile();
            String sidecarFilename = getSidecarFilename(imageFilename);

            if (sidecarFilename != null) {
                return new File(sidecarFilename).canWrite();
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
     * @param  fromXmp           XMP metadata
     * @param  toSidecarFilename name of the sidecar file
     * @return                   true if successfully written
     */
    public static boolean writeXmpToSidecarFile(Xmp fromXmp, String toSidecarFilename) {
        try {
            XMPMeta toXmpMeta = getXmpMetaOfSidecarFile(toSidecarFilename);

            deleteAllEditableMetadataFrom(toXmpMeta);
            setMetadata(fromXmp, toXmpMeta);

            return writeSidecarFile(toXmpMeta, toSidecarFilename);

        } catch (Exception ex) {
            AppLogger.logSevere(XmpMetadata.class, ex);
            return false;
        }
    }

    private static void setMetadata(Xmp fromXmp, XMPMeta toXmpMeta) throws XMPException {
        for (Column column : EditColumns.get()) {
            String namespaceUri = XmpColumnNamespaceUriMapping.getNamespaceUriOfColumn(column);
            String arrayName    = XmpColumnXmpArrayNameMapping.getXmpArrayNameOfColumn(column);
            copyMetadata(fromXmp, toXmpMeta, column, namespaceUri, arrayName);
            setLightroomHierarchicalSubjects(fromXmp, toXmpMeta);
        }
    }

    private static XMPMeta getXmpMetaOfSidecarFile(String sidecarFilename) throws XMPException {
        File sidecarFile = new File(sidecarFilename);

        if (FileUtil.existsFile(sidecarFile)) {
            String xmp = FileUtil.getFileContentAsString(sidecarFile, "UTF-8");
            if (xmp != null && !xmp.trim().isEmpty()) {
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
        Set<Column> editableXmpColumns = EditColumns.get();

        for (Column editableColumn : editableXmpColumns) {
            String namespaceUri = XmpColumnNamespaceUriMapping.getNamespaceUriOfColumn(editableColumn);
            String propertyName = XmpColumnXmpArrayNameMapping.getXmpArrayNameOfColumn(editableColumn);
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
    private static void copyMetadata(
            Xmp     fromXmp,
            XMPMeta toXmpMeta,
            Column  column,
            String  namespaceUri,
            String  arrayName
            )
            throws XMPException
            {
        Object xmpValue = fromXmp.getValue(column);
        if (xmpValue != null) {
            if (xmpValue instanceof String) {
                String value = (String) xmpValue;
                // 2009-08-02: No side effects if value is empty
                // ("orphaned data"), because previous metadata was deleted
                if (XmpColumnXmpDataTypeMapping.isText(column) && !value.trim().isEmpty()) {
                    toXmpMeta.setProperty(namespaceUri, arrayName, value);
                } else if (XmpColumnXmpDataTypeMapping.isLanguageAlternative(column)) {
                    toXmpMeta.setLocalizedText(namespaceUri, arrayName, "", "x-default", value);
                }
            } else if (xmpValue instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<String> values = (List<String>) xmpValue;
                Collections.sort(values);
                for (String value : values) {
                    value = value.trim();
                    if (!doesArrayItemExist(toXmpMeta, namespaceUri, arrayName, value)) {
                        toXmpMeta.appendArrayItem(namespaceUri, arrayName,
                                getArrayPropertyOptionsOf(column), value, null);
                    }
                }
            } else if (xmpValue instanceof Long) {
                Long value = (Long) xmpValue;
                toXmpMeta.setProperty(namespaceUri, arrayName, Long.toString(value));
            } else {
                AppLogger.logWarning(XmpMetadata.class, "XmpMetadata.Error.WriteSetMetadata", xmpValue.getClass());
            }
        }
    }

    private static boolean doesArrayItemExist(
            XMPMeta xmpMeta,
            String  namespaceUri,
            String  propertyName,
            String  item
            ) throws XMPException
            {
        if (xmpMeta.doesPropertyExist(namespaceUri, propertyName)) {
            for (XMPIterator it = xmpMeta.iterator(namespaceUri, propertyName, new IteratorOptions()); it.hasNext();) {
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
        XmpValueType valueType = XmpColumnXmpDataTypeMapping.getXmpValueTypeOfColumn(xmpColumn);

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

    private static boolean writeSidecarFile(XMPMeta fromXmpMeta, String toSidecarFilename) {
        FileOutputStream out         = null;
        File             sidecarFile = new File(toSidecarFilename);
        if (!IoUtil.lockLogWarning(sidecarFile, XmpMetadata.class))
            return false;
        try {
            out = new FileOutputStream(sidecarFile);
            out.getChannel().lock();
            XMPMetaFactory.serialize(fromXmpMeta, out, new SerializeOptions().setPadding(10).setOmitPacketWrapper(true));
            return true;
        } catch (Exception ex) {
            AppLogger.logSevere(XmpMetadata.class, ex);
            return false;
        } finally {
            FileLock.INSTANCE.unlock(sidecarFile, XmpMetadata.class);
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ex) {
                    AppLogger.logSevere(XmpMetadata.class, ex);
                }
            }
        }
    }

    private static void setLightroomHierarchicalSubjects(Xmp fromXmp, XMPMeta toXmpMeta) throws XMPException {
        List<String> dcSubjects = fromXmp.getDcSubjects(); // Copy of DC subjects

        if (dcSubjects == null || fromXmp.getHierarchicalSubjects() == null) return;

        toXmpMeta.deleteProperty(Namespace.LIGHTROOM.getUri(),
                                 ArrayName.LR_HIERARCHICAL_SUBJECTS.getName());

        // Adding not contained hierarchical subjects to a copy of DC subjects
        for (String hierSubjects : fromXmp.getHierarchicalSubjects()) {
            if (hrSubjectsOk(dcSubjects, hierSubjects)) {
                addHierarchicalSubjects(hierSubjects, dcSubjects);
                if (!dcSubjects.contains(hierSubjects)) {
                    dcSubjects.add(hierSubjects);
                }
            }
        }

        dcSubjects = StringUtil.getTrimmed(dcSubjects);
        Collections.sort(dcSubjects);

        for (String dcSubject : dcSubjects) {
            toXmpMeta.appendArrayItem(
                                    Namespace.LIGHTROOM.getUri(),
                                    ArrayName.LR_HIERARCHICAL_SUBJECTS.getName(),
                                    ArrayName.LR_HIERARCHICAL_SUBJECTS.getArrayPropertyOptions(),
                                    dcSubject,
                                    null);
        }
    }

    private static boolean hrSubjectsOk(List<String> dcSubjects, String hrSubjects) {
        for (String hrSubject : KeywordsHelper.getHierarchicalSubjectsFromString(hrSubjects)) {
            if (!dcSubjects.contains(hrSubject)) return false;
        }
        return true;
    }

    private static void addHierarchicalSubjects(String hrSubjects, List<String> toDcSubjects) {
        assert hrSubjects != null; if (hrSubjects == null) return;
        
        for (String hrSubject : KeywordsHelper.getHierarchicalSubjectsFromString(hrSubjects)) {
            if (!toDcSubjects.contains(hrSubject)) {
                toDcSubjects.add(hrSubject);
            }
        }
    }

    /**
     * Returns XMP metadata of a image file. If the image has a sidecar file,
     * it's metadata will be read. If the image hasn't a sidecar file but
     * embedded XMP metadata, the embedded XMP metadata will be read.
     *
     * @param  imageFilename name of the image file
     * @return               XMP metadata of the image file or null
     */
    public static Xmp getXmpFromSidecarFileOf(String imageFilename) {

        if (imageFilename == null || !hasImageASidecarFile(imageFilename)) return null;

        return getXmp(
                   getPropertyInfosOfSidecarFile(
                       new File(getSidecarFilename(imageFilename))), imageFilename, XmpLocation.SIDECAR_FILE);
    }

    /**
     * Returns XMP embedded in the image file.
     *
     * @param   imageFilename name of the image file
     * @return                embedded XMP metadata or null if no XMP is
     *                        embedded or when errors occur while reading the
     *                        file
     */
    public static Xmp getEmbeddedXmp(String imageFilename) {
        String xmpString = getEmbeddedXmpAsString(imageFilename);
        return xmpString == null
               ? null
               : getXmp(  getPropertyInfosOfXmpString(xmpString), imageFilename, XmpLocation.EMBEDDED);
    }

    /**
     * Puts into a map property infos where the map key is a string of
     * {@link XmpInDatabase#getPathPrefixes()}. The values are
     * {@link XMPPropertyInfo} instances with path prefixes matching the key.
     *
     * @param  xmpPropertyInfos unordered property infos
     * @return                   ordered property infos
     */
    public static Map<String, List<XMPPropertyInfo>> getOrderedPropertyInfosForDatabaseOf(List<XMPPropertyInfo> xmpPropertyInfos) {

        if (xmpPropertyInfos == null) throw new NullPointerException("xmpPropertyInfos == null");

        Map<String, List<XMPPropertyInfo>> propertyInfoWithPathStart = new HashMap<String, List<XMPPropertyInfo>>();
        Set<String>                        pathPrefixes              = XmpInDatabase.getPathPrefixes();

        for (String pathPrefix : pathPrefixes) {
            for (XMPPropertyInfo propertyInfo : xmpPropertyInfos) {
                if (propertyInfo.getPath().startsWith(pathPrefix)) {
                    List<XMPPropertyInfo> infos = propertyInfoWithPathStart.get(pathPrefix);
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

    private static Xmp getXmp(List<XMPPropertyInfo> xmpPropertyInfos, String areFromXmpImageFilename, XmpLocation xmpLocation) {
        Xmp xmp = null;
        if (xmpPropertyInfos != null) {
            xmp = new Xmp();
            for (XMPPropertyInfo xmpPropertyInfo : xmpPropertyInfos) {
                String path   = xmpPropertyInfo.getPath();
                Object value  = xmpPropertyInfo.getValue();
                Column column = XmpColumnXmpArrayNameMapping.findColumn(path);

                if (value != null && column != null && column.getDataType() != null) {
                    try {
                        xmp.setValue(column, column.getDataType().fromString(value.toString()));
                    } catch (Exception ex) {
                        AppLogger.logSevere(XmpMetadata.class, ex);
                    }
                }
                addExtension(path, xmp, value);
            }
            setLastModified(xmpLocation, xmp, areFromXmpImageFilename);
        }
        return xmp;
    }

    private static void addExtension(String fromPath, Xmp toXmp, Object value) {
        addLightroomHierarchicalSubjects(fromPath, toXmp, value);
    }

    private static void addLightroomHierarchicalSubjects(String fromPath, Xmp toXmp, Object value) {
        if (fromPath.startsWith(ArrayName.LR_HIERARCHICAL_SUBJECTS.getName()) &&
            value instanceof String &&
            ((String) value).contains(Xmp.HIER_SUBJECTS_DELIM)
            ) {
            String hrSubjects = ((String) value).trim();
            if (!hrSubjects.isEmpty()) {
                toXmp.addHierarchicalSubjects(hrSubjects);
            }
        }
    }

    private static void setLastModified(XmpLocation xmpType, Xmp xmp, String imageFilename) {

        String sidecarFilename = getSidecarFilename(imageFilename);
        File   imageFile       = new File(imageFilename);

        if (xmpType.equals(XmpLocation.SIDECAR_FILE) && sidecarFilename != null) {
            xmp.setLastModified(new File(sidecarFilename).lastModified());
        } else if (xmpType.equals(XmpLocation.EMBEDDED) && FileUtil.existsFile(imageFile)) {
            xmp.setLastModified(imageFile.lastModified());
        }
    }

    /**
     * Returns pairs of image files and their sidecar files.
     *
     * @param  imageFiles image files
     * @return            pairs of image files with their corresponding sidecar
     *                    files.
     *                    {@link de.elmar_baumann.lib.generics.Pair#getFirst()}
     *                    returns a reference to an image file object in the
     *                    <code>imageFiles</code> list.
     *                    {@link de.elmar_baumann.lib.generics.Pair#getSecond()}
     *                    returns the sidecar file of the referenced image file
     *                    or null if the image file has no sidecar file.
     */
    public static List<Pair<File, File>> getImageFilesWithSidecarFiles(List<File> imageFiles) {
        List<Pair<File, File>> filePairs = new ArrayList<Pair<File, File>>();

        for (File imageFile : imageFiles) {
            String sidecarFilename = getSidecarFilename(imageFile.getAbsolutePath());
            filePairs.add(new Pair<File, File>(
                    imageFile,
                    sidecarFilename == null ? null : new File(sidecarFilename)));
        }
        return filePairs;
    }

    private XmpMetadata() {
    }
}
