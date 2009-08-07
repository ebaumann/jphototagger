package de.elmar_baumann.imv.exporter;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

/**
 * Imports keywords exported by <strong>Adobe Photoshop Lightroom</strong>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-01
 */
final class HierarchicalKeywordsExporterLightroom
        implements HierarchicalKeywordsExporter {

    public static final HierarchicalKeywordsExporterLightroom INSTANCE =
            new HierarchicalKeywordsExporterLightroom();
    private static final Icon icon = AppIcons.getIcon("icon_lightroom.png");
    /**
     * Lightroom exports keywords within {} - constant if changed in later
     * Lightroom versions
     */
    private static final String CHILD_START_CHAR = "\t";

    @Override
    public void export(File file) {
        TreeModel tm = GUI.INSTANCE.getAppPanel().getTreeHierarchicalKeywords().
                getModel();
        assert tm instanceof TreeModelHierarchicalKeywords :
                "Not a TreeModelHierarchicalKeywords: " + tm;
        if (tm instanceof TreeModelHierarchicalKeywords) {
            Writer writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(file));
                addChildrenToRoot((DefaultMutableTreeNode) tm.getRoot(), writer);
            } catch (Exception ex) {
                AppLog.logSevere(getClass(), ex);
            } finally {
                try {
                    writer.close();
                } catch (Exception ex) {
                    AppLog.logSevere(getClass(), ex);
                }
            }
        }
    }

    private void addChildrenToRoot(DefaultMutableTreeNode root, Writer writer)
            throws IOException {
        for (Enumeration e = root.children(); e.hasMoreElements();) {
            Object el = e.nextElement();
            assert el instanceof DefaultMutableTreeNode :
                    "Not a DefaultMutableTreeNode: " + el;

            if (el instanceof DefaultMutableTreeNode) {
                addChildren((DefaultMutableTreeNode) el, 0, writer);
            }
        }
    }

    private void addChildren(
            DefaultMutableTreeNode node, int level, Writer writer)
            throws IOException {
        Object userObject = node.getUserObject();
        assert userObject instanceof HierarchicalKeyword :
                "Not a HierarchicalKeyword: " + userObject;

        boolean appended = false;
        if (userObject instanceof HierarchicalKeyword) {
            HierarchicalKeyword hkw = (HierarchicalKeyword) userObject;
            if (hkw.isReal()) {
                writer.append(getLevelPrefix(level) + hkw.getKeyword() + "\n");
                appended = true;
            }
        }
        for (Enumeration e = node.children(); e.hasMoreElements();) {
            addChildren(
                    (DefaultMutableTreeNode) e.nextElement(),
                    appended
                    ? level + 1
                    : level,
                    writer); // recursive
        }
    }

    private String getLevelPrefix(int level) {
        if (level == 0) return "";
        StringBuilder sb = new StringBuilder(level * CHILD_START_CHAR.length());
        for (int i = 0; i < level; i++) {
            sb.append(CHILD_START_CHAR);
        }
        return sb.toString();
    }

    @Override
    public String getDescription() {
        return Bundle.getString(
                "HierarchicalKeywordsExporterLightroom.Description");
    }

    @Override
    public String toString() {
        return getDescription();
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    private HierarchicalKeywordsExporterLightroom() {
    }

    @Override
    public FileFilter getFileFilter() {
        return new FileNameExtensionFilter(getDescription(), "txt");
    }
}
