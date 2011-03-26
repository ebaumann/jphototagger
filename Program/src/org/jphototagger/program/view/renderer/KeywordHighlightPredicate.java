package org.jphototagger.program.view.renderer;

import java.awt.Color;
import java.io.File;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.awt.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.database.DatabaseImageFiles;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class KeywordHighlightPredicate implements HighlightPredicate, ThumbnailsPanelListener {
    private List<String> keywordsOfSelectedImage = new ArrayList<String>();
    private static final Highlighter HIGHLIGHTER = createHighlighter();

    public KeywordHighlightPredicate() {
        listen();
    }

    private void listen() {
        ThumbnailsPanel thumbnailsPanel = GUI.getThumbnailsPanel();

        thumbnailsPanel.addThumbnailsPanelListener(this);
    }

    private static Highlighter createHighlighter() {
        Color background = AppLookAndFeel.LIST_SEL_IMG_HAS_KEYWORD_BACKGROUND;
        Color foreground = AppLookAndFeel.LIST_SEL_IMG_HAS_KEYWORD_FOREGROUND;
        KeywordHighlightPredicate predicate = new KeywordHighlightPredicate();

        ColorHighlighter highlighter = new ColorHighlighter(predicate, background, foreground);

        return highlighter;
    }

    public static Highlighter getHighlighter() {
        return HIGHLIGHTER;
    }

    @Override
    public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
        boolean isTemporarySelection = isTemporarySelection(renderer, adapter.row);

        if (isTemporarySelection) {
            return false;
        }

        Object value = adapter.getValue();

        return (value == null)
               ? false
               : keywordsOfSelectedImage.contains(value.toString());
    }

    private boolean isTemporarySelection(Component renderer, int row) {
        if (renderer instanceof ListCellRendererKeywords) {
            int tempSelectionRow = ((ListCellRendererKeywords) renderer).getTempSelectionRow();

            return tempSelectionRow == row;
        }

        return false;
    }

    @Override
    public void thumbnailsSelectionChanged() {
        ThumbnailsPanel thumbnailsPanel = GUI.getThumbnailsPanel();
        List<File> selectedFiles = thumbnailsPanel.getSelectedFiles();

        keywordsOfSelectedImage.clear();

        if (selectedFiles.size() == 1) {
            Collection<String> keywordsOfSelectedFile = DatabaseImageFiles.INSTANCE.getDcSubjectsOf(selectedFiles.get(0));

            keywordsOfSelectedImage.addAll(keywordsOfSelectedFile);
        }
    }

    @Override
    public void thumbnailsChanged() {

        // ignore
    }
}
