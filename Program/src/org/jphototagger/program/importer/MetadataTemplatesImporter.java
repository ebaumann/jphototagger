package org.jphototagger.program.importer;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.MetadataTemplate;
import org.jphototagger.program.database.DatabaseMetadataTemplates;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.selections.EditColumns;
import org.jphototagger.program.exporter.MetadataTemplatesExporter;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.filechooser.FileFilter;
import javax.swing.Icon;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class MetadataTemplatesImporter
        implements Importer, EntityResolver {
    public static final MetadataTemplatesImporter INSTANCE =
        new MetadataTemplatesImporter();
    private static final long                serialVersionUID = 1L;
    private static final Map<String, Column> COLUMN_OF_CLASSNAME =
        new HashMap<String, Column>();

    static {
        for (Column column : EditColumns.get()) {
            COLUMN_OF_CLASSNAME.put(column.getClass().getName(), column);
        }
    }

    @Override
    public void importFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            DocumentBuilderFactory docFactory =
                DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            docBuilder.setEntityResolver(this);

            Document doc = docBuilder.parse(file);

            importTemplates(doc);
        } catch (Exception ex) {
            AppLogger.logSevere(getClass(), ex);
        }
    }

    private void importTemplates(Document doc) {
        NodeList templates = doc.getElementsByTagName(
                                 MetadataTemplatesExporter.TAGNAME_TEMPLATE);
        int templateCount = templates.getLength();

        for (int i = 0; i < templateCount; i++) {
            Node templateNode = templates.item(i);

            if (templateNode.getNodeName().equals(
                    MetadataTemplatesExporter.TAGNAME_TEMPLATE)) {
                NodeList         entryNodeList = templateNode.getChildNodes();
                int              entryCount    = entryNodeList.getLength();
                MetadataTemplate template      = new MetadataTemplate();

                template
                    .setName(templateNode.getAttributes()
                        .getNamedItem(MetadataTemplatesExporter
                            .ATTR_NAME_TEMPLATE_NAME).getNodeValue().trim());

                for (int j = 0; j < entryCount; j++) {
                    Node entryNode = entryNodeList.item(j);

                    if (entryNode.getNodeName().equals(
                            MetadataTemplatesExporter.TAGNAME_ENTRY)) {
                        NamedNodeMap attrMap = entryNode.getAttributes();
                        String       valueStr =
                            attrMap.getNamedItem(MetadataTemplatesExporter
                                .ATTR_NAME_VALUE).getNodeValue().trim();

                        if (!valueStr.isEmpty()
                                &&!valueStr.equals(
                                    MetadataTemplatesExporter.NULL)) {
                            String columnClassName =
                                attrMap.getNamedItem(MetadataTemplatesExporter
                                    .ATTR_NAME_COLUMN).getNodeValue().trim();
                            String valueType =
                                attrMap.getNamedItem(MetadataTemplatesExporter
                                    .ATTR_NAME_VALUE_TYPE).getNodeValue()
                                        .trim();

                            insert(columnClassName, valueType, valueStr,
                                   template);
                        }
                    }
                }

                if ((template.getName() != null)
                        &&!DatabaseMetadataTemplates.INSTANCE.exists(
                            template.getName())) {
                    DatabaseMetadataTemplates.INSTANCE.insertOrUpdate(template);
                }
            }
        }
    }

    private void insert(String columnClassName, String valueType,
                        String valueStr, MetadataTemplate template) {
        Column column = COLUMN_OF_CLASSNAME.get(columnClassName);

        assert column != null;
        template.setValueOfColumn(column, getValue(valueType, valueStr));
    }

    private Object getValue(String valueType, String valueStr) {
        if (valueType.equals("java.lang.String")) {
            return valueStr;
        } else if (valueType.equals("java.util.Collection")) {
            StringTokenizer st =
                new StringTokenizer(valueStr,
                                    MetadataTemplatesExporter.COLLECTION_DELIM);
            List<String> collection = new ArrayList<String>(st.countTokens());

            while (st.hasMoreTokens()) {
                collection.add(st.nextToken());
            }

            if (!collection.isEmpty()) {
                return collection;
            }
        } else {
            assert false : valueType;
        }

        return null;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {
        if (systemId == null) {
            throw new NullPointerException("systemId == null");
        }

        InputStream stream = null;
        String      dtd    = MetadataTemplatesExporter.DTD;

        if (systemId.endsWith(dtd)) {
            String name = "/org/jphototagger/program/resource/dtd/" + dtd;

            stream = EntityResolver.class.getResourceAsStream(name);
            assert stream != null : name;
        }

        return (stream == null)
               ? null
               : new InputSource(new InputStreamReader(stream));
    }

    @Override
    public FileFilter getFileFilter() {
        return MetadataTemplatesExporter.INSTANCE.getFileFilter();
    }

    @Override
    public String getDisplayName() {
        return MetadataTemplatesExporter.INSTANCE.getDisplayName();
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return MetadataTemplatesExporter.INSTANCE.getDefaultFilename();
    }

    private MetadataTemplatesImporter() {}
}
