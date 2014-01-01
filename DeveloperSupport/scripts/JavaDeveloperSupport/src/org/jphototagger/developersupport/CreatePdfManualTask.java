package org.jphototagger.developersupport;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * @author Elmar Baumann
 */
public final class CreatePdfManualTask extends Task {

    private File helpcontentfile;
    private File pdffile;

    public void setHelpcontentfile(String filepath) {
        this.helpcontentfile = new File(filepath);
    }

    public void setPdffile(String filepath) {
        this.pdffile = new File(filepath);
    }

    @Override
    public void execute() throws BuildException {
        if (!helpcontentfile.exists()) {
            throw new BuildException("Content file '" + helpcontentfile + "' does not exist");
        }
        try {
            log("Creating PDF file '" + pdffile + "' from help contents file '" + helpcontentfile + "'");
            HelpContentUtil.createPdfManual(helpcontentfile, pdffile);
            log("PDF file created: " + pdffile);
        } catch (Throwable t) {
            throw new BuildException(t);
        }
    }
}
