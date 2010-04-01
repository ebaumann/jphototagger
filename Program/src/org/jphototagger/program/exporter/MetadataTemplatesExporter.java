/*
 * @(#)MetadataTemplatesExporter.java    Created on 2010-02-07
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.exporter;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.MetadataTemplate;
import org.jphototagger.program.database.DatabaseMetadataTemplates;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.io.CharEncoding;
import org.jphototagger.program.resource.JptBundle;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;

import java.io.File;

import java.util.Collection;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.Icon;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class MetadataTemplatesExporter implements Exporter {
    private static final long       serialVersionUID = 1L;
    private static final FileFilter FILE_FILTER      =
        new FileNameExtensionFilter(
            JptBundle.INSTANCE.getString(
                "MetadataTemplatesExporter.DisplayName.FileFilter"), "xml");
    public static final MetadataTemplatesExporter INSTANCE =
        new MetadataTemplatesExporter();
    public static final String DTD                     =
        "metadatatemplates.dtd";
    public static final String TAGNAME_ROOT            = "templates";
    public static final String TAGNAME_TEMPLATE        = "template";
    public static final String TAGNAME_ENTRY           = "entry";
    public static final String ATTR_NAME_TEMPLATE_NAME = "name";
    public static final String ATTR_NAME_COLUMN        = "column";
    public static final String ATTR_NAME_VALUE_TYPE    = "valuetype";
    public static final String ATTR_NAME_VALUE         = "value";
    public static final String COLLECTION_DELIM        = "|";
    public static final String NULL                    = "null";

    @Override
    public void exportFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            Document           doc   = getDoc();
            DOMSource          ds    = new DOMSource(doc);
            StreamResult       sr    = new StreamResult(file);
            TransformerFactory tf    = TransformerFactory.newInstance();
            Transformer        trans = tf.newTransformer();

            insertTemplates(doc);
            initTransformer(trans);
            trans.transform(ds, sr);
        } catch (Exception ex) {
            AppLogger.logSevere(MetadataTemplatesExporter.class, ex);
        }
    }

    private void insertTemplates(Document doc) {
        Element rootElement = doc.createElement(TAGNAME_ROOT);

        doc.appendChild(rootElement);

        for (MetadataTemplate template :
                DatabaseMetadataTemplates.INSTANCE.getAll()) {
            Element templateElement = doc.createElement(TAGNAME_TEMPLATE);

            templateElement.setAttribute(ATTR_NAME_TEMPLATE_NAME,
                                         template.getName());

            for (Column column : template.getColumns()) {
                Element entryElement = doc.createElement(TAGNAME_ENTRY);

                entryElement.setAttribute(ATTR_NAME_COLUMN,
                                          column.getClass().getName());
                setEntryValue(template.getValueOfColumn(column), entryElement);
                templateElement.appendChild(entryElement);
            }

            rootElement.appendChild(templateElement);
        }
    }

    private void setEntryValue(Object value, Element entryElement)
            throws DOMException {
        if (value instanceof Collection<?>) {
            int           index = 0;
            StringBuilder sb    = new StringBuilder();

            for (Object o : (Collection<?>) value) {
                assert o instanceof String : o;
                sb.append((index++ == 0)
                          ? ""
                          : COLLECTION_DELIM);
                sb.append(o.toString());
            }

            entryElement.setAttribute(ATTR_NAME_VALUE_TYPE,
                                      Collection.class.getName());
            entryElement.setAttribute(ATTR_NAME_VALUE, sb.toString());
        } else {
            entryElement.setAttribute(ATTR_NAME_VALUE_TYPE, (value == null)
                    ? NULL
                    : value.getClass().getName());
            entryElement.setAttribute(ATTR_NAME_VALUE, (value == null)
                    ? NULL
                    : value.toString());
        }
    }

    private Document getDoc() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder        builder = factory.newDocumentBuilder();
        DOMImplementation      impl    = builder.getDOMImplementation();
        Document               doc     = impl.createDocument(null, null, null);

        return doc;
    }

    private void initTransformer(Transformer trans)
            throws IllegalArgumentException {
        trans.setOutputProperty(OutputKeys.METHOD, "xml");
        trans.setOutputProperty(OutputKeys.ENCODING, CharEncoding.JPT_KEYWORDS);
        trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, DTD);
        trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
                                "4");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.setOutputProperty(OutputKeys.STANDALONE, "no");
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return JptBundle.INSTANCE.getString(
            "MetadataTemplatesExporter.DisplayName");
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_export.png");
    }

    @Override
    public String getDefaultFilename() {
        return "JptMetadataTemplates.xml";
    }

    private MetadataTemplatesExporter() {}
}
