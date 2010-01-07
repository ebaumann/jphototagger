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

import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.data.HierarchicalKeyword;
import de.elmar_baumann.jpt.io.CharEncoding;
import de.elmar_baumann.jpt.io.FilenameSuffixes;
import de.elmar_baumann.jpt.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
    private static final Icon icon = AppLookAndFeel.getIcon("icon_lightroom.png");
    /**
     * Lightroom exports keywords within {} - constant if changed in later
     * Lightroom versions
     */
    private static final String CHILD_START_CHAR = "\t";

    @Override
    public void export(File file) {
        TreeModel tm = GUI.INSTANCE.getAppPanel().getTreeEditKeywords().
                getModel();
        assert tm instanceof TreeModelHierarchicalKeywords :
                "Not a TreeModelHierarchicalKeywords: " + tm;
        if (tm instanceof TreeModelHierarchicalKeywords) {
            Writer writer = null;
            String suffix = "." + FilenameSuffixes.LIGHTROOM_KEYWORDS;
            try {
                if (!file.getName().endsWith(suffix)) {
                    file = new File(file.getAbsolutePath() + suffix);
                }
                writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(file.getAbsolutePath()), CharEncoding.LIGHTROOM_KEYWORDS));
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
        return Bundle.getString("HierarchicalKeywordsExporterLightroom.Description");
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
        return new FileNameExtensionFilter(
                getDescription(), FilenameSuffixes.LIGHTROOM_KEYWORDS);
    }
}
