/*
 * @(#)SynonymsImporter.java    Created on 2010-02-07
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
 * @author  Elmar Baumann
 */
public final class SynonymsImporter implements Importer, EntityResolver {
    public static final SynonymsImporter INSTANCE         =
        new SynonymsImporter();
    private static final long            serialVersionUID = 1L;

    @Override
    public void importFile(File file) {
        try {
            DocumentBuilderFactory docFactory =
                DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            docBuilder.setEntityResolver(this);

            Document doc = docBuilder.parse(file);

            importSynonyms(doc);
        } catch (Exception ex) {
            AppLogger.logSevere(getClass(), ex);
        }
    }

    private void importSynonyms(Document doc) {
        NodeList entries =
            doc.getElementsByTagName(SynonymsExporter.TAGNAME_ENTRY);
        int entryCount = entries.getLength();

        for (int i = 0; i < entryCount; i++) {
            Node     entryNode = entries.item(i);
            NodeList entryElts = entryNode.getChildNodes();
            int      eltCount  = entryElts.getLength();

            assert eltCount >= 2 : eltCount;

            if (eltCount >= 2) {
                List<String> synonyms = new ArrayList<String>();
                String       word     = "";

                for (int j = 0; j < eltCount; j++) {
                    Node   node = entryElts.item(j);
                    String text = node.getTextContent();

                    if ((text != null)
                            && node.getNodeName().equals(
                                SynonymsExporter.TAGNAME_WORD)) {
                        word = text.trim();
                    } else if ((text != null)
                               && node.getNodeName().equals(
                                   SynonymsExporter.TAGNAME_SYNONYM)) {
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
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {
        InputStream stream = null;
        String      dtd    = SynonymsExporter.DTD;

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
