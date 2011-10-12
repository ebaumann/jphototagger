package org.jphototagger.eximport.jpt.exporter;

import java.io.File;
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

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.repository.RepositoryDataExporter;
import org.jphototagger.domain.repository.SynonymsRepository;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataExporter.class)
public final class SynonymsExporter implements RepositoryDataExporter {

    public static final String DEFAULT_FILENAME = "JptSynonyms.xml";
    public static final String DISPLAY_NAME = Bundle.getString(SynonymsExporter.class, "SynonymsExporter.DisplayName");
    private static final ImageIcon ICON = IconUtil.getImageIcon("/org/jphototagger/eximport/jpt/icons/icon_export.png");
    public static final int POSITION = 20;
    private static final long serialVersionUID = 1L;
    public static final FileFilter FILE_FILTER = new FileNameExtensionFilter(Bundle.getString(SynonymsExporter.class, "SynonymsExporter.DisplayName.FileFilter"), "xml");
    public static final String DTD = "synonyms.dtd";
    public static final String TAGNAME_ROOT = "synonyms";
    public static final String TAGNAME_ENTRY = "entry";
    public static final String TAGNAME_WORD = "word";
    public static final String TAGNAME_SYNONYM = "synonym";
    private final SynonymsRepository repo = Lookup.getDefault().lookup(SynonymsRepository.class);

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

            insertSynonyms(doc);
            initTransformer(trans);
            trans.transform(ds, sr);
        } catch (Exception ex) {
            Logger.getLogger(SynonymsExporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void insertSynonyms(Document doc) {
        Element rootElement = doc.createElement(TAGNAME_ROOT);

        doc.appendChild(rootElement);

        for (String word : repo.findAllWords()) {
            Element entryElement = doc.createElement(TAGNAME_ENTRY);
            Element wordElement = doc.createElement(TAGNAME_WORD);

            wordElement.setTextContent(word);
            entryElement.appendChild(wordElement);

            for (String synonym : repo.findSynonymsOfWord(word)) {
                Element synonymElement = doc.createElement(TAGNAME_SYNONYM);

                synonymElement.setTextContent(synonym);
                entryElement.appendChild(synonymElement);
            }

            rootElement.appendChild(entryElement);
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
        trans.setOutputProperty(OutputKeys.ENCODING, "UTF8");
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
