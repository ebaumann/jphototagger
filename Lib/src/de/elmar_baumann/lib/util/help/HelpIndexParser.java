/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
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
package de.elmar_baumann.lib.util.help;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Reads the index file of the application's help: a XML file wich validates
 * against <code>/de/elmar_baumann/lib/resource/dtd/helpindex.dtd</code>.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-02
 */
public final class HelpIndexParser {

    /**
     * Reads the index file from an input stream and returns an the root node of
     * the index tree structure.
     *
     * @param  is  input stream
     * @return help root node of the index or null when errors occured
     */
    public static HelpNode parse(InputStream is) {
        if (is == null)
            throw new NullPointerException("is == null");

        HelpNode rootNode = null;
        try {
            DocumentBuilderFactory factory = getDocBuilderFactory();
            DocumentBuilder docBuilder = getDocBuilder(factory);
            Document document = docBuilder.parse(is);
            document.getDocumentElement().normalize();
            rootNode = getTree(document);
        } catch (Exception ex) {
            Logger.getLogger(HelpIndexParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rootNode;
    }

    private static HelpNode getTree(Document document) throws DOMException {
        HelpNode rootNode = new HelpNode();
        NodeList docNodes = document.getElementsByTagName("helpindex").item(0).getChildNodes();
        int length = docNodes.getLength();
        for (int i = 0; i < length; i++) {
            Node node = docNodes.item(i);
            String nodeName = node.getNodeName();
            if (nodeName.equals("node")) {
                parseNode((Element) node, rootNode);
            } else if (nodeName.equals("page")) {
                rootNode.addPage(getPage((Element) node));
            }
        }
        return rootNode;
    }

    private static void parseNode(Element section, HelpNode rootNode) {
        HelpNode helpNode = new HelpNode();
        NodeList title = section.getElementsByTagName("title");
        helpNode.setTitle(title.item(0).getFirstChild().getNodeValue().trim());
        NodeList nodes = section.getChildNodes();
        int length = nodes.getLength();
        for (int i = 0; i < length; i++) {
            if (nodes.item(i).getNodeName().equals("section")) {
                parseNode((Element) nodes.item(i), rootNode);
            } else if (nodes.item(i).getNodeName().equals("page")) {
                helpNode.addPage(getPage((Element) nodes.item(i)));
            }
        }
        rootNode.addNode(helpNode);
    }

    private static HelpPage getPage(Element page) throws DOMException {
        HelpPage helpPage = new HelpPage();
        NodeList url = page.getElementsByTagName("url");
        NodeList title = page.getElementsByTagName("title");
        helpPage.setUrl(url.item(0).getFirstChild().getNodeValue().trim());
        helpPage.setTitle(title.item(0).getFirstChild().getNodeValue().trim());
        return helpPage;
    }

    private static DocumentBuilderFactory getDocBuilderFactory() throws
            ParserConfigurationException {
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        factory.setFeature("http://xml.org/sax/features/validation", true);
        return factory;
    }

    private static DocumentBuilder getDocBuilder(DocumentBuilderFactory factory)
            throws ParserConfigurationException {
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        setEntityResolver(documentBuilder);
        setErrorHandler(documentBuilder);
        return documentBuilder;
    }

    private static void setEntityResolver(DocumentBuilder documentBuilder) {
        documentBuilder.setEntityResolver(new EntityResolver() {

            @Override
            public InputSource resolveEntity(String publicId, String systemId)
                    throws SAXException, IOException {
                InputStream is = HelpIndexParser.class.getResourceAsStream(
                        "/de/elmar_baumann/lib/resource/dtd/helpindex.dtd");
                InputSource ip = new InputSource(is);
                ip.setSystemId("helpindex.dtd");
                return ip;
            }
        });
    }

    private static void setErrorHandler(DocumentBuilder documentBuilder) {
        documentBuilder.setErrorHandler(new ErrorHandler() {

            @Override
            public void warning(SAXParseException exception) {
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws
                    SAXException {
                throw exception;
            }
        });
    }

    private HelpIndexParser() {
    }
}
