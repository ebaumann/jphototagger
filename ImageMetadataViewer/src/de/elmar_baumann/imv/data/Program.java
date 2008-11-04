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

    String nickname;
    File file;
    String parameters;

    public Program() {
    }

    public Program(String nickname, File file, String parameters) {
        this.nickname = nickname;
        this.file = file;
        this.parameters = parameters;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
}
