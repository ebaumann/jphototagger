package org.jphototagger.importfiles.subdircreators;

import java.io.File;
import org.jphototagger.api.file.SubdirectoryCreateStrategy;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = SubdirectoryCreateStrategy.class)
public final class NoSubdirectoryCreateStrategy implements SubdirectoryCreateStrategy {

    @Override
    public String suggestSubdirectoryName(File file) {
        return "";
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(NoSubdirectoryCreateStrategy.class, "NoSubdirectoryCreateStrategy.Displayname");
    }

    @Override
    public int getPosition() {
        return 100;
    }
}
