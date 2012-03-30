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
import org.jphototagger.domain.repository.event.wordsets.WordsetRenamedEvent;
import org.jphototagger.domain.repository.event.wordsets.WordsetUpdatedEvent;
import org.jphototagger.domain.repository.event.wordsets.WordsetWordAddedEvent;
import org.jphototagger.domain.repository.event.wordsets.WordsetWordRemovedEvent;
import org.jphototagger.domain.repository.event.wordsets.WordsetWordRenamedEvent;
import org.jphototagger.domain.wordsets.Wordset;

/**
 * @author Elmar Baumann
 */
final class WordsetsDatabase extends Database {

    static final WordsetsDatabase INSTANCE = new WordsetsDatabase();
    private static final Logger LOGGER = Logger.getLogger(WordsetsDatabase.class.getName());

    List<Wordset> findAll() {
        List<Wordset> wordsets = new LinkedList<Wordset>();
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            String sql = "SELECT id, name FROM wordsets ORDER BY name ASC";
            stmt = con.prepareStatement(sql);
            LOGGER.log(Level.FINEST, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                long id = rs.getLong(1);
                String name = rs.getString(2);
                Wordset wordset = new Wordset(name);
                wordset.setId(id);
                List<String> words = findWordsOfWordset(con, id);
                wordset.setWords(words);
                wordsets.add(wordset);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
            free(con);
        }
        return wordsets;
    }

    Wordset find(String wordsetName) {
        if (wordsetName == null) {
            throw new NullPointerException("wordsetName == null");
        }
        Wordset wordset = null;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            String sql = "SELECT id FROM wordsets WHERE name = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, wordsetName);
            LOGGER.log(Level.FINEST, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                long id = rs.getLong(1);
                wordset = new Wordset(wordsetName);
                wordset.setId(id);
                List<String> words = findWordsOfWordset(con, id);
                wordset.setWords(words);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
            free(con);
        }
        return wordset;
    }

    Wordset findById(long id) {
        Wordset wordset = null;
        try {
            String wordsetName = findWordsetNameById(id);
            if (wordsetName != null) {
                wordset = find(wordsetName);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return wordset;
    }

    String findWordsetNameById(long id) {
        String name = null;
        PreparedStatement stmt = null;
        Connection con = null;
        try {
            con = getConnection();
            String sql = "SELECT name FROM wordsets WHERE id = ?";
            stmt = con.prepareStatement(sql);
            stmt.setLong(1, id);
            LOGGER.log(Level.FINEST, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                name = rs.getString(1);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
            free(con);
        }
        return name;
    }

    List<String> findAllWordsetNames() {
        List<String> wordsetNames = new LinkedList<String>();
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            String sql = "SELECT name FROM wordsets ORDER BY name ASC";
            stmt = con.prepareStatement(sql);
            LOGGER.log(Level.FINEST, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString(1);
                wordsetNames.add(name);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
            free(con);
        }
        return wordsetNames;
    }

    private List<String> findWordsOfWordset(Connection con, long wordsetsId) throws SQLException {
        PreparedStatement stmt = null;
        List<String> words = new ArrayList<String>();
        try {
            String sql = "SELECT word FROM wordsets_words WHERE id_wordsets = ? ORDER BY word_order ASC";
            stmt = con.prepareStatement(sql);
            stmt.setLong(1, wordsetsId);
            LOGGER.log(Level.FINEST, stmt.toString());
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
            con = getConnection();
            String sql = "DELETE FROM wordsets WHERE name = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, wordsetName);
            LOGGER.log(Level.FINER, sql);
            con.setAutoCommit(true);
            countAffectedRows = stmt.executeUpdate();
            if (countAffectedRows > 0) {
                EventBus.publish(new WordsetRemovedEvent(this, wordsetName));
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
            con = getConnection();
            long wordsetsId = findWordsetId(con, wordsetName);
            if (wordsetsId > 0) {
                String sql = "INSERT INTO wordsets_words (id_wordsets, word, word_order) VALUES (?, ?, ?)";
                stmt = con.prepareStatement(sql);
                stmt.setLong(1, wordsetsId);
                stmt.setString(2, word);
                stmt.setLong(3, getWordCount(con, wordsetsId));
                LOGGER.log(Level.FINER, stmt.toString());
                countAffectedRows = stmt.executeUpdate();
                if (countAffectedRows > 0) {
                    EventBus.publish(new WordsetWordAddedEvent(this, wordsetName, word));
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
            con = getConnection();
            long wordsetsId = findWordsetId(con, wordsetName);
            if (wordsetsId > 0) {
                String sql = "REMOVE FROM wordsets_words WHERE id_wordsets = ? AND word = ?";
                stmt = con.prepareStatement(sql);
                stmt.setLong(1, wordsetsId);
                stmt.setString(2, word);
                LOGGER.log(Level.FINER, stmt.toString());
                countAffectedRows = stmt.executeUpdate();
                if (countAffectedRows > 0) {
                    EventBus.publish(new WordsetWordRemovedEvent(this, wordsetName, word));
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return countAffectedRows > 0;
    }

    boolean renameWord(String wordsetName, String oldWord, String newWord) {
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
            con = getConnection();
            long wordsetsId = findWordsetId(con, wordsetName);
            if (wordsetsId > 0) {
                String sql = "UPDATE wordsets_words SET word = ? WHERE id_wordsets = ? AND word = ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, newWord);
                stmt.setLong(2, wordsetsId);
                stmt.setString(3, oldWord);
                LOGGER.log(Level.FINER, stmt.toString());
                countAffectedRows = stmt.executeUpdate();
                if (countAffectedRows > 0) {
                    EventBus.publish(new WordsetWordRenamedEvent(this, wordsetName, oldWord, newWord));
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return countAffectedRows > 0;
    }

    boolean renameWordset(String oldWordsetName, String newWordsetName) {
        if (oldWordsetName == null) {
            throw new NullPointerException("oldWordsetName == null");
        }
        if (newWordsetName == null) {
            throw new NullPointerException("newWordsetName == null");
        }
        int countAffectedRows = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            String sql = "UPDATE wordsets SET name = ? WHERE name = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, oldWordsetName);
            stmt.setString(2, newWordsetName);
            LOGGER.log(Level.FINER, stmt.toString());
            countAffectedRows = stmt.executeUpdate();
            if (countAffectedRows > 0) {
                EventBus.publish(new WordsetRenamedEvent(this, oldWordsetName, newWordsetName));
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
            LOGGER.log(Level.FINER, stmt.toString());
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
            LOGGER.log(Level.SEVERE, null, ex);
            countAffectedRows = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return countAffectedRows == 1;
    }

    boolean update(Wordset wordset) {
        if (wordset == null) {
            throw new NullPointerException("wordset == null");
        }
        long wordsetsId = wordset.getId();
        Wordset oldWordset = findById(wordsetsId);
        if (oldWordset == null) {
            return false;
        }
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            deleteWordsOfWordset(con, wordsetsId);
            String oldName = oldWordset.getName();
            String newName = wordset.getName();
            if (!newName.equals(oldName)) {
                String sql = "UPDATE wordsets SET name = ? WHERE id = ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, wordset.getName());
                stmt.setLong(2, wordsetsId);
                stmt.executeUpdate();
                LOGGER.log(Level.FINER, stmt.toString());
            }
            insertWords(con, wordsetsId, wordset.getWords());
            con.commit();
            EventBus.publish(new WordsetUpdatedEvent(this, oldWordset, wordset));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            rollback(con);
            return false;
        } finally {
            close(stmt);
            free(con);
        }
        return true;
    }

    private void deleteWordsOfWordset(Connection con, long wordsetsId) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = "DELETE FROM wordsets_words WHERE id_wordsets = ?";
            stmt = con.prepareStatement(sql);
            stmt.setLong(1, wordsetsId);
            LOGGER.log(Level.FINER, stmt.toString());
            stmt.executeUpdate();
        } finally {
            close(stmt);
        }
    }

    private void insertWords(Connection con, long wordsetsId, List<String> words) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = "INSERT INTO wordsets_words (id_wordsets, word, word_order) VALUES (?, ?, ?)";
            stmt = con.prepareStatement(sql);
            int wordOrder = 0;
            for (String word : words) {
                stmt.setLong(1, wordsetsId);
                stmt.setString(2, word);
                stmt.setInt(3, wordOrder);
                LOGGER.log(Level.FINER, stmt.toString());
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
            LOGGER.log(Level.FINEST, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } finally {
            close(stmt);
        }
        return id;
    }

    long findWordsetId(String wordsetName) {
        if (wordsetName == null) {
            throw new NullPointerException("wordsetName == null");
        }
        long id = Long.MIN_VALUE;
        Connection con = null;
        try {
            con = getConnection();
            id = findWordsetId(con, wordsetName);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            free(con);
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
            LOGGER.log(Level.FINEST, stmt.toString());
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
            LOGGER.log(Level.FINEST, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
            LOGGER.log(Level.FINEST, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
            free(con);
        }
        return count > 0;
    }

    boolean existsWordset(Long id) {
        Connection con = null;
        PreparedStatement stmt = null;
        long count = 0;
        try {
            con = getConnection();
            String sql = "SELECT COUNT(*) FROM wordsets WHERE id = ?";
            stmt = con.prepareStatement(sql);
            stmt.setLong(1, id);
            LOGGER.log(Level.FINEST, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
            free(con);
        }
        return count > 0;
    }

    private WordsetsDatabase() {
    }
}
