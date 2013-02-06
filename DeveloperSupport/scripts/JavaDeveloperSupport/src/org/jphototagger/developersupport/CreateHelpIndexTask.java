package org.jphototagger.developersupport;

import java.io.File;
import java.io.PrintWriter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * @author Elmar Baumann
 */
public final class CreateHelpIndexTask extends Task {

    private File helpcontentfile;

    public void setHelpcontentfile(String filepath) {
        this.helpcontentfile = new File(filepath);
    }

    @Override
    public void execute() throws BuildException {
        if (!helpcontentfile.exists()) {
            throw new BuildException("Content file '" + helpcontentfile + "' does not exist");
        }
        File indexfile = new File(helpcontentfile.getParent() + File.separator + "index.html");
        try (PrintWriter writer = new PrintWriter(indexfile, "UTF8")) {
            writer.write(HelpContentUtil.createHtmlIndex(helpcontentfile));
        } catch (Throwable t) {
            throw new BuildException(t);
        }
    }
}
