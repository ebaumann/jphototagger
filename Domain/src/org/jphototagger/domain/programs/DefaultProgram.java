package org.jphototagger.domain.programs;

import org.jphototagger.lib.beansbinding.PropertyChanger;

/**
 * @author Elmar Baumann
 */
public final class DefaultProgram extends PropertyChanger {

    private long idProgram;
    private String programAlias;
    private String filenameSuffix;

    public String getFilenameSuffix() {
        return filenameSuffix;
    }

    public void setFilenameSuffix(String filenameSuffix) {
        Object old = this.filenameSuffix;
        this.filenameSuffix = filenameSuffix;
        firePropertyChange("filenameSuffix", old, this.filenameSuffix);
    }

    public long getIdProgram() {
        return idProgram;
    }

    public void setIdProgram(long idProgram) {
        Object old = this.idProgram;
        this.idProgram = idProgram;
        firePropertyChange("idProgram", old, this.idProgram);
    }

    public String getProgramAlias() {
        return programAlias;
    }

    public void setProgramAlias(String programAlias) {
        Object old = this.programAlias;
        this.programAlias = programAlias;
        firePropertyChange("programAlias", old, this.programAlias);
    }
}
