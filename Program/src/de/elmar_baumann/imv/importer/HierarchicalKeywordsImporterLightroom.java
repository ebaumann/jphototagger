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
package de.elmar_baumann.imv.importer;

import de.elmar_baumann.imv.app.AppLookAndFeel;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.io.CharEncoding;
import de.elmar_baumann.imv.io.FilenameSuffixes;
import de.elmar_baumann.imv.resource.Bundle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Imports keywords exported by <strong>Adobe Photoshop Lightroom</strong>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-01
 */
final class HierarchicalKeywordsImporterLightroom
        implements HierarchicalKeywordsImporter {

    public static final HierarchicalKeywordsImporterLightroom INSTANCE =
            new HierarchicalKeywordsImporterLightroom();
    private static final Icon icon = AppLookAndFeel.getIcon("icon_lightroom.png"); // NOI18N
    /**
     * Lightroom exports keywords within {} - constant if changed in later
     * Lightroom versions
     */
    private static final String SYNONYM_START_CHAR = "{"; // NOI18N
    private static final String ONE_LEVEL_INDENT_CHILD = "\t"; // NOI18N
    private final Node root = new Node(null, -1, "ROOT");

    @Override
    public Collection<List<String>> getPaths(File file) {
        try {
            if (file == null)
                throw new NullPointerException("file == null");
            Node node = root;
            for (String line : readLines(file)) {
                if (!isSynonym(line) && node != null) {
                    String keyword = removeBrackets(line.trim());
                    int level = levelOfLine(line);
                    node = node.addNode(level, keyword);
                }
            }
        } catch (Exception ex) {
            AppLog.logSevere(getClass(), ex);
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

    private Collection<List<String>> pathsOfNodes(
            Collection<? extends Node> nodes) {
        List<List<String>> paths = new ArrayList<List<String>>();
        for (Node node : nodes) {
            paths.add(pathOfNode(node));
        }
        return paths;
    }

    private List<String> pathOfNode(Node node) {
        List<String> path = new ArrayList<String>();
        Node parent = node;
        while (parent != root) {
            path.add(parent.getString());
            parent = parent.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    private int levelOfLine(String line) {
        if (!line.startsWith(ONE_LEVEL_INDENT_CHILD)) return 0;
        int length = line.length();
        boolean isLevelIndent = true;
        int index = 0;
        for (index = 0; isLevelIndent && index < length; index++) {
            isLevelIndent = line.substring(index, index + 1).equals(
                    ONE_LEVEL_INDENT_CHILD);
        }
        return index - 1;
    }

    private boolean isSynonym(String line) {
        return line.startsWith(SYNONYM_START_CHAR);
    }

    private String removeBrackets(String line) {
        boolean hasStartBracket = line.startsWith("["); // NOI18N
        boolean hasEndBracket = line.endsWith("]"); // NOI18N
        // proably more efficient than calculating start and end index and
        // returning a substring in one call
        return hasStartBracket && hasEndBracket
                ? line.substring(1, line.length() - 1)
                : hasStartBracket
                ? line.substring(1)
                : hasEndBracket
                ? line.substring(0, line.length() - 1)
                : line;

    }

    private List<String> readLines(File file)
            throws FileNotFoundException, IOException {
        List<String> lines = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                new FileInputStream(file.getAbsolutePath()),
                CharEncoding.LIGHTROOM_KEYWORDS));
        String line = null;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }

    @Override
    public FileFilter getFileFilter() {
        return new FileNameExtensionFilter(
                getDescription(), FilenameSuffixes.LIGHTROOM_KEYWORDS);
    }

    @Override
    public String getDescription() {
        return Bundle.getString(
                "HierarchicalKeywordsImporterLightroom.Description"); // NOI18N
    }

    @Override
    public String toString() {
        return getDescription();
    }

    private HierarchicalKeywordsImporterLightroom() {
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    private class Node {

        private final int level;
        private final Node parent;
        private final List<Node> children = new ArrayList<Node>();
        private final String string;

        public Node(Node parent, int level, String string) {
            this.parent = parent;
            this.level = level;
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
            assert index >= 0 && index < children.size() : index;
            return index >= 0 && index < children.size()
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
            if (!isLevel(level)) return null;

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
            return level >= 0 && level <= this.level + 1;
        }
    }
}
