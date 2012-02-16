package org.jphototagger.importfiles.subdircreators;

import java.io.File;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.importfiles.SubdirectoryCreateStrategy;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = SubdirectoryCreateStrategy.class)
public final class NoSubdirectoryCreateStrategy implements SubdirectoryCreateStrategy {

    @Override
    public String createSubdirectoryName(File file) {
        return "";
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(DateSubdirectoryCreateStrategy.class, "NoSubdirectoryCreateStrategy.Displayname");
    }

    @Override
    public int getPosition() {
        return 100;
    }
}
