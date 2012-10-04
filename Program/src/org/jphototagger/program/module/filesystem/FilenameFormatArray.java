package org.jphototagger.program.module.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.jphototagger.domain.templates.RenameTemplate;

/**
 * @author Elmar Baumann
 */
public final class FilenameFormatArray {

    private final List<FilenameFormat> formats = new ArrayList<FilenameFormat>();

    /**
     * Adds a format. {@code #format()} returns the filename built in the same order of the calls to this function.
     *
     * @param format format
     */
    public void addFormat(FilenameFormat format) {
        if (format == null) {
            throw new NullPointerException("format == null");
        }
        synchronized (formats) {
            formats.add(format);
        }
    }

    /**
     * Calls to every format {@code FilenameFormat#next()}
     */
    public void notifyNext() {
        synchronized (formats) {
            for (FilenameFormat format : formats) {
                format.next();
            }
        }
    }

    /**
     * Removes all formats
     */
    public void clear() {
        synchronized (formats) {
            formats.clear();
        }
    }

    /**
     * Returns the formatted filename: the appended strings of all formats
     * ({@code FilenameFormat#format()}).
     *
     * @return filename
     */
    public String format() {
        StringBuilder sb = new StringBuilder();

        synchronized (formats) {
            for (FilenameFormat format : formats) {
                sb.append(format.format());
            }
        }

        return sb.toString();
    }

    /**
     * Sets a file to all formats.
     *
     * @param file file
     */
    public void setFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        synchronized (formats) {
            for (FilenameFormat format : formats) {
                format.setFile(file);
            }
        }
    }

    public static FilenameFormatArray createFormatArrayFromRenameTemplate(RenameTemplate template)
            throws InstantiationException, IllegalAccessException {
        if (template == null) {
            throw new NullPointerException("template == null");
        }
        FilenameFormatArray array = new FilenameFormatArray();
        FilenameFormat formatAtBegin = createFormat(template.getFormatClassAtBegin(), template);
        formatAtBegin.setFormat(template.getTextAtBegin());
        array.addFormat(formatAtBegin);
        array.addFormat(new FilenameFormatConstantString(template.getDelimiter1()));
        FilenameFormat formatInTheMiddle = createFormat(template.getFormatClassInTheMiddle(), template);
        formatInTheMiddle.setFormat(template.getTextInTheMiddle());
        array.addFormat(formatInTheMiddle);
        array.addFormat(new FilenameFormatConstantString(template.getDelimiter2()));
        FilenameFormat formatAtEnd = createFormat(template.getFormatClassAtEnd(), template);
        formatAtEnd.setFormat(template.getTextAtEnd());
        array.addFormat(formatAtEnd);
        array.addFormat(new FilenameFormatFilenamePostfix());
        return array;
    }

    private static FilenameFormat createFormat(Class<?> clazz, RenameTemplate template) throws InstantiationException, IllegalAccessException {
        Object instance = clazz.newInstance();
        if (!(instance instanceof FilenameFormat)) {
            throw new IllegalStateException("Illegal filename format class: " + clazz);
        }
        FilenameFormat format = (FilenameFormat) instance;
        if (format instanceof FilenameFormatNumberSequence) {
            FilenameFormatNumberSequence f = (FilenameFormatNumberSequence) format;
            f.setStart(template.getStartNumber());
            f.setIncrement(template.getStepWidth());
            f.setCountDigits(template.getNumberCount());
        } else if (format instanceof FilenameFormatDate) {
            FilenameFormatDate f = (FilenameFormatDate) format;
            f.setDelimiter(template.getDateDelimiter());
        }
        return format;
    }
}
