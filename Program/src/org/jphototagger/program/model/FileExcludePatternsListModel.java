package org.jphototagger.program.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.domain.repository.FileExcludePatternsRepository;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.domain.repository.event.fileexcludepattern.FileExcludePatternDeletedEvent;
import org.jphototagger.domain.repository.event.fileexcludepattern.FileExcludePatternInsertedEvent;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;

/**
 * Element are {@code String}s.
 *
 * Filenames matching these patterns (strings) shall not be handled by
 * <strong>JPhotoTagger</strong>.
 *
 * @author Elmar Baumann
 */
public final class FileExcludePatternsListModel extends DefaultListModel {

    private static final long serialVersionUID = -8337739189362442866L;
    private volatile transient boolean listenToDb = true;
    private List<String> patterns;
    private final FileExcludePatternsRepository fepRepo = Lookup.getDefault().lookup(FileExcludePatternsRepository.class);

    public FileExcludePatternsListModel() {
        addElements();
        AnnotationProcessor.process(this);
    }

    public List<String> getPatterns() {
        return new ArrayList<String>(patterns);
    }

    public void insert(final String pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern == null");
        }

        listenToDb = false;

        String trimmedPattern = pattern.trim();

        if (fepRepo.existsFileExcludePattern(trimmedPattern)) {
            errorMessageExists(trimmedPattern);

            return;
        }

        if (fepRepo.saveFileExcludePattern(trimmedPattern)) {
            addElement(trimmedPattern);
            patterns.add(trimmedPattern);
        } else {
            errorMessageInsert(trimmedPattern);
        }

        listenToDb = true;
    }

    public void delete(final String pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern == null");
        }

        listenToDb = false;

        String trimmedPattern = pattern.trim();

        if (fepRepo.deleteFileExcludePattern(trimmedPattern)) {
            removeElement(trimmedPattern);
            patterns.remove(trimmedPattern);
        } else {
            errorMessageDelete(trimmedPattern);
        }

        listenToDb = true;
    }

    private void addElements() {
        Repository repo = Lookup.getDefault().lookup(Repository.class);

        if (repo == null || !repo.isInit()) {
            return;
        }

        patterns = fepRepo.findAllFileExcludePatterns();

        for (String pattern : patterns) {
            addElement(pattern);
        }
    }

    private void insertPattern(String pattern) {
        addElement(pattern);
        patterns.add(pattern);
    }

    private void deletePattern(String pattern) {
        removeElement(pattern);
        patterns.remove(pattern);
    }

    private void errorMessageDelete(String trimmedPattern) {
        String message = Bundle.getString(FileExcludePatternsListModel.class, "FileExcludePatternsListModel.Error.Delete", trimmedPattern);
        MessageDisplayer.error(null, message);
    }

    private void errorMessageInsert(String trimmedPattern) {
        String message = Bundle.getString(FileExcludePatternsListModel.class, "FileExcludePatternsListModel.Error.InsertPattern.Add", trimmedPattern);
        MessageDisplayer.error(null, message);
    }

    private void errorMessageExists(String trimmedPattern) {
        String message = Bundle.getString(FileExcludePatternsListModel.class, "FileExcludePatternsListModel.Error.InsertPattern.Exists", trimmedPattern);
        MessageDisplayer.error(null, message);
    }

    @EventSubscriber(eventClass = FileExcludePatternInsertedEvent.class)
    public void patternInserted(final FileExcludePatternInsertedEvent evt) {
        if (listenToDb) {
            insertPattern(evt.getPattern());
        }
    }

    @EventSubscriber(eventClass = FileExcludePatternDeletedEvent.class)
    public void patternDeleted(final FileExcludePatternDeletedEvent evt) {
        if (listenToDb) {
            deletePattern(evt.getPattern());
        }
    }
}
