package org.jphototagger.program.module.keywords.tree;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.swingx.JXTree;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * Listens to a {@code ThumbnailsPanel} and highlights in the tree of a  {@code KeywordsPanel} the keywords of the
 * selected image.
 *
 * @author Elmar Baumann
 */
public final class HighlightKeywordsTreeController {

    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);
    private final List<File> selectedFiles = new ArrayList<>();

    public HighlightKeywordsTreeController() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
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
        removeKeywords();
        List<String> keywords = new ArrayList<>();
        for (File file : selectedFiles) {
            keywords.addAll(repo.findDcSubjectsOfImageFile(file));
        }
                setKeywords(GUI.getSelKeywordsTree(), keywords);
                setKeywords(GUI.getEditKeywordsTree(), keywords);
                setKeywords(GUI.getInputHelperKeywordsTree(), keywords);
            }

    private void setKeywords(JTree tree, Collection<String> keywords) {
        TreeCellRenderer treeCellRenderer = tree.getCellRenderer();
        if (treeCellRenderer instanceof JXTree.DelegatingRenderer) {
            treeCellRenderer = ((JXTree.DelegatingRenderer) treeCellRenderer).getDelegateRenderer();
        }
        if (treeCellRenderer instanceof KeywordsTreeCellRenderer) {
            ((KeywordsTreeCellRenderer) treeCellRenderer).setHighlightKeywords(keywords);
            tree.repaint();
        }
    }

    private void removeKeywords() {
        setKeywords(GUI.getEditKeywordsTree(), new ArrayList<String>());
        setKeywords(GUI.getInputHelperKeywordsTree(), new ArrayList<String>());
    }
    }
