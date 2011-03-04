package org.jphototagger.program.importer;

import org.jphototagger.lib.generics.Pair;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.exporter.KeywordsExporterJpt;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
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
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import javax.swing.filechooser.FileFilter;
import javax.swing.Icon;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Imports Keywords exported by {@link KeywordsExporterJpt}.
 *
 * @author Elmar Baumann
 */
public final class KeywordsImporterJpt extends KeywordsImporter implements EntityResolver {
    public static final KeywordsImporterJpt INSTANCE = new KeywordsImporterJpt();

    @Override
    public Collection<List<Pair<String, Boolean>>> getPaths(File file) {
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
            AppLogger.logSevere(getClass(), ex);
        }

        return null;
    }

    private List<List<Pair<String, Boolean>>> getPaths(Node rootNode) {
        List<List<Pair<String, Boolean>>> paths = new ArrayList<List<Pair<String, Boolean>>>();
        List<Node> leafs = getAllLeafs(rootNode);
        List<Stack<Node>> pathStacks = new ArrayList<Stack<Node>>();

        for (Node leaf : leafs) {
            Stack<Node> stack = new Stack<Node>();

            stack.push(leaf);
            pushParents(stack, leaf);
            pathStacks.add(stack);
        }

        for (Stack<Node> stack : pathStacks) {
            List<Pair<String, Boolean>> path = new ArrayList<Pair<String, Boolean>>(stack.size());

            while (!stack.isEmpty()) {
                path.add(getKeyword(stack.pop()));
            }

            paths.add(path);
        }

        return paths;
    }

    private Pair<String, Boolean> getKeyword(Node node) {
        NamedNodeMap attr = node.getAttributes();
        String name = attr.getNamedItem(KeywordsExporterJpt.ATTRIBUTE_NAME).getNodeValue();
        Boolean real = attr.getNamedItem(KeywordsExporterJpt.ATTRIBUTE_TYPE).getNodeValue().equals(
                           KeywordsExporterJpt.VALUE_OF_ATTRIBUTE_TYPE.get(true));

        return new Pair<String, Boolean>(name, real);
    }

    private void pushParents(Stack<Node> nodes, Node node) {
        Node parent = node.getParentNode();

        if ((parent != null) &&!parent.getNodeName().equals(KeywordsExporterJpt.TAGNAME_ROOT)) {
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
        return KeywordsExporterJpt.INSTANCE.getFileFilter();
    }

    @Override
    public String getDisplayName() {
        return KeywordsExporterJpt.INSTANCE.getDisplayName();
    }

    @Override
    public Icon getIcon() {
        return KeywordsExporterJpt.INSTANCE.getIcon();
    }

    @Override
    public String getDefaultFilename() {
        return KeywordsExporterJpt.INSTANCE.getDefaultFilename();
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

    private KeywordsImporterJpt() {}
}
