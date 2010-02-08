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
package de.elmar_baumann.jpt.event;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-02-07
 */
public final class DatabaseSynonymsEvent {

    public enum Type {
        WORD_DELETED,
        WORD_UPDATED,
        SYNONYM_DELETED,
        SYNONYM_INSERTED,
        SYNONYM_UPDATED,
    };

    private final Type type;
    private String     word;
    private String     oldWord;
    private String     synonym;
    private String     oldSynonym;

    public DatabaseSynonymsEvent(Type type) {
        this.type = type;
    }

    /**
     * Returns the event type.
     *
     * @return event type
     */
    public Type getType() {
        return type;
    }

    public String getOldSynonym() {
        return oldSynonym;
    }

    public void setOldSynonym(String oldSynonym) {
        this.oldSynonym = oldSynonym;
    }

    public String getOldWord() {
        return oldWord;
    }

    public void setOldWord(String oldWord) {
        this.oldWord = oldWord;
    }

    public String getSynonym() {
        return synonym;
    }

    public void setSynonym(String synonym) {
        this.synonym = synonym;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
