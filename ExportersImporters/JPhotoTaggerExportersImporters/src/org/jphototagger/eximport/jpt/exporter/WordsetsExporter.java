package org.jphototagger.eximport.jpt.exporter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.jphototagger.domain.repository.RepositoryDataExporter;
import org.jphototagger.domain.repository.WordsetsRepository;
import org.jphototagger.domain.wordsets.Wordset;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.xml.bind.XmlObjectExporter;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataExporter.class)
public final class WordsetsExporter implements RepositoryDataExporter {

    public static final String DEFAULT_FILENAME = "JptWordsets.xml";
    public static final String DISPLAY_NAME = Bundle.getString(WordsetsExporter.class, "WordsetsExporter.DisplayName");
    private static final String FILE_FILTER_DESCRIPTION = Bundle.getString(WordsetsExporter.class, "WordsetsExporter.FileFilterDescription");
    private static final String FILE_FILTER_SUFFIX = "xml";
    public static final FileFilter FILE_FILTER = new FileNameExtensionFilter(FILE_FILTER_DESCRIPTION, FILE_FILTER_SUFFIX);
    public static final int POSITION = 130;
    private final WordsetsRepository repo = Lookup.getDefault().lookup(WordsetsRepository.class);

    @Override
    public void exportToFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        File xmlFile = FileUtil.ensureSuffix(file, ".xml");
        try {
            List<Wordset> wordsets = repo.findAll();
            XmlObjectExporter.export(new CollectionWrapper(wordsets), xmlFile);
        } catch (Throwable t) {
            Logger.getLogger(WordsetsExporter.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return ExportPreferences.ICON;
    }

    @Override
    public String getDefaultFilename() {
        return DEFAULT_FILENAME;
    }

    @XmlRootElement
    public static class CollectionWrapper {

        @XmlElementWrapper(name = "Wordsets")
        @XmlElement(type = Wordset.class)
        private final ArrayList<Wordset> collection = new ArrayList<>();

        public CollectionWrapper() {
        }

        public CollectionWrapper(Collection<Wordset> collection) {
            this.collection.addAll(collection);
        }

        public List<Wordset> getCollection() {
            return new ArrayList<>(collection);
        }
    }

    @Override
    public boolean isJPhotoTaggerData() {
        return true;
    }

    @Override
    public int getPosition() {
        return POSITION;
    }
}
