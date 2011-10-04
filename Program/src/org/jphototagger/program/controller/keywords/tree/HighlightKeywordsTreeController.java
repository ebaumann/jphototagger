package org.jphototagger.program.controller.keywords.tree;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.jdesktop.swingx.JXTree;

import org.openide.util.Lookup;

import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.renderer.KeywordsTreeCellRenderer;
import org.jphototagger.xmp.XmpMetadata;

/**
 * Listens to a {@code ThumbnailsPanel} and highlights in the tree
 * of a  {@code KeywordsPanel} the keywords of the selected image.
 *
 * @author Elmar Baumann
 */
public final class HighlightKeywordsTreeController {

    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    public HighlightKeywordsTreeController() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(final ThumbnailsSelectionChangedEvent evt) {
        applyCurrentSelection(evt);
    }

    private void applyCurrentSelection(ThumbnailsSelectionChangedEvent evt) {
        removeKeywords();

        if (evt.getSelectionCount() == 1) {
            List<File> selFiles = evt.getSelectedImageFiles();

            if ((selFiles.size() == 1) && hasSidecarFile(selFiles)) {
                Collection<String> keywords = repo.findDcSubjectsOfImageFile(selFiles.get(0));

                setKeywords(GUI.getEditKeywordsTree(), keywords);
                setKeywords(GUI.getInputHelperKeywordsTree(), keywords);
            }
        }
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

    private boolean hasSidecarFile(List<File> selFile) {
        assert selFile.size() == 1 : "Size < 1: " + selFile.size() + " - " + selFile;

        return XmpMetadata.hasImageASidecarFile(selFile.get(0));
    }
}
