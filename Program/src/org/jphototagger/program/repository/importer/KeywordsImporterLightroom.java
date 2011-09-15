package org.jphototagger.program.repository.importer;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

import org.jphototagger.domain.repository.RepositoryDataImporter;
import org.jphototagger.program.io.CharEncoding;
import org.jphototagger.program.repository.exporter.KeywordsExporterLightroom;
import org.openide.util.lookup.ServiceProvider;

/**
 * Imports keywords exported by <strong>Adobe Photoshop Lightroom</strong>.
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataImporter.class)
public final class KeywordsImporterLightroom extends KeywordsImporter implements RepositoryDataImporter {

    /**
     * Lightroom exports keywords within {} - constant if changed in later
     * Lightroom versions
     */
    private static final String SYNONYM_START_CHAR = "{";
    private static final String ONE_LEVEL_INDENT_CHILD = "\t";
    private final Node root = new Node(null, -1, "ROOT");

    @Override
    public Collection<List<KeywordString>> getPaths(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            Node node = root;

            for (String line : readLines(file)) {
                if (!isSynonym(line) && (node != null)) {
                    String keyword = removeBrackets(line.trim());
                    int level = levelOfLine(line);

                    node = node.addNode(level, keyword);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(KeywordsImporterLightroom.class.getName()).log(Level.SEVERE, null, ex);
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

    private Collection<List<KeywordString>> pathsOfNodes(Collection<? extends Node> nodes) {
        List<List<KeywordString>> paths = new ArrayList<List<KeywordString>>();

        for (Node node : nodes) {
            paths.add(pathOfNode(node));
        }

        return paths;
    }

    private List<KeywordString> pathOfNode(Node node) {
        List<KeywordString> path = new ArrayList<KeywordString>();
        Node parent = node;

        while (parent != root) {
            path.add(new KeywordString(parent.getString(), true));
            parent = parent.getParent();
        }

        Collections.reverse(path);

        return path;
    }

    private int levelOfLine(String line) {
        if (!line.startsWith(ONE_LEVEL_INDENT_CHILD)) {
            return 0;
        }

        int length = line.length();
        boolean isLevelIndent = true;
        int index = 0;

        for (index = 0; isLevelIndent && (index < length); index++) {
            isLevelIndent = line.substring(index, index + 1).equals(ONE_LEVEL_INDENT_CHILD);
        }

        return index - 1;
    }

    private boolean isSynonym(String line) {
        return line.startsWith(SYNONYM_START_CHAR);
    }

    private String removeBrackets(String line) {
        boolean hasStartBracket = line.startsWith("[");
        boolean hasEndBracket = line.endsWith("]");

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

    private List<String> readLines(File file) throws FileNotFoundException, IOException {
        List<String> lines = new ArrayList<String>();
        BufferedReader reader = null;
        FileInputStream fis = null;
        InputStreamReader isr = null;

        try {
            fis = new FileInputStream(file.getAbsolutePath());
            isr = new InputStreamReader(fis, CharEncoding.LIGHTROOM_KEYWORDS);
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
        return KeywordsExporterLightroom.FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return KeywordsExporterLightroom.DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return KeywordsExporterLightroom.ICON;
    }

    @Override
    public String getDefaultFilename() {
        return KeywordsExporterLightroom.DEFAULT_FILENAME;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    @Override
    public void importFile(File file) {
        importKeywordsFile(file);
    }

    private class Node {

        private final int level;
        private final Node parent;
        private final List<Node> children = new ArrayList<Node>();
        private final String string;

        Node(Node parent, int level, String string) {
            if (string == null) {
                throw new NullPointerException("string == null");
            }

            this.parent = parent;
            this.level = level;
            this.string = string;
        }

        public List<Node> getChildren() {
            return Collections.unmodifiableList(children);
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
            if (string == null) {
                throw new NullPointerException("string == null");
            }

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

    @Override
    public int getPosition() {
        return KeywordsExporterLightroom.POSITION;
    }

    @Override
    public boolean isJPhotoTaggerData() {
        return false;
    }
}
