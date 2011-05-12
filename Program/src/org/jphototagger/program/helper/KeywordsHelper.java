package org.jphototagger.program.helper;

import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.lib.generics.Pair;
import org.jphototagger.lib.util.ArrayUtil;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.ImageFile;
import org.jphototagger.program.data.Keyword;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.DatabaseKeywords;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpLastModified;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.model.TreeModelKeywords;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.tasks.UserTasks;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import org.jphototagger.program.view.renderer.TreeCellRendererKeywords;


import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTree;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.componentutil.ListUtil;

/**
 * Helper for hierarchical keywords and Dublin Core subjects ("flat" keywords).
 * <p>
 * <strong>Keyword</strong> means a hierarchical keyword, <strong>DC (Dublin
 * Core) subject</strong> a "flat" keyword.
 *
 * @author Elmar Baumann
 */
public final class KeywordsHelper {
    private KeywordsHelper() {}

    /**
     * Adds the keyword - contained as user object in a d.m. tree node -
     * and all it's parents to the metadata edit panel.
     *
     * @param node node with keyword. <em>All parents of that node have to be an
     *             instance of {@link DefaultMutableTreeNode}!</em>
     */
    public static void addKeywordsToEditPanel(final DefaultMutableTreeNode node) {
        if (node == null) {
            throw new NullPointerException("node == null");
        }

        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                EditMetadataPanels editPanels = GUI.getAppPanel().getEditMetadataPanels();
                List<String> keywordStrings = getKeywordStrings(node, true);

                for (String keyword : keywordStrings) {
                    editPanels.addText(ColumnXmpDcSubjectsSubject.INSTANCE, keyword);
                }

                if (keywordStrings.size() > 1) {

                    // else leaf is first element
                    Collections.reverse(keywordStrings);
                }
            }
        });
    }

    /**
     * Inserts into the database a dublin core subject if it does not already
     * exist.
     *
     * @param dcSubject subject
     */
    public static void insertDcSubject(String dcSubject) {
        if (!DatabaseImageFiles.INSTANCE.existsDcSubject(dcSubject)) {
            DatabaseImageFiles.INSTANCE.insertDcSubject(dcSubject);
        }
    }

    /**
     * Inserts into the Database a Dublin Core keyword via user input.
     */
    public static void insertDcSubject() {
        String dcSubject = MessageDisplayer.input("KeywordsHelper.Input.InsertDcSubject", "",
                               "KeywordsHelper.Input.InsertDcSubject.Settings");

        if ((dcSubject != null) && checkExistsDcSubject(dcSubject)) {
            if (DatabaseImageFiles.INSTANCE.insertDcSubject(dcSubject)) {
                insertDcSubjectAsKeyword(dcSubject);
            } else {
                MessageDisplayer.error(null, "KeywordsHelper.Error.InsertDcSubject", dcSubject);
            }
        }
    }

    private static void insertDcSubjectAsKeyword(String keyword) {
        if (!DatabaseKeywords.INSTANCE.exists(keyword)) {
            DatabaseKeywords.INSTANCE.insert(new Keyword(null, null, keyword, true));
        }
    }

    private static boolean checkExistsDcSubject(String dcSubject) {
        if (DatabaseImageFiles.INSTANCE.existsDcSubject(dcSubject)) {
            MessageDisplayer.error(null, "KeywordsHelper.Error.DcSubjectExists", dcSubject);

            return false;
        }

        return true;
    }

    public static void saveKeywordsToImageFile(List<String> keywordStrings, File imageFile) {
        if (keywordStrings == null) {
            throw new NullPointerException("keywordStrings == null");
        }

        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (!imageFile.exists()) {
            return;
        }

        Xmp xmp = null;

        try {
            xmp = XmpMetadata.getXmpFromSidecarFileOf(imageFile);
        } catch (IOException ex) {
            Logger.getLogger(KeywordsHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (xmp == null) {
            xmp = new Xmp();
        }

        for (String keyword : keywordStrings) {
            if (!xmp.containsValue(ColumnXmpDcSubjectsSubject.INSTANCE, keyword)) {
                xmp.setValue(ColumnXmpDcSubjectsSubject.INSTANCE, keyword);
            }
        }

        List<Pair<File, Xmp>> saveList = new ArrayList<Pair<File, Xmp>>();

        saveList.add(new Pair<File, Xmp>(imageFile, xmp));
        SaveXmp.save(saveList);
    }

    /**
     * Returns a keyword - contained as user object in a default mutable tree
     * node - and all it's parents.
     *
     * @param node node with keyword. <em>All parents of that node have to be
     *             instances of {@link DefaultMutableTreeNode}!</em>
     * @param real true if only real keywords shall be added
     * @return     all keywords or empty list
     */
    public static List<Keyword> getKeywords(DefaultMutableTreeNode node, boolean real) {
        List<Keyword> list = new ArrayList<Keyword>();
        DefaultMutableTreeNode n = node;

        while (n != null) {
            Object userObject = n.getUserObject();

            if (userObject instanceof Keyword) {
                Keyword keyword = (Keyword) userObject;

                if (!real || (real && keyword.isReal())) {
                    list.add(keyword);
                }
            }

            TreeNode parent = n.getParent();

            n = (parent instanceof DefaultMutableTreeNode)
                ? (DefaultMutableTreeNode) parent
                : null;
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
    public static void selectNode(final JTree tree, final Keyword keyword) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }

        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                TreeModelKeywords model = ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                DefaultMutableTreeNode selNode = null;

                for (Enumeration<DefaultMutableTreeNode> e = root.breadthFirstEnumeration();
                        (selNode == null) && e.hasMoreElements(); ) {
                    DefaultMutableTreeNode node = e.nextElement();
                    Object userObject = node.getUserObject();

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
        });
    }

    /**
     * Returns all names of the keyword's parents.
     *
     * @param  keyword keyword
     * @param  real    true if only real keyword names shall be added
     * @return         parent names
     */
    public static List<String> getParentKeywordNames(Keyword keyword, boolean real) {
        List<String> names = new ArrayList<String>();
        List<Keyword> parents = DatabaseKeywords.INSTANCE.getParents(keyword);

        for (Keyword parent : parents) {
            boolean add = !real || (real && parent.isReal());

            if (add) {
                names.add(parent.getName());
            }
        }

        return names;
    }

    public static void addHighlightKeywords(final Collection<String> keywords) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (TreeCellRendererKeywords treeCellRendererKeywords : getCellRenderer()) {
                    treeCellRendererKeywords.addSelImgKeywords(keywords);
                }
            }
        });
    }

    public static void removeHighlightKeyword(final String keyword) {
        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (TreeCellRendererKeywords treeCellRendererKeywords : getCellRenderer()) {
                    treeCellRendererKeywords.removeSelImgKeyword(keyword);
                }
            }
        });
    }

    private static List<TreeCellRendererKeywords> getCellRenderer() {
        List<TreeCellRendererKeywords> renderer = new ArrayList<TreeCellRendererKeywords>();

        for (JTree tree : getKeywordTrees()) {
            TreeCellRenderer treeCellRenderer = tree.getCellRenderer();

            if (treeCellRenderer instanceof JXTree.DelegatingRenderer) {
                treeCellRenderer = ((JXTree.DelegatingRenderer)treeCellRenderer).getDelegateRenderer();

            }

            if (treeCellRenderer instanceof TreeCellRendererKeywords) {
                renderer.add((TreeCellRendererKeywords) treeCellRenderer);
            }
        }

        return renderer;
    }

    private static List<JTree> getKeywordTrees() {
        return Arrays.<JTree>asList(GUI.getAppPanel().getTreeEditKeywords(), GUI.getAppPanel().getTreeSelKeywords(),
                       InputHelperDialog.INSTANCE.getPanelKeywords().getTree());
    }

    public static void selectInSelKeywordsList(final List<Integer> modelIndices) {
        if (modelIndices == null) {
            throw new NullPointerException("indices == null");
        }

        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                JXList selKeywordsList = GUI.getAppPanel().getListSelKeywords();

                selKeywordsList.clearSelection();
                GUI.getAppPanel().displaySelKeywordsList(AppPanel.SelectAlso.SEL_KEYWORDS_TAB);

                if (!modelIndices.isEmpty()) {
                    List<Integer> listIndices = ListUtil.convertModelIndicesToListIndices(modelIndices, selKeywordsList);
                    
                    selKeywordsList.setSelectedIndices(ArrayUtil.toIntArray(listIndices));
                    selKeywordsList.ensureIndexIsVisible(listIndices.get(0));
                }
            }
        });
    }

    public static List<String> getSelectedKeywordsFromList(JXList keywordsList) {
        if (keywordsList == null) {
            throw new NullPointerException("keywordsList == null");
        }

        List<String> selectedKeywords = new ArrayList<String>();
        ListModel listModel = keywordsList.getModel();
        int[] selectedIndices = keywordsList.getSelectedIndices();

        for (int selectedIndex : selectedIndices) {
            int modelIndex = keywordsList.convertIndexToModel(selectedIndex);
            Object selectedElement = listModel.getElementAt(modelIndex);

            if (selectedElement instanceof String) {
                selectedKeywords.add((String) selectedElement);
            }
        }

        return selectedKeywords;
    }

    public static void expandAllTreesTo(final DefaultMutableTreeNode node) {
        if (node == null) {
            throw new NullPointerException("node == null");
        }

        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (JTree tree : getKeywordTrees()) {
                    TreeUtil.expandPath(tree, new TreePath(node.getPath()));
                }
            }
        });
    }

    /**
     * Renames in the database and all sidecar files a Dublin Core subject.
     *
     * @param fromName old name
     * @param toName   new name
     */
    public static void renameDcSubject(String fromName, String toName) {
        if (fromName == null) {
            throw new NullPointerException("fromName == null");
        }

        if (toName == null) {
            throw new NullPointerException("toName == null");
        }

        assert !fromName.equalsIgnoreCase(toName);

        if (!fromName.equalsIgnoreCase(toName)) {
            UserTasks.INSTANCE.add(new RenameDcSubject(fromName, toName));
        }
    }

    /**
     * Renames in the database and all sidecar files a dublin core subject.
     *
     * @param keyword keyword
     */
    public static void deleteDcSubject(String keyword) {
        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }

        UserTasks.INSTANCE.add(new DeleteDcSubject(keyword));
    }

    private static void updateXmp(Xmp xmp, File imgFile, File sidecarFile) {
        if (XmpMetadata.writeXmpToSidecarFile(xmp, sidecarFile)) {
            ImageFile imageFile = new ImageFile();

            imageFile.setFile(imgFile);
            imageFile.setLastmodified(imgFile.lastModified());
            xmp.setValue(ColumnXmpLastModified.INSTANCE, sidecarFile.lastModified());
            imageFile.setXmp(xmp);
            imageFile.addInsertIntoDb(InsertImageFilesIntoDatabase.Insert.XMP);
            DatabaseImageFiles.INSTANCE.insertOrUpdate(imageFile);
        }
    }

    private static class DeleteDcSubject extends HelperThread {
        private final String dcSubject;
        private volatile boolean cancel;

        DeleteDcSubject(String keyword) {
            super("JPhotoTagger: Deleting keyword");
            this.dcSubject = keyword;
            setInfo(JptBundle.INSTANCE.getString("KeywordsHelper.Info.Delete"));
        }

        @Override
        public void run() {
            List<File> imageFiles =
                new ArrayList<File>(DatabaseImageFiles.INSTANCE.getImageFilesOfDcSubject(dcSubject));

            logStartDelete(dcSubject);
            progressStarted(0, 0, imageFiles.size(), null);

            int size = imageFiles.size();
            int index = 0;

            for (index = 0; !cancel &&!isInterrupted() && (index < size); index++) {
                File imageFile = imageFiles.get(index);
                File sidecarFile = XmpMetadata.suggestSidecarFile(imageFile);
                Xmp xmp = null;

                try {
                    xmp = XmpMetadata.getXmpFromSidecarFileOf(imageFile);
                } catch (IOException ex) {
                    Logger.getLogger(KeywordsHelper.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (xmp != null) {
                    xmp.removeValue(ColumnXmpDcSubjectsSubject.INSTANCE, dcSubject);
                    updateXmp(xmp, imageFile, sidecarFile);
                }

                progressPerformed(index, xmp);
            }

            checkDatabase();
            progressEnded(index);
        }

        private static void logStartDelete(String keyword) {
            AppLogger.logInfo(KeywordsHelper.class, "KeywordsHelper.Info.StartDelete", keyword);
        }

        @Override
        public void cancel() {
            cancel = true;
        }

        private void checkDatabase() {
            if (DatabaseImageFiles.INSTANCE.existsDcSubject(dcSubject)) {
                DatabaseImageFiles.INSTANCE.deleteDcSubject(dcSubject);
            }
        }
    }

    private static class RenameDcSubject extends HelperThread {
        private final String toName;
        private final String fromName;
        private volatile boolean cancel;

        RenameDcSubject(String fromName, String toName) {
            super("JPhotoTagger: Renaming DC subject");
            this.fromName = fromName;
            this.toName = toName;
            setInfo(JptBundle.INSTANCE.getString("KeywordsHelper.Info.Rename"));
        }

        @Override
        public void run() {
            List<File> imageFiles = new ArrayList<File>(DatabaseImageFiles.INSTANCE.getImageFilesOfDcSubject(fromName));

            logStartRename(fromName, toName);
            progressStarted(0, 0, imageFiles.size(), null);

            int size = imageFiles.size();
            int index = 0;

            for (index = 0; !cancel &&!isInterrupted() && (index < size); index++) {
                File imageFile = imageFiles.get(index);
                File sidecarFile = XmpMetadata.suggestSidecarFile(imageFile);
                Xmp xmp = null;

                try {
                    xmp = XmpMetadata.getXmpFromSidecarFileOf(imageFile);
                } catch (IOException ex) {
                    Logger.getLogger(KeywordsHelper.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (xmp != null) {
                    xmp.removeValue(ColumnXmpDcSubjectsSubject.INSTANCE, fromName);
                    xmp.setValue(ColumnXmpDcSubjectsSubject.INSTANCE, toName);
                    updateXmp(xmp, imageFile, sidecarFile);
                }

                progressPerformed(index + 1, xmp);
            }

            deleteKeyword();
            progressEnded(index);
        }

        private void deleteKeyword() {
            DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;

            if (!db.isDcSubjectReferenced(fromName)) {
                db.deleteDcSubject(fromName);
            }
        }

        private static void logStartRename(String fromName, String toName) {
            AppLogger.logInfo(KeywordsHelper.class, "KeywordsHelper.Info.StartRename", fromName, toName);
        }

        @Override
        public void cancel() {
            cancel = true;
        }
    }
}
