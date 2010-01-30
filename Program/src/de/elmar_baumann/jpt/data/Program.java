/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.data;

import de.elmar_baumann.jpt.database.DatabasePrograms;
import de.elmar_baumann.jpt.io.IoUtil;
import java.io.File;
import java.util.List;

/**
 * External program to start within the application. It is written persistent
 * into the database {@link DatabasePrograms}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-04
 */
public final class Program {

    private              long    id                        = Long.MIN_VALUE;
    private static final String  EMPTY                     = "";
    private              boolean action                    = false;
    private              File    file;
    private              String  alias;
    private              String  parametersBeforeFilename;
    private              String  parametersAfterFilename;
    private              String  pattern;
    private              boolean usePattern;
    private              boolean inputBeforeExecute        = false;
    private              boolean inputBeforeExecutePerFile = false;
    private              boolean singleFileProcessing      = false;
    private              boolean changeFile                = false;
    private              int     sequenceNumber            = Integer.MIN_VALUE;

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

    public String getCommandlineParameters(
            List<File> files,
            String     additionalParameters,
            boolean    additionalParametersBeforeFilenames
            ) {
        String sep = IoUtil.getDefaultCommandLineSeparator();

        String parametersBefore = (parametersBeforeFilename == null
                ? EMPTY
                : parametersBeforeFilename) +
                (additionalParametersBeforeFilenames
                ? sep + additionalParameters
                : EMPTY);
        String parametersAfter = (parametersAfterFilename == null
                ? EMPTY
                : parametersAfterFilename) +
                (additionalParametersBeforeFilenames
                ? EMPTY
                : sep + additionalParameters);

        return parametersBefore + sep + IoUtil.quoteForCommandLine(files) + sep + parametersAfter;
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
        return alias; // Never change that (will be used to find model items)!
    }
}
