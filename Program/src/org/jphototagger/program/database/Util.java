package org.jphototagger.program.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.List;

/**
 * Utils for databases.
 *
 * @author Elmar Baumann
 */
public final class Util {

    /**
     * Returns parameters (?) whitin paranteses.
     *
     * @param  count count of parameters
     * @return       parameters in parantheses, e.g. <code>"(?, ?, ?)"</code>
     *               if count equals 3
     */
    public static String getParamsInParentheses(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Count < 1: " + count);
        }

        StringBuilder sb = new StringBuilder(count * 2);

        sb.append("(");

        for (int i = 0; i < count; i++) {
            sb.append((i == 0)
                      ? ""
                      : ",").append("?");
        }

        sb.append(")");

        return sb.toString();
    }

    /**
     * Sets to a prepared statement all strings in a list in the order as they
     * appear in the list.
     *
     * @param  stmt   statement
     * @param  params parameters to set
     * @param  offset 0 if the first string is the first parameter in the
     *                statement. Greater than zero, if the first string shall
     *                be the parameter <code>1 + offset</code>.
     * @throws        SQLException
     */
    static void setStringParams(PreparedStatement stmt,
                                List<? extends String> params, int offset)
            throws SQLException {
        assert offset >= 0 : "Negative offset: " + offset;

        if (offset < 0) {
            return;
        }

        int size = params.size();

        for (int i = 0; i < size; i++) {
            stmt.setString(i + 1 + offset, params.get(i));
        }
    }

    private Util() {}
}
