package org.jphototagger.repositoryfilebrowser;

import java.io.File;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;

import org.jphototagger.lib.nodes.AbstractNode;

/**
 * @author Elmar Baumann
 */
public final class FileNode extends AbstractNode {

    private static final MessageFormat DISPLAY_NAME_FORMAT = new MessageFormat("{0} [{1}]");
    private static final MessageFormat HTML_DISPLAY_NAME_FORMAT = new MessageFormat(createHtmlDisplayNamePattern());
    private final File file;

    public FileNode(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        this.file = file;
    }

    @Override
    public Collection<?> getContent() {
        return Collections.singleton(file);
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return file.getAbsolutePath();
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME_FORMAT.format(new Object[]{file.getName(), file.getAbsolutePath()});
    }

    @Override
    public String getHtmlDisplayName() {
        return HTML_DISPLAY_NAME_FORMAT.format(new Object[]{file.getName(), file.getAbsolutePath()});
    }

    private static String createHtmlDisplayNamePattern() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>")
                .append("<b>")
                .append("{0}")
                .append("</b>")
                .append("&nbsp;&nbsp;&nbsp;")
                .append("[{1}]")
                .append("</html>");
        return sb.toString();
    }
}
