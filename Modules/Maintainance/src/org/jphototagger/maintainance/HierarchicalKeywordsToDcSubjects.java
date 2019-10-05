package org.jphototagger.maintainance;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicInteger;
import org.jphototagger.domain.repository.KeywordsRepository;
import org.jphototagger.lib.concurrent.HelperThread;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class HierarchicalKeywordsToDcSubjects extends HelperThread {

    private final AtomicInteger count = new AtomicInteger(0);
    private ActionListener actionListener;

    public HierarchicalKeywordsToDcSubjects() {
        super("JPhotoTagger: Transferring hierarchical keywords to Dublin Core subjects");
        setInfo(Bundle.getString(HierarchicalKeywordsToDcSubjects.class, "HierarchicalKeywordsToDcSubjects.Info"));
    }

    @Override
    public void run() {
        KeywordsRepository repo = Lookup.getDefault().lookup(KeywordsRepository.class);
        count.set(repo.hierarchicalKeywordsToDcSubjects());
        if (actionListener != null) {
            actionListener.actionPerformed(new ActionEvent(this, 0, ""));
        }
    }

    public int getCount() {
        return count.get();
    }

    /**
     * Actionlistener which will be notified on termination with this instance
     * as source.
     *
     * @param listener null allowed (no notification - is the default)
     */
    public void addActionListener(ActionListener listener) {
        this.actionListener = listener;
    }

    @Override
    public void cancel() {
        // ignore, because it's an atomar database process which shouldn't be interrupted
    }
}
