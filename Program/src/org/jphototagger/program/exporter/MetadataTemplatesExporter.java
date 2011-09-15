package org.jphototagger.program.exporter;

import java.io.File;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.repository.RepositoryDataExporter;
import org.jphototagger.domain.repository.MetadataTemplatesRepository;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.io.CharEncoding;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataExporter.class)
public final class MetadataTemplatesExporter implements RepositoryDataExporter {

    public static final String DEFAULT_FILENAME = "JptMetadataTemplates.xml";
    public static final String DISPLAY_NAME = Bundle.getString(MetadataTemplatesExporter.class, "MetadataTemplatesExporter.DisplayName");
    public static final ImageIcon ICON = AppLookAndFeel.getIcon("icon_export.png");
    public static final int POSITION = 60;
    private static final long serialVersionUID = 1L;
    public static final FileFilter FILE_FILTER = new FileNameExtensionFilter(Bundle.getString(MetadataTemplatesExporter.class, "MetadataTemplatesExporter.DisplayName.FileFilter"), "xml");
    public static final String DTD = "metadatatemplates.dtd";
    public static final String TAGNAME_ROOT = "templates";
    public static final String TAGNAME_TEMPLATE = "template";
    public static final String TAGNAME_ENTRY = "entry";
    public static final String ATTR_NAME_TEMPLATE_NAME = "name";
    public static final String ATTR_NAME_META_DATA_VALUE = "column";
    public static final String ATTR_NAME_VALUE_TYPE = "valuetype";
    public static final String ATTR_NAME_VALUE = "value";
    public static final String COLLECTION_DELIM = "|";
    public static final String NULL = "null";
    private final MetadataTemplatesRepository repo = Lookup.getDefault().lookup(MetadataTemplatesRepository.class);

    @Override
    public void exportFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            Document doc = getDoc();
            DOMSource ds = new DOMSource(doc);
            StreamResult sr = new StreamResult(file);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();

            insertTemplates(doc);
            initTransformer(trans);
            trans.transform(ds, sr);
        } catch (Exception ex) {
            Logger.getLogger(MetadataTemplatesExporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void insertTemplates(Document doc) {
        Element rootElement = doc.createElement(TAGNAME_ROOT);

        doc.appendChild(rootElement);

        for (MetadataTemplate template : repo.findAllMetadataTemplates()) {
            Element templateElement = doc.createElement(TAGNAME_TEMPLATE);

            templateElement.setAttribute(ATTR_NAME_TEMPLATE_NAME, template.getName());

            for (MetaDataValue mdValue : template.getMetaDataValues()) {
                Element entryElement = doc.createElement(TAGNAME_ENTRY);

                entryElement.setAttribute(ATTR_NAME_META_DATA_VALUE, mdValue.getClass().getName());
                setEntryValue(template.getMetaDataValue(mdValue), entryElement);
                templateElement.appendChild(entryElement);
            }

            rootElement.appendChild(templateElement);
        }
    }

    private void setEntryValue(Object value, Element entryElement) throws DOMException {
        if (value instanceof Collection<?>) {
            int index = 0;
            StringBuilder sb = new StringBuilder();

            for (Object o : (Collection<?>) value) {
                assert o instanceof String : o;
                sb.append((index++ == 0)
                        ? ""
                        : COLLECTION_DELIM);
                sb.append(o.toString());
            }

            entryElement.setAttribute(ATTR_NAME_VALUE_TYPE, Collection.class.getName());
            entryElement.setAttribute(ATTR_NAME_VALUE, sb.toString());
        } else {
            entryElement.setAttribute(ATTR_NAME_VALUE_TYPE, (value == null)
                    ? NULL
                    : value.getClass().getName());
            entryElement.setAttribute(ATTR_NAME_VALUE, (value == null)
                    ? NULL
                    : value.toString());
        }
    }

    private Document getDoc() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();
        Document doc = impl.createDocument(null, null, null);

        return doc;
    }

    private void initTransformer(Transformer trans) throws IllegalArgumentException {
        trans.setOutputProperty(OutputKeys.METHOD, "xml");
        trans.setOutputProperty(OutputKeys.ENCODING, CharEncoding.JPT_KEYWORDS);
        trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, DTD);
        trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.setOutputProperty(OutputKeys.STANDALONE, "no");
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return ICON;
    }

    @Override
    public String getDefaultFilename() {
        return DEFAULT_FILENAME;
    }

    @Override
    public boolean isJPhotoTaggerData() {
        return true;
    }

    @Override
    public int getPosition() {
        return POSITION;
    }
}
