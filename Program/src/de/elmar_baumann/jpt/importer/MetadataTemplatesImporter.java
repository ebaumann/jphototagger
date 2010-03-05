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
package de.elmar_baumann.jpt.importer;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.data.MetadataTemplate;
import de.elmar_baumann.jpt.database.DatabaseMetadataTemplates;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.selections.EditColumns;
import de.elmar_baumann.jpt.exporter.MetadataTemplatesExporter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-02-07
 */
public final class MetadataTemplatesImporter implements Importer, EntityResolver
{
    public static final  MetadataTemplatesImporter INSTANCE            = new MetadataTemplatesImporter();
    private static final long                      serialVersionUID    = 1L;
    private static Map<String, Column>             COLUMN_OF_CLASSNAME = new HashMap<String, Column>();

    static {
        for (Column column : EditColumns.get()) {
            COLUMN_OF_CLASSNAME.put(column.getClass().getName(), column);
        }
    }

    @Override
    public void importFile(File file) {

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder        docBuilder = docFactory.newDocumentBuilder();

            docBuilder.setEntityResolver(this);

            Document doc  = docBuilder.parse(file);

            importTemplates(doc);
        } catch (Exception ex) {
            AppLogger.logSevere(getClass(), ex);
        }
    }

    private void importTemplates(Document doc) {
        NodeList templates     = doc.getElementsByTagName(MetadataTemplatesExporter.TAGNAME_TEMPLATE);
        int      templateCount = templates.getLength();

        for (int i = 0; i < templateCount; i++) {
            Node templateNode = templates.item(i);
            if (templateNode.getNodeName().equals(MetadataTemplatesExporter.TAGNAME_TEMPLATE)) {
                NodeList         entryNodeList = templateNode.getChildNodes();
                int              entryCount    = entryNodeList.getLength();
                MetadataTemplate template      = new MetadataTemplate();

                template.setName(templateNode.getAttributes().getNamedItem(MetadataTemplatesExporter.ATTR_NAME_TEMPLATE_NAME).getNodeValue().trim());
                for (int j = 0; j < entryCount; j++) {
                    Node entryNode = entryNodeList.item(j);
                    if  (entryNode.getNodeName().equals(MetadataTemplatesExporter.TAGNAME_ENTRY)) {
                        NamedNodeMap attrMap = entryNode.getAttributes();
                        String valueStr = attrMap.getNamedItem(MetadataTemplatesExporter.ATTR_NAME_VALUE).getNodeValue().trim();
                        if (!valueStr.isEmpty() && !valueStr.equals(MetadataTemplatesExporter.NULL)) {
                            String columnClassName = attrMap.getNamedItem(MetadataTemplatesExporter.ATTR_NAME_COLUMN).getNodeValue().trim();
                            String valueType       = attrMap.getNamedItem(MetadataTemplatesExporter.ATTR_NAME_VALUE_TYPE).getNodeValue().trim();
                            insert(columnClassName, valueType, valueStr, template);
                        }
                    }
                }
                if (template.getName() != null &&
                   !DatabaseMetadataTemplates.INSTANCE.exists(template.getName())) {
                   DatabaseMetadataTemplates.INSTANCE.insertOrUpdate(template);
                }
            }
        }
    }

    private void insert(String columnClassName, String valueType, String valueStr, MetadataTemplate template) {
        Column column = COLUMN_OF_CLASSNAME.get(columnClassName);
        assert column != null;
        template.setValueOfColumn(column, getValue(valueType, valueStr));
    }

    private Object getValue(String valueType, String valueStr) {
        if (valueType.equals("java.lang.String")) {
            return valueStr;
        } else if (valueType.equals("java.util.Collection")) {
            StringTokenizer st         = new StringTokenizer(valueStr, MetadataTemplatesExporter.COLLECTION_DELIM);
            List<String>    collection = new ArrayList<String>(st.countTokens());
            while (st.hasMoreTokens()) {
                collection.add(st.nextToken());
            }
            if (!collection.isEmpty()) {
                return collection;
            }
        } else  {
            assert false : valueType;
        }
        return null;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        InputStream stream = null;
        String dtd = MetadataTemplatesExporter.DTD;
        if (systemId.endsWith(dtd)) {
            String name = "/de/elmar_baumann/jpt/resource/dtd/" + dtd;
            stream = EntityResolver.class.getResourceAsStream(name);
            assert stream != null : name;
        }
        return stream == null ? null : new InputSource(new InputStreamReader(stream));
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
