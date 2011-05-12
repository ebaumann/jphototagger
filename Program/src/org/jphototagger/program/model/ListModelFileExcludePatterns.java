package org.jphototagger.program.model;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseFileExcludePatterns;
import org.jphototagger.program.event.listener.DatabaseFileExcludePatternsListener;


import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Element are {@link String}s retrieved through
 * {@link DatabaseFileExcludePatterns#getAll()}.
 *
 * Filenames matching these patterns (strings) shall not be handled by
 * <strong>JPhotoTagger</strong>.
 *
 * @author Elmar Baumann
 */
public final class ListModelFileExcludePatterns extends DefaultListModel
        implements DatabaseFileExcludePatternsListener {
    private static final long serialVersionUID = -8337739189362442866L;
    private volatile transient boolean listenToDb = true;
    private List<String> patterns;

    public ListModelFileExcludePatterns() {
        addElements();
        DatabaseFileExcludePatterns.INSTANCE.addListener(this);
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

        if (DatabaseFileExcludePatterns.INSTANCE.exists(trimmedPattern)) {
            errorMessageExists(trimmedPattern);

            return;
        }

        if (DatabaseFileExcludePatterns.INSTANCE.insert(trimmedPattern)) {
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

        if (DatabaseFileExcludePatterns.INSTANCE.delete(trimmedPattern)) {
            removeElement(trimmedPattern);
            patterns.remove(trimmedPattern);
        } else {
            errorMessageDelete(trimmedPattern);
        }

        listenToDb = true;
    }

    private void addElements() {
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

        patterns = DatabaseFileExcludePatterns.INSTANCE.getAll();

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
        MessageDisplayer.error(null, "ListModelFileExcludePatterns.Error.Delete", trimmedPattern);
    }

    private void errorMessageInsert(String trimmedPattern) {
        MessageDisplayer.error(null, "ListModelFileExcludePatterns.Error.InsertPattern.Add", trimmedPattern);
    }

    private void errorMessageExists(String trimmedPattern) {
        MessageDisplayer.error(null, "ListModelFileExcludePatterns.Error.InsertPattern.Exists", trimmedPattern);
    }

    @Override
    public void patternInserted(final String pattern) {
        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (listenToDb) {
                    insertPattern(pattern);
                }
            }
        });
    }

    @Override
    public void patternDeleted(final String pattern) {
        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (listenToDb) {
                    deletePattern(pattern);
                }
            }
        });
    }
}
