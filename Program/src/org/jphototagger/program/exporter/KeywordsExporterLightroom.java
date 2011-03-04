package org.jphototagger.program.exporter;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.Keyword;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.io.CharEncoding;
import org.jphototagger.program.io.FilenameSuffixes;
import org.jphototagger.program.model.TreeModelKeywords;
import org.jphototagger.program.resource.JptBundle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.util.Enumeration;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

/**
 * Imports keywords exported by <strong>Adobe Photoshop Lightroom</strong>.
 *
 * @author Elmar Baumann
 */
public final class KeywordsExporterLightroom implements Exporter {
    public static final KeywordsExporterLightroom INSTANCE = new KeywordsExporterLightroom();
    private static final Icon ICON = AppLookAndFeel.getIcon("icon_lightroom.png");

    /**
     * Lightroom exports keywords within {} - constant if changed in later
     * Lightroom versions
     */
    private static final String CHILD_START_CHAR = "\t";

    @Override
    public void exportFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        TreeModel tm = ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);

        if (tm instanceof TreeModelKeywords) {
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
                AppLogger.logSevere(getClass(), ex);
            } finally {
                try {
                    writer.close();
                } catch (Exception ex) {
                    AppLogger.logSevere(getClass(), ex);
                }
            }
        }
    }

    private void addChildrenToRoot(DefaultMutableTreeNode root, Writer writer) throws IOException {
        for (Enumeration<?> e = root.children(); e.hasMoreElements(); ) {
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

        for (Enumeration<DefaultMutableTreeNode> e = node.children(); e.hasMoreElements(); ) {
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
        return JptBundle.INSTANCE.getString("KeywordExporterLightroom.DisplayName");
    }

    @Override
    public Icon getIcon() {
        return ICON;
    }

    @Override
    public FileFilter getFileFilter() {
        return new FileNameExtensionFilter(getDisplayName(), FilenameSuffixes.LIGHTROOM_KEYWORDS);
    }

    @Override
    public String getDefaultFilename() {
        return "LightroomKeywords.txt";
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    private KeywordsExporterLightroom() {}
}
