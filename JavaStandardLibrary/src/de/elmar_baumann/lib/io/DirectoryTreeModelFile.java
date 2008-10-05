package de.elmar_baumann.lib.io;

import de.elmar_baumann.lib.util.DirectoryTreeModelFileComparator;
import java.io.File;
import java.util.Collections;
import java.util.ArrayList;

/**
 * Datei für ein DirectoryTreeModel. Liefert bei Aufruf von <code>toString()</code>
 * gegenüber der Basisklasse nur den Dateinamen und liefert alle Unterverzeichnisse
 * in einer ArrayList, der nicht null sein kann.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/23
 */
public class DirectoryTreeModelFile extends File {

    /**
     * Sortierung der Verzeichnisse.
     */
    public enum SortType {

        none, ascending, descending, ascendingNoCase, descendingNoCase,
    };

    public DirectoryTreeModelFile(String name) {
        super(name);
    }

    /**
     * Liefert alle Unterverzeichnisse <em>nichtrekursiv</em>.
     * 
     * @param sortType Sortierung der Verzeichnisse
     * @return         Unterverzeichnisse
     */
    @SuppressWarnings("unchecked")
    public ArrayList<DirectoryTreeModelFile> getSubDirectories(SortType sortType) {
        File[] listFiles = listFiles(new DirectoryFilter());
        ArrayList<DirectoryTreeModelFile> directories = new ArrayList<DirectoryTreeModelFile>();

        for (int i = 0; listFiles != null && i < listFiles.length; i++) {
            directories.add(new DirectoryTreeModelFile(listFiles[i].getAbsolutePath()));
        }
        Collections.sort(directories, new DirectoryTreeModelFileComparator(sortType));

        return directories;
    }

    @Override
    public String toString() {
        String name = getName();

        // Windows-Laufwerksbuchstaben
        if (name.isEmpty()) {
            name = getAbsolutePath();
            if (name.endsWith("\\")) { // NOI18N
                name = name.substring(0, name.length() - 2) + ":"; // NOI18N
            }
        }

        return name;
    }
}
