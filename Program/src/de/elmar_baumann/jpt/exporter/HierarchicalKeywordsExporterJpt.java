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
package de.elmar_baumann.jpt.exporter;

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.HierarchicalKeyword;
import de.elmar_baumann.jpt.io.CharEncoding;
import de.elmar_baumann.jpt.io.FilenameSuffixes;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * JPhotoTagger's own export format.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-10-11
 */
public final class HierarchicalKeywordsExporterJpt
        implements HierarchicalKeywordsExporter {

    public static final HierarchicalKeywordsExporterJpt INSTANCE =
            new HierarchicalKeywordsExporterJpt();
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
    public static final Map<Boolean, String> VALUE_OF_ATTRIBUTE_TYPE =
            new HashMap<Boolean, String>();
    /**
     * Name of the attribute containing the keyword name within the keyword tag
     */
    public static final String ATTRIBUTE_NAME = "name";
    /**
     * Icon returned by {@link #getIcon()}
     */
    public static final Icon ICON = AppLookAndFeel.getIcon("icon_app_small.png");
    /**
     * Description returned by {@link #getDescription()}
     */
    public static final String DESCRIPTION = Bundle.getString("HierarchicalKeywordsExporterJpt.Description");
    /**
     * File filter returned by {@link #getFileFilter()}
     */
    public static final FileFilter FILE_FILTER = new FileNameExtensionFilter(DESCRIPTION, "xml");

    static {
        VALUE_OF_ATTRIBUTE_TYPE.put(true, "real");
        VALUE_OF_ATTRIBUTE_TYPE.put(false, "helper");
    }

    @Override
    public void export(File file) {
        try {
            Document doc = getXml();
            DOMSource ds = new DOMSource(doc);
            StreamResult sr = new StreamResult(checkSuffix(file));
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            initTransformer(trans);
            trans.transform(ds, sr);
        } catch (Exception ex) {
            AppLog.logSevere(getClass(), ex);
            MessageDisplayer.error(null, "HierarchicalKeywordsExporterJpt.Error");
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
        Object rootNode = GUI.INSTANCE.getAppPanel().
                getTreeEditKeywords().getModel().getRoot();
        Element rootElement = doc.createElement(TAGNAME_ROOT);
        doc.appendChild(rootElement);
        appendChildren(doc, rootElement, (DefaultMutableTreeNode) rootNode);
        return doc;
    }

    private void appendChildren(
            Document doc, Element element, DefaultMutableTreeNode node) {

        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode childNode =
                    (DefaultMutableTreeNode) node.getChildAt(i);
            Element childElement = doc.createElement(TAGNAME_KEYWORD);
            setElementAttributes(childElement, getKeyword(childNode));
            element.appendChild(childElement);
            appendChildren(doc, childElement, childNode); // recursive
        }
    }

    private HierarchicalKeyword getKeyword(DefaultMutableTreeNode parentNode) {
        return (HierarchicalKeyword) parentNode.getUserObject();
    }

    private void setElementAttributes(Element el, HierarchicalKeyword keyword)
            throws DOMException {
        el.setAttribute(ATTRIBUTE_NAME, keyword.getKeyword());
        el.setAttribute(
                ATTRIBUTE_TYPE, VALUE_OF_ATTRIBUTE_TYPE.get(keyword.isReal()));
    }

    private void initTransformer(Transformer trans)
            throws IllegalArgumentException {
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
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public Icon getIcon() {
        return ICON;
    }

    @Override
    public String toString() {
        return getDescription();
    }

    private HierarchicalKeywordsExporterJpt() {
    }
}
