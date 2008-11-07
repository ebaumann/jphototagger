package de.elmar_baumann.imv.data;

import de.elmar_baumann.imv.database.DatabasePrograms;
import java.io.File;

/**
 * External program to start within the application. It is written persistent 
 * into the database {@link DatabasePrograms}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/04
 */
public class Program {

    private long id = Long.MIN_VALUE;
    private boolean action = false;
    private File file;
    private String alias;
    private String parametersBeforeFilename;
    private String parametersAfterFilename;
    private boolean inputBeforeExecute = false;
    private boolean inputBeforeExecutePerFile = false;
    private boolean singleFileProcessing = false;
    private boolean changeFile = false;
    private int sequenceNumber = Integer.MIN_VALUE;

    public Program() {
    }

    public Program(File file, String alias) {
        this.file = file;
        this.alias = alias;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getParametersAfterFilename() {
        return parametersAfterFilename;
    }

    public void setParametersAfterFilename(String parametersAfterFilename) {
        this.parametersAfterFilename = parametersAfterFilename;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public boolean isAction() {
        return action;
    }

    public void setAction(boolean action) {
        this.action = action;
    }

    public boolean isInputBeforeExecute() {
        return inputBeforeExecute;
    }

    public void setInputBeforeExecute(boolean inputBeforeExecute) {
        this.inputBeforeExecute = inputBeforeExecute;
    }

    public boolean isInputBeforeExecutePerFile() {
        return inputBeforeExecutePerFile;
    }

    public void setInputBeforeExecutePerFile(boolean inputBeforeExecutePerFile) {
        this.inputBeforeExecutePerFile = inputBeforeExecutePerFile;
    }

    public String getParametersBeforeFilename() {
        return parametersBeforeFilename;
    }

    public void setParametersBeforeFilename(String parametersBeforeFilename) {
        this.parametersBeforeFilename = parametersBeforeFilename;
    }

    public boolean isSingleFileProcessing() {
        return singleFileProcessing;
    }

    public void setSingleFileProcessing(boolean singleFileProcessing) {
        this.singleFileProcessing = singleFileProcessing;
    }

    public boolean isChangeFile() {
        return changeFile;
    }

    public void setChangeFile(boolean changeFile) {
        this.changeFile = changeFile;
    }

    public String getCommandlineAfterProgram(
        String filenames,
        String additionalParameters, boolean additionalParametersBeforeFilenames) {

        String parametersBefore = (parametersBeforeFilename == null
            ? "" : parametersBeforeFilename) +
            (additionalParametersBeforeFilenames
            ? " " + additionalParameters : "");
        String parametersAfter = (parametersAfterFilename == null
            ? "" : parametersAfterFilename) +
            (additionalParametersBeforeFilenames
            ? "" : " " + additionalParameters);

        return parametersBefore + " " + filenames + " " + parametersAfter;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Program other = (Program) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return alias;
    }
}
