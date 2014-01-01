package org.jphototagger.eximport.jpt.importer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import org.jphototagger.domain.repository.RepositoryDataImporter;
import org.jphototagger.domain.repository.WordsetsRepository;
import org.jphototagger.domain.wordsets.Wordset;
import org.jphototagger.eximport.jpt.exporter.WordsetsExporter;
import org.jphototagger.eximport.jpt.exporter.WordsetsExporter.CollectionWrapper;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataImporter.class)
public final class WordsetsImporter implements RepositoryDataImporter {

    private final WordsetsRepository repo = Lookup.getDefault().lookup(WordsetsRepository.class);

    @Override
    public void importFromFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        try {
            WordsetsExporter.CollectionWrapper wrapper = (CollectionWrapper) XmlObjectImporter.importObject(file,
                    WordsetsExporter.CollectionWrapper.class);
            for (Wordset wordset : wrapper.getCollection()) {
                if (!repo.existsWordset(wordset.getName())) {
                    repo.insert(wordset);
                }
            }
        } catch (Throwable t) {
            Logger.getLogger(WordsetsImporter.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return WordsetsExporter.FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return WordsetsExporter.DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return ImportPreferences.ICON;
    }

    @Override
    public String getDefaultFilename() {
        return WordsetsExporter.DEFAULT_FILENAME;
    }

    @Override
    public int getPosition() {
        return WordsetsExporter.POSITION;
    }

    @Override
    public boolean isJPhotoTaggerData() {
        return true;
    }
}
