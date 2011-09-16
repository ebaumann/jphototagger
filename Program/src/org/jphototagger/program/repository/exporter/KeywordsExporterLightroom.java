package org.jphototagger.program.repository.exporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.keywords.Keyword;
import org.jphototagger.domain.repository.RepositoryDataExporter;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.io.CharEncoding;
import org.jphototagger.program.io.FilenameSuffixes;
import org.jphototagger.program.model.KeywordsTreeModel;

/**
 * Imports keywords exported by <strong>Adobe Photoshop Lightroom</strong>.
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataExporter.class)
public final class KeywordsExporterLightroom implements RepositoryDataExporter {

    public static final String DEFAULT_FILENAME = "LightroomKeywords.txt";
    public static final String DISPLAY_NAME = Bundle.getString(KeywordsExporterLightroom.class, "KeywordExporterLightroom.DisplayName");
    public static final Icon ICON = AppLookAndFeel.getIcon("icon_lightroom.png");
    public static final int POSITION = 10000;
    /**
     * Lightroom exports keywords within {} - constant if changed in later
     * Lightroom versions
     */
    private static final String CHILD_START_CHAR = "\t";
    public static final FileFilter FILE_FILTER = new FileNameExtensionFilter(DISPLAY_NAME, FilenameSuffixes.LIGHTROOM_KEYWORDS);

    @Override
    public void exportFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        TreeModel tm = ModelFactory.INSTANCE.getModel(KeywordsTreeModel.class);

        if (tm instanceof KeywordsTreeModel) {
            Writer writer = null;
            String suffix = "." + FilenameSuffixes.LIGHTROOM_KEYWORDS;

            try {
                File outFile = file;

                if (!outFile.getName().endsWith(suffix)) {
                    outFile = new File(file.getAbsolutePath() + suffix);
                }

                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile.getAbsolutePath()),
                        CharEncoding.LIGHTROOM_KEYWORDS));
                addChildrenToRoot((DefaultMutableTreeNode) tm.getRoot(), writer);
            } catch (Exception ex) {
                Logger.getLogger(KeywordsExporterLightroom.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    writer.close();
                } catch (Exception ex) {
                    Logger.getLogger(KeywordsExporterLightroom.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void addChildrenToRoot(DefaultMutableTreeNode root, Writer writer) throws IOException {
        for (Enumeration<?> e = root.children(); e.hasMoreElements();) {
            Object el = e.nextElement();

            if (el instanceof DefaultMutableTreeNode) {
                addChildren((DefaultMutableTreeNode) el, 0, writer);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void addChildren(DefaultMutableTreeNode node, int level, Writer writer) throws IOException {
        Object userObject = node.getUserObject();
        boolean appended = false;

        if (userObject instanceof Keyword) {
            Keyword hkw = (Keyword) userObject;

            if (hkw.isReal()) {
                writer.append(getLevelPrefix(level) + hkw.getName() + "\n");
                appended = true;
            }
        }

        for (Enumeration<DefaultMutableTreeNode> e = node.children(); e.hasMoreElements();) {
            addChildren(e.nextElement(), appended
                    ? level + 1
                    : level, writer);    // recursive
        }
    }

    private String getLevelPrefix(int level) {
        if (level == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder(level * CHILD_START_CHAR.length());

        for (int i = 0; i < level; i++) {
            sb.append(CHILD_START_CHAR);
        }

        return sb.toString();
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return ICON;
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getDefaultFilename() {
        return DEFAULT_FILENAME;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    @Override
    public boolean isJPhotoTaggerData() {
        return false;
    }

    @Override
    public int getPosition() {
        return POSITION;
    }
}
