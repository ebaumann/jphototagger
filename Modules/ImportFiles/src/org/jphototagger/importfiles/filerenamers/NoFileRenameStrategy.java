package org.jphototagger.importfiles.filerenamers;

import java.io.File;
import org.jphototagger.api.file.FileRenameStrategy;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileRenameStrategy.class)
public final class NoFileRenameStrategy implements FileRenameStrategy {

    @Override
    public void init() {
        // nothing to do
    }

    @Override
    public File suggestNewFile(File sourceFile, String targetDirectoryPath) {
        String sourceFileName = sourceFile.getName();
        return new File(targetDirectoryPath + File.separator + sourceFileName);
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(NoFileRenameStrategy.class, "NoFileRenameStrategy.DisplayName");
    }

    @Override
    public int getPosition() {
        return 0;
    }
}
