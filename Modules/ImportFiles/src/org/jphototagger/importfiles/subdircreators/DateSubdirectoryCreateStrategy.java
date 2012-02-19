package org.jphototagger.importfiles.subdircreators;

import java.io.File;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.file.SubdirectoryCreateStrategy;
import org.jphototagger.importfiles.NameUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = SubdirectoryCreateStrategy.class)
public final class DateSubdirectoryCreateStrategy implements SubdirectoryCreateStrategy {

    @Override
    public String suggestSubdirectoryName(File file) {
        return NameUtil.getDateString(file);
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(DateSubdirectoryCreateStrategy.class, "DateSubdirectoryCreateStrategy.Displayname");
    }

    @Override
    public int getPosition() {
        return 200;
    }
}
