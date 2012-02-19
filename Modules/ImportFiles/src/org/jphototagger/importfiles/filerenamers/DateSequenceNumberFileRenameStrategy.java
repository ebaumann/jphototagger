package org.jphototagger.importfiles.filerenamers;

import java.io.File;
import java.text.DecimalFormat;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.file.FileRenameStrategy;
import org.jphototagger.importfiles.NameUtil;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileRenameStrategy.class)
public final class DateSequenceNumberFileRenameStrategy implements FileRenameStrategy {

    private static final DecimalFormat SEQUENCE_NUMBER_FORMAT = new DecimalFormat();
    private String lastDateString = "";
    private int sequenceNumber;

    static {
        SEQUENCE_NUMBER_FORMAT.setMinimumIntegerDigits(3);
        SEQUENCE_NUMBER_FORMAT.setGroupingUsed(false);
    }

    @Override
    public void init() {
        sequenceNumber = 0;
        lastDateString = "";
    }

    @Override
    public File suggestNewFile(File sourceFile, String targetDirectoryPath) {
        return findNextFile(sourceFile, targetDirectoryPath);
    }

    private File findNextFile(File sourceFile, String targetDirectoryPath) {
        String dateString = NameUtil.getDateString(sourceFile);
        if (!lastDateString.equals(dateString)) {
            lastDateString = dateString;
            sequenceNumber = 0;
        }
        String filenameSuffix = FileUtil.getSuffix(sourceFile);
        String dirPathname = targetDirectoryPath + File.separator;
        sequenceNumber++;
        String filePathname =
                dirPathname
                + dateString
                + "-"
                + SEQUENCE_NUMBER_FORMAT.format(sequenceNumber)
                + '.'
                + filenameSuffix;
        return new File(filePathname);
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(DateSequenceNumberFileRenameStrategy.class, "DateSequenceNumberFileRenameStrategy.DisplayName");
    }

    @Override
    public int getPosition() {
        return 100;
    }
}
