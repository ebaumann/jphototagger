package org.jphototagger.lib.util.logging;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Parst Java-Logdateien <em>im XML-Format</em> geschrieben von
 * <code>java.util.logging.Logger</code>.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author Elmar Baumann
 */
public final class LogfileParser implements EntityResolver {

    /**
     * Parst eine Logdatei und liefert deren Datensätze.
     *
     * @param filename Dateiname
     * @return         Datensätze
     * @throws         NullPointerException wenn der Dateiname null ist
     */
    public static List<LogfileRecord> parseLogfile(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");
        }

        List<LogfileRecord> records = new ArrayList<LogfileRecord>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            builder.setEntityResolver(new LogfileParser());

            Document document = builder.parse(getFileAsInputStream(filename));
            NodeList recordNodeList = document.getElementsByTagName("record");
            int recordCount = recordNodeList.getLength();

            for (int index = 0; index < recordCount; index++) {
                Node recordNode = recordNodeList.item(index);

                if (recordNode.getNodeType() == Node.ELEMENT_NODE) {
                    LogfileRecord record = new LogfileRecord();

                    record.setDate(getElement(recordNode, "date"));
                    record.setMillis(new Long(getElement(recordNode, "millis")));
                    record.setSequence(getElement(recordNode, "sequence"));
                    record.setLogger(getElement(recordNode, "logger"));
                    record.setLevel(getElement(recordNode, "level"));
                    record.setClassname(getElement(recordNode, "class"));
                    record.setMethodname(getElement(recordNode, "method"));
                    record.setThread(getElement(recordNode, "thread"));
                    record.setMessage(getElement(recordNode, "message"));
                    record.setKey(getElement(recordNode, "key"));
                    record.setCatalog(getElement(recordNode, "catalog"));
                    setException(record, recordNode);
                    setParams(record, recordNode);
                    records.add(record);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(LogfileParser.class.getName()).log(Level.SEVERE, null, ex);
        }

        return records;
    }

    private static void setException(LogfileRecord record, Node recordNode) {
        NodeList nodeList = ((Element) recordNode).getElementsByTagName("exception");

        if ((nodeList != null) && (nodeList.getLength() == 1)) {
            Node exceptionNode = nodeList.item(0);

            if (recordNode.getNodeType() == Node.ELEMENT_NODE) {
                ExceptionLogfileRecord ex = new ExceptionLogfileRecord();

                ex.setMessage(getElement(exceptionNode, "message"));
                setFrames(ex, exceptionNode);
                record.setException(ex);
            }
        }
    }

    private static void setFrames(ExceptionLogfileRecord ex, Node exceptionNode) {
        NodeList nodeList = ((Element) exceptionNode).getElementsByTagName("frame");

        if (nodeList != null) {
            int nodeCount = nodeList.getLength();

            for (int index = 0; index < nodeCount; index++) {
                Node node = nodeList.item(index);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    FrameLogfileRecord frame = new FrameLogfileRecord();

                    frame.setClassName(getElement(node, "class"));
                    frame.setLine(getElement(node, "line"));
                    frame.setMethodName(getElement(node, "method"));
                    ex.addFrame(frame);
                }
            }
        }
    }

    private static void setParams(LogfileRecord record, Node recordNode) {
        NodeList nodeList = ((Element) recordNode).getElementsByTagName("param");

        if (nodeList != null) {
            int count = nodeList.getLength();

            for (int index = 0; index < count; index++) {
                Node node = nodeList.item(index);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String param = getElement(node, "param");

                    if (param != null) {
                        record.addParam(param);
                    }
                }
            }
        }
    }

    /**
     * Liefert den Inhalt eines untergeordneten Elements.
     *
     * @param node    Node
     * @param tagName Name des Tags, dessen Inhalt geliefert werden soll
     * @return        Inhalt oder null
     */
    private static String getElement(Node recordNode, String tagName) {
        String elementData = null;
        NodeList nodeList = ((Element) recordNode).getElementsByTagName(tagName);

        if (nodeList != null) {
            Element firstElement = (Element) nodeList.item(0);

            if (firstElement != null) {
                NodeList childNodeList = firstElement.getChildNodes();

                if (childNodeList != null) {
                    Node elementNode = childNodeList.item(0);

                    if (elementNode != null) {
                        elementData = elementNode.getNodeValue().trim();
                    }
                }
            }
        }

        return elementData;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        InputStream stream = null;

        if (systemId.endsWith("logger.dtd")) {
            stream = EntityResolver.class.getResourceAsStream("/org/jphototagger/lib/resource/dtd/logger.dtd");
        }

        return new InputSource(new InputStreamReader(stream));
    }

    private static InputStream getFileAsInputStream(String filename) {
        assert filename != null;

        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(filename));

            StringBuilder sb = new StringBuilder(1000);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            String content = sb.toString();

            if (!content.endsWith("</log>")) {    // // Sonst Parse-Exception
                content += "</log>";
            }

            return new ByteArrayInputStream(content.getBytes(System.getProperty("file.encoding")));
        } catch (Exception ex) {
            Logger.getLogger(LogfileParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception ex) {
                Logger.getLogger(LogfileParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    private LogfileParser() {}
}
