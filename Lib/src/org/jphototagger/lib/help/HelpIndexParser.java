package org.jphototagger.lib.help;

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
 * against <code>helpindex.dtd</code> (located in this package).
 *
 * @author Elmar Baumann
 */
public final class HelpIndexParser {

    private final String contentsUrl;
    private final String contentsUrlPath;

    public HelpIndexParser(String contentsUrl) {
        if (contentsUrl == null) {
            throw new NullPointerException("contentsUrl == null");
        }

        this.contentsUrl = contentsUrl;
        int lastIndex = contentsUrl.lastIndexOf('/');
        if (lastIndex > 0) {
            contentsUrlPath = contentsUrl.substring(0, lastIndex);
        } else {
            contentsUrlPath = "";
        }
    }

    /**
     * Reads the index file from an input stream and returns an the root node of
     * the index tree structure.
     *
     * @param  is  input stream
     * @return help root node of the index or null when errors occured
     */
    public HelpNode parse(InputStream is) {
        if (is == null) {
            throw new NullPointerException("is == null");
        }

        HelpNode rootNode = null;

        try {
            DocumentBuilderFactory factory = getDocBuilderFactory();
            DocumentBuilder docBuilder = getDocBuilder(factory);
            Document document = docBuilder.parse(is);

            document.getDocumentElement().normalize();
            rootNode = getTree(document);
        } catch (Throwable t) {
            Logger.getLogger(HelpIndexParser.class.getName()).log(Level.SEVERE, null, t);
        }

        return rootNode;
    }

    private HelpNode getTree(Document document) throws DOMException {
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

    private void parseNode(Element section, HelpNode rootNode) {
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

    private HelpPage getPage(Element page) throws DOMException {
        HelpPage helpPage = new HelpPage();
        NodeList url = page.getElementsByTagName("url");
        NodeList title = page.getElementsByTagName("title");
        String urlString = url.item(0).getFirstChild().getNodeValue().trim();

        if (!urlString.startsWith(contentsUrlPath)) {
            urlString = contentsUrlPath + '/' + urlString;
        }

        helpPage.setUrl(urlString);
        helpPage.setTitle(title.item(0).getFirstChild().getNodeValue().trim());

        return helpPage;
    }

    private DocumentBuilderFactory getDocBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setFeature("http://xml.org/sax/features/validation", true);

        return factory;
    }

    private DocumentBuilder getDocBuilder(DocumentBuilderFactory factory) throws ParserConfigurationException {
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();

        setEntityResolver(documentBuilder);
        setErrorHandler(documentBuilder);

        return documentBuilder;
    }

    private void setEntityResolver(DocumentBuilder documentBuilder) {
        documentBuilder.setEntityResolver(new EntityResolver() {

            @Override
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                InputStream is = HelpIndexParser.class.getResourceAsStream("/org/jphototagger/lib/help/helpindex.dtd");
                InputSource ip = new InputSource(is);

                ip.setSystemId("helpindex.dtd");

                return ip;
            }
        });
    }

    private void setErrorHandler(DocumentBuilder documentBuilder) {
        documentBuilder.setErrorHandler(new ErrorHandler() {

            @Override
            public void warning(SAXParseException exception) {
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }
        });
    }
}
