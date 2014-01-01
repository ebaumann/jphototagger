package org.jphototagger.program.module.keywords;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.program.module.keywords.list.KeywordsListCellRenderer;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class KeywordHighlightPredicate implements HighlightPredicate {

    private static final Highlighter HIGHLIGHTER = createHighlighter();
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);
    private final Set<String> keywordsOfSelectedImages = new HashSet<>();
    private final List<File> selectedFiles = new ArrayList<>();

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
        return value == null
                ? false
                : containsKeyword(value.toString());
    }

    private boolean containsKeyword(String keyword) {
        for (String kw : keywordsOfSelectedImages) {
            if (kw != null && kw.equalsIgnoreCase(keyword)) {
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
        selectedFiles.clear();
        selectedFiles.addAll(evt.getSelectedFiles());
        setKeywords();
        }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(XmpUpdatedEvent evt) {
        setKeywords();
    }

    private void setKeywords() {
        keywordsOfSelectedImages.clear();
        for (File file : selectedFiles) {
            keywordsOfSelectedImages.addAll(repo.findDcSubjectsOfImageFile(file));
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
