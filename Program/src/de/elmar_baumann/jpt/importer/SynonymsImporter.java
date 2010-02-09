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
package de.elmar_baumann.jpt.importer;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.database.DatabaseSynonyms;
import de.elmar_baumann.jpt.exporter.SynonymsExporter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
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
public final class SynonymsImporter extends AbstractAction implements EntityResolver {

    public static final  SynonymsImporter INSTANCE         = new SynonymsImporter();
    private static final long             serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
        importSynonyms();
    }

    public void importSynonyms() {
        File file = SynonymsExporter.INSTANCE.getFile();

        if (!checkExistsFile(file)) return;

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder        docBuilder = docFactory.newDocumentBuilder();

            docBuilder.setEntityResolver(this);

            Document doc  = docBuilder.parse(file);

            importSynonyms(doc);
        } catch (Exception ex) {
            AppLogger.logSevere(getClass(), ex);
            MessageDisplayer.error(null, "SynonymsImporter.Error.Import");
        }
    }

    private void importSynonyms(Document doc) {
        NodeList entries     = doc.getElementsByTagName(SynonymsExporter.TAGNAME_ENTRY);
        int      entryCount  = entries.getLength();
        int      importCount = 0;

        for (int i = 0; i < entryCount; i++) {
            Node     entryNode = entries.item(i);
            NodeList entryElts = entryNode.getChildNodes();
            int      eltCount  = entryElts.getLength();

            assert eltCount >= 2 : eltCount;
            if (eltCount >= 2) {
                List<String> synonyms = new ArrayList<String>();
                String word = "";
                for (int j = 0; j < eltCount; j++) {
                    Node   node = entryElts.item(j);
                    String text = node.getTextContent();
                    if (text != null && node.getNodeName().equals(SynonymsExporter.TAGNAME_WORD)) {
                        word = text.trim();
                    } else if (text != null && node.getNodeName().equals(SynonymsExporter.TAGNAME_SYNONYM)) {
                        synonyms.add(text.trim());
                    }
                }
                for (String synonym : synonyms) {
                    importCount += DatabaseSynonyms.INSTANCE.insert(word, synonym);
                }
            }
        }

        MessageDisplayer.information(null, "SynonymsExporter.Info.Count", importCount);
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        InputStream stream = null;
        String dtd = SynonymsExporter.DTD;
        if (systemId.endsWith(dtd)) {
            stream = EntityResolver.class.getResourceAsStream(
                    "/de/elmar_baumann/jpt/resource/dtd/" + dtd);
        }
        return new InputSource(new InputStreamReader(stream));
    }

    private boolean checkExistsFile(File file) {
        if (!file.exists()) {
            MessageDisplayer.error(null, "SynonymsImporter.Error.FileDoesNotExist", file);
            return false;
        }
        return true;
    }

    private SynonymsImporter() {}
}
