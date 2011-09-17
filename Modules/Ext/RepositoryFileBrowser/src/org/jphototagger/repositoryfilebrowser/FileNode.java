package org.jphototagger.repositoryfilebrowser;

import java.io.File;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jphototagger.lib.nodes.AbstractNode;
import org.jphototagger.lib.swing.IconUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class FileNode extends AbstractNode {

    public static final ImageIcon SMALL_ICON = IconUtil.getImageIcon(FileNode.class, "icon_file.png");
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
    public Icon getSmallIcon() {
        return SMALL_ICON;
    }

    @Override
    public String getDisplayName() {
        String pattern = "{0} [{1}]";

        return MessageFormat.format(pattern, file.getName(), file.getAbsolutePath());
    }

    @Override
    public String getHtmlDisplayName() {
        StringBuilder pattern = new StringBuilder("<html>");

        pattern.append("<span style=\"background-color:#EAF5FF\">").append("{0}").append("</span>").append("&nbsp;&nbsp;&nbsp;").append("[{1}]").append("</html>");

        return MessageFormat.format(pattern.toString(), file.getName(), file.getAbsolutePath());
    }
}
