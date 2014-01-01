package org.jphototagger.lib.util;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Elmar Baumann
 */
public class DateUtilTest {

    @Test
    public void testIsValidGregorianDate() {
        assertFalse(DateUtil.isValidGregorianDate(-1, 01, 01));
        assertFalse(DateUtil.isValidGregorianDate(1581, 01, 01));
        assertTrue(DateUtil.isValidGregorianDate(1582, 01, 01));
        assertTrue(DateUtil.isValidGregorianDate(2013, 01, 13));
        assertTrue(DateUtil.isValidGregorianDate(2012, 02, 29));
        assertFalse(DateUtil.isValidGregorianDate(2011, 02, 29));
    }

    @Test
    public void testIsLeapYear() {
        assertFalse(DateUtil.isLeapYear(1900));
        assertTrue(DateUtil.isLeapYear(2000));
        assertFalse(DateUtil.isLeapYear(2003));
        assertTrue(DateUtil.isLeapYear(2004));
    }
}
