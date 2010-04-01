/*
 * @(#)DatabaseSynonyms.java    Created on 2010-02-06
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.database;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.event.listener.DatabaseSynonymsListener;
import org.jphototagger.program.event.listener.impl.ListenerSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class DatabaseSynonyms extends Database {
    public static final DatabaseSynonyms                    INSTANCE =
        new DatabaseSynonyms();
    private final ListenerSupport<DatabaseSynonymsListener> ls       =
        new ListenerSupport<DatabaseSynonymsListener>();

    public int updateSynonymOf(String word, String oldSynonym,
                               String newSynonym) {
        int               count = 0;
        Connection        con   = null;
        PreparedStatement stmt  = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("UPDATE synonyms SET synonym = ?"
                                        + " WHERE word = ? AND synonym = ?");
            stmt.setString(1, newSynonym);
            stmt.setString(2, word);
            stmt.setString(3, oldSynonym);
            logFiner(stmt);
            count = stmt.executeUpdate();
            con.commit();

            if (count > 0) {
                notifySynonymOfWordRenamed(word, oldSynonym, newSynonym);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            count = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return count;
    }

    public int updateWord(String oldWord, String newWord) {
        int               count = 0;
        Connection        con   = null;
        PreparedStatement stmt  = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
                "UPDATE synonyms SET word = ? WHERE word = ?");
            stmt.setString(1, newWord);
            stmt.setString(2, oldWord);
            logFiner(stmt);
            count = stmt.executeUpdate();
            con.commit();

            if (count > 0) {
                notifyWordRenamed(oldWord, newWord);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            count = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return count;
    }

    public int updateSynonym(String oldSynonym, String newSynonym) {
        int               count = 0;
        Connection        con   = null;
        PreparedStatement stmt  = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
                "UPDATE synonyms SET synonym = ? WHERE synonym = ?");
            stmt.setString(1, newSynonym);
            stmt.setString(2, oldSynonym);
            logFiner(stmt);
            count = stmt.executeUpdate();
            con.commit();

            if (count > 0) {
                notifySynonymRenamed(oldSynonym, newSynonym);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            count = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return count;
    }

    public boolean existsWord(String word) {
        long              count = 0;
        Connection        con   = null;
        PreparedStatement stmt  = null;
        ResultSet         rs    = null;

        try {
            con = getConnection();

            String sql = "SELECT COUNT(*) FROM synonyms WHERE word = ?";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, word);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            count = 0;
            rollback(con);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return count > 0;
    }

    public boolean exists(String word, String synonym) {
        long              count = 0;
        Connection        con   = null;
        PreparedStatement stmt  = null;
        ResultSet         rs    = null;

        try {
            con = getConnection();

            String sql =
                "SELECT COUNT(*) FROM synonyms WHERE word = ? AND synonym = ?";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, word);
            stmt.setString(2, synonym);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            count = 0;
            rollback(con);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return count == 1;
    }

    public int insert(String word, String synonym) {
        if (exists(word, synonym)) {
            return 0;
        }

        int               count = 0;
        Connection        con   = null;
        PreparedStatement stmt  = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
                "INSERT INTO synonyms (word, synonym) VALUES (?, ?)");
            stmt.setString(1, word);
            stmt.setString(2, synonym);
            logFiner(stmt);
            count = stmt.executeUpdate();
            con.commit();

            if (count > 0) {
                notifySynonymInserted(word, synonym);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            count = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return count;
    }

    public int delete(String word, String synonym) {
        int               count = 0;
        Connection        con   = null;
        PreparedStatement stmt  = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
                "DELETE FROM synonyms WHERE word = ? AND synonym = ?");
            stmt.setString(1, word);
            stmt.setString(2, synonym);
            logFiner(stmt);
            count = stmt.executeUpdate();
            con.commit();

            if (count > 0) {
                notifySynonymOfWordDeleted(word, synonym);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
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
    public int deleteWord(String word) {
        int               count = 0;
        Connection        con   = null;
        PreparedStatement stmt  = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("DELETE FROM synonyms WHERE word = ?");
            stmt.setString(1, word);
            logFiner(stmt);
            count = stmt.executeUpdate();
            con.commit();

            if (count > 0) {
                notifyWordDeleted(word);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
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
    public Set<String> getSynonymsOf(String word) {
        Set<String>       synonyms = new LinkedHashSet<String>();
        Connection        con      = null;
        PreparedStatement stmt     = null;
        ResultSet         rs       = null;

        try {
            con  = getConnection();
            stmt = con.prepareStatement(getGetSynonymsOfSql());
            stmt.setString(1, word);
            stmt.setString(2, word);
            logFinest(stmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                synonyms.add(rs.getString(1));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return synonyms;
    }

    public Set<String> getAllWords() {
        Set<String> words = new LinkedHashSet<String>();
        Connection  con   = null;
        Statement   stmt  = null;
        ResultSet   rs    = null;

        try {
            con = getConnection();

            String sql = "SELECT DISTINCT word FROM synonyms ORDER BY word";

            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                words.add(rs.getString(1));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            words.clear();
        } finally {
            close(rs, stmt);
            free(con);
        }

        return words;
    }

    public void addListener(DatabaseSynonymsListener listener) {
        ls.add(listener);
    }

    public void removeListener(DatabaseSynonymsListener listener) {
        ls.remove(listener);
    }

    private void notifySynonymOfWordDeleted(String word, String synonym) {
        Set<DatabaseSynonymsListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseSynonymsListener listener : listeners) {
                listener.synonymOfWordDeleted(word, synonym);
            }
        }
    }

    private void notifyWordDeleted(String word) {
        Set<DatabaseSynonymsListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseSynonymsListener listener : listeners) {
                listener.wordDeleted(word);
            }
        }
    }

    private void notifySynonymInserted(String word, String synonym) {
        Set<DatabaseSynonymsListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseSynonymsListener listener : listeners) {
                listener.synonymInserted(word, synonym);
            }
        }
    }

    private void notifySynonymOfWordRenamed(String word, String oldSynonymName,
            String newSynonymName) {
        Set<DatabaseSynonymsListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseSynonymsListener listener : listeners) {
                listener.synonymOfWordRenamed(word, oldSynonymName,
                                              newSynonymName);
            }
        }
    }

    private void notifySynonymRenamed(String oldSynonymName,
                                      String newSynonymName) {
        Set<DatabaseSynonymsListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseSynonymsListener listener : listeners) {
                listener.synonymRenamed(oldSynonymName, newSynonymName);
            }
        }
    }

    private void notifyWordRenamed(String fromName, String toName) {
        Set<DatabaseSynonymsListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseSynonymsListener listener : listeners) {
                listener.wordRenamed(fromName, toName);
            }
        }
    }
}
