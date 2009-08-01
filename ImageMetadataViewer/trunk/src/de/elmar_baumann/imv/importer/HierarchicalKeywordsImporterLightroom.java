package de.elmar_baumann.imv.importer;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.resource.Bundle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
    private static final Icon icon = AppIcons.getIcon("icon_lightroom.png");
    /**
     * Lightroom exports keywords within {} - constant if changed in later
     * Lightroom versions
     */
    private static final String SYNONYM_START_CHAR = "{";
    private static final String CHILD_START_CHAR = "\t";

    @Override
    public Collection<List<String>> getPaths(File file) {
        if (file == null)
            throw new NullPointerException("file == null"); // NOI18N

        List<List<String>> paths = new ArrayList<List<String>>();
        try {
            List<String> lines = readLines(file);
            int linecount = lines.size();
            for (int lineIndex = 0; lineIndex < linecount; lineIndex++) {
                int nextIndex = addChildren(lineIndex, lines, paths);
                if (nextIndex == lineIndex) { // Noting added
                    String line = lines.get(lineIndex).trim();
                    if (!isSynonym(line)) {
                        List<String> keywords = new ArrayList<String>();
                        keywords.add(removeBrackets(line));
                        paths.add(keywords);
                    }
                } else {
                    lineIndex = nextIndex - 1;
                }
            }
        } catch (Exception ex) {
            AppLog.logSevere(HierarchicalKeywordsImporterLightroom.class, ex);
        }
        return paths;
    }

    private int addChildren(
            int lineIndex, List<String> lines, List<List<String>> paths) {

        if (lineIndex == lines.size() - 1) return lineIndex;
        boolean hasChild = lines.get(lineIndex + 1).startsWith(CHILD_START_CHAR);
        if (!hasChild) return lineIndex;

        int toIndex = getNextParentIndex(lines, lineIndex + 1);
        Tree tree = new Tree(removeBrackets(lines.get(lineIndex).trim()));
        for (int i = lineIndex + 1; i < toIndex; i++) {
            String line = lines.get(i);
            int level = getChildStartCharCount(line);
            if (!isSynonym(line.trim())) {
                tree.addNode(level, removeBrackets(line.trim()));
            }
        }
        tree.addPaths(paths);
        return toIndex;
    }

    private int getChildStartCharCount(String s) {
        if (!s.startsWith(CHILD_START_CHAR)) return 0;
        int length = s.length();
        boolean isChildStartChar = true;
        int count = 0;
        for (count = 0; isChildStartChar && count < length; count++) {
            isChildStartChar = s.substring(count, count + 1).equals(
                    CHILD_START_CHAR);
        }
        return count > 0
               ? count - 1
               : 0;
    }

    private int getNextParentIndex(List<String> lines, int startIndex) {
        boolean isChild = true;
        int lineIndex = startIndex;
        int lineCount = lines.size();
        while (isChild && lineIndex < lineCount) {
            if (!lines.get(lineIndex).startsWith(CHILD_START_CHAR)) {
                return lineIndex;
            }
            lineIndex++;
        }
        return lineCount;
    }

    private boolean isSynonym(String line) {
        return line.startsWith(SYNONYM_START_CHAR);
    }

    private String removeBrackets(String line) {
        boolean hasStartBracket = line.startsWith("[");
        boolean hasEndBracket = line.endsWith("]");
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
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }

    @Override
    public FileFilter getFileFilter() {
        return new FileNameExtensionFilter(getDescription(), "txt");
    }

    @Override
    public String getDescription() {
        return Bundle.getString(
                "HierarchicalKeywordsImporterLightroom.Description");
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

        private final List<Node> children = new ArrayList<Node>();
        private final String string;

        public Node(String string) {
            this.string = string;
        }

        public List<Node> getChildren() {
            return children;
        }

        public int getChildCount() {
            return children.size();
        }

        public Node getChildAt(int index) {
            assert index >= 0 && index < children.size() :
                    "Invalid index: " + index + ". Has to be between 0 and " +
                    (children.size() - 1);
            return index >= 0 && index < children.size()
                   ? children.get(index)
                   : null;
        }

        public String getString() {
            return string;
        }

        public void addChild(Node child) {
            children.add(child);
        }
    }

    // Works only in this special case (Lightroom export file)!
    private class Tree {

        private final Node root;

        public Tree(String root) {
            this.root = new Node(root);
        }

        public void addNode(int level, String string) {
            assert level >= 1 : "Level has to be 1 and not " + level;
            assert string != null : "String is null!";
            if (level == 1) {
                root.addChild(new Node(string));
            } else if (level > 1) {
                Node child = root;
                int currentLevel = 2;
                do {
                    child = child.getChildAt(child.getChildCount() - 1);
                } while (child != null && currentLevel++ < level);
                assert child != null : "Child is null!";
                if (child != null) {
                    child.addChild(new Node(string));
                }
            }
        }

        private void addPaths(List<List<String>> paths) {
            for (int i = 0; i < root.getChildCount(); i++) {
                List<String> path = new ArrayList<String>();
                path.add(root.getString());
                addToPath(root.getChildAt(i), path);
                paths.add(path);
            }
        }

        private void addToPath(Node node, List<String> path) {
            path.add(node.getString());
            for (int i = 0; i < node.getChildCount(); i++) {
                addToPath(node.getChildAt(i), path); // recursive
            }
        }
    }
}
