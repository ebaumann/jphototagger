package org.jphototagger.importfiles.subdircreators;

import java.io.File;
import org.jphototagger.api.file.SubdirectoryCreateStrategy;
import org.jphototagger.importfiles.NameUtil;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = SubdirectoryCreateStrategy.class)
public final class YearAndMonthSubdirectoryCreateStrategy implements SubdirectoryCreateStrategy {

    @Override
    public String suggestSubdirectoryName(File file) {
        return getDateDirString(file);
    }

    private static String getDateDirString(File file) {
        String dateString = NameUtil.getDateString(file);
        String[] dateToken = dateString.split("-");
        String yearString = dateToken[0];
        String monthString = dateToken[1];
        return yearString + File.separator + monthString;
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(YearAndMonthSubdirectoryCreateStrategy.class, "YearAndMonthSubdirectoryCreateStrategy.Displayname",
                File.separator);
    }

    @Override
    public int getPosition() {
        return 400;
    }
}
