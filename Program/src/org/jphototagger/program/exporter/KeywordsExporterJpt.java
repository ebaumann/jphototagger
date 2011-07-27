package org.jphototagger.program.exporter;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.domain.Keyword;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.io.CharEncoding;
import org.jphototagger.program.io.FilenameSuffixes;
import org.jphototagger.program.model.TreeModelKeywords;
import org.jphototagger.program.resource.JptBundle;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

/**
 * JPhotoTagger's own exportFile format.
 *
 * @author Elmar Baumann
 */
public final class KeywordsExporterJpt implements Exporter {
    public static final KeywordsExporterJpt INSTANCE = new KeywordsExporterJpt();

    /**
     * DTD of the exported file
     */
    public static final String DTD = "keywords.dtd";

    /**
     * Name of the root tag in the exported file. The root tag isn't a keyword,
     * but all it's children are keywords and the keyword's children are
     * keywords
     */
    public static final String TAGNAME_ROOT = "keywords";

    /**
     * Name of a keyword tag in the exported file
     */
    public static final String TAGNAME_KEYWORD = "keyword";

    /**
     * Name of the attribute containing the keyword type - real or helper -
     * within the keyword tag
     */
    public static final String ATTRIBUTE_TYPE = "type";

    /**
     * String values of the two possible values of {@link #ATTRIBUTE_TYPE},
     * <code>true</code> for a real keyword and <code>false</code> for a helper
     * keyword
     */
    public static final Map<Boolean, String> VALUE_OF_ATTRIBUTE_TYPE = new HashMap<Boolean, String>();

    /**
     * Name of the attribute containing the keyword name within the keyword tag
     */
    public static final String ATTRIBUTE_NAME = "name";

    /**
     * Icon returned by {@link #getIcon()}
     */
    private static final Icon ICON = AppLookAndFeel.getIcon("icon_app_small.png");
    private static final String DISPLAY_NAME = JptBundle.INSTANCE.getString("KeywordExporterJpt.DisplayName");
    private static final FileFilter FILE_FILTER = new FileNameExtensionFilter(DISPLAY_NAME, "xml");

    static {
        VALUE_OF_ATTRIBUTE_TYPE.put(true, "real");
        VALUE_OF_ATTRIBUTE_TYPE.put(false, "helper");
    }

    @Override
    public void exportFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            Document doc = getXml();
            DOMSource ds = new DOMSource(doc);
            StreamResult sr = new StreamResult(checkSuffix(file));
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();

            initTransformer(trans);
            trans.transform(ds, sr);
        } catch (Exception ex) {
            AppLogger.logSevere(getClass(), ex);
        }
    }

    private File checkSuffix(File file) {
        String suffix = "." + FilenameSuffixes.JPT_KEYWORDS;

        if (!file.getName().endsWith(suffix)) {
            return new File(file.getAbsolutePath() + suffix);
        }

        return file;
    }

    private Document getXml() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();
        Document doc = impl.createDocument(null, null, null);
        Object rootNode = ModelFactory.INSTANCE.getModel(TreeModelKeywords.class).getRoot();
        Element rootElement = doc.createElement(TAGNAME_ROOT);

        doc.appendChild(rootElement);
        appendChildren(doc, rootElement, (DefaultMutableTreeNode) rootNode);

        return doc;
    }

    private void appendChildren(Document doc, Element element, DefaultMutableTreeNode node) {
        int childCount = node.getChildCount();

        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            Element childElement = doc.createElement(TAGNAME_KEYWORD);

            setElementAttributes(childElement, getKeyword(childNode));
            element.appendChild(childElement);
            appendChildren(doc, childElement, childNode);    // recursive
        }
    }

    private Keyword getKeyword(DefaultMutableTreeNode parentNode) {
        return (Keyword) parentNode.getUserObject();
    }

    private void setElementAttributes(Element el, Keyword keyword) throws DOMException {
        el.setAttribute(ATTRIBUTE_NAME, keyword.getName());
        el.setAttribute(ATTRIBUTE_TYPE, VALUE_OF_ATTRIBUTE_TYPE.get(keyword.isReal()));
    }

    private void initTransformer(Transformer trans) throws IllegalArgumentException {
        trans.setOutputProperty(OutputKeys.METHOD, "xml");
        trans.setOutputProperty(OutputKeys.ENCODING, CharEncoding.JPT_KEYWORDS);
        trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, DTD);
        trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.setOutputProperty(OutputKeys.STANDALONE, "no");
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return ICON;
    }

    @Override
    public String getDefaultFilename() {
        return "JptKeywords.xml";
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    private KeywordsExporterJpt() {}
}
