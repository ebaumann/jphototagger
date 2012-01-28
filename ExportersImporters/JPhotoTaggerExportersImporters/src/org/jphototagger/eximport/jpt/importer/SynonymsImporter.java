package org.jphototagger.eximport.jpt.importer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.repository.RepositoryDataImporter;
import org.jphototagger.domain.repository.SynonymsRepository;
import org.jphototagger.eximport.jpt.exporter.SynonymsExporter;
import org.jphototagger.lib.swing.IconUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataImporter.class)
public final class SynonymsImporter implements RepositoryDataImporter, EntityResolver {

    private static final long serialVersionUID = 1L;
    private final SynonymsRepository repo = Lookup.getDefault().lookup(SynonymsRepository.class);
    private static final ImageIcon ICON = IconUtil.getImageIcon("/org/jphototagger/eximport/jpt/icons/icon_import.png");

    @Override
    public void importFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            docBuilder.setEntityResolver(this);

            Document doc = docBuilder.parse(file);

            importSynonyms(doc);
        } catch (Exception ex) {
            Logger.getLogger(SynonymsImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void importSynonyms(Document doc) {
        NodeList entries = doc.getElementsByTagName(SynonymsExporter.TAGNAME_ENTRY);
        int entryCount = entries.getLength();

        for (int i = 0; i < entryCount; i++) {
            Node entryNode = entries.item(i);
            NodeList entryElts = entryNode.getChildNodes();
            int eltCount = entryElts.getLength();

            assert eltCount >= 2 : eltCount;

            if (eltCount >= 2) {
                List<String> synonyms = new ArrayList<String>();
                String word = "";

                for (int j = 0; j < eltCount; j++) {
                    Node node = entryElts.item(j);
                    String text = node.getTextContent();

                    if ((text != null) && node.getNodeName().equals(SynonymsExporter.TAGNAME_WORD)) {
                        word = text.trim();
                    } else if ((text != null) && node.getNodeName().equals(SynonymsExporter.TAGNAME_SYNONYM)) {
                        synonyms.add(text.trim());
                    }
                }

                for (String synonym : synonyms) {
                    repo.saveSynonym(word, synonym);
                }
            }
        }
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        if (systemId == null) {
            throw new NullPointerException("systemId == null");
        }

        InputStream stream = null;
        String dtd = SynonymsExporter.DTD;

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
        return SynonymsExporter.FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return SynonymsExporter.DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return ICON;
    }

    @Override
    public String getDefaultFilename() {
        return SynonymsExporter.DEFAULT_FILENAME;
    }

    @Override
    public int getPosition() {
        return SynonymsExporter.POSITION;
    }

    @Override
    public boolean isJPhotoTaggerData() {
        return true;
    }
}
