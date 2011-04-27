package org.jphototagger.lib.util;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class NumberUtil {

    /**
     * 
     * @param  string may be null
     * @return 
     */
    static boolean isShort(String string) {
        if (string == null) {
            return false;
        }
        
        try {
            Short.parseShort(string);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * 
     * @param  string may be null
     * @return 
     */
    static boolean isLong(String string) {
        if (string == null) {
            return false;
        }
        
        try {
            Long.parseLong(string);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * 
     * @param  string may be null
     * @return 
     */
    static boolean isInteger(String string) {
        if (string == null) {
            return false;
        }
        
        try {
            Integer.parseInt(string);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * 
     * @param  string may be null
     * @return 
     */
    static boolean isFloat(String string) {
        if (string == null) {
            return false;
        }
        
        try {
            Float.parseFloat(string);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * 
     * @param  string may be null
     * @return 
     */
    static boolean isDouble(String string) {
        if (string == null) {
            return false;
        }
        
        try {
            Double.parseDouble(string);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
    
    private NumberUtil() {
    }
}
