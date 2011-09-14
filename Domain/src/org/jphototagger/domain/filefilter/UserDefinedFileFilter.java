package org.jphototagger.domain.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jphototagger.lib.util.Bundle;

/**
 *
 *
 * @author Elmar Baumann
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class UserDefinedFileFilter implements Serializable {

    private static final long serialVersionUID = 1211554910220214424L;
    private Long id;
    private Boolean isNot = Boolean.FALSE;
    private Type type = Type.CONTAINS;
    private String name;
    private String expression;

    public enum Type {

        STARTS_WITH(0, Bundle.getString(UserDefinedFileFilter.class, "UserDefinedFileFilter.DisplayName.StartsWith")),
        CONTAINS(1, Bundle.getString(UserDefinedFileFilter.class, "UserDefinedFileFilter.DisplayName.Contains")),
        ENDS_WITH(2, Bundle.getString(UserDefinedFileFilter.class, "UserDefinedFileFilter.DisplayName.EndsWith")),
        REGEX(3, Bundle.getString(UserDefinedFileFilter.class, "UserDefinedFileFilter.DisplayName.Regex")),;
        private final int value;
        private final String displayName;

        private Type(int value, String displayName) {
            this.value = value;
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getValue() {
            return value;
        }

        public static Type parseValue(int value) {
            for (Type type : values()) {
                if (type.value == value) {
                    return type;
                }
            }

            throw new IllegalArgumentException("Illegal value: " + value);
        }

        public String getRegex(String s) {
            switch (this) {
                case STARTS_WITH:
                    return "^" + makeIgnoreCase(s) + ".*";

                case ENDS_WITH:
                    return ".*" + makeIgnoreCase(s) + "$";

                case CONTAINS:
                    return ".*" + makeIgnoreCase(s) + ".*";

                case REGEX:
                    return s;

                default:
                    assert false : this;
            }

            return "";
        }

        private String makeIgnoreCase(String s) {
            int length = s.length();
            StringBuilder sb = new StringBuilder(length * 4);    // * 4: A -> [Aa]
            String escaped = s.replace("\\", "\\\\").replace("*", "\\*").replace("[", "\\[").replace("]", "\\]");
            int escLength = escaped.length();

            for (int i = 0; i < escLength; i++) {
                char c = escaped.charAt(i);

                if (Character.isLetter(c)) {
                    char lc = Character.toLowerCase(c);
                    char uc = Character.toUpperCase(c);

                    sb.append("[").append(uc).append(lc).append("]");
                } else {
                    sb.append(c);
                }
            }

            return sb.toString();
        }
    }

    public UserDefinedFileFilter() {
    }

    public UserDefinedFileFilter(UserDefinedFileFilter other) {
        if (other == null) {
            throw new NullPointerException("other == null");
        }

        set(other);
    }

    public void set(UserDefinedFileFilter other) {
        if (other == null) {
            throw new NullPointerException("other == null");
        }

        if (other != this) {
            id = other.id;
            isNot = other.isNot;
            type = other.type;
            name = other.name;
            expression = other.expression;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isValid() {
        return (type != null) && (name != null) && (expression != null) && !name.trim().isEmpty()
                && !expression.trim().isEmpty();
    }

    public RegexFileFilter getFileFilter() {
        return new RegexFileFilter(type.getRegex(expression), isNot, id);
    }

    public Boolean getIsNot() {
        return isNot;
    }

    public void setIsNot(Boolean isNot) {
        this.isNot = isNot;
    }

    @Override
    public String toString() {
        return name == null
                ? ""
                : name;
    }

    /**
     * Two file filters are equals if their IDs ({@link #getId()}) are equals.
     *
     * @param  obj other object
     * @return     if the other object is of this type and has the same ID
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final UserDefinedFileFilter other = (UserDefinedFileFilter) obj;

        if ((this.id != other.id) && ((this.id == null) || !this.id.equals(other.id))) {
            return false;
        }

        return true;
    }

    /**
     * Filters are equals if they were created by an equal instance of this
     * class, that means, the IDs ({@link #getId()}) of the objects, which
     * created the file filters, are equal.
     *
     * @param  left  left file filter
     * @param  right right file filter
     * @return       true, if both file filters were created by equal instances
     *               of this class
     */
    public boolean filtersEquals(RegexFileFilter left, RegexFileFilter right) {
        if (left == null) {
            throw new NullPointerException("left == null");
        }

        if (right == null) {
            throw new NullPointerException("right == null");
        }

        return left.id == right.id;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 29 * hash + ((this.id != null)
                ? this.id.hashCode()
                : 0);

        return hash;
    }

    public static class RegexFileFilter implements FileFilter, Serializable {

        private static final long serialVersionUID = -1657911795602944754L;
        private final String pattern;
        private final boolean isNot;
        private final long id;

        public RegexFileFilter(String pattern, boolean not, long id) {
            if (pattern == null) {
                throw new NullPointerException("pattern == null");
            }

            this.pattern = pattern;
            this.isNot = not;
            this.id = id;
        }

        @Override
        public boolean accept(File file) {
            String filename = file.getName();

            try {
                return isNot
                        ? !filename.matches(pattern)
                        : filename.matches(pattern);
            } catch (Exception ex) {
                Logger.getLogger(UserDefinedFileFilter.class.getName()).log(Level.SEVERE, null, ex);
            }

            return false;
        }
    }
}
