package de.elmar_baumann.lib.util.logging;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parst Java-Logdateien <em>im XML-Format</em> geschrieben von 
 * <code>java.util.logging.Logger</code>.
 * 
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
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
        if (filename == null)
            throw new NullPointerException("filename == null"); // NOI18N

        List<LogfileRecord> records = new ArrayList<LogfileRecord>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new LogfileParser());
            Document document = builder.parse(getFileAsInputStream(filename));

            NodeList recordNodeList = document.getElementsByTagName("record"); // NOI18N
            int recordCount = recordNodeList.getLength();
            for (int index = 0; index < recordCount; index++) {
                Node recordNode = recordNodeList.item(index);
                if (recordNode.getNodeType() == Node.ELEMENT_NODE) {
                    LogfileRecord record = new LogfileRecord();
                    record.setDate(getElement(recordNode, "date")); // NOI18N
                    record.setMillis(new Long(getElement(recordNode, "millis"))); // NOI18N
                    record.setSequence(getElement(recordNode, "sequence")); // NOI18N
                    record.setLogger(getElement(recordNode, "logger")); // NOI18N
                    record.setLevel(getElement(recordNode, "level")); // NOI18N
                    record.setClassname(getElement(recordNode, "class")); // NOI18N
                    record.setMethodname(getElement(recordNode, "method")); // NOI18N
                    record.setThread(getElement(recordNode, "thread")); // NOI18N
                    record.setMessage(getElement(recordNode, "message")); // NOI18N
                    record.setKey(getElement(recordNode, "key")); // NOI18N
                    record.setCatalog(getElement(recordNode, "catalog")); // NOI18N
                    setException(record, recordNode);
                    setParams(record, recordNode);
                    records.add(record);
                }
            }
        } catch (SAXException ex) {
            Logger.getLogger(LogfileParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LogfileParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(LogfileParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LogfileParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return records;
    }

    private static void setException(LogfileRecord record, Node recordNode) {
        assert record != null : record;
        assert recordNode != null : recordNode;

        NodeList nodeList = ((Element) recordNode).getElementsByTagName("exception"); // NOI18N
        if (nodeList != null && nodeList.getLength() == 1) {
            Node exceptionNode = nodeList.item(0);
            if (recordNode.getNodeType() == Node.ELEMENT_NODE) {
                LogfileRecordException ex = new LogfileRecordException();
                ex.setMessage(getElement(exceptionNode, "message")); // NOI18N
                setFrames(ex, exceptionNode);
                record.setException(ex);
            }
        }
    }

    private static void setFrames(LogfileRecordException ex, Node exceptionNode) {
        assert ex != null : ex;
        assert exceptionNode != null : exceptionNode;

        NodeList nodeList = ((Element) exceptionNode).getElementsByTagName(
            "frame"); // NOI18N
        if (nodeList != null) {
            int nodeCount = nodeList.getLength();
            for (int index = 0; index < nodeCount; index++) {
                Node node = nodeList.item(index);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    LogfileRecordFrame frame = new LogfileRecordFrame();
                    frame.setClassName(getElement(node, "class")); // NOI18N
                    frame.setLine(getElement(node, "line")); // NOI18N
                    frame.setMethodName(getElement(node, "method")); // NOI18N
                    ex.addFrame(frame);
                }
            }
        }
    }

    private static void setParams(LogfileRecord record, Node recordNode) { // TODO Testen, bislang keinen Musterdatensatz gefunden
        assert record != null : record;
        assert recordNode != null : recordNode;

        NodeList nodeList = ((Element) recordNode).getElementsByTagName("param"); // NOI18N
        if (nodeList != null) {
            int count = nodeList.getLength();
            for (int index = 0; index < count; index++) {
                Node node = nodeList.item(index);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String param = getElement(node, "param"); // NOI18N
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
        assert recordNode != null : recordNode;
        assert tagName != null : tagName;

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
    public InputSource resolveEntity(String publicId, String systemId) throws
        SAXException, IOException {
        InputStream stream = null;
        if (systemId.endsWith("logger.dtd")) { // NOI18N
            stream = EntityResolver.class.getResourceAsStream(
                "/de/elmar_baumann/lib/resource/dtd/logger.dtd"); // NOI18N
        }
        return new InputSource(new InputStreamReader(stream));
    }

    private static InputStream getFileAsInputStream(String filename) {
        assert filename != null;

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(filename));
            StringBuffer stringBuffer = new StringBuffer(1000);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            String content = stringBuffer.toString();
            if (!content.endsWith("</log>")) { // NOI18N // Sonst Parse-Exception
                content += "</log>"; // NOI18N
            }
            return new ByteArrayInputStream(content.getBytes(System.getProperty(
                "file.encoding"))); // NOI18N
        } catch (IOException ex) {
            Logger.getLogger(LogfileParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(LogfileParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private LogfileParser() {
    }
}
