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

    private List<File> roots = new ArrayList<File>();

    public DirectoryTreeModelRoots() {
        init();
    }

    public boolean add(File newFile) {
        if (!roots.contains(newFile)) {
            roots.add(newFile);
            return true;
        }
        return false;
    }

    private void init() {
        File[] fileRoots = File.listRoots();

        for (int i = 0; i < fileRoots.length; i++) {
            roots.add(fileRoots[i]);
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
    public File getChild(int index) {
        return roots.get(index);
    }

    public boolean remove(File file) {
        return roots.remove(file);
    }

    public boolean replace(File oldFile, File newFile) {
        int index = roots.indexOf(oldFile);
        if (index >= 0) {
            roots.set(index, newFile);
            return true;
        }
        return false;
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
