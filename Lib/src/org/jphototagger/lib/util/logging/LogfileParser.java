package org.jphototagger.lib.util.logging;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parses <em>XML</em> log files written by <code>java.util.logging.Logger</code>.
 *
 * @author Elmar Baumann
 */
public final class LogfileParser implements EntityResolver {

    public static List<LogfileRecord> parseLogfile(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");
        }

        List<LogfileRecord> records = new ArrayList<>();

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

                    record.setDate(getContentOfChildElement(recordNode, "date"));
                    record.setMillis(new Long(getContentOfChildElement(recordNode, "millis")));
                    record.setSequence(getContentOfChildElement(recordNode, "sequence"));
                    record.setLogger(getContentOfChildElement(recordNode, "logger"));
                    record.setLevel(getContentOfChildElement(recordNode, "level"));
                    record.setClassname(getContentOfChildElement(recordNode, "class"));
                    record.setMethodname(getContentOfChildElement(recordNode, "method"));
                    record.setThread(getContentOfChildElement(recordNode, "thread"));
                    record.setMessage(getContentOfChildElement(recordNode, "message"));
                    record.setKey(getContentOfChildElement(recordNode, "key"));
                    record.setCatalog(getContentOfChildElement(recordNode, "catalog"));
                    setException(record, recordNode);
                    setParams(record, recordNode);
                    records.add(record);
                }
            }
        } catch (Throwable t) {
            Logger.getLogger(LogfileParser.class.getName()).log(Level.SEVERE, null, t);
        }

        return records;
    }

    private static void setException(LogfileRecord record, Node recordNode) {
        NodeList nodeList = ((Element) recordNode).getElementsByTagName("exception");

        if ((nodeList != null) && (nodeList.getLength() == 1)) {
            Node exceptionNode = nodeList.item(0);

            if (recordNode.getNodeType() == Node.ELEMENT_NODE) {
                ExceptionLogfileRecord ex = new ExceptionLogfileRecord();

                ex.setMessage(getContentOfChildElement(exceptionNode, "message"));
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

                    frame.setClassName(getContentOfChildElement(node, "class"));
                    frame.setLine(getContentOfChildElement(node, "line"));
                    frame.setMethodName(getContentOfChildElement(node, "method"));
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
                    String param = getContentOfChildElement(node, "param");

                    if (param != null) {
                        record.addParam(param);
                    }
                }
            }
        }
    }

    private static String getContentOfChildElement(Node recordNode, String tagName) {
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
            stream = LogfileParser.class.getResourceAsStream("/org/jphototagger/lib/util/logging/logger.dtd");
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
        } catch (Throwable t) {
            Logger.getLogger(LogfileParser.class.getName()).log(Level.SEVERE, null, t);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Throwable t) {
                Logger.getLogger(LogfileParser.class.getName()).log(Level.SEVERE, null, t);
            }
        }

        return null;
    }

    private LogfileParser() {
    }
}
