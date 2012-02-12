package org.jphototagger.repository.hsqldb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.EventBus;

import org.jphototagger.domain.repository.event.wordsets.WordsetInsertedEvent;
import org.jphototagger.domain.repository.event.wordsets.WordsetRemovedEvent;
import org.jphototagger.domain.repository.event.wordsets.WordsetWordAddedEvent;
import org.jphototagger.domain.repository.event.wordsets.WordsetWordRemovedEvent;
import org.jphototagger.domain.repository.event.wordsets.WordsetWordUpdatedEvent;
import org.jphototagger.domain.wordsets.Wordset;

/**
 * @author Elmar Baumann
 */
final class WordsetsDatabase extends Database {

    static final WordsetsDatabase INSTANCE = new WordsetsDatabase();

    List<Wordset> findAll() {
        List<Wordset> wordsets = new LinkedList<Wordset>();
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            String sql = "SELECT id, name FROM wordsets ORDER BY name ASC";
            con.prepareStatement(sql);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                long id = rs.getLong(1);
                String name = rs.getString(2);
                Wordset wordset = new Wordset(name);
                List<String> words = findWordsOfWordset(con, id);
                wordset.setWords(words);
                wordsets.add(wordset);
            }
        } catch (Exception ex) {
            Logger.getLogger(WordsetsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
            free(con);
        }
        return wordsets;
    }

    private List<String> findWordsOfWordset(Connection con, long wordsetsId) throws SQLException {
        PreparedStatement stmt = null;
        List<String> words = new ArrayList<String>();
        try {
            String sql = "SELECT word FROM wordsets_words WHERE id_wordsets = ? ORDER BY word_order ASC";
            stmt = con.prepareStatement(sql);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String word = rs.getString(1);
                words.add(word);
            }
        } finally {
            close(stmt);
        }
        return words;
    }

    boolean remove(String wordsetName) {
        if (wordsetName == null) {
            throw new NullPointerException("wordsetName == null");
        }
        int countAffectedRows = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            String sql = "DELETE FROM wordsets WHERE name = ?";
            logFiner(sql);
            con.prepareStatement(sql);
            con.setAutoCommit(true);
            countAffectedRows = stmt.executeUpdate();
            if (countAffectedRows > 0) {
                EventBus.publish(new WordsetRemovedEvent(this, wordsetName));
            }
        } catch (Exception ex) {
            Logger.getLogger(WordsetsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
            free(con);
        }
        return countAffectedRows > 0;
    }

    boolean addToWords(String wordsetName, String word) {
        if (wordsetName == null) {
            throw new NullPointerException("wordsetName == null");
        }
        if (word == null) {
            throw new NullPointerException("word == null");
        }
        if (existsWord(wordsetName, word)) {
            return false;
        }
        int countAffectedRows = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            long wordsetsId = findWordsetId(con, wordsetName);
            if (wordsetsId > 0) {
                String sql = "INSERT INTO wordsets_words (id_wordsets, word, word_order) VALUES (?, ?, ?)";
                stmt = con.prepareStatement(sql);
                stmt.setLong(1, wordsetsId);
                stmt.setString(2, word);
                stmt.setLong(3, getWordCount(con, wordsetsId));
                logFiner(stmt);
                countAffectedRows = stmt.executeUpdate();
                if (countAffectedRows > 0) {
                    EventBus.publish(new WordsetWordAddedEvent(this, wordsetName, word));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(WordsetsDatabase.class.getName()).log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return countAffectedRows > 0;
    }

    boolean removeFromWords(String wordsetName, String word) {
        if (wordsetName == null) {
            throw new NullPointerException("wordsetName == null");
        }
        if (word == null) {
            throw new NullPointerException("word == null");
        }
        int countAffectedRows = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            long wordsetsId = findWordsetId(con, wordsetName);
            if (wordsetsId > 0) {
                String sql = "REMOVE FROM wordsets_words WHERE id_wordsets = ? AND word = ?";
                stmt = con.prepareStatement(sql);
                stmt.setLong(1, wordsetsId);
                stmt.setString(2, word);
                logFiner(stmt);
                countAffectedRows = stmt.executeUpdate();
                if (countAffectedRows > 0) {
                    EventBus.publish(new WordsetWordRemovedEvent(this, wordsetName, word));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(WordsetsDatabase.class.getName()).log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return countAffectedRows > 0;
    }

    boolean updateWord(String wordsetName, String oldWord, String newWord) {
        if (wordsetName == null) {
            throw new NullPointerException("wordsetName == null");
        }
        if (oldWord == null) {
            throw new NullPointerException("oldWord == null");
        }
        if (newWord == null) {
            throw new NullPointerException("newWord == null");
        }
        if (newWord.equals(oldWord)) {
            return false;
        }
        int countAffectedRows = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            long wordsetsId = findWordsetId(con, wordsetName);
            if (wordsetsId > 0) {
                String sql = "UPDATE wordsets_words SET word = ? WHERE id_wordsets = ? AND word = ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, newWord);
                stmt.setLong(2, wordsetsId);
                stmt.setString(3, oldWord);
                logFiner(stmt);
                countAffectedRows = stmt.executeUpdate();
                if (countAffectedRows > 0) {
                    EventBus.publish(new WordsetWordUpdatedEvent(this, wordsetName, oldWord, newWord));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(WordsetsDatabase.class.getName()).log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return countAffectedRows > 0;
    }

    boolean insert(Wordset wordset) {
        if (wordset == null) {
            throw new NullPointerException("wordset == null");
        }
        String wordsetName = wordset.getName();
        if (existsWordset(wordsetName)) {
            return false;
        }
        int countAffectedRows = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            String sql = "INSERT INTO wordsets (name) VALUES (?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, wordsetName);
            logFiner(stmt);
            countAffectedRows = stmt.executeUpdate();
            if (countAffectedRows == 1) {
                long wordsetsId = findWordsetId(con, wordsetName);
                if (wordsetsId > 0) {
                    insertWords(con, wordsetsId, wordset.getWords());
                    con.commit();
                    EventBus.publish(new WordsetInsertedEvent(this, wordset));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(WordsetsDatabase.class.getName()).log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return countAffectedRows == 1;
    }

    private void insertWords(Connection con, long wordsetsId, List<String> words) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = "INSERT INTO wordsets_words (id_wordsets, word, word_order) VALUES";
            stmt = con.prepareStatement(sql);
            int wordOrder = 0;
            for (String word : words) {
                stmt.setLong(1, wordsetsId);
                stmt.setString(2, word);
                stmt.setInt(3, wordOrder);
                logFiner(stmt);
                stmt.executeUpdate();
                wordOrder++;
            }
        } finally {
            close(stmt);
        }
    }

    private long findWordsetId(Connection con, String wordsetName) throws SQLException {
        PreparedStatement stmt = null;
        int id = -1;
        try {
            String sql = "SELECT id FROM wordsets WHERE name = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, wordsetName);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } finally {
            close(stmt);
        }
        return id;
    }

    private long getWordCount(Connection con, long wordsetsId) throws SQLException {
        PreparedStatement stmt = null;
        long count = 0;
        try {
            String sql = "SELECT COUNT(*) FROM wordsets_words WHERE id_wordsets = ?";
            stmt = con.prepareStatement(sql);
            stmt.setLong(1, wordsetsId);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getLong(1);
            }
        } finally {
            close(stmt);
        }
        return count;
    }

    boolean existsWord(String wordsetName, String word) {
        if (wordsetName == null) {
            throw new NullPointerException("wordsetName == null");
        }
        if (word == null) {
            throw new NullPointerException("word == null");
        }
        Connection con = null;
        PreparedStatement stmt = null;
        long count = 0;
        try {
            con = getConnection();
            String sql = "SELECT COUNT(*) FROM wordsets ws INNER JOIN wordsets_words wsw"
                    + " ON wsw.id_wordsets = ws.id WHERE ws.name = ? AND wsw.word = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, wordsetName);
            stmt.setString(2, word);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(WordsetsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
            free(con);
        }
        return count > 0;
    }

    boolean existsWordset(String wordsetName) {
        if (wordsetName == null) {
            throw new NullPointerException("wordsetName == null");
        }
        Connection con = null;
        PreparedStatement stmt = null;
        long count = 0;
        try {
            con = getConnection();
            String sql = "SELECT COUNT(*) FROM wordsets WHERE name = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, wordsetName);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(WordsetsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
            free(con);
        }
        return count > 0;
    }

    private WordsetsDatabase() {
    }
}
