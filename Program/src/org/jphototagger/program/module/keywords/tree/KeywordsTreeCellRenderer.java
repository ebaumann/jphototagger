package org.jphototagger.program.module.keywords.tree;

import java.awt.Color;
import java.awt.Component;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.CommonPreferences;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.domain.metadata.keywords.Keyword;
import org.jphototagger.domain.repository.DcSubjectsStatistics;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.resources.Icons;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class KeywordsTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;
    private static final Icon ICON_REAL = Icons.getIcon("icon_keyword.png");
    private static final Icon ICON_HELPER = Icons.getIcon("icon_folder.png");
    private static final Color TREE_FOREGROUND = AppLookAndFeel.getTreeForeground();
    private static final Color TREE_BACKGROUND = AppLookAndFeel.getTreeBackground();
    private static final Color TREE_SELECTION_FOREGROUND = AppLookAndFeel.getTreeSelectionForeground();
    private static final Color TREE_SELECTION_BACKGROUND = AppLookAndFeel.getTreeSelectionBackground();
    private static final DcSubjectsStatistics STATISTICS = Lookup.getDefault().lookup(DcSubjectsStatistics.class);
    private boolean isDisplayCount = Lookup.getDefault().lookup(Preferences.class).getBoolean(CommonPreferences.KEY_DISPLAY_DC_SUBJECT_COUNT);
    private JTree tree;
    private int tempSelectionRow = -1;
    private final Set<String> highlightKeywords = new HashSet<>();

    public KeywordsTreeCellRenderer() {
        setOpaque(true);
        AnnotationProcessor.process(this);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        this.tree = tree;
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false, row, hasFocus);
        render(((DefaultMutableTreeNode) value).getUserObject(), tree, row, sel);

        return this;
    }

    private void render(Object userObject, JTree tree, int row, boolean itemAtIndexIsSelected) {
        boolean selImgHasKeyword = false;
        boolean real = false;
        boolean helper = false;    // to know whether to render root item
        if (userObject instanceof Keyword) {
            Keyword keyword = (Keyword) userObject;
            real = keyword.isReal();
            helper = !real;
            String name = keyword.getName();
            selImgHasKeyword = real && isHighlightKeyword(name);
            setText(isDisplayCount
                    ? name + "   [" + STATISTICS.getImageCountOfDcSubject(name) + "]"
                    : name);
        }
        boolean tempSelRowIsSelected = tempSelectionRow < 0 ? false : tree.isRowSelected(tempSelectionRow);
        boolean tempSelExists = tempSelectionRow >= 0;
        boolean isTempSelRow = row == tempSelectionRow;
        boolean isSelection = isTempSelRow
                || (!tempSelExists && itemAtIndexIsSelected)
                || (tempSelExists && !isTempSelRow && itemAtIndexIsSelected && tempSelRowIsSelected);
        setIcon(real
                ? ICON_REAL
                : helper
                ? ICON_HELPER
                : ICON_REAL);    // Last: Root item
        setForeground(isSelection
                ? TREE_SELECTION_FOREGROUND
                : selImgHasKeyword
                ? AppLookAndFeel.TREE_SEL_IMG_HAS_KEYWORD_FOREGROUND
                : TREE_FOREGROUND);
        setBackground(isSelection
                ? TREE_SELECTION_BACKGROUND
                : selImgHasKeyword
                ? AppLookAndFeel.TREE_SEL_IMG_HAS_KEYWORD_BACKGROUND
                : TREE_BACKGROUND);
    }

    private boolean isHighlightKeyword(Object value) {
        String stringValue = value.toString();
        synchronized (highlightKeywords) {
            for (String keyword : highlightKeywords) {
                if (keyword != null && keyword.equalsIgnoreCase(stringValue)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void setHighlightKeywords(Collection<? extends String> keywordsToHighlight) {
        if (keywordsToHighlight == null) {
            throw new NullPointerException("keywords == null");
        }
        synchronized (this.highlightKeywords) {
            this.highlightKeywords.clear();
            this.highlightKeywords.addAll(keywordsToHighlight);
        }
    }

    public void addSelImgKeywords(Collection<? extends String> keywords) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }
        synchronized (this.highlightKeywords) {
            this.highlightKeywords.addAll(keywords);
        }
    }

    public void removeSelImgKeywords(Collection<? extends String> keywords) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }
        synchronized (this.highlightKeywords) {
            this.highlightKeywords.removeAll(keywords);
        }
    }

    public void addSelImgKeyword(String keyword) {
        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }
        synchronized (highlightKeywords) {
            highlightKeywords.add(keyword);
        }
    }

    public void removeSelImgKeyword(String keyword) {
        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }
        synchronized (highlightKeywords) {
            highlightKeywords.remove(keyword);
        }
    }

    public void setTempSelectionRow(int index) {
        tempSelectionRow = index;
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void preferencesChanged(PreferencesChangedEvent evt) {
        if (evt.isKey(CommonPreferences.KEY_DISPLAY_DC_SUBJECT_COUNT)) {
            isDisplayCount = (boolean) evt.getNewValue();
            if (tree != null) {
                TreeModel tm = tree.getModel();
                if (tm instanceof DefaultTreeModel) {
                    DefaultTreeModel dtm = (DefaultTreeModel) tm;
                    dtm.reload();
                }
            }
        }
    }
}
