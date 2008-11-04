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
    File file;
    String alias;
    String parameters;
    int sequenceNumber = Integer.MIN_VALUE;

    public Program() {
    }

    public Program(File file, String alias) {
        this.file = file;
        this.alias = alias;
    }

    public Program(File file, String alias, String parameters) {
        this.file = file;
        this.alias = alias;
        this.parameters = parameters;
    }

    public Program(File file, String alias, String parameters, int sequenceNumber) {
        this.file = file;
        this.alias = alias;
        this.parameters = parameters;
        this.sequenceNumber = sequenceNumber;
    }

    public Program(long id, File file, String alias, String parameters, Integer sequenceNumber) {
        this.id = id;
        this.file = file;
        this.alias = alias;
        this.parameters = parameters;
        this.sequenceNumber = sequenceNumber = Integer.MIN_VALUE;
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

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
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
