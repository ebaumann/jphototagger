package org.jphototagger.domain.repository.browse;

import java.util.Objects;

/**
 * A ResultSetBrowser will be notified while <strong>iterating</strong> through
 * a {@link java.sql.ResultSet}.
 *
 * @author Elmar Baumann
 */
public interface ResultSetBrowser {

    public static final class Result {

        public enum Value {
            SUCCESS,
            THRWON,
            NOT_SUPPORTED
        }

        private final Value value;
        private final Throwable thrown;
        private final long executionTimeInMilliSec;

        public Result(Value value, Throwable thrown, long executionTimeInMilliSec) {
            this.value = Objects.requireNonNull(value, "value == null");
            this.thrown = thrown;
            this.executionTimeInMilliSec = executionTimeInMilliSec;
        }

        public Value getValue() {
            return value;
        }

        public Throwable getThrown() {
            return thrown;
        }

        public long getExecutionTimeInMilliSec() {
            return executionTimeInMilliSec;
        }
    }

    /**
     * Retrieves the column names of a ResultSet.
     *
     * @param columnNames columnNames
     */
    void columnNames(Object[] columnNames);

    /**
     * Retrieves a row of a ResultSet.
     *
     * @param rowData row data, each Object represents a column value
     */
    void row(Object[] rowData);

    /**
     * Will be called after iterating through a ResultSet.
     *
     * @param result result
     */
    void finished(Result result);
}
