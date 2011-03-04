package org.jphototagger.program.importer;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.database.DatabaseSynonyms;
import org.jphototagger.program.exporter.SynonymsExporter;

import org.w3c.dom.Document;
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
import java.util.List;

import javax.swing.filechooser.FileFilter;
import javax.swing.Icon;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class SynonymsImporter implements Importer, EntityResolver {
    public static final SynonymsImporter INSTANCE = new SynonymsImporter();
    private static final long serialVersionUID = 1L;

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
            AppLogger.logSevere(getClass(), ex);
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
                    DatabaseSynonyms.INSTANCE.insert(word, synonym);
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
        return SynonymsExporter.INSTANCE.getFileFilter();
    }

    @Override
    public String getDisplayName() {
        return SynonymsExporter.INSTANCE.getDisplayName();
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return SynonymsExporter.INSTANCE.getDefaultFilename();
    }

    private SynonymsImporter() {}
}
