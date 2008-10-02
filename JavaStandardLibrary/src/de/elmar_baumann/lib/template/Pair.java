package de.elmar_baumann.lib.template;

// Code: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6229146
/**
 * Paar.
 *
 * @param <A> 1. Klasse
 * @param <B> 2. Klasse
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/18
 */
public class Pair<A, B> {

    private final A first;
    private final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")"; // NOI18N
    }

    private static boolean equals(Object x, Object y) {
        return (x == null && y == null) || (x != null && x.equals(y));
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Pair &&
            equals(first, ((Pair) other).first) &&
            equals(second, ((Pair) other).second);
    }

    @Override
    public int hashCode() {
        if (first == null) {
            return (second == null) ? 0 : second.hashCode() + 1;
        } else if (second == null) {
            return first.hashCode() + 2;
        } else {
            return first.hashCode() * 17 + second.hashCode();
        }
    }
}
