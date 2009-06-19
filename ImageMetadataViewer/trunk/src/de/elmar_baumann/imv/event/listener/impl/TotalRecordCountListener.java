package de.elmar_baumann.imv.event.listener.impl;

import de.elmar_baumann.imv.database.DatabaseStatistics;
import de.elmar_baumann.imv.event.DatabaseImageEvent;
import de.elmar_baumann.imv.event.DatabaseListener;
import de.elmar_baumann.imv.event.DatabaseProgramEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;

/**
 * Beobachtet Änderungen in der Datenbank und liefert die Anzahl aller Datensätze
 * danach.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/17
 */
public final class TotalRecordCountListener implements DatabaseListener {

    private final DatabaseStatistics db = DatabaseStatistics.INSTANCE;
    private final List<JLabel> labels = new ArrayList<JLabel>();
    boolean listen = false;

    public TotalRecordCountListener() {
        db.addDatabaseListener(this);
    }

    /**
     * Sets wheter to listen to the database.
     * 
     * @param listen  true if listen. Default: false
     */
    public void setListenToDatabase(boolean listen) {
        this.listen = listen;
    }

    /**
     * Fügt ein Label hinzu. In dieses wird die Anzahl der Datensätze nach
     * Modifikationen der Datenbank geschrieben.
     * 
     * @param label Label
     */
    public void addLabel(JLabel label) {
        labels.add(label);
    }

    /**
     * Entfernt ein Label.
     * 
     * @param label Label
     */
    public void removeLabel(JLabel label) {
        labels.remove(label);
    }

    @Override
    public void actionPerformed(DatabaseImageEvent event) {
        setCount();
    }

    @Override
    public void actionPerformed(DatabaseProgramEvent event) {
        setCount();
    }

    private void setCount() {
        if (listen) {
            long count = db.getTotalRecordCount();
            setLabels(count);
        }
    }

    private void setLabels(Long count) {
        for (JLabel label : labels) {
            label.setText(count.toString());
        }
    }
}
