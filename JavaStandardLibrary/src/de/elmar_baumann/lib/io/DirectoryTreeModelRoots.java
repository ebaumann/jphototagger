package de.elmar_baumann.lib.io;

import de.elmar_baumann.lib.resource.Bundle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Alle Wurzelverzeichnisse des Systems für ein DirectoryTreeModel.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 * @see     de.elmar_baumann.lib.model.TreeModelDirectories
 */
public class DirectoryTreeModelRoots {

    private List<DirectoryTreeModelFile> roots = new ArrayList<DirectoryTreeModelFile>();

    public DirectoryTreeModelRoots() {
        init();
    }

    private void init() {
        File[] fileRoots = File.listRoots();

        for (int index = 0; index < fileRoots.length; index++) {
            //if (FileExists.exists(fileRoots[index])) {
                roots.add(new DirectoryTreeModelFile(fileRoots[index].getAbsolutePath()));
            //}
        }
    }

    /**
     * Liefert die Anzahl der Wurzelverzeichnisse.
     * 
     * @return Anzahl der Wurzelverzeichnisse
     */
    public int getChildCount() {
        return roots.size();
    }

    /**
     * Liefert den Index eines bestimmten Wurzelverzeichnisses.
     * 
     * @param child Wurzelverzeichnis
     * @return      Index oder -1, falls child kein Wurzelverzeichnis ist
     */
    public int getIndexOfChild(Object child) {
        return roots.indexOf(child);
    }

    /**
     * Liefert ein Wurzelverzeichnis mit bestimmten Index.
     * 
     * @param index Index
     * @return      Wurzelverzeichnis
     */
    public DirectoryTreeModelFile getChild(int index) {
        return roots.get(index);
    }

    @Override
    public boolean equals(Object object) {
        return object == this;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public String toString() {
        return Bundle.getString("DirectoryTreeModelRoots.Root");
    }
}
