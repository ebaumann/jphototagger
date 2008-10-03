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
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/02
 */
public class HelpIndexParser {

    public static HelpIndex parse(InputStream is) {
        HelpIndex helpIndex = null;
        try {
            DocumentBuilderFactory factory = getDocBuilderFactory();
            DocumentBuilder docBuilder = getDocBuilder(factory);
            Document document = docBuilder.parse(is);
            document.getDocumentElement().normalize();
            helpIndex = getHelpIndex(document);
        } catch (SAXException ex) {
            Logger.getLogger(HelpIndex.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HelpIndex.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(HelpIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        return helpIndex;
    }

    private static HelpIndex getHelpIndex(Document document) throws DOMException {
        HelpIndex helpIndex = new HelpIndex();
        NodeList docNodes = document.getElementsByTagName("helpindex").item(0).getChildNodes();
        int length = docNodes.getLength();
        for (int i = 0; i < length; i++) {
            Node node = docNodes.item(i);
            String nodeName = node.getNodeName();
            if (nodeName.equals("node")) {
                parseNode((Element)node, helpIndex);
            } else if (nodeName.equals("page")) {
                helpIndex.addPage(getPage((Element)node));
            }
        }
        return helpIndex;
    }

    private static void parseNode(Element section, HelpIndex helpIndex) {
        HelpNode helpNode = new HelpNode();
        NodeList title = section.getElementsByTagName("title");
        helpNode.setTitle(title.item(0).getFirstChild().getNodeValue().trim());
        NodeList nodes = section.getChildNodes();
        int length = nodes.getLength();
        for (int i = 0; i < length; i++) {
            if (nodes.item(i).getNodeName().equals("section")) {
                parseNode((Element)nodes.item(i), helpIndex);
            } else if (nodes.item(i).getNodeName().equals("page")) {
                helpNode.addPage(getPage((Element)nodes.item(i)));
            }
        }
        helpIndex.addNode(helpNode);
    }

    private static HelpPage getPage(Element page) throws DOMException {
            HelpPage helpPage = new HelpPage();
            NodeList uri = page.getElementsByTagName("uri");
            NodeList title = page.getElementsByTagName("title");
            helpPage.setUri(uri.item(0).getFirstChild().getNodeValue().trim());
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
                    "/de/elmar_baumann/lib/resource/helpindex.dtd");
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
}
