package de.elmar_baumann.imagemetadataviewer.event.listener;

import de.elmar_baumann.imagemetadataviewer.database.Database;
import de.elmar_baumann.imagemetadataviewer.event.DatabaseAction;
import de.elmar_baumann.imagemetadataviewer.event.DatabaseListener;
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
public class TotalRecordCountListener implements DatabaseListener {

    private Database db = Database.getInstance();
    private List<JLabel> labels = new ArrayList<JLabel>();
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
    public void actionPerformed(DatabaseAction action) {
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
