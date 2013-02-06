package org.jphototagger.developersupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Elmar Baumann
 */
public class HelpContentUtil {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("org/jphototagger/developersupport/Bundle");

    // For debugging
    public static void main(String[] args) throws Exception {
        getSingleHtmlFileFile(new File("somewhere on my computer"));
    }

    /**
     * @param contentXmlFile
     * @return HTML file containing the merged contents of all help HTML files
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static String getSingleHtmlFileFile(File contentXmlFile) throws SAXException, IOException, ParserConfigurationException {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<html>");
        appendHeadToHtmlIndex(sb);
        sb.append("\n\t<body>");
        for (File file : getHtmlFiles(contentXmlFile)) { // Parser would loop infinitely, because HTML is no XML (don't know, why not throwing an Exception)
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                boolean inBody = false;
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.toLowerCase().contains("</body>")) {
                        inBody = false;
                    }
                    if (inBody) {
                        sb.append("\n").append(line);
                    }
                    if (line.toLowerCase().contains("<body>")) {
                        inBody = true;
                    }
                }
            }
        }
        sb.append("\n\t</body>");
        sb.append("\n</html>");
        return sb.toString();
    }

    /**
     * @param contentXmlFile <code>contents.xml</code> within the HTML manual pages
     * @return  HTML manual files withing &lt;url&gt; tag of <code>contents.xml</code>
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static List<File> getHtmlFiles(File contentXmlFile) throws SAXException, IOException, ParserConfigurationException {
        List<File> files = new ArrayList<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(contentXmlFile);
        NodeList urlNodes = doc.getElementsByTagName("url");
        for (int nodeIndex = 0; nodeIndex < urlNodes.getLength(); nodeIndex++) {
            Element url = (Element) urlNodes.item(nodeIndex);
            String filename = url.getTextContent();
            File file = new File(contentXmlFile.getParent() + File.separator + filename);
            files.add(file);
        }
        return files;
    }

    public static String createHtmlIndex(File contentXmlFile) throws SAXException, IOException, ParserConfigurationException {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        appendHeadToHtmlIndex(sb);
        sb.append("\n\t<body>");
        sb.append("\n\t\t<h1>").append(BUNDLE.getString("HelpContentUtil.HtmlManual.title")).append("</h1>");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(contentXmlFile);
        appendNodesToHtmlIndex(doc.getElementsByTagName("node"), sb);
        appendStatusToHtmlIndex(sb);
        sb.append("\n\t</body>");
        sb.append("\n</html>");
        return sb.toString();
    }

    // nested nodes are not expected!
    private static void appendNodesToHtmlIndex(NodeList nodes, StringBuilder sb) {
        for (int i = 0; i < nodes.getLength(); i++) {
            Element child = (Element) nodes.item(i);
            appendTitleToHtmlIndex(child.getElementsByTagName("title"), sb);
            NodeList pages = child.getElementsByTagName("page");
            sb.append("\n").append(createTabIndent(3)).append("<ul>");
            for (int j = 0; j < pages.getLength(); j++) {
                appendPageToHtmlIndex((Element) pages.item(j), sb);
            }
            sb.append("\n").append(createTabIndent(3)).append("</ul>");
        }
    }

    private static void appendTitleToHtmlIndex(NodeList titleElement, StringBuilder sb) {
        String title = titleElement.item(0).getTextContent();
        sb.append("\n")
          .append(createTabIndent(3))
          .append("<h2>")
          .append(escapeHtml(title))
          .append("</h2>");
    }

    private static void appendPageToHtmlIndex(Element page, StringBuilder sb) {
        NodeList titleNode = page.getElementsByTagName("title");
        NodeList urlNode = page.getElementsByTagName("url");
        String title = titleNode.item(0).getTextContent();
        String url = urlNode.item(0).getTextContent();
        sb.append("\n")
          .append(createTabIndent(4))
          .append("<li>")
          .append("<a href=\"")
          .append(url)
          .append("\">")
          .append(escapeHtml(title))
          .append("</a>")
          .append("</li>");
    }

    private static void appendHeadToHtmlIndex(StringBuilder sb) {
        DateFormat htmlMetaDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        String title = BUNDLE.getString("HelpContentUtil.HtmlManual.title");
        sb
          .append("\n\t<head>")
          .append("\n\t\t<title>")
          .append(escapeHtml(title))
          .append("</title>")
          .append("\n\t\t<meta http-equiv=\"content-type\" content=\"text/html; charset=")
          .append(BUNDLE.getString("HelpContentUtil.HtmlManual.Charset")).append("\">")
          .append("\n\t\t<meta name=\"date\" content=\"")
          .append(htmlMetaDateFormat.format(new Date())).append("\" />")
          .append("\n\t</head>");
    }

    private static void appendStatusToHtmlIndex(StringBuilder sb) {
        Locale locale = Locale.forLanguageTag(BUNDLE.getString("HelpContentUtil.HtmlManual.Locale"));
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
        String pattern = BUNDLE.getString("HelpContentUtil.HtmlManual.Status");
        sb.append("\n")
          .append(createTabIndent(2))
          .append("<p>")
          .append(MessageFormat.format(pattern, df.format(new Date())))
          .append("</p>");
    }

    private static String createTabIndent(int level) {
        StringBuilder sb = new StringBuilder(level);
        for (int i = 0; i < level; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }

    private static final Map<String, String> HTML_ESCAPES = new LinkedHashMap<>(); // Order is important!

    static {
        HTML_ESCAPES.put("&", "&amp;");
        HTML_ESCAPES.put("\"", "&quot;");
        HTML_ESCAPES.put("<", "&lt;");
        HTML_ESCAPES.put(">", "&gt;");
        HTML_ESCAPES.put("\n\n", "<p>");
        HTML_ESCAPES.put("\n", "<br/>");
    }

    public static String escapeHtml(String s) {
        if (s == null) {
            throw new NullPointerException("s == null");
        }
        String result = s;
        for (String toEscape : HTML_ESCAPES.keySet()) {
            result = result.replace(toEscape, HTML_ESCAPES.get(toEscape));
        }
        return result;
    }

}
