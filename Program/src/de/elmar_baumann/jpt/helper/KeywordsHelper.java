/*
 * JPhotoTagger tags and finds images fast
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
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.data.ImageFile;
import de.elmar_baumann.jpt.data.Keyword;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.DatabaseKeywords;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpLastModified;
import de.elmar_baumann.jpt.factory.ModelFactory;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.model.TreeModelKeywords;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.tasks.UserTasks;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.EditMetadataPanels;
import de.elmar_baumann.jpt.view.renderer.TreeCellRendererKeywords;
import de.elmar_baumann.lib.generics.Pair;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.util.ArrayUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JList;
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
            editPanels.addText(ColumnXmpDcSubjectsSubject.INSTANCE, keyword);
        }
        if (keywordStrings.size() > 1) {
            Collections.reverse(keywordStrings); // else leaf is first element
        }
    }

    public static void saveKeywordsToImageFile(List<String> keywordStrings, String imageFilename) {
        if (!FileUtil.existsFile(imageFilename)) return;
        Xmp xmp = XmpMetadata.getXmpFromSidecarFileOf(imageFilename);
        if (xmp == null) xmp = new Xmp();
        for (String keyword : keywordStrings) {
            if (!xmp.containsValue(ColumnXmpDcSubjectsSubject.INSTANCE, keyword)) {
                xmp.setValue(ColumnXmpDcSubjectsSubject.INSTANCE, keyword);
            }
        }
        List<Pair<String, Xmp>> saveList = new ArrayList<Pair<String, Xmp>>();
        saveList.add(new Pair<String, Xmp>(imageFilename, xmp));
        SaveXmp.save(saveList);
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

    public static void addHighlightKeywords(Collection<String> keywords) {
        for (TreeCellRendererKeywords r : getCellRenderer()) {
            r.addSelImgKeywords(keywords);
        }
    }

    public static void removeHighlightKeyword(String keyword) {
        for (TreeCellRendererKeywords r : getCellRenderer()) {
            r.removeSelImgKeyword(keyword);
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

    public static void selectInSelKeywordsList(List<Integer> indices) {

        JList selKeywordsList = GUI.INSTANCE.getAppPanel().getListSelKeywords();

        selKeywordsList.clearSelection();
        GUI.INSTANCE.getAppPanel().displaySelKeywordsList(AppPanel.SelectAlso.SEL_KEYWORDS_TAB);

        if (!indices.isEmpty()) {
            selKeywordsList.setSelectedIndices(ArrayUtil.toIntArray(indices));
            selKeywordsList.ensureIndexIsVisible(indices.get(0));
        }
    }

    /**
     * Renames in the database and all sidecar files a keyword.
     *
     * @param oldName old name
     * @param newName new name
     */
    public static void renameKeyword(String oldName, String newName) {
        boolean valid = oldName != null && newName != null && !oldName.equalsIgnoreCase(newName);
        assert valid;
        if (valid) {
            UserTasks.INSTANCE.add(new RenameKeyword(oldName, newName));
        }
    }

    /**
     * Renames in the database and all sidecar files a keyword.
     *
     * @param keyword keyword
     */
    public static void deleteKeyword(String keyword) {
        boolean valid = keyword != null;
        assert valid;
        if (valid) {
            UserTasks.INSTANCE.add(new DeleteKeyword(keyword));
        }
    }

    private static class RenameKeyword extends HelperThread {
        private final    String  oldName;
        private final    String  newName;
        private volatile boolean stop;

        public RenameKeyword(String oldName, String newName) {
            this.oldName = oldName;
            this.newName = newName;
            setName("Renaming keyword @ " + getClass().getSimpleName());
            setInfo(JptBundle.INSTANCE.getString("KeywordsHelper.Info.Rename"));
        }

        @Override
        public void run() {
            List<String> filenames = new ArrayList<String>(DatabaseImageFiles.INSTANCE.getFilenamesOfDcSubject(oldName));
            logStartRename(oldName, newName);
            progressStarted(0, 0, filenames.size(), null);
            int size = filenames.size();
            int index = 0;
            for (index = 0; !stop && index < size; index++) {
                String filename = filenames.get(index);
                String sidecarFilename = XmpMetadata.suggestSidecarFilename(filename);
                Xmp xmp = XmpMetadata.getXmpFromSidecarFileOf(filename);
                if (xmp != null) {
                    xmp.removeValue(ColumnXmpDcSubjectsSubject.INSTANCE, oldName);
                    xmp.setValue(ColumnXmpDcSubjectsSubject.INSTANCE, newName);
                    updateXmp(xmp, filename, sidecarFilename);
                }
                progressPerformed(index + 1, xmp);
            }
            progressEnded(index);
        }

        private static void logStartRename(String oldName, String newName) {
            AppLogger.logInfo(KeywordsHelper.class, "KeywordsHelper.Info.StartRename", oldName, newName);
        }

        @Override
        protected void stopRequested() {
            stop = true;
        }

    }

    private static class DeleteKeyword extends HelperThread {
        private final    String  keyword;
        private volatile boolean stop;

        public DeleteKeyword(String keyword) {
            this.keyword = keyword;
            setName("Deleting keyword @ " + getClass().getSimpleName());
            setInfo(JptBundle.INSTANCE.getString("KeywordsHelper.Info.Delete"));
        }

        @Override
        public void run() {
            List<String> filenames = new ArrayList<String>(DatabaseImageFiles.INSTANCE.getFilenamesOfDcSubject(keyword));
            logStartDelete(keyword);
            progressStarted(0, 0, filenames.size(), null);
            int size = filenames.size();
            int index = 0;
            for (index = 0; !stop && index < size; index++) {
                String filename = filenames.get(index);
                String sidecarFilename = XmpMetadata.suggestSidecarFilename(filename);
                Xmp xmp = XmpMetadata.getXmpFromSidecarFileOf(filename);
                if (xmp != null) {
                    xmp.removeValue(ColumnXmpDcSubjectsSubject.INSTANCE, keyword);
                    updateXmp(xmp, filename, sidecarFilename);
                }
                progressPerformed(index, xmp);
            }
            progressEnded(index);
        }

        private static void logStartDelete(String keyword) {
            AppLogger.logInfo(KeywordsHelper.class, "KeywordsHelper.Info.StartDelete", keyword);
        }

        @Override
        protected void stopRequested() {
            stop = true;
        }

    }

    private static void updateXmp(Xmp xmp, String filename, String sidecarFilename) {
        if (XmpMetadata.writeXmpToSidecarFile(xmp, sidecarFilename)) {
            ImageFile imageFile = new ImageFile();
            imageFile.setFilename(filename);
            imageFile.setLastmodified(FileUtil.getLastModified(filename));
            xmp.setValue(ColumnXmpLastModified.INSTANCE, FileUtil.getLastModified(sidecarFilename));
            imageFile.setXmp(xmp);
            imageFile.addInsertIntoDb(InsertImageFilesIntoDatabase.Insert.XMP);
            DatabaseImageFiles.INSTANCE.insertOrUpdate(imageFile);
        }
    }

    private KeywordsHelper() {
    }
}
