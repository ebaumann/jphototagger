package org.jphototagger.program.module.keywords;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.program.module.keywords.list.KeywordsListCellRenderer;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class KeywordHighlightPredicate implements HighlightPredicate {

    private List<String> keywordsOfSelectedImage = new ArrayList<String>();
    private static final Highlighter HIGHLIGHTER = createHighlighter();
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    public KeywordHighlightPredicate() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
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
                : containsKeyword(value.toString());
    }

    private boolean containsKeyword(String keyword) {
        for (String keywordOfSelectedImage : keywordsOfSelectedImage) {
            if (keywordOfSelectedImage != null
                    && keywordOfSelectedImage.equalsIgnoreCase(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTemporarySelection(Component renderer, int row) {
        if (renderer instanceof KeywordsListCellRenderer) {
            int tempSelectionRow = ((KeywordsListCellRenderer) renderer).getTempSelectionRow();
            return tempSelectionRow == row;
        }
        return false;
    }

    @EventSubscriber(eventClass = ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(final ThumbnailsSelectionChangedEvent evt) {
        keywordsOfSelectedImage.clear();
        if (evt.getSelectionCount() == 1) {
            List<File> selectedFiles = evt.getSelectedFiles();
            Collection<String> keywordsOfSelectedFile = repo.findDcSubjectsOfImageFile(selectedFiles.get(0));
            keywordsOfSelectedImage.addAll(keywordsOfSelectedFile);
        }
        repaintLists();
    }

    private void repaintLists() {
        repaint(GUI.getSelKeywordsList());
        repaint(GUI.getEditKeywordsList());
        repaint(GUI.getInputHelperKeywordsList());
    }

    private void repaint(Component component) {
        if (component.isShowing()) {
            component.repaint();
        }
    }
}
