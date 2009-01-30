package de.elmar_baumann.lib.persistence;

import java.util.LinkedList;
import java.util.Properties;

/**
 * Enthält aktuelle Strings. Nutzung beispielsweise für eine Liste der zuletzt
 * bearbeiteten Dateien.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/11/12
 */
public final class Recent {

    private final LinkedList<String> recentList = new LinkedList<String>();
    private String name = "de.elmar_baumann.settings.Recent"; // NOI18N
    private int maxCount = 5;

    public Recent() {
    }

    /**
     * Konstruktor mit Werten.
     * 
     * @param name     Name zum Erzeugen eines Schlüssels für die Schlüssel zum
     *                 Speichern in einem Hash. Bedingung: Nicht leer. Default:
     *                 <code>de.elmar_baumann.xmpviewer.util.Recent</code>.
     * @param maxCount Maximale Anzahl der zu merkenden Strings.
     *      Bedingung: Größer als Null. Default: 5.
     */
    public Recent(String name, int maxCount) {
        if (!name.isEmpty()) {
            this.name = name;
        }
        if (maxCount > 0) {
            this.maxCount = maxCount;
        }
    }

    /**
     * Setzt die maximale Anzahl der zu merkenden Strings (neu). Ist diese
     * geringer, werden die überzähligen ältesten Strings entfernt.
     * 
     * @param maxCount Maximale Anzahl der zu merkenden Objekte.
     *                 Bedingung: Größer als Null. Default: 5.
     */
    public void setMaxCount(int maxCount) {
        if (maxCount > 0) {
            this.maxCount = maxCount;
        }
        int currentCount = recentList.size();
        if (currentCount > maxCount) {
            int removeCount = currentCount - maxCount;
            for (int index = 0; index < removeCount; index++) {
                recentList.removeLast();
            }
        }
    }

    /**
     * Liefert die maximale Anzahl der gespeicherten Strings.
     * 
     * @return Maximale Anzahl
     */
    public int getMaxCount() {
        return maxCount;
    }

    /**
     * Liefert den Basisnamen für das Generieren von Hash-Schlüsseln.
     * 
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt den Namen neu, der Grundlage für die Schlüssel zum Speichern in
     * Hashes ist.
     * 
     * @param name Name. Bedingung: Nicht leer.
     *             Default: <code>de.elmar_baumann.xmpviewer.util.Recent</code>.
     */
    public void setName(String name) {
        if (!name.isEmpty()) {
            this.name = name;
        }
    }

    /**
     * Setzt einen String als aktuellsten.
     * 
     * @param recent Aktuellster String
     */
    public void setRecent(String recent) {
        int indexOf = recentList.indexOf(recent);
        if (indexOf >= 0) {
            recentList.remove(indexOf);
        }
        recentList.addFirst(recent);
        if (recentList.size() > maxCount) {
            recentList.removeLast();
        }
    }

    /**
     * Entfernt einen String.
     * 
     * @param element Zu entfernender String
     */
    public void remove(String element) {
        recentList.remove(element);
    }

    /**
     * Liefert alle aktuellen Strings.
     * 
     * @return Objekte
     */
    public String[] getRecent() {
        String[] recent = new String[recentList.size()];
        recentList.toArray(recent);
        return recent;
    }

    /**
     * Schreibt die aktuellen Strings in ein Properties-Objekt.
     * 
     * @param properties Properties-Objekt
     */
    public void write(Properties properties) {
        int index = 0;
        for (String recent : recentList) {
            properties.put(name + Integer.toString(index), recent);
            index++;
        }
    }

    /**
     * Liest aus einem Properties-Objekt alle aktuellen Strings.
     * 
     * @param properties Properties-Objekt
     */
    public void read(Properties properties) {
        int index = 0, indexOf = 0;
        String value;
        recentList.clear();
        String key = name + Integer.toString(index);
        while (properties.containsKey(key) && index < maxCount) {
            value = properties.getProperty(key);
            indexOf = recentList.indexOf(value);
            if (indexOf < 0) {
                recentList.add(new String(value));
            }
            index++;
            key = name + Integer.toString(index);
        }
    }
}
