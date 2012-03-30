package org.jphototagger.repository.hsqldb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.EventBus;

import org.jphototagger.domain.repository.event.synonyms.SynonymInsertedEvent;
import org.jphototagger.domain.repository.event.synonyms.SynonymOfWordDeletedEvent;
import org.jphototagger.domain.repository.event.synonyms.SynonymOfWordRenamedEvent;
import org.jphototagger.domain.repository.event.synonyms.SynonymRenamedEvent;
import org.jphototagger.domain.repository.event.synonyms.WordDeletedEvent;
import org.jphototagger.domain.repository.event.synonyms.WordRenamedEvent;

/**
 * @author Elmar Baumann
 */
final class SynonymsDatabase extends Database {

    static final SynonymsDatabase INSTANCE = new SynonymsDatabase();
    private static final Logger LOGGER = Logger.getLogger(SynonymsDatabase.class.getName());

    int updateSynonymOfWord(String word, String oldSynonym, String newSynonym) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }
        if (oldSynonym == null) {
            throw new NullPointerException("oldSynonym == null");
        }
        if (newSynonym == null) {
            throw new NullPointerException("newSynonym == null");
        }
        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("UPDATE synonyms SET synonym = ? WHERE word = ? AND synonym = ?");
            stmt.setString(1, newSynonym);
            stmt.setString(2, word);
            stmt.setString(3, oldSynonym);
            LOGGER.log(Level.FINER, stmt.toString());
            count = stmt.executeUpdate();
            con.commit();
            if (count > 0) {
                notifySynonymOfWordRenamed(word, oldSynonym, newSynonym);
            }
        } catch (Exception ex) {
            Logger.getLogger(SynonymsDatabase.class.getName()).log(Level.SEVERE, null, ex);
            count = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return count;
    }

    int updateWord(String oldWord, String newWord) {
        if (oldWord == null) {
            throw new NullPointerException("oldWord == null");
        }
        if (newWord == null) {
            throw new NullPointerException("newWord == null");
        }
        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("UPDATE synonyms SET word = ? WHERE word = ?");
            stmt.setString(1, newWord);
            stmt.setString(2, oldWord);
            LOGGER.log(Level.FINER, stmt.toString());
            count = stmt.executeUpdate();
            con.commit();
            if (count > 0) {
                notifyWordRenamed(oldWord, newWord);
            }
        } catch (Exception ex) {
            Logger.getLogger(SynonymsDatabase.class.getName()).log(Level.SEVERE, null, ex);
            count = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return count;
    }

    int updateSynonym(String oldSynonym, String newSynonym) {
        if (oldSynonym == null) {
            throw new NullPointerException("oldSynonym == null");
        }
        if (newSynonym == null) {
            throw new NullPointerException("newSynonym == null");
        }
        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("UPDATE synonyms SET synonym = ? WHERE synonym = ?");
            stmt.setString(1, newSynonym);
            stmt.setString(2, oldSynonym);
            LOGGER.log(Level.FINER, stmt.toString());
            count = stmt.executeUpdate();
            con.commit();
            if (count > 0) {
                notifySynonymRenamed(oldSynonym, newSynonym);
            }
        } catch (Exception ex) {
            Logger.getLogger(SynonymsDatabase.class.getName()).log(Level.SEVERE, null, ex);
            count = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return count;
    }

    boolean existsWord(String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }
        long count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT COUNT(*) FROM synonyms WHERE word = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, word);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(SynonymsDatabase.class.getName()).log(Level.SEVERE, null, ex);
            count = 0;
            rollback(con);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return count > 0;
    }

    boolean existsSynonym(String word, String synonym) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }
        if (synonym == null) {
            throw new NullPointerException("synonym == null");
        }
        long count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT COUNT(*) FROM synonyms WHERE word = ? AND synonym = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, word);
            stmt.setString(2, synonym);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(SynonymsDatabase.class.getName()).log(Level.SEVERE, null, ex);
            count = 0;
            rollback(con);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return count == 1;
    }

    int insertSynonym(String word, String synonym) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }
        if (synonym == null) {
            throw new NullPointerException("synonym == null");
        }
        if (existsSynonym(word, synonym)) {
            return 0;
        }
        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("INSERT INTO synonyms (word, synonym) VALUES (?, ?)");
            stmt.setString(1, word);
            stmt.setString(2, synonym);
            LOGGER.log(Level.FINER, stmt.toString());
            count = stmt.executeUpdate();
            con.commit();
            if (count > 0) {
                notifySynonymInserted(word, synonym);
            }
        } catch (Exception ex) {
            Logger.getLogger(SynonymsDatabase.class.getName()).log(Level.SEVERE, null, ex);
            count = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return count;
    }

    int deleteSynonym(String word, String synonym) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }
        if (synonym == null) {
            throw new NullPointerException("synonym == null");
        }
        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("DELETE FROM synonyms WHERE word = ? AND synonym = ?");
            stmt.setString(1, word);
            stmt.setString(2, synonym);
            LOGGER.log(Level.FINER, stmt.toString());
            count = stmt.executeUpdate();
            con.commit();
            if (count > 0) {
                notifySynonymOfWordDeleted(word, synonym);
            }
        } catch (Exception ex) {
            Logger.getLogger(SynonymsDatabase.class.getName()).log(Level.SEVERE, null, ex);
            count = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return count;
    }

    /**
     * Deletes a word and all it's synonyms.
     *
     * @param  word word
     * @return      count of deleted word synonym pairs
     */
    int deleteWord(String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }
        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("DELETE FROM synonyms WHERE word = ?");
            stmt.setString(1, word);
            LOGGER.log(Level.FINER, stmt.toString());
            count = stmt.executeUpdate();
            con.commit();
            if (count > 0) {
                notifyWordDeleted(word);
            }
        } catch (Exception ex) {
            Logger.getLogger(SynonymsDatabase.class.getName()).log(Level.SEVERE, null, ex);
            count = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return count;
    }

    private String getGetSynonymsOfSql() {
        return "SELECT synonym FROM synonyms WHERE word = ?"
                + " UNION SELECT word FROM synonyms WHERE synonym = ?"
                + " ORDER BY 1";
    }

    /**
     * Returns all synonyms of a word.
     *
     * @param  word word
     * @return      synonyms or empty set
     */
    Set<String> getSynonymsOfWord(String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }
        Set<String> synonyms = new LinkedHashSet<String>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement(getGetSynonymsOfSql());
            stmt.setString(1, word);
            stmt.setString(2, word);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            while (rs.next()) {
                synonyms.add(rs.getString(1));
            }
        } catch (Exception ex) {
            Logger.getLogger(SynonymsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return synonyms;
    }

    Set<String> getAllWords() {
        Set<String> words = new LinkedHashSet<String>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT DISTINCT word FROM synonyms ORDER BY word";
            stmt = con.createStatement();
            LOGGER.log(Level.FINEST, sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                words.add(rs.getString(1));
            }
        } catch (Exception ex) {
            Logger.getLogger(SynonymsDatabase.class.getName()).log(Level.SEVERE, null, ex);
            words.clear();
        } finally {
            close(rs, stmt);
            free(con);
        }
        return words;
    }

    private void notifySynonymOfWordDeleted(String word, String synonym) {
        EventBus.publish(new SynonymOfWordDeletedEvent(this, word, synonym));
    }

    private void notifyWordDeleted(String word) {
        EventBus.publish(new WordDeletedEvent(this, word));
    }

    private void notifySynonymInserted(String word, String synonym) {
        EventBus.publish(new SynonymInsertedEvent(this, word, synonym));
    }

    private void notifySynonymOfWordRenamed(String word, String oldSynonymName, String newSynonymName) {
        EventBus.publish(new SynonymOfWordRenamedEvent(this, word, oldSynonymName, newSynonymName));
    }

    private void notifySynonymRenamed(String oldSynonymName, String newSynonymName) {
        EventBus.publish(new SynonymRenamedEvent(this, oldSynonymName, newSynonymName));
    }

    private void notifyWordRenamed(String fromName, String toName) {
        EventBus.publish(new WordRenamedEvent(this, fromName, toName));
    }

    private SynonymsDatabase() {
    }
}
