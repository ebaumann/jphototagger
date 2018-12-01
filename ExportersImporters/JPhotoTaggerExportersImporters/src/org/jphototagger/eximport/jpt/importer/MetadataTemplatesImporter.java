package org.jphototagger.eximport.jpt.importer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.repository.MetadataTemplatesRepository;
import org.jphototagger.domain.repository.RepositoryDataImporter;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.eximport.jpt.exporter.MetadataTemplatesExporter;
import org.jphototagger.xmp.EditableMetaDataValues;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataImporter.class)
public final class MetadataTemplatesImporter implements RepositoryDataImporter, EntityResolver {

    private static final Map<String, MetaDataValue> META_DATA_VALUE_OF_CLASSNAME = new HashMap<>();
    private final MetadataTemplatesRepository repo = Lookup.getDefault().lookup(MetadataTemplatesRepository.class);

    static {
        for (MetaDataValue mdValue : EditableMetaDataValues.get()) {
            META_DATA_VALUE_OF_CLASSNAME.put(mdValue.getClass().getName(), mdValue);
        }
    }

    @Override
    public void importFromFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            docBuilder.setEntityResolver(this);

            Document doc = docBuilder.parse(file);

            importTemplates(doc);
        } catch (Throwable t) {
            Logger.getLogger(MetadataTemplatesImporter.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    private void importTemplates(Document doc) {
        NodeList templates = doc.getElementsByTagName(MetadataTemplatesExporter.TAGNAME_TEMPLATE);
        int templateCount = templates.getLength();

        for (int i = 0; i < templateCount; i++) {
            Node templateNode = templates.item(i);

            if (templateNode.getNodeName().equals(MetadataTemplatesExporter.TAGNAME_TEMPLATE)) {
                NodeList entryNodeList = templateNode.getChildNodes();
                int entryCount = entryNodeList.getLength();
                MetadataTemplate template = new MetadataTemplate();

                template.setName(
                        templateNode.getAttributes().getNamedItem(
                        MetadataTemplatesExporter.ATTR_NAME_TEMPLATE_NAME).getNodeValue().trim());

                for (int j = 0; j < entryCount; j++) {
                    Node entryNode = entryNodeList.item(j);

                    if (entryNode.getNodeName().equals(MetadataTemplatesExporter.TAGNAME_ENTRY)) {
                        NamedNodeMap attrMap = entryNode.getAttributes();
                        String valueStr =
                                attrMap.getNamedItem(MetadataTemplatesExporter.ATTR_NAME_VALUE).getNodeValue().trim();

                        if (!valueStr.isEmpty() && !valueStr.equals(MetadataTemplatesExporter.NULL)) {
                            String mdClassName = attrMap.getNamedItem(MetadataTemplatesExporter.ATTR_NAME_META_DATA_VALUE).getNodeValue().trim();
                            String valueType =
                                    attrMap.getNamedItem(
                                    MetadataTemplatesExporter.ATTR_NAME_VALUE_TYPE).getNodeValue().trim();

                            insert(mdClassName, valueType, valueStr, template);
                        }
                    }
                }

                if ((template.getName() != null) && !repo.existsMetadataTemplate(template.getName())) {
                    repo.saveOrUpdateMetadataTemplate(template);
                }
            }
        }
    }

    private void insert(String mdClassName, String valueType, String valueStr, MetadataTemplate template) {
        MetaDataValue mdValue = META_DATA_VALUE_OF_CLASSNAME.get(mdClassName);

        assert mdValue != null;
        template.setMetaDataValue(mdValue, getValue(valueType, valueStr));
    }

    private Object getValue(String valueType, String valueStr) {
        if (valueType.equals("java.lang.String")) {
            return valueStr;
        } else if (valueType.equals("java.util.Collection")) {
            StringTokenizer st = new StringTokenizer(valueStr, MetadataTemplatesExporter.COLLECTION_DELIM);
            List<String> collection = new ArrayList<>(st.countTokens());

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
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        if (systemId == null) {
            throw new NullPointerException("systemId == null");
        }

        InputStream stream = null;
        String dtd = MetadataTemplatesExporter.DTD;

        if (systemId.endsWith(dtd)) {
            String name = "/org/jphototagger/program/resource/dtd/" + dtd;

            stream = MetadataTemplatesImporter.class.getResourceAsStream(name);
            assert stream != null : name;
        }

        return (stream == null)
                ? null
                : new InputSource(new InputStreamReader(stream));
    }

    @Override
    public FileFilter getFileFilter() {
        return MetadataTemplatesExporter.FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return MetadataTemplatesExporter.DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return ImportPreferences.ICON;
    }

    @Override
    public String getDefaultFilename() {
        return MetadataTemplatesExporter.DEFAULT_FILENAME;
    }

    @Override
    public int getPosition() {
        return MetadataTemplatesExporter.POSITION;
    }

    @Override
    public boolean isJPhotoTaggerData() {
        return true;
    }
}
