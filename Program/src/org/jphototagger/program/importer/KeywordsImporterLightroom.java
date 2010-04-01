/*
 * @(#)KeywordsImporterLightroom.java    Created on 2009-08-01
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

package org.jphototagger.program.importer;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.exporter.KeywordsExporterLightroom;
import org.jphototagger.program.io.CharEncoding;
import org.jphototagger.lib.generics.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.filechooser.FileFilter;
import javax.swing.Icon;

/**
 * Imports keywords exported by <strong>Adobe Photoshop Lightroom</strong>.
 *
 * @author  Elmar Baumann
 */
public final class KeywordsImporterLightroom extends KeywordsImporter {
    public static final KeywordsImporterLightroom INSTANCE =
        new KeywordsImporterLightroom();

    /**
     * Lightroom exports keywords within {} - constant if changed in later
     * Lightroom versions
     */
    private static final String SYNONYM_START_CHAR     = "{";
    private static final String ONE_LEVEL_INDENT_CHILD = "\t";
    private final Node          root                   = new Node(null, -1,
                                                             "ROOT");

    @Override
    public Collection<List<Pair<String, Boolean>>> getPaths(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            Node node = root;

            for (String line : readLines(file)) {
                if (!isSynonym(line) && (node != null)) {
                    String keyword = removeBrackets(line.trim());
                    int    level   = levelOfLine(line);

                    node = node.addNode(level, keyword);
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(getClass(), ex);
        }

        List<Node> leafs = new ArrayList<Node>();

        addLeafs(root, leafs);

        return pathsOfNodes(leafs);
    }

    private void addLeafs(Node node, Collection<? super Node> leafs) {
        int childCount = node.getChildCount();

        if (childCount == 0) {
            leafs.add(node);
        } else {
            for (int i = 0; i < childCount; i++) {
                addLeafs(node.getChildAt(i), leafs);
            }
        }
    }

    private Collection<List<Pair<String, Boolean>>> pathsOfNodes(
            Collection<? extends Node> nodes) {
        List<List<Pair<String, Boolean>>> paths =
            new ArrayList<List<Pair<String, Boolean>>>();

        for (Node node : nodes) {
            paths.add(pathOfNode(node));
        }

        return paths;
    }

    private List<Pair<String, Boolean>> pathOfNode(Node node) {
        List<Pair<String, Boolean>> path = new ArrayList<Pair<String,
                                               Boolean>>();
        Node parent = node;

        while (parent != root) {
            path.add(new Pair<String, Boolean>(parent.getString(), true));
            parent = parent.getParent();
        }

        Collections.reverse(path);

        return path;
    }

    private int levelOfLine(String line) {
        if (!line.startsWith(ONE_LEVEL_INDENT_CHILD)) {
            return 0;
        }

        int     length        = line.length();
        boolean isLevelIndent = true;
        int     index         = 0;

        for (index = 0; isLevelIndent && (index < length); index++) {
            isLevelIndent = line.substring(index,
                                           index
                                           + 1).equals(ONE_LEVEL_INDENT_CHILD);
        }

        return index - 1;
    }

    private boolean isSynonym(String line) {
        return line.startsWith(SYNONYM_START_CHAR);
    }

    private String removeBrackets(String line) {
        boolean hasStartBracket = line.startsWith("[");
        boolean hasEndBracket   = line.endsWith("]");

        // proably more efficient than calculating start and end index and
        // returning a substring in one call
        return (hasStartBracket && hasEndBracket)
               ? line.substring(1, line.length() - 1)
               : hasStartBracket
                 ? line.substring(1)
                 : hasEndBracket
                   ? line.substring(0, line.length() - 1)
                   : line;
    }

    private List<String> readLines(File file)
            throws FileNotFoundException, IOException {
        List<String>      lines  = new ArrayList<String>();
        BufferedReader    reader = null;
        FileInputStream   fis    = null;
        InputStreamReader isr    = null;

        try {
            fis    = new FileInputStream(file.getAbsolutePath());
            isr    = new InputStreamReader(fis,
                                           CharEncoding.LIGHTROOM_KEYWORDS);
            reader = new BufferedReader(isr);

            String line = null;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (fis != null) {
                fis.close();
            }

            if (isr != null) {
                isr.close();
            }

            if (reader != null) {
                reader.close();
            }
        }

        return lines;
    }

    @Override
    public FileFilter getFileFilter() {
        return KeywordsExporterLightroom.INSTANCE.getFileFilter();
    }

    @Override
    public String getDisplayName() {
        return KeywordsExporterLightroom.INSTANCE.getDisplayName();
    }

    @Override
    public Icon getIcon() {
        return KeywordsExporterLightroom.INSTANCE.getIcon();
    }

    @Override
    public String getDefaultFilename() {
        return KeywordsExporterLightroom.INSTANCE.getDefaultFilename();
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    private class Node {
        private final int        level;
        private final Node       parent;
        private final List<Node> children = new ArrayList<Node>();
        private final String     string;

        public Node(Node parent, int level, String string) {
            this.parent = parent;
            this.level  = level;
            this.string = string;
        }

        public List<Node> getChildren() {
            return children;
        }

        public int getChildCount() {
            return children.size();
        }

        public int getLevel() {
            return level;
        }

        public Node getParent() {
            return parent;
        }

        public Node getChildAt(int index) {
            assert (index >= 0) && (index < children.size()) : index;

            return ((index >= 0) && (index < children.size()))
                   ? children.get(index)
                   : null;
        }

        public String getString() {
            return string;
        }

        @Override
        public String toString() {
            return string;
        }

        public Node addNode(int level, String string) {
            assert isLevel(level) : level;

            if (!isLevel(level)) {
                return null;
            }

            Node newNode = null;

            if (level == this.level + 1) {
                newNode = new Node(this, level, string);
                children.add(newNode);
            } else {
                assert parent != null;

                if (parent != null) {
                    newNode = parent.addNode(level, string);
                }
            }

            return newNode;
        }

        private boolean isLevel(int level) {
            return (level >= 0) && (level <= this.level + 1);
        }
    }


    private KeywordsImporterLightroom() {}
}
