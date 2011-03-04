package org.jphototagger.program.data;

import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.io.RuntimeUtil;

import java.io.File;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * External program to start within the application. It is written persistent
 * into the database {@link DatabasePrograms}.
 *
 * @author Elmar Baumann
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class Program {
    @XmlTransient
    private static final String EMPTY = "";
    private long id = Long.MIN_VALUE;
    private boolean action = false;
    private File file;
    private String alias;
    private String parametersBeforeFilename;
    private String parametersAfterFilename;
    private String pattern;
    private boolean usePattern;
    private boolean inputBeforeExecute;
    private boolean inputBeforeExecutePerFile;
    private boolean singleFileProcessing;
    private boolean changeFile;
    private int sequenceNumber = Integer.MIN_VALUE;

    public Program() {}

    public Program(File file, String alias) {
        this.file = file;
        this.alias = alias;
    }

    public Program(Program program) {
        if (program == null) {
            throw new NullPointerException("program == null");
        }

        set(program);
    }

    public void set(Program other) {
        if (other == null) {
            throw new NullPointerException("program == null");
        }

        if (other != this) {
            this.id = other.id;
            this.action = other.action;
            this.alias = other.alias;
            this.changeFile = other.changeFile;
            this.file = other.file;
            this.inputBeforeExecute = other.inputBeforeExecute;
            this.inputBeforeExecutePerFile = other.inputBeforeExecutePerFile;
            this.parametersAfterFilename = other.parametersAfterFilename;
            this.parametersBeforeFilename = other.parametersBeforeFilename;
            this.pattern = other.pattern;
            this.sequenceNumber = other.sequenceNumber;
            this.singleFileProcessing = other.singleFileProcessing;
            this.usePattern = other.usePattern;
        }
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

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isUsePattern() {
        return usePattern;
    }

    public void setUsePattern(boolean usePattern) {
        this.usePattern = usePattern;
    }

    public String getCommandlineParameters(List<File> files, String additionalParameters,
            boolean additionalParametersBeforeFilenames) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }

        if (additionalParameters == null) {
            throw new NullPointerException("additionalParameters == null");
        }

        String sep = RuntimeUtil.getDefaultCommandLineSeparator();
        String parametersBefore = ((parametersBeforeFilename == null)
                                   ? EMPTY
                                   : parametersBeforeFilename) + (additionalParametersBeforeFilenames
                ? sep + additionalParameters
                : EMPTY);
        String parametersAfter = ((parametersAfterFilename == null)
                                  ? EMPTY
                                  : parametersAfterFilename) + (additionalParametersBeforeFilenames
                ? EMPTY
                : sep + additionalParameters);

        return parametersBefore + sep + RuntimeUtil.quoteForCommandLine(files) + sep + parametersAfter;
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

        // Never change that (will be used to find model items)!
        return alias;
    }
}
