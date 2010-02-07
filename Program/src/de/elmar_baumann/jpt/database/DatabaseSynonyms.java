/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.database;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.event.DatabaseSynonymsEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseSynonymsListener;
import de.elmar_baumann.jpt.event.listener.impl.ListenerSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-02-06
 */
public final class DatabaseSynonyms extends Database {

    public static final DatabaseSynonyms                          INSTANCE        = new DatabaseSynonyms();
    private final       ListenerSupport<DatabaseSynonymsListener> listenerSupport = new ListenerSupport<DatabaseSynonymsListener>();

    public int updateSynonymOf(String word, String oldSynonym, String newSynonym) {
        int        count      = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE synonyms SET synonym = ? WHERE word = ? AND synonym = ?");
            stmt.setString(1, newSynonym);
            stmt.setString(2, word);
            stmt.setString(3, oldSynonym);
            logFiner(stmt);
            count = stmt.executeUpdate();
            connection.commit();
            stmt.close();
            if (count > 0) {
                notifyListeners(DatabaseSynonymsEvent.Type.SYNONYM_UPDATED, word, word, oldSynonym, newSynonym);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            count = 0;
            rollback(connection);
        } finally {
            free(connection);
        }
        return count;
    }

    public int updateWord(String oldWord, String newWord) {
        int        count      = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE synonyms SET word = ? WHERE word = ?");
            stmt.setString(1, newWord);
            stmt.setString(2, oldWord);
            logFiner(stmt);
            count = stmt.executeUpdate();
            connection.commit();
            stmt.close();
            if (count > 0) {
                notifyListeners(DatabaseSynonymsEvent.Type.WORD_UPDATED, oldWord, newWord, null, null);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            count = 0;
            rollback(connection);
        } finally {
            free(connection);
        }
        return count;
    }

    public int updateSynonym(String oldSynonym, String newSynonym) {
        int        count      = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE synonyms SET synonym = ? WHERE synonym = ?");
            stmt.setString(1, newSynonym);
            stmt.setString(2, oldSynonym);
            logFiner(stmt);
            count = stmt.executeUpdate();
            connection.commit();
            stmt.close();
            if (count > 0) {
                notifyListeners(DatabaseSynonymsEvent.Type.SYNONYM_UPDATED, null, null, oldSynonym, newSynonym);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            count = 0;
            rollback(connection);
        } finally {
            free(connection);
        }
        return count;
    }

    public boolean exists(String word, String synonym) {
        long       count      = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            String            sql  = "SELECT COUNT(*) FROM synonyms WHERE word = ? AND synonym = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, word);
            stmt.setString(2, synonym);
            logFiner(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getLong(1);
            }
            stmt.close();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            count = 0;
            rollback(connection);
        } finally {
            free(connection);
        }
        return count == 1;
    }

    public int insert(String word, String synonym) {
        if (exists(word, synonym)) return 0;
        int        count      = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO synonyms (word, synonym) VALUES (?, ?)");
            stmt.setString(1, word);
            stmt.setString(2, synonym);
            logFiner(stmt);
            count = stmt.executeUpdate();
            connection.commit();
            stmt.close();
            if (count > 0) {
                notifyListeners(DatabaseSynonymsEvent.Type.SYNONYM_INSERTED, word, word, synonym, synonym);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            count = 0;
            rollback(connection);
        } finally {
            free(connection);
        }
        return count;
    }

    public int delete(String word, String synonym) {
        int        count      = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM synonyms WHERE word = ? AND synonym = ?");
            stmt.setString(1, word);
            stmt.setString(2, synonym);
            logFiner(stmt);
            count = stmt.executeUpdate();
            connection.commit();
            stmt.close();
            if (count > 0) {
                notifyListeners(DatabaseSynonymsEvent.Type.SYNONYM_DELETED, word, word, synonym, synonym);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            count = 0;
            rollback(connection);
        } finally {
            free(connection);
        }
        return count;
    }

    /**
     * Deletes a word and all it's synonyms.
     *
     * @param  word word
     * @return      count of deleted word synonym pairs
     */
    public int deleteWord(String word) {
        int        count      = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM synonyms WHERE word = ?");
            stmt.setString(1, word);
            logFiner(stmt);
            count = stmt.executeUpdate();
            connection.commit();
            stmt.close();
            if (count > 0) {
                notifyListeners(DatabaseSynonymsEvent.Type.WORD_DELETED, word, word, null, null);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            count = 0;
            rollback(connection);
        } finally {
            free(connection);
        }
        return count;
    }

    private String getGetSynonymsOfSql() {
        return "SELECT synonym FROM synonyms WHERE word = ?" +
               " UNION SELECT word FROM synonyms WHERE synonym = ?";
    }

    /**
     * Returns all synonyms of a word.
     *
     * @param  word word
     * @return      synonyms or empty set
     */
    public Set<String> getSynonymsOf(String word) {
        Set<String> synonyms   = new HashSet<String>();
        Connection  connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(getGetSynonymsOfSql());
            stmt.setString(1, word);
            stmt.setString(2, word);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                synonyms.add(rs.getString(1));
            }
            stmt.close();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
        } finally {
            free(connection);
        }
        return synonyms;
    }

    public Set<String> getAllWords() {
        Set<String> words = new HashSet<String>();
        Connection  connection = null;
        try {
            connection = getConnection();
            String    sql  = "SELECT DISTINCT word FROM synonyms";
            Statement stmt = connection.createStatement();
            logFinest(sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                words.add(rs.getString(1));
            }
            stmt.close();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            words.clear();
        } finally {
            free(connection);
        }
        return words;
    }


    public void addListener(DatabaseSynonymsListener listener) {
        listenerSupport.add(listener);
    }

    public void removeListener(DatabaseSynonymsListener listener) {
        listenerSupport.remove(listener);
    }

    private void notifyListeners(
            DatabaseSynonymsEvent.Type type,
            String oldWord,
            String newWord,
            String oldSynonym,
            String newSynonym
            ) {

        DatabaseSynonymsEvent         evt       = new DatabaseSynonymsEvent(type);
        Set<DatabaseSynonymsListener> listeners = listenerSupport.get();

        evt.setWord(newWord);
        evt.setOldWord(oldWord);
        evt.setSynonym(newSynonym);
        evt.setOldSynonym(oldSynonym);

        synchronized (listeners) {
            for (DatabaseSynonymsListener listener : listeners) {
                listener.actionPerformed(evt);
            }
        }
    }
}
