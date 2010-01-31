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
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.data.Keyword;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.DatabaseKeywords;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.metadata.keywords.ColumnKeyword;
import de.elmar_baumann.jpt.factory.ModelFactory;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.model.TreeModelKeywords;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.EditMetadataPanels;
import de.elmar_baumann.jpt.view.renderer.TreeCellRendererKeywords;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.generics.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Helps with keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-05
 */
public final class KeywordsHelper {

    /**
     * Adds the keyword - contained as user object in a d.m. tree node -
     * and all it's parents to the metadata edit panel.
     *
     * @param node node with keyword. <em>All parents of that node have to be an
     *             instance of {@link DefaultMutableTreeNode}!</em>
     */
    public static void addKeywordsToEditPanel(DefaultMutableTreeNode node) {
        EditMetadataPanels editPanels     = GUI.INSTANCE.getAppPanel().getEditMetadataPanels();
        List<String>       keywordStrings = getKeywordStrings(node, true);
        for (String keyword : keywordStrings) {
            editPanels.addText(ColumnKeyword.INSTANCE, keyword);
        }
        if (keywordStrings.size() > 1) {
            Collections.reverse(keywordStrings); // else leaf is first element
        // FIXME
            //editPanels.addHierarchicalSubjects(getHierarchicalSubjectsFromList(keywordStrings));
        }
    }

    /**
     * Returns a keyword - contained as user object in a default mutable tree
     * node - and all it's parents.
     *
     * @param node node with keyword. <em>All parents of that node have to be
     *             instances of {@link DefaultMutableTreeNode}!</em>
     * @param real true if only real keywords shall be added
     * @return     all keywords
     */
    public static List<Keyword> getKeywords(DefaultMutableTreeNode node, boolean real) {
        List<Keyword> list = new ArrayList<Keyword>();
        while (node != null) {
            Object userObject = node.getUserObject();
            if (userObject instanceof Keyword) {
                Keyword keyword = (Keyword) userObject;
                if (!real || real && keyword.isReal()) {
                    list.add(keyword);
                }
            }
            TreeNode parent = node.getParent();
            assert parent == null || parent instanceof DefaultMutableTreeNode : "Not a DefaultMutableTreeNode: " + parent;
            
            node = parent instanceof DefaultMutableTreeNode ? (DefaultMutableTreeNode) parent : null;
        }
        return list;
    }

    /**
     * Returns a keyword - contained as user object in a default mutable tree
     * node - and all it's parents as a list of strings.
     *
     * @param node node with keyword. <em>All parents of that node have to be
     *             instances of {@link DefaultMutableTreeNode}!</em>
     * @param real true if only real keywords shall be added
     * @return     all keywords as strings
     */
    public static List<String> getKeywordStrings(DefaultMutableTreeNode node, boolean real) {
        List<String> list = new ArrayList<String>();
        for (Keyword keyword : getKeywords(node, real)) {
            list.add(keyword.getName());
        }
        return list;
    }

    /**
     * Selects in {@link AppPanel#getTreeSelKeywords()} a node with a specific
     * keyword.
     *
     * @param tree    tree with {@link TreeModelKeywords} and all
     *                nodes of the type {@link DefaultMutableTreeNode}
     * @param keyword keyword to select
     */
    @SuppressWarnings("unchecked")
    public static void selectNode(JTree tree, Keyword keyword) {
        TreeModelKeywords      model   = ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);
        DefaultMutableTreeNode root    = (DefaultMutableTreeNode) model.getRoot();
        DefaultMutableTreeNode selNode = null;
        for (Enumeration<DefaultMutableTreeNode> e = root.breadthFirstEnumeration(); selNode == null && e.hasMoreElements();) {
            DefaultMutableTreeNode node       = e.nextElement();
            Object                 userObject = node.getUserObject();
            if (userObject instanceof Keyword) {
                Keyword hkw = (Keyword) userObject;
                if (hkw.equals(keyword)) {
                    selNode = node;
                }
            }
        }
        if (selNode != null) {
            tree.setSelectionPath(new TreePath(selNode.getPath()));
        }
    }

    /**
     * Deletes in XMP sidecar files a keyword and all it's children.
     *
     * @param keyword keyword
     */
    public static void deleteInFiles(Keyword keyword) {
        new DeleteKeywordInFiles(getPathString(keyword)).start();

    }

    public static class DeleteKeywordInFiles extends Thread {

        private final String keywordPath;
        private final String keywordName;

        public DeleteKeywordInFiles(String keywordPath) {
            this.keywordPath = keywordPath;
            this.keywordName = getLastPathElement(keywordPath);
            setName("Deleting keyword " + keywordPath);
        }

        @Override
        public void run() {
            // FIXME
//            Set<String> filenames = DatabaseImageFiles.INSTANCE.getFilenamesOfDcSubject(keywordName);
//
//            for (String filename : filenames) {
//                Xmp xmp = XmpMetadata.getXmpFromSidecarFileOf(filename);
//
//                if (xmp != null && xmp.removeHierarchicalSubjectPath(keywordPath)) {
//                    XmpMetadata.writeXmpToSidecarFile(xmp, filename);
//                }
//            }
        }
    }

    /**
     * Returns the last element in a keywords path delimited by
     * {@link Xmp#HIER_SUBJECTS_DELIM}.
     *
     * @param  keywordPath path
     * @return      last element (rightmost element)
     */
    public static String getLastPathElement(String keywordPath) {
        StringTokenizer st      = new StringTokenizer(keywordPath, Xmp.HIER_SUBJECTS_DELIM);
        int             count   = st.countTokens();
        String          element = keywordPath;

        for (int i = 0; i < count; i++) {
            element = st.nextToken();
        }

        return element;
    }

    /**
     * Renames the parent keywords in all XMP sidecar files after moving
     * a keyword to the new names and updates the database.
     *
     * @param parentKeywordNamesBeforeMove
     * @param keywordAfterMove
     */
    public static void moveInFiles(List<String> parentKeywordNamesBeforeMove, Keyword keywordAfterMove) {
        // FIXME
//        String       movedKeyword = keywordAfterMove.getName();
//        List<String> oldKeywords  = new ArrayList<String>(parentKeywordNamesBeforeMove);
//        List<String> newKeywords  = getParentKeywordNames(keywordAfterMove, true);
//
//        oldKeywords.add(movedKeyword);
//        Collection<String> filenames = DatabaseImageFiles.INSTANCE.getFilenamesOfAllDcSubjects(oldKeywords);
//        oldKeywords.remove(movedKeyword);
//
//        if (filenames.size() > 0) {
//            new Replace(oldKeywords, newKeywords, filenames).start();
//        }
    }

    /**
     * Renames a keyword in all XMP sidecar files and updates the database.
     *
     * @param oldName old name
     * @param keyword keyword with new name
     */
    public static void renameInFiles(String oldName, Keyword keyword) {
        // FIXME
//        boolean equals = oldName.equalsIgnoreCase(keyword.getName()); assert !equals;
//        if (equals) return;
//        List<String> keywords = getParentKeywordNames(keyword, true);
//
//        keywords.add(oldName);
//        Collection<String> filenames = DatabaseImageFiles.INSTANCE.getFilenamesOfAllDcSubjects(keywords);
//        if (filenames.size() > 0) {
//            new Rename(oldName, keyword.getName(), filenames).start();
//        }
    }

    /**
     * Returns all names of the keyword's parents.
     *
     * @param  keyword keyword
     * @param  real    true if only real keyword names shall be added
     * @return         parent names
     */
    public static List<String> getParentKeywordNames(Keyword keyword, boolean real) {
        List<String>  names   = new ArrayList<String>();
        List<Keyword> parents = DatabaseKeywords.INSTANCE.getParents(keyword);
        for (Keyword parent : parents) {
            boolean add = !real || real && parent.isReal();
            if  (add) {
                names.add(parent.getName());
            }
        }
        return names;
    }

    private static class Rename extends Thread {
        private final String             oldName;
        private final String             newName;
        private final Collection<String> filenames = new ArrayList<String>();

        public Rename(String oldName, String newName, Collection<String> filenames) {
            this.oldName = oldName;
            this.newName = newName;
            this.filenames.addAll(filenames);
            setName("Renaming keywords in files @ " + getClass().getSimpleName());
        }

        @Override
        public void run() {
            // FIXME
//            List<Pair<String, Xmp>> xmps = new ArrayList<Pair<String, Xmp>>();
//            for (String filename : filenames) {
//                Xmp xmp = XmpMetadata.getXmpFromSidecarFileOf(filename);
//                if (xmp != null) {
//                    List<String> keywords = xmp.getDcSubjects();
//                    keywords.remove(oldName);
//                    keywords.add(newName);
//                    xmp.setDcSubjects(keywords);
//                    xmps.add(new Pair<String, Xmp>(filename, xmp));
//                }
//            }
//            SaveXmp.save(xmps);
//            new CheckExists(Arrays.asList(oldName)).run(); // No separate thread
        }
    }

    private static class Replace extends Thread {
        private final List<String>       toReplace   = new ArrayList<String>();
        private final List<String>       replacement = new ArrayList<String>();
        private final Collection<String> filenames   = new ArrayList<String>();

        public Replace(Collection<String> toReplace, Collection<String> replacement, Collection<String> filenames) {
            this.toReplace.addAll(toReplace);
            this.replacement.addAll(replacement);
            this.filenames.addAll(filenames);
            setName("Replacing keywords in files @ " + getClass().getSimpleName());
        }

        @Override
        public void run() {
            // FIXME
//            List<Pair<String, Xmp>> xmps = new ArrayList<Pair<String, Xmp>>();
//            for (String filename : filenames) {
//                Xmp xmp = XmpMetadata.getXmpFromSidecarFileOf(filename);
//                if (xmp != null) {
//                    List<String> keywords = xmp.getDcSubjects();
//                    keywords.removeAll(toReplace);
//                    keywords.addAll(replacement);
//                    xmp.setDcSubjects(keywords);
//                    xmps.add(new Pair<String, Xmp>(filename, xmp));
//                }
//            }
//            SaveXmp.save(xmps);
//            new CheckExists(toReplace).run(); // No separate thread
        }
    }

    private static class CheckExists extends Thread {
        private final List<String> keywords = new ArrayList<String>();

        public CheckExists(Collection<String> keywords) {
            this.keywords.addAll(keywords);
            setName("Checking keywords existence in keywords tree @ " + getClass().getSimpleName());
        }

        @Override
        public void run() {
            for (String keyword : keywords) {
                if (!DatabaseKeywords.INSTANCE.exists(keyword) &&
                    DatabaseImageFiles.INSTANCE.exists(ColumnKeyword.INSTANCE, keyword)
                    ) {
                    TreeModelKeywords model = ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);
                    model.insert((DefaultMutableTreeNode) model.getRoot(), keyword, true);
                }
            }
        }
    }

    public static void addHighlightKeywords(Collection<String> keywords) {
        for (TreeCellRendererKeywords r : getCellRenderer()) {
            r.addHighlightKeywords(keywords);
        }
    }

    public static void removeHighlightKeyword(String keyword) {
        for (TreeCellRendererKeywords r : getCellRenderer()) {
            r.removeHighlightKeyword(keyword);
        }
    }

    private static List<TreeCellRendererKeywords> getCellRenderer() {
        List<TreeCellRendererKeywords> renderer = new ArrayList<TreeCellRendererKeywords>();

        for (JTree tree : getKeywordTrees()) {
            TreeCellRenderer r = tree.getCellRenderer();
            if (r instanceof TreeCellRendererKeywords) {
                renderer.add((TreeCellRendererKeywords) r);
            }
        }
        return renderer;
    }

    private static List<JTree> getKeywordTrees() {
        List<JTree> trees = new ArrayList<JTree>();

        trees.add(GUI.INSTANCE.getAppPanel().getTreeEditKeywords());
        trees.add(GUI.INSTANCE.getAppPanel().getTreeSelKeywords());
        trees.add(InputHelperDialog.INSTANCE.getPanelKeywords().getTree());

        return trees;
    }

    public static void insertHierarchicalSubjects(TreeModelKeywords model, String hSubjects) {
        DefaultMutableTreeNode rootNode             = (DefaultMutableTreeNode) model.getRoot();
        List<String>           hierarchicalSubjects = getHierarchicalSubjectsFromString(hSubjects);
        
        if (TreeUtil.existsPathBelow(rootNode, hierarchicalSubjects, true)) return;

        DefaultMutableTreeNode node  = TreeUtil.getBestMatchingNodeBelow(rootNode, hierarchicalSubjects, true);
        int                    level = node.getLevel();
        int                    size  = hierarchicalSubjects.size();

        logInsertHierarchicalSubjects(level, size, hSubjects);
        for (int i = level; i < size; i++) {
            String subject = hierarchicalSubjects.get(i);
            node = model.insert(node, subject, true);
            assert node != null;
            if (node == null) return;
        }
    }

    private static void logInsertHierarchicalSubjects(int level, int size, String hSubjects) {
        if (level > size) return;
        AppLogger.logFine(KeywordsHelper.class, "KeywordsHelper.Info.InsertHierarchicalSubjects", hSubjects);
    }

    public static List<String> getHierarchicalSubjectsFromString(String hSubject) {
        List<String>    hSubjects = new ArrayList<String>();
        StringTokenizer st        = new StringTokenizer(hSubject, Xmp.HIER_SUBJECTS_DELIM);

        while (st.hasMoreTokens()) {
            hSubjects.add(st.nextToken().trim());
        }

        return hSubjects;
    }

    public static String getHierarchicalSubjectsFromList(List<String> hSubjects) {
        StringBuilder sb    = new StringBuilder();
        int           size  = hSubjects.size();

        for (int i = 0; i < size; i++) {
            sb.append(hSubjects.get(i));
            sb.append(i == size - 1 ? "" : Xmp.HIER_SUBJECTS_DELIM);
        }

        return sb.toString();
    }

    /**
     * Returns the path: The names from the root to a keyword delimited by
     * {@link Xmp#HIER_SUBJECTS_DELIM}.
     *
     * @param  keyword keyword
     * @return         hierarchical path
     */
    public static String getPathString(Keyword keyword) {
        List<Keyword> parents = DatabaseKeywords.INSTANCE.getParents(keyword);
        List<String>  path    = new ArrayList<String>(parents.size() + 1);

        Collections.reverse(parents);

        for (Keyword parent : parents) {
            path.add(parent.getName());
        }

        path.add(keyword.getName());

        return getHierarchicalSubjectsFromList(path);
    }

    private KeywordsHelper() {
    }
}
