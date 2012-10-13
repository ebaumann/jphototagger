package org.jphototagger.program.module.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.file.FileRenameStrategy;
import org.jphototagger.api.file.FileRenameStrategyProvider;
import org.jphototagger.domain.repository.RenameTemplatesRepository;
import org.jphototagger.domain.templates.RenameTemplate;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileRenameStrategyProvider.class)
public final class RenameTemplateFileRenameStrategyProvider implements FileRenameStrategyProvider {

    private final RenameTemplatesRepository renameTemplatesRepository = Lookup.getDefault().lookup(RenameTemplatesRepository.class);

    private static class FileRenameStrategyImpl implements FileRenameStrategy {

        private final RenameTemplate renameTemplate;
        private FilenameFormatArray filenameFormatArray;
        private int position;

        private FileRenameStrategyImpl(RenameTemplate renameTemplate) throws InstantiationException, IllegalAccessException {
            if (renameTemplate == null) {
                throw new NullPointerException("renameTemplate == null");
            }
            this.renameTemplate = renameTemplate;
            filenameFormatArray = FilenameFormatArray.createFormatArrayFromRenameTemplate(renameTemplate);
        }

        @Override
        public void init() {
            try {
                filenameFormatArray = FilenameFormatArray.createFormatArrayFromRenameTemplate(renameTemplate);
            } catch (Throwable t) {
                Logger.getLogger(RenameTemplateFileRenameStrategyProvider.class.getName()).log(Level.SEVERE, null, t);
            }
        }

        @Override
        public File suggestNewFile(File sourceFile, String targetDirectoryPath) {
            filenameFormatArray.setFile(sourceFile);
            String newFilename = filenameFormatArray.format();
            filenameFormatArray.notifyNext();
            return new File(targetDirectoryPath + File.separator + newFilename);
        }

        @Override
        public String getDisplayName() {
            return renameTemplate.getName();
        }

        @Override
        public int getPosition() {
            return position;
        }
    }

    @Override
    public Collection<FileRenameStrategy> getFileRenameStrategies() {
        Set<RenameTemplate> renameTemplates = renameTemplatesRepository.findAllRenameTemplates();
        List<FileRenameStrategy> strategies = new ArrayList<>(renameTemplates.size());
        int pos = 10000;
        try {
            for (RenameTemplate template : renameTemplates) {
                FileRenameStrategyImpl strategy = new FileRenameStrategyImpl(template);
                strategy.position = pos;
                pos += 100;
                strategies.add(strategy);
            }
        } catch (Throwable t) {
            Logger.getLogger(RenameTemplateFileRenameStrategyProvider.class.getName()).log(Level.SEVERE, null, t);
        }
        return strategies;
    }
}
