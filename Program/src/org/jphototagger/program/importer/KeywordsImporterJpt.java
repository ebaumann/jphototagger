package org.jphototagger.program.importer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jphototagger.domain.repository.Importer;
import org.jphototagger.program.exporter.KeywordsExporterJpt;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Imports Keywords exported by {@link KeywordsExporterJpt}.
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = Importer.class)
public final class KeywordsImporterJpt extends KeywordsImporter implements Importer, EntityResolver {

    @Override
    public Collection<List<KeywordString>> getPaths(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            docBuilder.setEntityResolver(this);

            Document doc = docBuilder.parse(file);
            NodeList nl = doc.getElementsByTagName(KeywordsExporterJpt.TAGNAME_ROOT);

            if (nl.getLength() > 0) {
                return getPaths(nl.item(0));
            }
        } catch (Exception ex) {
            Logger.getLogger(KeywordsImporterJpt.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private List<List<KeywordString>> getPaths(Node rootNode) {
        List<List<KeywordString>> paths = new ArrayList<List<KeywordString>>();
        List<Node> leafs = getAllLeafs(rootNode);
        List<Stack<Node>> pathStacks = new ArrayList<Stack<Node>>();

        for (Node leaf : leafs) {
            Stack<Node> stack = new Stack<Node>();

            stack.push(leaf);
            pushParents(stack, leaf);
            pathStacks.add(stack);
        }

        for (Stack<Node> stack : pathStacks) {
            List<KeywordString> path = new ArrayList<KeywordString>(stack.size());

            while (!stack.isEmpty()) {
                path.add(getKeyword(stack.pop()));
            }

            paths.add(path);
        }

        return paths;
    }

    private KeywordString getKeyword(Node node) {
        NamedNodeMap attr = node.getAttributes();
        String name = attr.getNamedItem(KeywordsExporterJpt.ATTRIBUTE_NAME).getNodeValue();
        Boolean real = attr.getNamedItem(KeywordsExporterJpt.ATTRIBUTE_TYPE).getNodeValue().equals(KeywordsExporterJpt.VALUE_OF_ATTRIBUTE_TYPE.get(true));

        return new KeywordString(name, real);
    }

    private void pushParents(Stack<Node> nodes, Node node) {
        Node parent = node.getParentNode();

        if ((parent != null) && !parent.getNodeName().equals(KeywordsExporterJpt.TAGNAME_ROOT)) {
            nodes.push(parent);
            pushParents(nodes, parent);    // recursive
        }
    }

    private List<Node> getAllLeafs(Node rootNode) {
        List<Node> leafs = new ArrayList<Node>();
        NodeList nl = rootNode.getChildNodes();
        int length = nl.getLength();

        for (int i = 0; i < length; i++) {
            addLeaf(leafs, nl.item(i));
        }

        return leafs;
    }

    private void addLeaf(Collection<Node> leafs, Node node) {
        if (!node.getNodeName().equals(KeywordsExporterJpt.TAGNAME_KEYWORD)) {
            return;
        }

        NodeList nl = node.getChildNodes();
        int length = nl.getLength();

        if (length <= 0) {
            leafs.add(node);

            return;
        }

        for (int i = 0; i < length; i++) {
            addLeaf(leafs, nl.item(i));    // recursive
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return KeywordsExporterJpt.FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return KeywordsExporterJpt.DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return KeywordsExporterJpt.ICON;
    }

    @Override
    public String getDefaultFilename() {
        return KeywordsExporterJpt.DEFAULT_FILENAME;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        if (systemId == null) {
            throw new NullPointerException("systemId == null");
        }

        InputStream stream = null;
        String dtd = KeywordsExporterJpt.DTD;

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
    public int getPosition() {
        return KeywordsExporterJpt.POSITION;
    }

    @Override
    public boolean isJPhotoTaggerData() {
        return true;
    }

    @Override
    public void importFile(File file) {
        importKeywordsFile(file);
    }
}
