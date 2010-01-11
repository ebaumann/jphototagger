/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.importer;

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.exporter.KeywordExporterJpt;
import de.elmar_baumann.lib.generics.Pair;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Imports Keywords exported by {@link KeywordExporterJpt}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-10-11
 */
public final class KeywordImporterJpt
        implements KeywordImporter, EntityResolver {

    public static final KeywordImporterJpt INSTANCE =
            new KeywordImporterJpt();

    @Override
    public Collection<List<Pair<String, Boolean>>> getPaths(File file) {
        try {
            DocumentBuilderFactory docFactory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            docBuilder.setEntityResolver(this);
            Document doc = docBuilder.parse(file);
            NodeList nl = doc.getElementsByTagName(
                    KeywordExporterJpt.TAGNAME_ROOT);
            if (nl.getLength() > 0) {
                return getPaths(nl.item(0));
            }
        } catch (Exception ex) {
            AppLog.logSevere(getClass(), ex);
            MessageDisplayer.error(null, "KeywordImporterJpt.Error");
        }
        return null;
    }

    private List<List<Pair<String, Boolean>>> getPaths(Node rootNode) {
        List<List<Pair<String, Boolean>>> paths =
                new ArrayList<List<Pair<String, Boolean>>>();
        List<Node> leafs = getAllLeafs(rootNode);
        List<Stack<Node>> pathStacks = new ArrayList<Stack<Node>>();
        for (Node leaf : leafs) {
            Stack<Node> stack = new Stack<Node>();
            stack.push(leaf);
            pushParents(stack, leaf);
            pathStacks.add(stack);
        }
        for (Stack<Node> stack : pathStacks) {
            List<Pair<String, Boolean>> path =
                    new ArrayList<Pair<String, Boolean>>(stack.size());
            while (!stack.isEmpty()) {
                path.add(getKeyword(stack.pop()));
            }
            paths.add(path);
        }
        return paths;
    }

    private Pair<String, Boolean> getKeyword(Node node) {
        NamedNodeMap attr = node.getAttributes();
        String name = attr.getNamedItem(
                KeywordExporterJpt.ATTRIBUTE_NAME).getNodeValue();
        Boolean real =
                attr.getNamedItem(
                KeywordExporterJpt.ATTRIBUTE_TYPE).getNodeValue().
                equals(
                KeywordExporterJpt.VALUE_OF_ATTRIBUTE_TYPE.get(true));
        return new Pair<String, Boolean>(name, real);
    }

    private void pushParents(Stack<Node> nodes, Node node) {
        Node parent = node.getParentNode();
        if (parent != null && !parent.getNodeName().equals(
                KeywordExporterJpt.TAGNAME_ROOT)) {
            nodes.push(parent);
            pushParents(nodes, parent); // recursive
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
        if (!node.getNodeName().equals(
                KeywordExporterJpt.TAGNAME_KEYWORD))
            return;
        NodeList nl = node.getChildNodes();
        int length = nl.getLength();
        if (length <= 0) {
            leafs.add(node);
            return;
        }
        for (int i = 0; i < length; i++) {
            addLeaf(leafs, nl.item(i)); // recursive
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return KeywordExporterJpt.FILE_FILTER;
    }

    @Override
    public String getDescription() {
        return KeywordExporterJpt.DESCRIPTION;
    }

    @Override
    public Icon getIcon() {
        return KeywordExporterJpt.ICON;
    }

    private KeywordImporterJpt() {
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {
        InputStream stream = null;
        String dtd = KeywordExporterJpt.DTD;
        if (systemId.endsWith(dtd)) {
            stream = EntityResolver.class.getResourceAsStream(
                    "/de/elmar_baumann/jpt/resource/dtd/" + dtd);
        }
        return new InputSource(new InputStreamReader(stream));
    }
}
